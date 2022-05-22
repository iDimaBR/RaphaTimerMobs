package com.github.idimabr.utils;

import com.github.idimabr.RaphaTimerMobs;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.Field;
import java.util.List;

public class ModifyUtil {

    public static Entity spawnEntityWithAttributes(int id, boolean forceHostile, EntityType type, Location location, List<Pair<String, Double>> attributes){
        Entity entity = location.getWorld().spawnEntity(location, type);
        entity.setMetadata("id", new FixedMetadataValue(RaphaTimerMobs.getInstance(), id));

        if(!(entity instanceof Creature)){
            RaphaTimerMobs.getInstance().getLogger().warning("Entidade " + id + " ( " + type.name() + " ) nao foi permitido ser modificado.");
            return entity;
        }

        Creature creature = (Creature) entity;
        CraftCreature craftCreature = (CraftCreature) creature;
        EntityCreature nmsCreature = craftCreature.getHandle();
        EntityInsentient nms = nmsCreature;

        for (Pair<String, Double> key : attributes) {

            IAttribute attributeInstance = getAttribute(key.getKey());
            if(attributeInstance == null) continue;

            try {
                AttributeInstance attribute = nms.getAttributeInstance(attributeInstance);
                attribute.setValue(key.getValue());
            } catch (Exception ex){
                RaphaTimerMobs.getInstance().getLogger().warning("Entidade " + id + " ( " + type.name() + " )" + " nao possui attributo: " + key.getKey());
            }
        }

        if(forceHostile) {
            try {
                nms.goalSelector.a(4, new PathfinderGoalMeleeAttack(nmsCreature, EntityHuman.class, 8.0D, true));
                nms.targetSelector.a(1, new PathfinderGoalHurtByTarget(nmsCreature, false));
                nms.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(nmsCreature, EntityHuman.class, false));
            }catch (Exception ex){
                RaphaTimerMobs.getInstance().getLogger().warning("Não foi possível forçar hostilidade! ERRO: " + ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }

        ((CraftCreature) entity).setHealth(((CraftCreature) entity).getMaxHealth());
        return entity;
    }

    public static void removeCreaturesFromWorld(int id, World world){
        world.getLivingEntities().forEach($ -> {
            if($.hasMetadata("id") &&
                    $.getMetadata("id").size() > 0 &&
                    $.getMetadata("id").get(0).asInt() == id)
                $.remove();
        });
    }

    private static IAttribute getAttribute(String type){
        if(type == null) return null;

        switch(type.toLowerCase()){
            case "range":
                return GenericAttributes.FOLLOW_RANGE;
            case "damage":
                return GenericAttributes.ATTACK_DAMAGE;
            case "speed":
                return GenericAttributes.MOVEMENT_SPEED;
            case "health":
                return GenericAttributes.maxHealth;
            case "knock":
                return GenericAttributes.c;
        }
        return null;
    }
}
