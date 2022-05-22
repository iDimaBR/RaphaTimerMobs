package com.github.idimabr.tasks;

import com.github.idimabr.RaphaTimerMobs;
import com.github.idimabr.manager.TimerManager;
import com.github.idimabr.objects.BackupEntity;
import com.github.idimabr.utils.ModifyUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;

public class TimerRunnable extends BukkitRunnable {

    @Override
    public void run() {

        HashMap<Integer, Pair<Entity, Integer>> timerList = TimerManager.getTimerList();

        for (Map.Entry<Integer, Pair<Entity, Integer>> entry : timerList.entrySet()) {
            int id = entry.getKey();
            Pair<Entity, Integer> values = entry.getValue();

            Entity entity = values.getLeft();
            if(!entity.isDead()) continue;

            Integer time = values.getValue();
            if(time == 0){
                BackupEntity backup = TimerManager.getBackupList().get(id);
                Entity newEntity = ModifyUtil.spawnEntityWithAttributes(id, backup.isHostile(), backup.getType(), backup.getLocation(), backup.getAttributes());
                if(backup.getName() != null){
                    newEntity.setCustomNameVisible(true);
                    newEntity.setCustomName(backup.getName().replace("&","§"));
                }
                timerList.put(id, Pair.of(newEntity, backup.getTimer()));
                return;
            }

            timerList.put(id, Pair.of(entity, (time - 1)));
        }
    }
}
