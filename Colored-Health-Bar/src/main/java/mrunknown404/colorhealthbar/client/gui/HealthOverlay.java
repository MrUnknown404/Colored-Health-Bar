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
	
	private double playerHealth, playerAbsorption, lastPlayerHealth, lastPlayerAbsorption;
	private long healthUpdateCounter, absorptionUpdateCounter;
	private boolean isRegen = false;
	
	public void renderAll(EntityPlayer player, int width, int height) {
		int xStart = width / 2 - 91;
		int yStart = height - GuiIngameForge.left_height;
		
		mc.mcProfiler.startSection("health");
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		
		renderBar(true, player, xStart, yStart);
		
		mc.mcProfiler.endStartSection("armor");
		redrawArmor(player, width, height);
		
		mc.mcProfiler.endStartSection("health");
		if (player.getAbsorptionAmount() > 0) {
			renderBar(false, player, xStart, yStart);
		}
		
		GlStateManager.color(1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		mc.mcProfiler.endSection();
	}
	
	private void renderBar(boolean isHealth, EntityPlayer player, int xStart, int yStart) {
		long updateCounter = mc.ingameGUI.getUpdateCounter();
		
		if (player.getHealth() < playerHealth && player.hurtResistantTime > 0) {
			healthUpdateCounter = updateCounter + 20;
			lastPlayerHealth = playerHealth;
		} else if (player.getHealth() > playerHealth && player.hurtResistantTime > 0) {
			healthUpdateCounter = updateCounter + 20;
			lastPlayerHealth = playerHealth;
		}
		
		playerHealth = player.getHealth();
		
		if (lastPlayerHealth > player.getHealth()) {
			isRegen = false;
		} else if (lastPlayerHealth != 0 && lastPlayerHealth < player.getHealth()) {
			isRegen = true;
		}
		
		double value, displayValue;
		boolean highlight;
		if (isHealth) {
			value = player.getHealth();
			displayValue = value;
			
			highlight = healthUpdateCounter > updateCounter && (healthUpdateCounter - updateCounter) / 3 % 2 == 1;
			
			if (lastPlayerHealth > value) {
				displayValue = value + (lastPlayerHealth - value) * ((double) player.hurtResistantTime / player.maxHurtResistantTime);
			} else if (lastPlayerHealth != 0 && lastPlayerHealth < value) {
				displayValue = value - lastPlayerHealth;
			}
		} else {
			value = player.getAbsorptionAmount();
			displayValue = value;
			
			highlight = absorptionUpdateCounter > updateCounter && (absorptionUpdateCounter - updateCounter) / 3 % 2 == 1;
			if (!isRegen) {
				lastPlayerHealth = playerHealth;
			}
			
			if (value < playerAbsorption && player.hurtResistantTime > 0) {
				absorptionUpdateCounter = updateCounter + 20;
				lastPlayerAbsorption = playerAbsorption;
			} else if (value > playerAbsorption && player.hurtResistantTime > 0) {
				absorptionUpdateCounter = updateCounter + 20;
				lastPlayerAbsorption = playerAbsorption;
			}
			
			playerAbsorption = value;
			
			if (lastPlayerAbsorption > value) {
				displayValue = value + (lastPlayerAbsorption - value) * ((double) player.hurtResistantTime / player.maxHurtResistantTime);
			} else if (lastPlayerAbsorption != 0 && lastPlayerAbsorption < value) {
				displayValue = value - lastPlayerAbsorption;
			}
		}
		
		int ymod = !isHealth ? 10 : 0;
		
		GlStateManager.color(1, 1, 1);
		Utils.drawTexturedModalRect(xStart, yStart - ymod, 0, (highlight) ? 9 : 0, 81, 9);
		
		if (displayValue != value) {
			if (displayValue > value) {
				GlStateManager.color(1, 1, 1);
				Utils.drawTexturedModalRect(xStart + 2, yStart + 2 - ymod, 0, 18, Utils.getWidth(displayValue, player.getMaxHealth()), 5);
			} else {
				if (highlight) {
					Utils.drawTexturedModalRect(xStart + 2, yStart + 2 - ymod, 0, 18, Utils.getWidth(value, player.getMaxHealth()), 5);
					colorBarGl(player, true, true, isHealth);
					Utils.drawTexturedModalRect(xStart + 2, yStart + 2 - ymod, 0, 18, Utils.getWidth(value - displayValue, player.getMaxHealth()), 5);
				} else {
					colorBarGl(player, true, true, isHealth);
					Utils.drawTexturedModalRect(xStart + 2, yStart + 2 - ymod, 0, 18, Utils.getWidth(value, player.getMaxHealth()), 5);
				}
			}
		}
		
		if (!isRegen || !isHealth) {
			colorBarGl(player, true, true, isHealth);
			Utils.drawTexturedModalRect(xStart + 2, yStart + 2 - ymod, 0, 18, Utils.getWidth(value, player.getMaxHealth()), 5);
			GlStateManager.color(1, 1, 1);
		}
	}
	
	private void redrawArmor(EntityPlayer player, int width, int height) {
		if (player.getTotalArmorValue() <= 0) {
			return;
		}
		
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(Gui.ICONS);
		
		int left = (width / 2) - 91, top = height - 49 - (player.getAbsorptionAmount() > 0 ? 10 : 0);
		
		if (player.getTotalArmorValue() < 20) {
			for (int i = player.getTotalArmorValue() + player.getTotalArmorValue() / 20; i < 20 + player.getTotalArmorValue() / 20; i++) {
				if (i % 2 == 0) {
					Utils.drawTexturedModalRect(left + (i / 2) * 8, top, 16, 9, 9, 9);
				}
			}
		}
		
		for (int i = 1; i < Math.min(player.getTotalArmorValue(), (ModConfig.maxArmorRows * 20)) + 1; i++) {
			int ymod = ((i - 1) / 2 / 10) * 10;
			if (i % 2 == 0) {
				Utils.drawTexturedModalRect(left + ((i - 1) / 2) * 8 - (ymod * 8), top - ymod, 34, 9, 9, 9);
			} else if (i == player.getTotalArmorValue()) {
				Utils.drawTexturedModalRect(left + ((i - 1) / 2) * 8 - (ymod * 8), top - ymod, 25, 9, 9, 9);
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
	
	private int colorBarGl(EntityPlayer pl, boolean isWhiteHeart, boolean isBlinkable, boolean isHeart) {
		return isHeart ? healthToColorGl(pl, isWhiteHeart, isBlinkable) : absorptionToColorGl(pl, isWhiteHeart);
	}
}
