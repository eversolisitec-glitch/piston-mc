package com.castros.pistongravedad;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PistonGravedad extends JavaPlugin implements Listener {

    private static final int LIMITE_VANILLA = 12; // hasta aqui el piston normal ya funciona solo
    private static final int MAXIMO = 70;         // limite de este plugin

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new GolemCuracion(), this);
    }

    private boolean esGravedad(Material m) {
        return m == Material.SAND || m == Material.GRAVEL;
    }

    @EventHandler(ignoreCancelled = true)
    public void alActualizarBloque(BlockPhysicsEvent e) {
        Block piston = e.getBlock();
        Material tipo = piston.getType();
        if (tipo != Material.PISTON && tipo != Material.STICKY_PISTON) return;

        // se revisa en el siguiente tick para no modificar bloques dentro del evento
        Bukkit.getScheduler().runTask(this, () -> intentar(piston));
    }

    private void intentar(Block piston) {
        Material tipo = piston.getType();
        if (tipo != Material.PISTON && tipo != Material.STICKY_PISTON) return;

        BlockData datos = piston.getBlockData();
        if (!(datos instanceof Piston p)) return;
        if (p.isExtended() || !piston.isBlockIndirectlyPowered()) return;

        BlockFace cara = p.getFacing();
        World mundo = piston.getWorld();

        List<Block> fila = new ArrayList<>();
        Block actual = piston.getRelative(cara);

        while (esGravedad(actual.getType())) {
            fila.add(actual);
            if (fila.size() > MAXIMO) return; // demasiados bloques
            actual = actual.getRelative(cara);
            if (actual.getY() < mundo.getMinHeight() || actual.getY() >= mundo.getMaxHeight()) return;
            if (!mundo.isChunkLoaded(actual.getX() >> 4, actual.getZ() >> 4)) return;
        }

        // 12 o menos: lo hace el piston normal
        if (fila.size() <= LIMITE_VANILLA) return;

        // tiene que haber hueco libre al final de la fila
        if (!actual.getType().isAir()) return;

        // mover desde el ultimo hacia el primero para no pisar bloques
        for (int i = fila.size() - 1; i >= 0; i--) {
            Block origen = fila.get(i);
            origen.getRelative(cara).setType(origen.getType(), false);
        }

        // brazo del piston en el hueco que deja el primer bloque
        PistonHead cabeza = (PistonHead) Bukkit.createBlockData(Material.PISTON_HEAD);
        cabeza.setFacing(cara);
        cabeza.setType(tipo == Material.STICKY_PISTON
                ? TechnicalPiston.Type.STICKY
                : TechnicalPiston.Type.NORMAL);
        fila.get(0).setBlockData(cabeza, false);

        p.setExtended(true);
        piston.setBlockData(p, false);

        mundo.playSound(piston.getLocation(), Sound.BLOCK_PISTON_EXTEND, 0.5f, 0.75f);
    }
}
