package ivl.milkorama;

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
	// RETYPEME: This is where the mod reads the milkable-mob config file; later
	// tasks will fill it with the actual config loading call.
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	}
}