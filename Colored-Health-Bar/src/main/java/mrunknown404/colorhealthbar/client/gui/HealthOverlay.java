package mrunknown404.colorhealthbar.client.gui;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

import mrunknown404.colorhealthbar.util.ModConfig;
import mrunknown404.colorhealthbar.util.Utils;
import mrunknown404.colorhealthbar.util.Utils.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.GuiIngameForge;

public class HealthOverlay {
	private final Minecraft mc = Minecraft.getMinecraft();
	
	private double playerHealth = 0;
	private long healthUpdateCounter = 0;
	private double lastPlayerHealth = 0;
	
	public void renderBar(EntityPlayer player, int width, int height) {
		int updateCounter = mc.ingameGUI.getUpdateCounter();
		
		double health = player.getHealth();
		boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3 % 2 == 1;
		
		if (health < playerHealth && player.hurtResistantTime > 0) {
			healthUpdateCounter = (long) (updateCounter + 20);
			lastPlayerHealth = playerHealth;
		} else if (health > playerHealth && player.hurtResistantTime > 0) {
			healthUpdateCounter = (long) (updateCounter + 20);
			lastPlayerHealth = playerHealth;
		}
		playerHealth = health;
		double displayHealth = health + (lastPlayerHealth - health) * ((double) player.hurtResistantTime / player.maxHurtResistantTime);
		
		int xStart = width / 2 - 91;
		int yStart = height - GuiIngameForge.left_height;
		double maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
		
		mc.mcProfiler.startSection("health");
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		int p1 = 16;
		
		if (player.isPotionActive(MobEffects.POISON)) {
			p1 += 36;
		} else if (player.isPotionActive(MobEffects.WITHER)) {
			p1 += 72;
		}
		
		int i4 = (highlight) ? 18 : 0;
		
		Utils.drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);
		int alpha = health <= 0 ? 1 : health / maxHealth <= 0.2 && true ? (int) (Minecraft.getSystemTime() / 250) % 2 : 1;
		
		if (displayHealth != health) {
			GlStateManager.color(1, 1, 1, alpha);
			if (displayHealth > health) {
				Utils.drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, Utils.getWidth(displayHealth, maxHealth), 7);
			}
		}
		
		Utils.getColor(health, maxHealth, p1).color2Gla(alpha);
		Utils.drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, Utils.getWidth(health, maxHealth), 7);
		
		if (p1== 52) {
			GlStateManager.color(0, .5f, 0, .5f);
			Utils.drawTexturedModalRect(xStart + 1, yStart + 1, 1, 36, Utils.getWidth(health, maxHealth), 7);
		}
		
		mc.mcProfiler.endStartSection("armor");
		redrawArmor(width, height);
		
		mc.mcProfiler.endStartSection("health");
		if (player.getAbsorptionAmount() > 0) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(Utils.ICON_BAR);
			
			GlStateManager.color(1, 1, 1);
			Utils.drawTexturedModalRect(xStart, yStart - 10, 0, 0, 81, 9);
			
			if (p1 == 16) {
				Utils.hex2Color(Colors.ABSORPTION_COLOR).color2Gl();
			} else if (p1 == 52) {
				Utils.hex2Color(Colors.ABSORPTION_POISON_COLOR).color2Gl();
			} else if (p1 == 88) {
				Utils.hex2Color(Colors.ABSORPTION_WITHER_COLOR).color2Gl();
			}
			
			if (player.getAbsorptionAmount() <= maxHealth) {
				Utils.drawTexturedModalRect(xStart + 1, yStart + 1 - 10, 1, 10, Utils.getWidth(player.getAbsorptionAmount(), maxHealth), 7);
			} else {
				Utils.drawTexturedModalRect(xStart + 1, yStart + 1 - 10, 1, 10, 79, 7);
			}
		}
		
		GlStateManager.color(1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		mc.mcProfiler.endSection();
	}
	
	private void redrawArmor(int width, int height) {
		if (!(mc.getRenderViewEntity() instanceof EntityPlayer)) {
			return;
		}
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(Gui.ICONS);
		
		EntityPlayer player = (EntityPlayer) this.mc.getRenderViewEntity();
		int left = width / 2;
		left -= 91;
		int top = height - 49 - (player.getAbsorptionAmount() > 0 ? 10 : 0);
		
		if (player.getTotalArmorValue() <= 0) {
			return;
		}
		
		for (int i = 0; i < 10; i++) {
			int threshold = i * 2 + 1;
			if (threshold < player.getTotalArmorValue()) {
				Utils.drawTexturedModalRect(left + i * 8, top, 34, 9, 9, 9);
			} else if (threshold == player.getTotalArmorValue()) {
				Utils.drawTexturedModalRect(left + i * 8, top, 25, 9, 9, 9);
			} else {
				Utils.drawTexturedModalRect(left + i * 8, top, 16, 9, 9, 9);
			}
		}
	}
	
	public void renderText(EntityPlayer player, int width, int height) {
		double health = player.getHealth();
		
		int xStart = width / 2 - 91;
		int yStart = height - GuiIngameForge.left_height;
		double maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
		
		int k5 = 16;
		
		if (player.isPotionActive(MobEffects.POISON)) {
			k5 += 36;
		} else if (player.isPotionActive(MobEffects.WITHER)) {
			k5 += 72;
		}
		
		String h;
		if (ModConfig.roundTo != 0) {
			String oo = StringUtils.repeat("0", ModConfig.roundTo);
			
			DecimalFormat df = new DecimalFormat("#0." + oo);
			h = df.format(health) + "/" + df.format(maxHealth);
		} else {
			h = (int) Math.floor(health) + "/" + (int) Math.floor(maxHealth);
		}
		
		Utils.drawStringOnHUD(h, xStart - 9 - Utils.getStringLength(h) - 5, yStart - 1,
				Utils.getColor(Utils.roundTo(health, ModConfig.roundTo), Utils.roundTo(maxHealth, ModConfig.roundTo), k5).colorToText());
		
		float absorb = player.getAbsorptionAmount();
		if (absorb > 0) {
			int a1 = Utils.getStringLength((int) absorb + "");
			int c = 0;
			
			if (k5 == 16) {
				c = Utils.hex2Color(Colors.ABSORPTION_COLOR).colorToText();
			} else if (k5 == 52) {
				c = Utils.hex2Color(Colors.ABSORPTION_POISON_COLOR).colorToText();
			} else if (k5 == 88) {
				c = Utils.hex2Color(Colors.ABSORPTION_WITHER_COLOR).colorToText();
			}
			
			Utils.drawStringOnHUD("" + (int) absorb, xStart - a1 - 9 - 5, yStart - 11, c);
		}
	}
	
	public void renderIcon(EntityPlayer player, int width, int height) {
		int p1 = 0, p2 = 0;
		if (player.isPotionActive(MobEffects.POISON)) {
			p1 += 9;
			p2 += 72;
		} else if (player.isPotionActive(MobEffects.WITHER)) {
			p1 += 18;
			p2 += 36;
		}
		
		int xStart = width / 2 - 91;
		int yStart = height - GuiIngameForge.left_height;
		GlStateManager.color(1, 1, 1);
		
		int hMod = Utils.getHeartMulti(player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
		
		mc.getTextureManager().bindTexture(Gui.ICONS);
		Utils.drawTexturedModalRect(xStart - 10, yStart, 16, 0, 9, 9);
		mc.getTextureManager().bindTexture(Utils.HEARTS);
		Utils.drawTexturedModalRect(xStart - 10, yStart, 9 * hMod + p1, p1 + (player.world.getWorldInfo().isHardcoreModeEnabled() ? 27 : 0), 9, 9);
		
		if (player.getAbsorptionAmount() > 0) {
			mc.getTextureManager().bindTexture(Gui.ICONS);
			Utils.drawTexturedModalRect(xStart - 10, yStart - 10, 16, 0, 9, 9);
			Utils.drawTexturedModalRect(xStart - 10, yStart - 10, 160 - p2, player.world.getWorldInfo().isHardcoreModeEnabled() ? 45 : 0, 9, 9);
		}
	}
}
