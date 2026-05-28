package com.example.orbitalstrike.strike;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.PrimedTnt;
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

    // 16 TNT drop from 80 blocks above in a ring; fuse 120t (6s) to reach ground
    private static void spawnNukeRing(ServerLevel level, Vec3 center, int ringSize) {
        int count = 16;
        double radius = ringSize;
        for (int i = 0; i < count; i++) {
            double angle = (2.0 * Math.PI * i) / count;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            PrimedTnt tnt = new PrimedTnt(level, x, center.y + 80.0, z, null);
            tnt.setFuse(120);
            level.addFreshEntity(tnt);
        }
    }

    // 15 TNT stacked in a vertical line at cursor; fuse 1t (instant)
    private static void spawnStabColumn(ServerLevel level, Vec3 center) {
        for (int i = 0; i < 15; i++) {
            PrimedTnt tnt = new PrimedTnt(level, center.x, center.y + i, center.z, null);
            tnt.setFuse(1);
            level.addFreshEntity(tnt);
        }
    }
}
