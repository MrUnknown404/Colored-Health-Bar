package mrunknown404.colorhealthbar.client.gui;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

import mrunknown404.colorhealthbar.util.ModConfig;
import mrunknown404.colorhealthbar.util.Utils;
import mrunknown404.unknownlibs.utils.ColorUtils;
import mrunknown404.unknownlibs.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.GuiIngameForge;

public class HealthOverlay {
	private final Minecraft mc = Minecraft.getMinecraft();
	
	private double playerHealth = 0;
	private long healthUpdateCounter = 0;
	private double lastPlayerHealth = 0;
	
	public void renderAll(EntityPlayer player, int width, int height) {
		int xStart = width / 2 - 91;
		int yStart = height - GuiIngameForge.left_height;
		
		mc.mcProfiler.startSection("health");
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		
		renderBar(player, xStart, yStart);
		
		mc.mcProfiler.endStartSection("armor");
		redrawArmor(width, height);
		
		mc.mcProfiler.endStartSection("health");
		if (player.getAbsorptionAmount() > 0) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(Utils.ICON_BAR);
			Utils.drawTexturedModalRect(xStart, yStart - 10, 0, 0, 81, 9);
			
			absorptionToColorGl(player, true);
			
			if (player.getAbsorptionAmount() <= player.getMaxHealth()) {
				Utils.drawTexturedModalRect(xStart + 1, yStart + 1 - 10, 1, 10, Utils.getWidth(player.getAbsorptionAmount(), player.getMaxHealth()), 7);
			} else {
				Utils.drawTexturedModalRect(xStart + 1, yStart + 1 - 10, 1, 10, 79, 7);
			}
		}
		
		GlStateManager.color(1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		mc.mcProfiler.endSection();
	}
	
	private void renderBar(EntityPlayer player, int xStart, int yStart) {
		int updateCounter = mc.ingameGUI.getUpdateCounter();
		
		double health = player.getHealth(), displayHealth = health;
		boolean highlight = healthUpdateCounter > updateCounter && (healthUpdateCounter - updateCounter) / 3 % 2 == 1;
		
		if (health < playerHealth && player.hurtResistantTime > 0) {
			healthUpdateCounter = updateCounter + 20;
			lastPlayerHealth = playerHealth;
		} else if (health > playerHealth && player.hurtResistantTime > 0) {
			healthUpdateCounter = updateCounter + 20;
			lastPlayerHealth = playerHealth;
		}
		
		playerHealth = health;
		
		boolean isRegen = false;
		if (lastPlayerHealth > health) {
			displayHealth = health + (lastPlayerHealth - health) * ((double) player.hurtResistantTime / player.maxHurtResistantTime);
		} else if (lastPlayerHealth != 0 && lastPlayerHealth < health) {
			isRegen = true;
			displayHealth = health - lastPlayerHealth;
		}
		
		Utils.drawTexturedModalRect(xStart, yStart, 0, (highlight) ? 18 : 0, 81, 9);
		int alpha = health <= 0 ? 1 : health / player.getMaxHealth() <= 0.2 && true ? (int) (Minecraft.getSystemTime() / 250) % 2 : 1;
		
		if (displayHealth != health) {
			GlStateManager.color(1, 1, 1, alpha);
			if (displayHealth > health) {
				Utils.drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, Utils.getWidth(displayHealth, player.getMaxHealth()), 7);
			} else {
				if (highlight) {
					Utils.drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, Utils.getWidth(health, player.getMaxHealth()), 7);
					healthToColorGl(player, true, true);
					Utils.drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, Utils.getWidth(health - displayHealth, player.getMaxHealth()), 7);
				} else {
					healthToColorGl(player, true, true);
					Utils.drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, Utils.getWidth(health, player.getMaxHealth()), 7);
				}
			}
		}
		
		if (!isRegen) {
			healthToColorGl(player, true, true);
			Utils.drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, Utils.getWidth(health, player.getMaxHealth()), 7);
		}
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
		int xStart = width / 2 - 91;
		int yStart = height - GuiIngameForge.left_height;
		
		String h;
		if (ModConfig.roundTo != 0) {
			String oo = StringUtils.repeat("0", ModConfig.roundTo);
			
			DecimalFormat df = new DecimalFormat("#0." + oo);
			h = df.format(player.getHealth()) + "/" + df.format(player.getMaxHealth());
		} else {
			h = (int) Math.floor(player.getHealth()) + "/" + (int) Math.floor(player.getMaxHealth());
		}
		
		int color = healthToColorGl(player, true, true);
		Utils.drawStringOnHUD(h, xStart - 9 - Utils.getStringLength(h) - 5, yStart - 1, color);
		
		float absorb = player.getAbsorptionAmount();
		if (absorb > 0) {
			color = absorptionToColorGl(player, true);
			
			Utils.drawStringOnHUD("" + (int) absorb, xStart - Utils.getStringLength((int) absorb + "") - 9 - 5, yStart - 11, color);
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
		
		int xStart = width / 2 - 91, yStart = height - GuiIngameForge.left_height;
		int hMod = Utils.getHeartMulti(player.getMaxHealth());
		
		healthToColorGl(player, false, false);
		
		if (ModConfig.useCustomColor) {
			hMod = Utils.HEARTS.length - 1;
		}
		
		mc.getTextureManager().bindTexture(Gui.ICONS);
		Utils.drawTexturedModalRect(xStart - 10, yStart, 16, 0, 9, 9);
		mc.getTextureManager().bindTexture(Utils.ICON_HEARTS);
		Utils.drawTexturedModalRect(xStart - 10, yStart, 9 * hMod, p1 + (player.world.getWorldInfo().isHardcoreModeEnabled() ? 27 : 0), 9, 9);
		
		if (player.getAbsorptionAmount() > 0) {
			absorptionToColorGl(player, ModConfig.useCustomColor);
			if (ModConfig.useCustomColor) {
				hMod = Utils.HEARTS.length - 1;
			}
			
			mc.getTextureManager().bindTexture(Gui.ICONS);
			Utils.drawTexturedModalRect(xStart - 10, yStart - 10, 16, 0, 9, 9);
			if (ModConfig.useCustomColor) {
				mc.getTextureManager().bindTexture(Utils.ICON_HEARTS);
				Utils.drawTexturedModalRect(xStart - 10, yStart - 10, 9 * hMod, p1 + (player.world.getWorldInfo().isHardcoreModeEnabled() ? 27 : 0), 9, 9);
			} else {
				mc.getTextureManager().bindTexture(Gui.ICONS);
				Utils.drawTexturedModalRect(xStart - 10, yStart - 10, 160 - p2, player.world.getWorldInfo().isHardcoreModeEnabled() ? 45 : 0, 9, 9);
			}
		}
		
		GlStateManager.color(1, 1, 1);
	}
	
	private int healthToColorGl(EntityPlayer pl, boolean isWhiteHeart, boolean isBlinkable) {
		if (ModConfig.useCustomColor) {
			if (pl.isPotionActive(MobEffects.POISON) && ColorUtils.isValidHexColor(ModConfig.hexCustomHealthPoisonColor)) {
				Utils.color2Gl(ColorUtils.hex2Color(ModConfig.hexCustomHealthPoisonColor), 1);
				return Utils.colorToText(ColorUtils.hex2Color(ModConfig.hexCustomHealthPoisonColor));
			} else if (pl.isPotionActive(MobEffects.WITHER) && ColorUtils.isValidHexColor(ModConfig.hexCustomHealthWitherColor)) {
				Utils.color2Gl(ColorUtils.hex2Color(ModConfig.hexCustomHealthWitherColor), 1);
				return Utils.colorToText(ColorUtils.hex2Color(ModConfig.hexCustomHealthWitherColor));
			} else if (ColorUtils.isValidHexColor(ModConfig.hexCustomHealthColor)) {
				Utils.color2Gl(ColorUtils.hex2Color(ModConfig.hexCustomHealthColor), 1);
				return Utils.colorToText(ColorUtils.hex2Color(ModConfig.hexCustomHealthColor));
			} else {
				Utils.color2Gl(ColorUtils.hex2Color("#000000"), 1);
				return Utils.colorToText(ColorUtils.hex2Color("#000000"));
			}
		}
		
		int p1 = pl.isPotionActive(MobEffects.POISON) ? 52 : pl.isPotionActive(MobEffects.WITHER) ? 88 : 16;
		float alpha = pl.getHealth() <= 0 ? 1 : pl.getHealth() / pl.getMaxHealth() <= 0.2 && true ? (int) (Minecraft.getSystemTime() / 250) % 2 : 1;
		if (isBlinkable) {
			Utils.color2Gl(Utils.getColor(pl.getMaxHealth(), p1), alpha);
		}
		if (!isWhiteHeart) {
			GlStateManager.color(1, 1, 1);
		}
		
		return Utils.colorToText(Utils.getColor(MathUtils.roundTo(pl.getMaxHealth(), ModConfig.roundTo), p1));
	}
	
	private int absorptionToColorGl(EntityPlayer pl, boolean isWhiteHeart) {
		if (ModConfig.useCustomColor) {
			if (pl.isPotionActive(MobEffects.POISON) && ColorUtils.isValidHexColor(ModConfig.hexCustomAbsorptionPoisonColor)) {
				Utils.color2Gl(ColorUtils.hex2Color(ModConfig.hexCustomAbsorptionPoisonColor), 1);
				return Utils.colorToText(ColorUtils.hex2Color(ModConfig.hexCustomAbsorptionPoisonColor));
			} else if (pl.isPotionActive(MobEffects.WITHER) && ColorUtils.isValidHexColor(ModConfig.hexCustomAbsorptionWitherColor)) {
				Utils.color2Gl(ColorUtils.hex2Color(ModConfig.hexCustomAbsorptionWitherColor), 1);
				return Utils.colorToText(ColorUtils.hex2Color(ModConfig.hexCustomAbsorptionWitherColor));
			} else if (ColorUtils.isValidHexColor(ModConfig.hexCustomAbsorptionColor)) {
				Utils.color2Gl(ColorUtils.hex2Color(ModConfig.hexCustomAbsorptionColor), 1);
				return Utils.colorToText(ColorUtils.hex2Color(ModConfig.hexCustomAbsorptionColor));
			} else {
				GlStateManager.color(0, 0, 0);
				return Utils.colorToText(ColorUtils.hex2Color("#000000"));
			}
		}
		
		if (pl.isPotionActive(MobEffects.POISON)) {
			Utils.color2Gl(ColorUtils.hex2Color(Utils.ABSORPTION_POISON_COLOR));
			return Utils.colorToText(ColorUtils.hex2Color(Utils.ABSORPTION_POISON_COLOR));
		} else if (pl.isPotionActive(MobEffects.WITHER)) {
			Utils.color2Gl(ColorUtils.hex2Color(Utils.ABSORPTION_WITHER_COLOR));
			return Utils.colorToText(ColorUtils.hex2Color(Utils.ABSORPTION_WITHER_COLOR));
		} else {
			if (isWhiteHeart) {
				Utils.color2Gl(ColorUtils.hex2Color(Utils.ABSORPTION_COLOR));
				return Utils.colorToText(ColorUtils.hex2Color(Utils.ABSORPTION_COLOR));
			}
			GlStateManager.color(1, 1, 1);
			return Utils.colorToText(ColorUtils.hex2Color("#ffffff"));
		}
	}
}
