package com.example.orbitalstrike.strike;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StrikeScheduler {

    private static final List<PendingStrike> PENDING = new ArrayList<>();

    private record PendingStrike(ServerLevel level, Vec3 position, StrikeType type, long fireTick, int ringSize) {}

    public static void scheduleNuke(ServerLevel level, Vec3 position, long fireTick, int ringSize) {
        PENDING.add(new PendingStrike(level, position, StrikeType.NUKE, fireTick, ringSize));
    }

    public static void scheduleStab(ServerLevel level, Vec3 position, long fireTick) {
        PENDING.add(new PendingStrike(level, position, StrikeType.STAB, fireTick, 0));
    }

    public static void tick(MinecraftServer server) {
        if (PENDING.isEmpty()) return;
        Iterator<PendingStrike> iter = PENDING.iterator();
        while (iter.hasNext()) {
            PendingStrike strike = iter.next();
            if (strike.level().getGameTime() >= strike.fireTick()) {
                iter.remove();
                executeStrike(strike);
            }
        }
    }

    private static void executeStrike(PendingStrike strike) {
        switch (strike.type()) {
            case NUKE -> spawnNukeRing(strike.level(), strike.position(), strike.ringSize());
            case STAB -> spawnStabColumn(strike.level(), strike.position());
        }
    }

    // Spawn `ringCount` concentric rings of TNT falling from 80 blocks above; fuse 120t (6s) to reach ground
    private static void spawnNukeRing(ServerLevel level, Vec3 center, int ringCount) {
        for (int ring = 1; ring <= ringCount; ring++) {
            double radius = ring * 5.0;
            int count = (int) (2.0 * Math.PI * radius / 1.5);
            for (int i = 0; i < count; i++) {
                double angle = (2.0 * Math.PI * i) / count;
                double x = center.x + radius * Math.cos(angle);
                double z = center.z + radius * Math.sin(angle);
                PrimedTnt tnt = new PrimedTnt(level, x, center.y + 80.0, z, null);
                // PrimedTnt's constructor adds a random horizontal velocity kick that
                // scatters the ring; zero it so each TNT falls straight onto its exact spot.
                tnt.setDeltaMovement(0.0, 0.0, 0.0);
                tnt.setPos(x, center.y + 80.0, z);
                tnt.setFuse(120);
                level.addFreshEntity(tnt);
            }
        }
    }

    // 15 direct explosions stacked vertically; power 8 (2x vanilla TNT) for instant, high-damage strike
    private static void spawnStabColumn(ServerLevel level, Vec3 center) {
        for (int i = 0; i < 15; i++) {
            level.explode(null, center.x, center.y + i, center.z, 8.0f, Level.ExplosionInteraction.TNT);
        }
    }
}
