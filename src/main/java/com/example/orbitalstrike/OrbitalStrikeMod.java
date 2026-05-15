package com.example.orbitalstrike;

import com.example.orbitalstrike.command.OrbitalGiveCommand;
import com.example.orbitalstrike.strike.StrikeScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrbitalStrikeMod implements ModInitializer {

    public static final String MOD_ID = "orbitalstrike";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModComponents.register();
        ModItems.register();
        ServerTickEvents.END_SERVER_TICK.register(StrikeScheduler::tick);
        CommandRegistrationCallback.EVENT.register(OrbitalGiveCommand::register);
        LOGGER.info("Orbital Strike mod loaded.");
    }
}
