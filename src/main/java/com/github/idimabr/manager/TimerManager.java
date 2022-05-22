package com.github.idimabr.manager;

import com.github.idimabr.RaphaTimerMobs;
import com.github.idimabr.objects.BackupEntity;
import com.github.idimabr.utils.ConfigUtil;
import com.github.idimabr.utils.ItemBuilder;
import com.github.idimabr.utils.ModifyUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TimerManager {

    private static HashMap<Integer, Pair<Entity, Integer>> TIMER = Maps.newHashMap();
    private static HashMap<Integer, BackupEntity> BACKUP = Maps.newHashMap();

    public static HashMap<Integer, Pair<Entity, Integer>> getTimerList() {
        return TIMER;
    }

    public static HashMap<Integer, BackupEntity> getBackupList() {
        return BACKUP;
    }

    public static int loadTimerMobs() {
        TIMER.clear();
        BACKUP.clear();

        ConfigUtil config = RaphaTimerMobs.getInstance().getConfig();

        ConfigurationSection mainSection = config.getConfigurationSection("Entities");
        int numberEntities = 0;
        for (String key : mainSection.getKeys(false)) {
            ConfigurationSection entitySection = mainSection.getConfigurationSection(key);

            int id = entitySection.getInt("ID");
            int timer = entitySection.getInt("Timer");
            boolean hostile = entitySection.getBoolean("ForceHostile");
            EntityType type = EntityType.valueOf(entitySection.getString("Type"));
            Location location = config.getLocation(mainSection.getCurrentPath() + "." + key + ".Location");
            if(location == null) continue;

            removeCreatures(location.getWorld());

            List<Pair<String, Double>> attributes = Lists.newArrayList();
            ConfigurationSection attributeMainSection = entitySection.getConfigurationSection("Attributes");
            for (String attributeKey : attributeMainSection.getKeys(false)) {

                String attributeType = attributeMainSection.getString(attributeKey + ".Type");
                double attributeValue = attributeMainSection.getDouble(attributeKey + ".Value");

                attributes.add(Pair.of(attributeType, attributeValue));
            }

            List<Pair<Integer, ItemStack>> drops = Lists.newArrayList();
            ConfigurationSection dropsMainSection = entitySection.getConfigurationSection("Drops");
            for (String dropKey : dropsMainSection.getKeys(false)) {
                ConfigurationSection dropsSection = dropsMainSection.getConfigurationSection(dropKey);

                int chance = dropsSection.getInt("Chance");

                Material material = Material.getMaterial(dropsSection.getString("Material"));
                if(material == null) continue;

                ItemBuilder builder = new ItemBuilder(material);

                if(dropsSection.contains("Data"))
                    builder.setDurability((short) dropsSection.getInt("Data"));

                if(dropsSection.contains("Amount"))
                    builder.setAmount(dropsSection.getInt("Amount"));

                if(dropsSection.contains("Name"))
                    builder.setName(dropsSection.getString("Name").replace("&","§"));

                if(dropsSection.contains("Lore"))
                    builder.setLore(dropsSection.getStringList("Lore").stream().map($ -> $.replace("&","§")).collect(Collectors.toList()));

                if(dropsSection.contains("Enchantments")) {
                    for (String valueEnchant : dropsSection.getStringList("Enchantments")) {
                        if(!valueEnchant.contains(";")) continue;

                        String nameEnchant = valueEnchant.split(";")[0];
                        int levelEnchant = Integer.parseInt(valueEnchant.split(";")[1]);

                        Enchantment ench = Enchantment.getByName(nameEnchant);
                        if(ench == null) continue;

                        builder.addUnsafeEnchantment(ench, levelEnchant);
                    }
                }
                drops.add(Pair.of(chance, builder.toItemStack()));
            }

            Entity entity = ModifyUtil.spawnEntityWithAttributes(id, hostile, type, location, attributes);
            if(entity instanceof LivingEntity)
                ((LivingEntity) entity).setRemoveWhenFarAway(false);

            if(entitySection.isSet("Name")) {
                String name = entitySection.getString("Name").replace("&","§");
                entity.setCustomNameVisible(true);
                entity.setCustomName(name);
                BACKUP.put(id, new BackupEntity(id, hostile, name, type, location, timer, attributes, drops));
            }else{
                BACKUP.put(id, new BackupEntity(id, hostile, type, location, timer, attributes, drops));
            }

            TIMER.put(id, Pair.of(entity, timer));
            numberEntities++;
        }

        System.out.println("Foram carregadas " + numberEntities + " entidades");
        return numberEntities;
    }

    private static void removeCreatures(World world){
        world.getLivingEntities().forEach($ -> {
            if($.hasMetadata("id"))
                $.remove();
        });
    }
}
