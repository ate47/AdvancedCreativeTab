package fr.atesab.act.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiUtils {
	public static void buttonHoverMessage(GuiScreen guiScreen, Minecraft mc, GuiButton button, int mouseX, int mouseY,
			FontRenderer fontRendererObj, String[] text, int color) {
		if (GuiUtils.isHover(button, mouseX, mouseY))
			GuiUtils.drawTextBox(guiScreen, mc, fontRendererObj, text, mouseX + 5, mouseY + 5, color);
	}

	public static void drawCenterString(FontRenderer fontRendererObj, String text, int x, int y, int HEIGHT,
			int color) {
		fontRendererObj.drawString(text, x - fontRendererObj.getStringWidth(text) / 2,
				y + HEIGHT / 2 - fontRendererObj.FONT_HEIGHT / 2, color);
	}

	public static void drawRightString(FontRenderer fontRendererObj, String text, int x, int y, int HEIGHT, int color) {
		fontRendererObj.drawString(text, x - fontRendererObj.getStringWidth(text),
				y + HEIGHT / 2 - fontRendererObj.FONT_HEIGHT / 2, color);
	}

	public static void drawTextBox(GuiScreen guiScreen, Minecraft mc, FontRenderer fontRendererObj, String[] text,
			int X, int Y, int TextColor) {
		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/backgrounds.png"));
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.blendFunc(770, 771);
		int maxSize = 0;
		X = 5 + X;
		Y = 5 + Y;
		for (int i = 0; i < text.length; i++) {
			if (fontRendererObj.getStringWidth(text[i]) > maxSize)
				maxSize = fontRendererObj.getStringWidth(text[i]);
		}
		guiScreen.drawTexturedModalRect(X, Y, 0, 0, maxSize + 10, (fontRendererObj.FONT_HEIGHT + 1) * text.length + 10);
		for (int i = 0; i < text.length; i++)
			fontRendererObj.drawString(text[i], X + 5, Y + 5 + (fontRendererObj.FONT_HEIGHT + 1) * i, TextColor);
	}

	public static boolean isHover(GuiButton button, int mouseX, int mouseY) {
		return isHover(button.x, button.y, button.width, button.height, mouseX, mouseY);
	}

	public static boolean isHover(int X, int Y, int sizeX, int sizeY, int mouseX, int mouseY) {
		if ((mouseX >= X) && (mouseX <= X + sizeX) && (mouseY >= Y) && (mouseY <= Y + sizeY)) {
			return true;
		}
		return false;
	}

	public GuiUtils() {
	}
}
