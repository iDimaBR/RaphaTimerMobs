package com.github.idimabr;

import com.github.idimabr.commands.TimerMobsCommand;
import com.github.idimabr.listeners.DeathCreatureListener;
import com.github.idimabr.manager.TimerManager;
import com.github.idimabr.tasks.TimerRunnable;
import com.github.idimabr.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RaphaTimerMobs extends JavaPlugin {

    private static RaphaTimerMobs instance;
    private ConfigUtil config;

    @Override
    public void onEnable() {
        instance = this;
        config = new ConfigUtil(this, null, "config.yml", false);
        TimerManager.loadTimerMobs();

        getCommand("timermobs").setExecutor(new TimerMobsCommand(this));
        Bukkit.getPluginManager().registerEvents(new DeathCreatureListener(), this);
        new TimerRunnable().runTaskTimer(this, 20L, 20L);
    }

    @Override
    public void onDisable() {
        config.reloadConfig();
    }

    public static RaphaTimerMobs getInstance() {
        return instance;
    }

    @Override
    public ConfigUtil getConfig() {
        return config;
    }
}
