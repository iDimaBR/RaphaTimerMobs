package com.github.idimabr.commands;

import com.github.idimabr.RaphaTimerMobs;
import com.github.idimabr.manager.TimerManager;
import com.github.idimabr.utils.ConfigUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TimerMobsCommand implements CommandExecutor {

    private RaphaTimerMobs plugin;

    public TimerMobsCommand(RaphaTimerMobs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("§cSomente jogadores.");
            return false;
        }

        final Player player = (Player) sender;
        final ConfigUtil config = plugin.getConfig();

        if(args.length == 2 && args[0].equalsIgnoreCase("definir")) {
            final String keyEntity = args[1];

            if (!config.isSet("Entities." + keyEntity)) {

                final String availables = StringUtils.join(
                        config.getConfigurationSection("Entities").getKeys(false),
                        ", "
                );

                player.sendMessage("§cCriatura não encontrada...");
                player.sendMessage("§cDisponíveis: §f" + availables);
                return false;
            }

            final Location location = player.getLocation();

            ConfigurationSection mainSection = config.getConfigurationSection("Entities." + keyEntity);
            mainSection.set("Location.World", location.getWorld().getName());
            mainSection.set("Location.X", location.getX());
            mainSection.set("Location.Y", location.getY());
            mainSection.set("Location.Z", location.getZ());
            config.saveConfig();

            player.sendMessage("§aA localização de " + keyEntity + " foi definida!");
            player.sendMessage("§7Obs: Será necessário utilizar /timermobs reload");
            return false;
        }else if(args.length == 4 && args[0].equalsIgnoreCase("criar")) {

            final String keyEntity = args[1];
            final String typeString = args[2];
            final Location location = player.getLocation();

            if (config.isSet("Entities." + keyEntity)) {
                player.sendMessage("§cEssa criatura já está definida na configuração...");
                return false;
            }

            if (!isValidType(typeString)) {
                player.sendMessage("§cEntidade '" + typeString + "' não é válida!");
                return false;
            }

            final int timer = Integer.parseInt(args[3]);

            final ConfigurationSection mainSection = config.createSection("Entities." + keyEntity);

            int id = Integer.parseInt(RandomStringUtils.randomNumeric(4));

            mainSection.set("ID", id);
            mainSection.set("Type", typeString.toUpperCase());
            mainSection.set("Timer", timer);
            mainSection.set("ForceHostile", false);

            mainSection.set("Attributes.Damage.Type", "damage");
            mainSection.set("Attributes.Damage.Value", 5.0);

            mainSection.set("Attributes.Health.Type", "health");
            mainSection.set("Attributes.Health.Value", 100.0);

            final ConfigurationSection drops = mainSection.createSection("Drops");

            drops.set("Example.Chance", 100);
            drops.set("Example.Material", "DIRT");
            drops.set("Example.Amount", 1);


            mainSection.set("Location.World", location.getWorld().getName());
            mainSection.set("Location.X", location.getX());
            mainSection.set("Location.Y", location.getY());
            mainSection.set("Location.Z", location.getZ());
            config.saveConfig();

            player.sendMessage("§aA configuração do '" + keyEntity + "' foi gerada!");
            player.sendMessage("§7Obs: Será necessário utilizar /timermobs reload");
            return false;
        }else if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
            config.saveConfig();
            config.reloadConfig();
            int loaded = TimerManager.loadTimerMobs();
            int totalEntities = config.getConfigurationSection("Entities").getKeys(false).size();
            player.sendMessage(" ");
            player.sendMessage("§aConfiguração reiniciada!");
            player.sendMessage("§aForam carregadas §f" + loaded + " §ade §f" + totalEntities + " §aentidades!");
            player.sendMessage(" ");
            return false;
        }else{
            player.sendMessage("§cCOMANDOS:");
            player.sendMessage("§c");
            player.sendMessage("§c /timermobs definir <mob>");
            player.sendMessage("§c /timermob criar <name> <mobType> <timer>");
            return false;
        }
    }

    private boolean isValidType(String type){
        return Arrays.stream(EntityType.values()).anyMatch($ -> $.name().equalsIgnoreCase(type));
    }
}
