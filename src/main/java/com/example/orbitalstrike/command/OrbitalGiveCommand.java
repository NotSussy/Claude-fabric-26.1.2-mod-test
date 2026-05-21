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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class OrbitalGiveCommand {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher,
        CommandBuildContext registryAccess,
        Commands.CommandSelection environment
    ) {
        dispatcher.register(
            Commands.literal("orbitalgive")
                .then(Commands.literal("nuke")
                    .then(Commands.argument("delay_seconds", IntegerArgumentType.integer(0, 300))
                        .executes(ctx -> giveItem(ctx, ModItems.NUKE_CANNON, "Nuke Cannon"))))
                .then(Commands.literal("stab")
                    .then(Commands.argument("delay_seconds", IntegerArgumentType.integer(0, 300))
                        .executes(ctx -> giveItem(ctx, ModItems.STAB_CANNON, "Stab Cannon"))))
        );
    }

    private static int giveItem(CommandContext<CommandSourceStack> ctx, Item item, String name)
        throws CommandSyntaxException
    {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int delaySecs = IntegerArgumentType.getInteger(ctx, "delay_seconds");
        int delayTicks = delaySecs * 20;

        ItemStack stack = new ItemStack(item);
        stack.set(ModComponents.STRIKE_DELAY_TICKS, delayTicks);

        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }

        ctx.getSource().sendSuccess(
            () -> Component.literal("Given " + name + " with " + delaySecs + "s delay."),
            false
        );
        return 1;
    }
}
