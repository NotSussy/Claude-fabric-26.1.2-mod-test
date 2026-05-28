package com.example.orbitalstrike.command;

import com.example.orbitalstrike.ModComponents;
import com.example.orbitalstrike.ModItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class OrbitalGiveCommand {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher,
        CommandBuildContext registryAccess,
        Commands.CommandSelection environment
    ) {
        dispatcher.register(
            Commands.literal("orbitalgive")
                // /orbitalgive nuke <delay> [ring_size]
                .then(Commands.literal("nuke")
                    .then(Commands.argument("delay_seconds", IntegerArgumentType.integer(0, 300))
                        .executes(ctx -> giveNuke(ctx, 5))
                        .then(Commands.argument("ring_size", IntegerArgumentType.integer(1, 30))
                            .executes(ctx -> giveNuke(ctx, IntegerArgumentType.getInteger(ctx, "ring_size"))))))
                // /orbitalgive stab <delay>
                .then(Commands.literal("stab")
                    .then(Commands.argument("delay_seconds", IntegerArgumentType.integer(0, 300))
                        .executes(ctx -> giveStab(ctx))))
        );
    }

    private static int giveNuke(CommandContext<CommandSourceStack> ctx, int ringSize)
        throws CommandSyntaxException
    {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int delaySecs = IntegerArgumentType.getInteger(ctx, "delay_seconds");

        ItemStack stack = new ItemStack(ModItems.NUKE_CANNON);
        stack.set(ModComponents.STRIKE_DELAY_TICKS, delaySecs * 20);
        stack.set(ModComponents.RING_SIZE, ringSize);

        if (!player.getInventory().add(stack)) player.drop(stack, false);

        ctx.getSource().sendSuccess(
            () -> Component.literal("Given Orbital Nuke Cannon — delay: " + delaySecs + "s, ring size: " + ringSize),
            false
        );
        return 1;
    }

    private static int giveStab(CommandContext<CommandSourceStack> ctx)
        throws CommandSyntaxException
    {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int delaySecs = IntegerArgumentType.getInteger(ctx, "delay_seconds");

        ItemStack stack = new ItemStack(ModItems.STAB_CANNON);
        stack.set(ModComponents.STRIKE_DELAY_TICKS, delaySecs * 20);

        if (!player.getInventory().add(stack)) player.drop(stack, false);

        ctx.getSource().sendSuccess(
            () -> Component.literal("Given Orbital Stab Cannon — delay: " + delaySecs + "s"),
            false
        );
        return 1;
    }
}
