package fr.atesab.act.utils;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class GuiUtils {
	private static class DelayScreen {
		private GuiScreen screen;
		private long delay;

		public DelayScreen(GuiScreen screen, long delay) {
			this.screen = screen;
			this.delay = delay;
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent
		public void onTick(TickEvent ev) {
			if (delay < 0) {
				Minecraft.getMinecraft().displayGuiScreen(screen);
				MinecraftForge.EVENT_BUS.unregister(this);
			} else
				delay--;
		}
	}

	public static void addToClipboard(String text) {
		StringSelection select = new StringSelection(text);
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(select, select);
	}

	public static void displayScreen(GuiScreen screen) {
		displayScreen(screen, false);
	}

	public static void displayScreen(GuiScreen screen, boolean forceDelay) {
		Minecraft mc = Minecraft.getMinecraft();
		if (forceDelay || mc.currentScreen instanceof GuiChat)
			new DelayScreen(screen, 20);
		else
			mc.displayGuiScreen(screen);
	}

	public static void drawCenterString(FontRenderer fontRenderer, String text, int x, int y, int color) {
		drawCenterString(fontRenderer, text, x, y, color, fontRenderer.FONT_HEIGHT);
	}

	public static void drawCenterString(FontRenderer fontRenderer, String text, int x, int y, int color, int height) {
		drawString(fontRenderer, text, x - fontRenderer.getStringWidth(text) / 2, y, color, height);
	}

	public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor,
			float zLevel) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double) right, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double) left, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static void drawGradientRect(int left, int top, int right, int bottom, int leftColor, int topColor,
			int rightColor, int bottomColor, float zLevel) {
		float f = (float) (leftColor >> 24 & 255) / 255.0F;
		float f1 = (float) (leftColor >> 16 & 255) / 255.0F;
		float f2 = (float) (leftColor >> 8 & 255) / 255.0F;
		float f3 = (float) (leftColor & 255) / 255.0F;
		float f4 = (float) (topColor >> 24 & 255) / 255.0F;
		float f5 = (float) (topColor >> 16 & 255) / 255.0F;
		float f6 = (float) (topColor >> 8 & 255) / 255.0F;
		float f7 = (float) (topColor & 255) / 255.0F;
		float f8 = (float) (rightColor >> 24 & 255) / 255.0F;
		float f9 = (float) (rightColor >> 16 & 255) / 255.0F;
		float f10 = (float) (rightColor >> 8 & 255) / 255.0F;
		float f11 = (float) (rightColor & 255) / 255.0F;
		float f12 = (float) (bottomColor >> 24 & 255) / 255.0F;
		float f13 = (float) (bottomColor >> 16 & 255) / 255.0F;
		float f14 = (float) (bottomColor >> 8 & 255) / 255.0F;
		float f15 = (float) (bottomColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double) right, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double) left, (double) top, (double) zLevel).color(f5, f6, f7, f4).endVertex();
		bufferbuilder.pos((double) left, (double) bottom, (double) zLevel).color(f9, f10, f11, f8).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, (double) zLevel).color(f13, f14, f15, f12).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static void drawItemStack(RenderItem itemRender, float zLevel, GuiScreen gui, ItemStack itemstack, int x,
			int y) {

		if (itemstack == null || itemstack.isEmpty())
			return;

		GlStateManager.enableDepth();
		itemRender.renderItemAndEffectIntoGUI(itemstack, x, y);
		itemRender.renderItemOverlayIntoGUI(gui.mc.fontRenderer, itemstack, x, y, null);
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
	}

	public static void drawRelative(GuiTextField field, int offsetX, int offsetY) {
		field.x += offsetX;
		field.y += offsetY;
		field.drawTextBox();
		field.x -= offsetX;
		field.y -= offsetY;
	}

	public static void drawRelative(Minecraft mc, GuiButton button, int offsetX, int offsetY, int mouseX, int mouseY,
			float partialTicks) {
		button.x += offsetX;
		button.y += offsetY;
		button.drawButton(mc, mouseX + offsetX, mouseY + offsetY, partialTicks);
		button.x -= offsetX;
		button.y -= offsetY;
	}

	public static void drawRightString(FontRenderer fontRenderer, String text, GuiTextField field, int color) {
		drawRightString(fontRenderer, text, field.x, field.y, color, field.height);
	}

	public static void drawRightString(FontRenderer fontRenderer, String text, GuiTextField field, int color,
			int offsetX, int offsetY) {
		drawRightString(fontRenderer, text, field.x + offsetX, field.y + offsetY, color, field.height);
	}

	public static void drawRightString(FontRenderer fontRenderer, String text, int x, int y, int color) {
		drawCenterString(fontRenderer, text, x, y, color, fontRenderer.FONT_HEIGHT);
	}

	public static void drawRightString(FontRenderer fontRenderer, String text, int x, int y, int color, int height) {
		drawString(fontRenderer, text, x - fontRenderer.getStringWidth(text), y, color, height);
	}

	public static void drawString(FontRenderer fontRenderer, String text, int x, int y, int color, int height) {
		fontRenderer.drawString(text, x, y + height / 2 - fontRenderer.FONT_HEIGHT / 2, color);
	}

	public static Tuple<Integer, Integer> getRelativeBoxPos(int x, int y, int width, int height, int parentWidth,
			int parentHeight) {
		if (x + width > parentWidth) {
			x -= width + 5;
			if (x < 0)
				x = 0;
		}
		if (y + height > parentHeight) {
			y -= height + 5;
			if (y < 0)
				y = 0;
		}
		return new Tuple<Integer, Integer>(x, y);
	}

	public static void drawBox(int x, int y, int width, int height, float zLevel) {
		zLevel -= 50F;
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int l = -267386864;
		drawGradientRect(x - 3, y - 4, x + width + 3, y - 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y + height + 3, x + width + 3, y + height + 4, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y - 3, x + width + 3, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 4, y - 3, x - 3, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x + width + 3, y - 3, x + width + 4, y + height + 3, -267386864, -267386864, zLevel);
		int i1 = 1347420415;
		int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
		drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, 1347420415, 1344798847, zLevel);
		drawGradientRect(x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, 1347420415, 1344798847, zLevel);
		drawGradientRect(x - 3, y - 3, x + width + 3, y - 3 + 1, 1347420415, 1347420415, zLevel);
		drawGradientRect(x - 3, y + height + 2, x + width + 3, y + height + 3, 1344798847, 1344798847, zLevel);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableRescaleNormal();
	}

	public static int getRedGreen(boolean value) {
		return (value ? Color.GREEN : Color.RED).getRGB();
	}

	public static boolean isHover(GuiButton button, int mouseX, int mouseY) {
		return isHover(button.x, button.y, button.width, button.height, mouseX, mouseY);
	}

	public static boolean isHover(GuiTextField field, int mouseX, int mouseY) {
		return isHover(field.x, field.y, field.width, field.height, mouseX, mouseY);
	}

	public static boolean isHover(int X, int Y, int sizeX, int sizeY, int mouseX, int mouseY) {
		return mouseX >= X && mouseX <= X + sizeX && mouseY >= Y && mouseY <= Y + sizeY;
	}
}
