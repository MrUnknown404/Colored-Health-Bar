package mrunknown404.colorhealthbar.util;

import mrunknown404.colorhealthbar.Main;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Main.MOD_ID)
@Config.LangKey(Main.MOD_ID + ".config.title")
public class ModConfig {
	
	@Config.Comment("Health decimal to round to")
	@Config.RangeInt(min = 1, max = 10)
	public static int roundTo = 1;
	@Config.Comment("Should the default health color system be replaced with the custom color")
	public static boolean useCustomColor = false;
	
	@Config.Comment("Custom health color in hex (example '#1ebbc6')")
	public static String hexCustomHealthColor = "#ff0000";
	@Config.Comment("Custom poison color in hex (example '#1ebbc6')")
	public static String hexCustomHealthPoisonColor = "#00ff00";
	@Config.Comment("Custom wither color in hex (example '#1ebbc6')")
	public static String hexCustomHealthWitherColor = "#555555";
	
	@Config.Comment("Custom health color in hex (example '#1ebbc6')")
	public static String hexCustomAbsorptionColor = "#d4af37";
	@Config.Comment("Custom poison color in hex (example '#1ebbc6')")
	public static String hexCustomAbsorptionPoisonColor = "#c5d117";
	@Config.Comment("Custom wither color in hex (example '#1ebbc6')")
	public static String hexCustomAbsorptionWitherColor = "#70590d";
	
	@EventBusSubscriber(modid = Main.MOD_ID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
			if (e.getModID().equals(Main.MOD_ID)) {
				ConfigManager.sync(Main.MOD_ID, Config.Type.INSTANCE);
			}
		}
	}
}
