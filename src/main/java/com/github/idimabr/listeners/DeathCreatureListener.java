package com.github.idimabr.listeners;

import com.github.idimabr.manager.TimerManager;
import com.github.idimabr.objects.BackupEntity;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathCreatureListener implements Listener {

    @EventHandler
    public void onDeathCreature(EntityDeathEvent e){
        LivingEntity entity = e.getEntity();
        if(!entity.hasMetadata("id")) return;

        int id = entity.getMetadata("id").get(0).asInt();
        BackupEntity backup = TimerManager.getBackupList().get(id);

        List<ItemStack> drops = e.getDrops();
        e.getDrops().clear();

        for (Pair<Integer, ItemStack> drop : backup.getDrops()) {
            int chance = drop.getKey();
            if(RandomUtils.nextInt(100) > chance) return;

            ItemStack item = drop.getValue();
            drops.add(item);
        }
    }

    @EventHandler
    public void checkDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(!entity.hasMetadata("id")) return;
        if(e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)
            e.setCancelled(true);
    }
}
