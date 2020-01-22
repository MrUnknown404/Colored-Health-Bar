package mrunknown404.colorhealthbar.handlers;

import mrunknown404.colorhealthbar.Main;
import mrunknown404.colorhealthbar.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderEventHandler {
	@SubscribeEvent
	public void renderBars(RenderGameOverlayEvent.Pre e) {
		if (e.getType() == ElementType.HEALTH) {
			e.setCanceled(true);
		} else if (e.getType() == ElementType.ARMOR) {
			e.setCanceled(true);
			return;
		} else {
			return;
		}
		
		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		if (!(entity instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) entity;
		if (player.capabilities.isCreativeMode || player.isSpectator()) {
			return;
		}
		
		int initial_left_height = GuiIngameForge.left_height;
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(Utils.ICON_BAR);
		
		Main.main.healthBar.renderAll(player, e.getResolution().getScaledWidth(), e.getResolution().getScaledHeight());
		GuiIngameForge.left_height += 10;
		
		GuiIngameForge.left_height = initial_left_height;
		
		Main.main.healthBar.renderText(player, e.getResolution().getScaledWidth(), e.getResolution().getScaledHeight());
		GuiIngameForge.left_height += 10;
		
		GuiIngameForge.left_height = initial_left_height;
		
		Main.main.healthBar.renderIcon(player, e.getResolution().getScaledWidth(), e.getResolution().getScaledHeight());
		GuiIngameForge.left_height += 10;
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
	}
}
