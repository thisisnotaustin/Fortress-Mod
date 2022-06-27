package net.thisisnotaustin.fortressmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FortressMod implements ModInitializer {
	// Finalize the mod ID as a string to be used elsewhere.
	public static final String MOD_ID = "fortressmod";

	// Create a logger for this mod.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Mod initialization code.
	@Override public void onInitialize() {
		LOGGER.info("Initializing mod \"" + MOD_ID + "\"...");
	}
}