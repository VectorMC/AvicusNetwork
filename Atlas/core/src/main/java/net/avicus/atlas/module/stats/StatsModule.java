package net.avicus.atlas.module.stats;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.event.player.PlayerReceiveMVPEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.BridgeableModule;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.ModuleBridge;
import net.avicus.atlas.module.damagetrack.DamageTrackModule;
import net.avicus.atlas.module.stats.action.ActionStore;
import net.avicus.atlas.module.stats.action.lifetime.LifetimeDisplayUtils;
import net.avicus.atlas.module.stats.action.lifetime.LifetimeStore;
import net.avicus.atlas.module.stats.action.lifetime.type.PlayerLifetime;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.Paste;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.utils.Strings;
import net.avicus.grave.event.PlayerDeathEvent;
import net.avicus.magma.channel.staff.StaffChannels;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.github.paperspigot.Title;

import java.util.*;

@ToString(exclude = "match")
public class StatsModule extends BridgeableModule<ModuleBridge<StatsModule>> implements Module {

    private final Match match;
    @Getter
    private ActionStore store;

    @Getter
    private List<Localizable> matchFacts;

    public StatsModule(Match match) {
        this.match = match;
        buildBridges(this);
    }

    private static Map<UUID, Double> sortScores(Map<UUID, Double> aMap) {
        Set<Map.Entry<UUID, Double>> mapEntries = aMap.entrySet();

        // used linked list to sort, because insertion of elements in linked list is faster than an array list.
        List<Map.Entry<UUID, Double>> aList = new LinkedList<Map.Entry<UUID, Double>>(mapEntries);

        // sorting the List
        Collections.sort(aList, new Comparator<Map.Entry<UUID, Double>>() {
            @Override
            public int compare(Map.Entry<UUID, Double> ele1,
                               Map.Entry<UUID, Double> ele2) {

                return ele2.getValue().compareTo(ele1.getValue());
            }
        });

        // Storing the list into Linked HashMap to preserve the order of insertion.
        Map<UUID, Double> aMap2 = new LinkedHashMap<UUID, Double>();
        for (Map.Entry<UUID, Double> entry : aList) {
            aMap2.put(entry.getKey(), entry.getValue());
        }
        return aMap2;
    }

    @Override
    public void open() {
        this.store = new ActionStore(this.match);
        Events.register(this.store);
        getBridges().values().forEach(b -> b.onOpen(this));
    }

    @Override
    public void close() {
        Events.unregister(this.store);
        getBridges().values().forEach(b -> b.onClose(this));
    }

    @EventHandler
    public void showLifeRecap(PlayerDeathEvent event) {
        // Display stats then recap
        DamageTrackModule trackModule = match.getRequiredModule(DamageTrackModule.class);
        List<Localizable> damage = trackModule.getPlayerPVPRecap(event.getPlayer());
        damage.forEach(event.getPlayer()::sendMessage);

        // Recap
        PlayerLifetime lifetime = this.store.getLifetimeStore()
                .getCurrentLifetime(event.getPlayer(), false);
        if (lifetime != null) {
            // Header
            BaseComponent name = Translations.STATS_RECAP_LIFE.with(ChatColor.GOLD)
                    .translate(event.getPlayer().getLocale());

            List<Localizable> melee = LifetimeDisplayUtils.getMeleeDisplay(lifetime);
            List<Localizable> objectives = LifetimeDisplayUtils.getObjectiveDisplay(lifetime, event.getPlayer());

            if (melee.size() + objectives.size() > 0) {
                event.getPlayer().sendMessage(Strings
                        .padTextComponent(name, " ", ChatColor.DARK_AQUA.toString() + ChatColor.STRIKETHROUGH,
                                ChatColor.BLUE));
                melee.forEach(event.getPlayer()::sendMessage);
                objectives.forEach(event.getPlayer()::sendMessage);
            }
        }
    }

    @EventHandler
    public void showMatchRecap(MatchStateChangeEvent event) {
        if (event.isChangeToNotPlaying() && event.isFromPlaying()) {
            AtlasTask.of(() -> {
                event.getMatch().getPlayers().forEach(this::matchRecap);
            }).nowAsync();
        }
    }

    @EventHandler
    public void showMatchRecap(PlayerChangedGroupEvent event) {
        if (event.getGroup().isSpectator() && event.getGroupFrom().isPresent() && !event.getGroupFrom()
                .get().isSpectator()) {
            AtlasTask.of(() -> {
                matchRecap(event.getPlayer());
            }).nowAsync();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void generateFacts(MatchStateChangeEvent event) {
        if (event.isChangeToNotPlaying() && event.isFromPlaying()) {
            AtlasTask.of(() -> {
                this.matchFacts = LifetimeDisplayUtils.getMatchFacts(this.store.getLifetimeStore());
                Bukkit.broadcast(LifetimeDisplayUtils.getRandomMatchFact(this.matchFacts));
                Bukkit.broadcast(Translations.STATS_FACTS_COMMAND.with(ChatColor.GREEN));
            }).nowAsync();
            AtlasTask.of(() -> {
                if (this.match.isLoaded()) {
                    UUID mvp = getHighestScore();
                    if (mvp == null) {
                        return;
                    }
                    User mvpUser = Users.user(mvp).get();
                    String userName;
                    if (!Bukkit.getPlayer(mvp).isOnline()) {
                        userName = Bukkit.getOfflinePlayer(mvp).getName();
                    } else {
                        userName = Bukkit.getPlayer(mvp).getDisplayName();
                    }
                    // Users.getTranslatableDisplayName(mvpUser, false) <- SCREW YOU TRYNA TRANSLATE A NAME TODO
                    userName = ChatColor.DARK_AQUA + userName;
                    Localizable name = Messages.UI_MVP_TITLE
                            .with(ChatColor.GREEN, userName);
                    Localizable sub;
                    if (Bukkit.getPlayer(mvp) == null || !Bukkit.getPlayer(mvp).isOnline()) {
                        sub = Messages.UI_MVP_NOT_ONLINE.with(ChatColor.RED);
                    } else {
                        sub = LifetimeDisplayUtils.randomTag(Arrays.asList(
                                Messages.UI_MVP_TAG_1,
                                Messages.UI_MVP_TAG_2,
                                Messages.UI_MVP_TAG_3,
                                Messages.UI_MVP_TAG_4,
                                Messages.UI_MVP_TAG_5,
                                Messages.UI_MVP_TAG_6,
                                Messages.UI_MVP_TAG_7,
                                Messages.UI_MVP_TAG_8
                        ));
                    }
                    Events.call(new PlayerReceiveMVPEvent(Bukkit.getPlayer(mvp)));
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        p.sendTitle(new Title(name.translate(p), sub.translate(p), 20, 150, 40));
                    });

                    StringBuilder data = new StringBuilder("Match Action Data:\n");
                    data.append("MVP: " + Users.getMeta(Bukkit.getPlayer(mvp)).getLeft() + Bukkit.getPlayer(mvp).getName() + "\n");
                    this.store.getLifetimeStore().getPlayerLifetimes().entries().forEach(e -> {
                        data.append(Users.user(e.getKey()).map(User::getName).orElse("[User] Not in DB: " + e.getKey())).append(": \n");
                        e.getValue().getActions()
                                .forEach(a -> data.append("  " + a.getScore() + "  " + a.getDebugMessage() + "\n"));
                    });
                    List<DamageTrackModule.DamageExchange> exc = match.getRequiredModule(DamageTrackModule.class).getDamageExchanges();
                    data.append("\nDamage Exchanges: [").append(exc.size()).append(" total]\n");
                    for (DamageTrackModule.DamageExchange exchange : exc) {
                        data.append(exchange.toString()).append("\n");
                    }
                    String link = new Paste("Action Data", "Console", data.toString(), true).upload();
                    TextComponent message = new TextComponent("Data Paste: " + link);
                    message.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
                    StaffChannels.DEV_CHANNEL.simpleLocalSend(null, message);
                }
            }).laterAsync(5 * 20);
        }
    }

    private UUID getHighestScore() {
        Map<UUID, Double> scores = Maps.newLinkedHashMap();
        LifetimeStore store = this.store.getLifetimeStore();
        this.store.getLifetimeStore().getPlayerLifetimes().keys().forEach(u -> {
            double score = store.getScore(u);
            if (score > 0) {
                scores.put(u, score);
            }
        });
        Map<UUID, Double> sortScores = sortScores(scores);
        return sortScores.keySet().stream().findFirst().orElse(null);
    }

    private void matchRecap(Player player) {
        // Header
        BaseComponent name = Translations.STATS_RECAP_MATCH.with(ChatColor.GOLD)
                .translate(player.getLocale());

        List<Localizable> melee = LifetimeDisplayUtils
                .getMeleeDisplay(this.store.getLifetimeStore(), player.getUniqueId());
        List<Localizable> objectives = LifetimeDisplayUtils
                .getObjectiveDisplay(this.store.getLifetimeStore(), player.getUniqueId(), player);

        if (melee.size() + objectives.size() > 0) {
            player.sendMessage(Strings
                    .padTextComponent(name, " ", ChatColor.DARK_AQUA.toString() + ChatColor.STRIKETHROUGH,
                            ChatColor.BLUE));
            melee.forEach(player::sendMessage);
            objectives.forEach(player::sendMessage);
        }
    }
}