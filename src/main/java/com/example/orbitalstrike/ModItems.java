package com.example.orbitalstrike;

import com.example.orbitalstrike.item.NukeCannonItem;
import com.example.orbitalstrike.item.StabShotCannonItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final NukeCannonItem NUKE_CANNON;
    public static final StabShotCannonItem STAB_CANNON;

    static {
        ResourceKey<Item> nukeKey = ResourceKey.create(
            Registries.ITEM,
            Identifier.of(OrbitalStrikeMod.MOD_ID, "nuke_cannon")
        );
        NUKE_CANNON = Registry.register(
            BuiltInRegistries.ITEM,
            nukeKey,
            new NukeCannonItem(new Item.Properties().setId(nukeKey).stacksTo(1))
        );

        ResourceKey<Item> stabKey = ResourceKey.create(
            Registries.ITEM,
            Identifier.of(OrbitalStrikeMod.MOD_ID, "stab_cannon")
        );
        STAB_CANNON = Registry.register(
            BuiltInRegistries.ITEM,
            stabKey,
            new StabShotCannonItem(new Item.Properties().setId(stabKey).stacksTo(1))
        );
    }

    public static void register() {
        // Triggers static initialization
    }
}
