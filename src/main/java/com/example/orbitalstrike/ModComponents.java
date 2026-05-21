package com.example.orbitalstrike;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class ModComponents {

    // Delay in ticks before TNT spawns (20 ticks = 1 second)
    public static final DataComponentType<Integer> STRIKE_DELAY_TICKS =
        Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(OrbitalStrikeMod.MOD_ID, "strike_delay_ticks"),
            DataComponentType.<Integer>builder()
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.INT)
                .build()
        );

    public static void register() {
        // Triggers static initialization
    }
}
