package com.castros.pistongravedad;

import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Cuando los aldeanos generan un golem de hierro (defensa de aldea),
 * los aldeanos cercanos reciben Regeneracion 4.
 */
public class GolemCuracion implements Listener {

    private static final double RADIO = 24.0;          // bloques alrededor del golem
    private static final int DURACION_TICKS = 20 * 30; // 30 segundos
    private static final int NIVEL = 3;                // amplificador 3 = Regeneracion IV

    @EventHandler(ignoreCancelled = true)
    public void alGenerarse(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof IronGolem golem)) return;
        // solo el golem que generan los aldeanos, no el construido por un jugador
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE) return;

        for (Entity cerca : golem.getNearbyEntities(RADIO, RADIO, RADIO)) {
            if (cerca instanceof Villager aldeano) {
                aldeano.addPotionEffect(new PotionEffect(
                        PotionEffectType.REGENERATION, DURACION_TICKS, NIVEL));
            }
        }
    }
}
