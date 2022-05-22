package com.github.idimabr.utils;

import com.github.idimabr.RaphaTimerMobs;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.Field;
import java.util.List;

public class ModifyUtil {

    public static Entity spawnEntityWithAttributes(int id, boolean forceHostile, EntityType type, Location location, List<Pair<String, Double>> attributes){
        Entity entity = location.getWorld().spawnEntity(location, type);
        entity.setMetadata("id", new FixedMetadataValue(RaphaTimerMobs.getInstance(), id));

        Creature creature = (Creature) entity;
        CraftCreature craftCreature = (CraftCreature) creature;
        EntityCreature nmsCreature = craftCreature.getHandle();
        EntityInsentient nms = nmsCreature;

        for (Pair<String, Double> key : attributes) {

            IAttribute attributeInstance = getAttribute(key.getKey());
            if(attributeInstance == null) continue;

            AttributeInstance attribute = nms.getAttributeInstance(attributeInstance);
            attribute.setValue(key.getValue());
        }

        if(forceHostile) {
            try {
                List goalB = (List) getPrivateField("b", PathfinderGoalSelector.class, nms.goalSelector);
                goalB.clear();
                List goalC = (List) getPrivateField("c", PathfinderGoalSelector.class, nms.goalSelector);
                goalC.clear();
                List targetB = (List) getPrivateField("b", PathfinderGoalSelector.class, nms.goalSelector);
                targetB.clear();
                List targetC = (List) getPrivateField("c", PathfinderGoalSelector.class, nms.goalSelector);
                targetC.clear();

                nms.goalSelector.a(1, new PathfinderGoalFloat(nms));
                nms.goalSelector.a(3, new PathfinderGoalLeapAtTarget(nms, 0.4F));
                nms.goalSelector.a(4, new PathfinderGoalMeleeAttack(nmsCreature, EntityHuman.class, 20.0D, true));
                nms.goalSelector.a(5, new PathfinderGoalRandomStroll(nmsCreature, 0.8));
                nms.goalSelector.a(6, new PathfinderGoalLookAtPlayer(nms, EntityHuman.class, 8.0F));
                nms.goalSelector.a(6, new PathfinderGoalRandomLookaround(nms));
                nms.targetSelector.a(1, new PathfinderGoalHurtByTarget(nmsCreature, false));
                nms.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(nmsCreature, EntityHuman.class, false));
            }catch (Exception ex){
                System.out.println("Erro ocorreu: " + ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }

        ((CraftCreature) entity).setHealth(((CraftCreature) entity).getMaxHealth());
        return entity;
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

    public static Object getPrivateField(String fieldName, Class clazz, Object object)
    {
        Field field;
        Object o = null;
        try
        {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        }
        catch(NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return o;
    }
}
