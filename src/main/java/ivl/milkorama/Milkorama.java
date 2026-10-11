package ivl.milkorama;

import ivl.milkorama.config.MilkConfig;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

// FUNC: Entry point for the Milkorama mod, tying together lifecycle and config.
@Mod(modid = Milkorama.MOD_ID, name = Milkorama.NAME, version = Milkorama.VERSION)
public class Milkorama {
	public static final String MOD_ID = "milkorama";
	public static final String NAME = "Milkorama";
	public static final String VERSION = "0.0.1";

	// FUNC: Called once at startup, before anything else is set up.
	// RETYPEME: Pre-initialization reads the milkable-mob config from the
	// Minecraft configuration directory so the list is ready before any entity
	// can be milked.
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MilkConfig.load(event.getModConfigurationDirectory());
	}
}