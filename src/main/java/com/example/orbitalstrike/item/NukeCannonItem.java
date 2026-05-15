package com.example.orbitalstrike.item;

import com.example.orbitalstrike.ModComponents;
import com.example.orbitalstrike.strike.StrikeScheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class NukeCannonItem extends Item {

    private static final int DEFAULT_DELAY_TICKS = 200;

    public NukeCannonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = player.getItemInHand(hand);
        int delayTicks = stack.getOrDefault(ModComponents.STRIKE_DELAY_TICKS, DEFAULT_DELAY_TICKS);

        HitResult hit = player.pick(200.0, 0.0f, false);
        Vec3 target = hit.getLocation();

        ServerLevel serverLevel = (ServerLevel) level;
        long fireTick = serverLevel.getGameTime() + Math.max(1, delayTicks);
        StrikeScheduler.scheduleNuke(serverLevel, target, fireTick);

        player.getCooldowns().addCooldown(stack.getItem(), 20);
        return InteractionResult.SUCCESS;
    }
}
