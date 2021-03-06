package net.avicus.hook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import lombok.Getter;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.config.ConfigFile;

public class Main {

    @Getter
    public static Hook hook;

    public static Logger getLogger(String name) {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new CustomFormatter());
        Logger logger = Logger.getLogger(name);
        logger.addHandler(handler);
        try {
            FileHandler fileHandler = new FileHandler("hook.log", 1024000, 3, true);
            fileHandler.setFormatter(new CustomFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.setUseParentHandlers(false);
        return logger;
    }

    public static void main(String[] args) {
        File remote = new File(getFolder(), "config.yml");
        InputStream local = Main.class.getResourceAsStream("/config.yml");

        if (!remote.exists()) {
            new Config(local).save(remote);
        }

        File dbRemote = new File(getFolder(), "db.yml");
        InputStream dbLocal = Main.class.getResourceAsStream("/db.yml");
        if (!dbRemote.exists()) {
            new Config(dbLocal).save(dbRemote);
        }

        try {
            local.close();
            dbLocal.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        hook = new Hook(new ConfigFile(dbRemote));
        hook.start();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static File getFolder() {
        try {
            return new File(
                    Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                    .getParentFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
