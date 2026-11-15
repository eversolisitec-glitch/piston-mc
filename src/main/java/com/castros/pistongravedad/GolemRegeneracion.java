package com.castros.pistongravedad;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GolemRegeneracion implements Listener {

    private static final int NIVEL = 4; // Regeneracion IV

    private final JavaPlugin plugin;

    public GolemRegeneracion(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void alGenerarseGolem(CreatureSpawnEvent e) {
        if (e.getEntityType() != EntityType.IRON_GOLEM) return;
        // VILLAGE_DEFENSE = golem generado por los aldeanos (no el construido por un jugador)
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE) return;

        LivingEntity golem = e.getEntity();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!golem.isValid()) return;
            golem.addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    PotionEffect.INFINITE_DURATION,
                    NIVEL - 1,   // el nivel IV se indica como 3
                    false,       // ambient
                    true,        // particulas
                    true));      // icono
        });
    }
}
