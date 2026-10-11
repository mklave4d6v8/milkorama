package ivl.milkorama;

import ivl.milkorama.config.MilkConfig;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

// Initialization
@Mod(modid = Milkorama.MOD_ID, name = Milkorama.NAME, version = Milkorama.VERSION)
public class Milkorama {
	public static final String MOD_ID = "milkorama";
	public static final String NAME = "Milkorama";
	public static final String VERSION = "0.0.1";

	// Pre-init, reads the milkable-mob config from the Minecraft configuration directory and hooks event handler onto event bus, ready before worldload.
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MilkConfig.load(event.getModConfigurationDirectory());
		MinecraftForge.EVENT_BUS.register(new MilkEventHandler());
	}
}