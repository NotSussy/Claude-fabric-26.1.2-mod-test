package com.example.orbitalstrike.strike;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StrikeScheduler {

    private static final List<PendingStrike> PENDING = new ArrayList<>();

    private record PendingStrike(ServerLevel level, Vec3 position, StrikeType type, long fireTick) {}

    public static void scheduleNuke(ServerLevel level, Vec3 position, long fireTick) {
        PENDING.add(new PendingStrike(level, position, StrikeType.NUKE, fireTick));
    }

    public static void scheduleStab(ServerLevel level, Vec3 position, long fireTick) {
        PENDING.add(new PendingStrike(level, position, StrikeType.STAB, fireTick));
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
            case NUKE -> spawnNukeRing(strike.level(), strike.position());
            case STAB -> spawnStabColumn(strike.level(), strike.position());
        }
    }

    // Spawns a ring of 16 TNT entities around the target, falling from above
    private static void spawnNukeRing(ServerLevel level, Vec3 center) {
        int count = 16;
        double radius = 5.0;
        double dropHeight = 80.0;
        for (int i = 0; i < count; i++) {
            double angle = (2.0 * Math.PI * i) / count;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            spawnTnt(level, x, center.y + dropHeight, z);
        }
    }

    // Spawns 10 TNT entities in a vertical column above the target
    private static void spawnStabColumn(ServerLevel level, Vec3 center) {
        int count = 10;
        double startHeight = 20.0;
        double spacing = 4.0;
        for (int i = 0; i < count; i++) {
            spawnTnt(level, center.x, center.y + startHeight + (i * spacing), center.z);
        }
    }

    private static void spawnTnt(ServerLevel level, double x, double y, double z) {
        PrimedTnt tnt = new PrimedTnt(level, x, y, z, null);
        tnt.setFuse(40);
        level.addFreshEntity(tnt);
    }
}
