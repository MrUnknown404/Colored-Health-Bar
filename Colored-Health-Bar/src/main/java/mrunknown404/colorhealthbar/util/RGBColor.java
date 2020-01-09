package mrunknown404.colorhealthbar.util;

import net.minecraft.client.renderer.GlStateManager;

public class RGBColor {
	public final int r, g, b;
	
	public RGBColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void color2Gl() {
		GlStateManager.color(r / 255f, g / 255f, b / 255f);
	}
	
	public RGBColor colorBlend(RGBColor c, double d) {
		return new RGBColor((int) Math.floor(this.r * (1 - d) + c.r * d), (int) Math.floor(this.g * (1 - d) + c.g * d), (int) Math.floor(this.b * (1 - d) + c.b * d));
	}
	
	public int colorToText() {
		return (this.r << 16) + (this.g << 8) + this.b;
	}
	
	public void color2Gla(float a) {
		GlStateManager.color(r / 255f, g / 255f, b / 255f, a);
	}
}
