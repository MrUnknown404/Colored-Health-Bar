package mrunknown404.colorhealthbar.util;

import org.lwjgl.util.Color;

import mrunknown404.colorhealthbar.Main;
import mrunknown404.unknownlibs.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Utils {
	public static final ResourceLocation ICON_BAR = new ResourceLocation(Main.MOD_ID, "textures/gui/health.png");
	public static final ResourceLocation ICON_HEARTS = new ResourceLocation(Main.MOD_ID, "textures/gui/hearts.png");
	private static final FontRenderer FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;
	
	public static void drawTexturedModalRect(float x, float y, int textureX, int textureY, int width, int height) {
		Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
	}
	
	public static int getWidth(double d1, double d2) {
		if (d1 >= d2) {
			return 78;
		}
		
		int w = 78;
		double d3 = Math.max(w * d1 / d2, 0);
		return (int) Math.ceil(d3);
	}
	
	public static int getStringLength(String s) {
		return FONT_RENDERER.getStringWidth(s);
	}
	
	public static void drawStringOnHUD(String string, int xOffset, int yOffset, int color) {
		xOffset += 2;
		yOffset += 2;
		
		FONT_RENDERER.drawString(string, xOffset, yOffset, color, true);
	}
	
	public static Color getColor(double d2, int effect) {
		String[] colorCodes = null;
		
		if (effect == 16) {
			colorCodes = NORMAL_COLORS;
		} else if (effect == 52) {
			colorCodes = POISONED_COLORS;
		} else if (effect == 88) {
			colorCodes = WITHERED_COLORS;
		}
		
		return ColorUtils.hex2Color(colorCodes[getHeartMulti(d2)]);
	}
	
	public static int colorToText(Color c) {
		return (c.getRed() << 16) + (c.getGreen() << 8) + c.getBlue();
	}
	
	public static void color2Gl(Color c) {
		color2Gl(c, 1);
	}
	
	public static void color2Gl(Color c, float a) {
		GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a);
	}
	
	public static int getHeartMulti(double health) {
		if (health <= 20) {
			return 0;
		} else if (health >= HEARTS[HEARTS.length - 2]) {
			return HEARTS.length - 1;
		}
		
		return (int) Math.ceil(health / 20) - 1;
	}
	
	public final static String ABSORPTION_COLOR = "#d4af37";
	public final static String ABSORPTION_POISON_COLOR = "#c5d117";
	public final static String ABSORPTION_WITHER_COLOR = "#70590d";
	
	public final static double[] HEARTS = new double[] { 20, 40, 60, 80, 100, 120, 140, 180, 200, 220, 240, 260 };
	
	public final static String[] NORMAL_COLORS = new String[] { "#ff0000", "#ee8100", "#e5ce00", "#00da00", "#0c9df1", "#b486ff", "#ec8afb", "#fb8bad", "#fbd78b", "#03efec",
			"#b7e7fd", "#ededed" };
	public final static String[] POISONED_COLORS = new String[] { "#00ff00", "#619613", "#86cf1a", "#6aa514", "#5a8c11", "#68a114", "#72b116", "#70ad16", "#94e221", "#80c619",
			"#96e326", "#9ee536" };
	public final static String[] WITHERED_COLORS = new String[] { "#555555", "#474747", "#434343", "#3e3e3e", "#4e4e4e", "#898989", "#898989", "#898989", "#898989", "#484848",
			"#9d9d9d", "#adadad" };
}
