package mrunknown404.colorhealthbar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import mrunknown404.colorhealthbar.client.gui.HealthOverlay;
import mrunknown404.colorhealthbar.handlers.RenderEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;

@Mod(modid = Main.MOD_ID, clientSideOnly = true, useMetadata = true, dependencies = "required-after:unknownlibs@[1.0.1,)")
public class Main {
	public static final String MOD_ID = "colorhealthbar";
	
	@Instance
	public static Main main;
	
	public HealthOverlay healthBar;
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
		healthBar = new HealthOverlay();
		
		if (Loader.isModLoaded("mantle")) {
			ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners;
			try {
				Field f = EventBus.class.getDeclaredField("listeners");
				f.setAccessible(true);
				listeners = (ConcurrentHashMap<Object, ArrayList<IEventListener>>) f.get(MinecraftForge.EVENT_BUS);
				listeners.keySet().forEach(key -> {
					String s = key.getClass().getCanonicalName();
					if ("slimeknights.mantle.client.ExtraHeartRenderHandler".equals(s)) {
						MinecraftForge.EVENT_BUS.unregister(key);
					}
				});
			} catch (IllegalAccessException | NoSuchFieldException e1) {
				e1.printStackTrace();
			}
		}
	}
}
