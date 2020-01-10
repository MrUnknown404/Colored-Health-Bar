package mrunknown404.colorhealthbar.util;

import mrunknown404.colorhealthbar.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class Utils {
	public static final ResourceLocation ICON_BAR = new ResourceLocation(Main.MOD_ID, "textures/gui/health.png");
	public static final ResourceLocation HEARTS = new ResourceLocation(Main.MOD_ID, "textures/gui/hearts.png");
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
	
	public static float roundTo(double number, int decimal) {
		double tempDecimal = 1;
		for (int i = 0; i < decimal; i++) {
			tempDecimal *= 10;
		}
		
		return (float) (Math.round(number * tempDecimal) / tempDecimal);
	}
	
	public static RGBColor hex2Color(String s) {
		int i1 = Integer.decode(s);
		int r = i1 >> 16 & 0xFF;
		int g = i1 >> 8 & 0xFF;
		int b = i1 & 0xFF;
		return new RGBColor(r, g, b);
	}
	
	public static RGBColor getColor(double d1, double d2, int effect) {
		String[] colorCodes = null;
		
		if (effect == 16) {
			colorCodes = Colors.NORMAL_COLORS;
		} else if (effect == 52) {
			colorCodes = Colors.POISONED_COLORS;
		} else if (effect == 88) {
			colorCodes = Colors.WITHERED_COLORS;
		}
		
		if (d2 <= 20) {
			return hex2Color(colorCodes[0]);
		} else if (d2 >= Colors.HEARTS[Colors.HEARTS.length - 1]) {
			return hex2Color(colorCodes[Colors.HEARTS.length - 1]);
		}
		
		return hex2Color(colorCodes[(int) Math.ceil(d2 / 20) - 1]);
	}
	
	public static int getHeartMulti(double health) {
		if (health <= 20) {
			return 0;
		} else if (health >= Colors.HEARTS[Colors.HEARTS.length - 1]) {
			return Colors.HEARTS.length - 1;
		}
		
		return (int) Math.ceil(health / 20) - 1;
	}
	
	public static class Colors {
		public final static String ABSORPTION_COLOR = "#d4af37";
		public final static String ABSORPTION_POISON_COLOR = "#c5d117";
		public final static String ABSORPTION_WITHER_COLOR = "#70590d";
		
		public final static double[] HEARTS = new double[] { 20, 40, 60, 80, 100, 120, 140, 180, 200, 220, 240, 260 };
		
		public final static String[] NORMAL_COLORS = new String[] { "#ff0000", "#ee8100", "#e5ce00", "#00da00", "#0c9df1", "#b486ff", "#ec8afb", "#fb8bad", "#fbd78b", "#03efec",
				"#b7e7fd", "#ededed" };
		public final static String[] POISONED_COLORS = new String[] { "#00ff00", "#619613", "#86cf1a", "#6aa514", "#5a8c11", "#68a114", "#72b116", "#70ad16", "#94e221", "#cyan",
				"#80c619", "#9ee536" };
		public final static String[] WITHERED_COLORS = new String[] { "#555555", "#474747", "#434343", "#3e3e3e", "#4e4e4e", "#898989", "#898989", "#898989", "#898989", "#484848",
				"#9d9d9d", "#adadad" };
	}
	
	public static boolean isValidHexColor(String hexColor) {
		if (!hexColor.startsWith("#")) {
			return false;
		}
		
		hexColor = hexColor.substring(1);
		
		if (hexColor.length() == 0 || (hexColor.charAt(0) != '-' && Character.digit(hexColor.charAt(0), 16) == -1))
			return false;
		if (hexColor.length() == 1 && hexColor.charAt(0) == '-')
			return false;
		
		for (int i = 1; i < hexColor.length(); i++)
			if (Character.digit(hexColor.charAt(i), 16) == -1)
				return false;
		return true;
	}
}
