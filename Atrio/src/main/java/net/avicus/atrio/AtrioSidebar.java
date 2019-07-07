package net.avicus.atrio;

import net.avicus.compendium.TextStyle;
import net.avicus.compendium.alternator.TimedAlternator;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.utils.Sidebar;
import net.avicus.hook.credits.Credits;
import net.avicus.magma.NetworkIdentification;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class AtrioSidebar implements Listener {

    private final static String pad = "        ";

    public static final TimedAlternator<String> TITLE = new TimedAlternator<>(
            1000,
            pad + ChatColor.AQUA.toString() + ChatColor.BOLD + NetworkIdentification.NAME + pad,
            pad + ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + NetworkIdentification.NAME + pad,
            pad + ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + NetworkIdentification.NAME + pad,
            pad + ChatColor.WHITE.toString() + ChatColor.BOLD + NetworkIdentification.NAME + pad,
            pad + ChatColor.RED.toString() + ChatColor.BOLD + NetworkIdentification.NAME + pad,
            pad + ChatColor.GOLD.toString() + ChatColor.BOLD + NetworkIdentification.NAME + pad,
            pad + ChatColor.YELLOW.toString() + ChatColor.BOLD + NetworkIdentification.NAME + pad,
            pad + ChatColor.GREEN.toString() + ChatColor.BOLD + NetworkIdentification.NAME + pad,
            pad + ChatColor.BLUE.toString() + ChatColor.BOLD + NetworkIdentification.NAME + pad
    );

    private final AtrioPlugin plugin;
    private final Player player;
    private final Sidebar sidebar;
    private BukkitTask task;

    public AtrioSidebar(AtrioPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.sidebar = new Sidebar("Title");
    }

    public void start() {
        this.player.setScoreboard(this.sidebar.getScoreboard());
        Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.task = Bukkit.getServer().getScheduler()
                .runTaskTimer(this.plugin, new AtrioSidebarTask(), 0, 2);
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (this.task != null) {
            this.task.cancel();
        }
    }

    public class AtrioSidebarTask implements Runnable {

        @Override
        public void run() {
            String title = TITLE.next();

            sidebar.setTitle(title);

            int credits = Credits.getCredits(player);
            Localizable creditText = new LocalizedNumber(credits,
                    TextStyle.ofColor(ChatColor.YELLOW).bold());

            sidebar.replace(10, "You are playing on the ");
            sidebar.replace(9, ChatColor.YELLOW.toString() + ChatColor.BOLD
                    + "Lobby" + ChatColor.WHITE + " server");
            sidebar.replace(8, "");
            sidebar.replace(7, "Play!");
            sidebar.replace(6, ChatColor.YELLOW + "/server Atlas");
            sidebar.replace(5, "");
            sidebar.replace(4, "Credits");
            sidebar.replace(3, creditText.translate(player.getLocale()).toLegacyText());
            sidebar.replace(2, "");
            sidebar.replace(1, "Website");
            sidebar.replace(0, ChatColor.YELLOW + ChatColor.BOLD.toString() + NetworkIdentification.URL);
        }
    }
}
