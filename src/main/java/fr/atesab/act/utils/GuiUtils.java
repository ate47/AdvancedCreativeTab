package fr.atesab.act.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * A set of tools to help to draw, show and modify {@link Screen}
 * 
 * @author ATE47
 * @since 2.0
 */
public class GuiUtils {
	private static class DelayScreen {
		private Screen screen;
		private long delay;

		DelayScreen(Screen screen, long delay) {
			this.screen = screen;
			this.delay = delay;
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent
		public void onTick(TickEvent ev) {
			if (delay < 0) {
				Minecraft.getInstance().displayGuiScreen(screen);
				MinecraftForge.EVENT_BUS.unregister(this);
			} else
				delay--;
		}
	}

	public static final IPressable EMPTY_PRESS = b -> {
	};

	/**
	 * Set clipboard text
	 * 
	 * @param text
	 * @since 2.0
	 */
	public static void addToClipboard(String text) {
		StringSelection select = new StringSelection(text);
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(select, select);
	}

	/**
	 * Display a {@link Screen} with delay if in chat (to avoid screen close)
	 * 
	 * @param screen
	 * @see GuiUtils#displayScreen(Screen, boolean)
	 * @since 2.0
	 */
	public static void displayScreen(Screen screen) {
		displayScreen(screen, false);
	}

	/**
	 * Display a {@link Screen} with delay if in chat (to avoid screen close)
	 * 
	 * @param screen
	 * @param forceDelay
	 *            force the delay if the currentScreen isn't a {@link GuiChat}
	 * @see GuiUtils#displayScreen(Screen)
	 * @since 2.0
	 */
	public static void displayScreen(Screen screen, boolean forceDelay) {
		Minecraft mc = Minecraft.getInstance();
		if (forceDelay || mc.currentScreen instanceof ChatScreen)
			new DelayScreen(screen, 20);
		else
			mc.displayGuiScreen(screen);
	}

	/**
	 * Draw a box on the screen
	 * 
	 * @since 2.0
	 */
	public static void drawBox(int x, int y, int width, int height, float zLevel) {
		zLevel -= 50F;
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepthTest();
		drawGradientRect(x - 3, y - 4, x + width + 3, y - 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y + height + 3, x + width + 3, y + height + 4, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y - 3, x + width + 3, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 4, y - 3, x - 3, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x + width + 3, y - 3, x + width + 4, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, 1347420415, 1344798847, zLevel);
		drawGradientRect(x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, 1347420415, 1344798847, zLevel);
		drawGradientRect(x - 3, y - 3, x + width + 3, y - 3 + 1, 1347420415, 1347420415, zLevel);
		drawGradientRect(x - 3, y + height + 2, x + width + 3, y + height + 3, 1344798847, 1344798847, zLevel);
		GlStateManager.enableLighting();
		GlStateManager.enableDepthTest();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableRescaleNormal();
	}

	/**
	 * Draw a String centered
	 * 
	 * @since 2.0
	 * @see #drawCenterString(FontRenderer, String, int, int, int, int)
	 * @see #drawRightString(FontRenderer, String, int, int, int)
	 */
	public static void drawCenterString(FontRenderer fontRenderer, String text, int x, int y, int color) {
		drawCenterString(fontRenderer, text, x, y, color, fontRenderer.FONT_HEIGHT);
	}

	/**
	 * Draw a String centered of a vertical segment
	 * 
	 * @param fontRenderer
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 * @param height
	 *            segment length
	 * @since 2.0
	 * @see #drawCenterString(FontRenderer, String, int, int, int)
	 * @see #drawString(FontRenderer, String, int, int, int, int)
	 */
	public static void drawCenterString(FontRenderer fontRenderer, String text, int x, int y, int color, int height) {
		drawString(fontRenderer, text, x - fontRenderer.getStringWidth(text) / 2, y, color, height);
	}

	/**
	 * Draw a rectangle with a vertical gradient
	 * 
	 * @see #drawGradientRect(int, int, int, int, int, int, int, int, float)
	 * @since 2.0
	 */
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
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
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
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	/**
	 * Draw a gradient rectangle
	 * 
	 * @see #drawGradientRect(int, int, int, int, int, int, float)
	 * @since 2.0
	 */
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
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
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
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	/**
	 * Draw an {@link ItemStack} on a {@link Screen}
	 * 
	 * @since 2.1.1
	 */
	public static void drawItemStack(ItemRenderer itemRender, ItemStack itemstack, int x, int y) {
		if (itemstack == null || itemstack.isEmpty())
			return;
		GlStateManager.enableDepthTest();
		itemRender.renderItemAndEffectIntoGUI(itemstack, x, y);
		itemRender.renderItemOverlayIntoGUI(Minecraft.getInstance().fontRenderer, itemstack, x, y, null);
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
	}

	/**
	 * Draw an {@link ItemStack} on a {@link Screen}
	 * 
	 * @since 2.0
	 */
	public static void drawItemStack(ItemRenderer itemRender, Screen screen, ItemStack itemstack, int x, int y) {
		if (itemstack == null || itemstack.isEmpty())
			return;
		GlStateManager.enableDepthTest();
		itemRender.renderItemAndEffectIntoGUI(itemstack, x, y);
		itemRender.renderItemOverlayIntoGUI(screen.getMinecraft().fontRenderer, itemstack, x, y, null);
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
	}

	/**
	 * Draws a solid color rectangle with the specified coordinates and color.
	 */
	public static void drawRect(int left, int top, int right, int bottom, int color) {
		AbstractGui.fill(left, top, right, bottom, color);
	}

	/**
	 * Draw relatively a {@link GuiTextField}
	 * 
	 * @see #drawRelative(Minecraft, GuiButton, int, int, int, int, float)
	 * @since 2.0
	 */
	public static void drawRelative(Widget field, int offsetX, int offsetY, int mouseX, int mouseY,
			float partialTicks) {
		field.x += offsetX;
		field.y += offsetY;
		field.render(mouseX + offsetX, mouseY + offsetY, partialTicks);
		field.x -= offsetX;
		field.y -= offsetY;
	}

	/**
	 * Draw relatively a {@link Widget}
	 * 
	 * @see #drawRelative(Minecraft, GuiButton, int, int, int, int, float)
	 * @since 2.0
	 */
	public static void drawRelativeToolTip(Widget widget, int offsetX, int offsetY, int mouseX, int mouseY,
			float partialTicks) {
		widget.x += offsetX;
		widget.y += offsetY;
		widget.renderToolTip(mouseX + offsetX, mouseY + offsetY);
		widget.x -= offsetX;
		widget.y -= offsetY;
	}

	/**
	 * Draw a String to the the right of a location
	 * 
	 * @since 2.0
	 * @see #drawCenterString(FontRenderer, String, int, int, int)
	 * @see #drawRightString(FontRenderer, String, int, int, int, int)
	 */

	public static void drawRightString(FontRenderer fontRenderer, String text, int x, int y, int color) {
		drawCenterString(fontRenderer, text, x, y, color, fontRenderer.FONT_HEIGHT);
	}

	/**
	 * Draw a String on the screen at middle of an height to the right of location
	 * 
	 * @since 2.0
	 * @see #drawString(FontRenderer, String, int, int, int, int)
	 * @see #drawCenterString(FontRenderer, String, int, int, int, int)
	 */
	public static void drawRightString(FontRenderer fontRenderer, String text, int x, int y, int color, int height) {
		drawString(fontRenderer, text, x - fontRenderer.getStringWidth(text), y, color, height);
	}

	/**
	 * Draw a String to the right of a {@link GuiTextField}
	 * 
	 * @since 2.0
	 * @see #drawRightString(FontRenderer, String, GuiTextField, int, int, int)
	 */
	public static void drawRightString(FontRenderer fontRenderer, String text, TextFieldWidget field, int color) {
		drawRightString(fontRenderer, text, field.x, field.y, color, field.getHeight());
	}

	/**
	 * Draw a String to the right of a {@link GuiTextField} with offsets
	 * 
	 * @since 2.0
	 * @see #drawRightString(FontRenderer, String, GuiTextField, int)
	 */
	public static void drawRightString(FontRenderer fontRenderer, String text, TextFieldWidget field, int color,
			int offsetX, int offsetY) {
		drawRightString(fontRenderer, text, field.x + offsetX, field.y + offsetY, color, field.getHeight());
	}

	/**
	 * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used
	 * anywhere in vanilla code.
	 */
	public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
			int height, float tileWidth, float tileHeight) {
		float scaleX = 1.0F / tileWidth;
		float scaleY = 1.0F / tileHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) x, (double) (y + height), 0.0D)
				.tex((double) (u * scaleX), (double) ((v + (float) vHeight) * scaleY)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D)
				.tex((double) ((u + (float) uWidth) * scaleX), (double) ((v + (float) vHeight) * scaleY)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) y, 0.0D)
				.tex((double) ((u + (float) uWidth) * scaleX), (double) (v * scaleY)).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).tex((double) (u * scaleX), (double) (v * scaleY)).endVertex();
		tessellator.draw();
	}

	/**
	 * Draw a String on the screen at middle of an height
	 * 
	 * @since 2.0
	 * @see #drawCenterString(FontRenderer, String, int, int, int, int)
	 * @see #drawRightString(FontRenderer, String, int, int, int, int)
	 */
	public static void drawString(FontRenderer fontRenderer, String text, int x, int y, int color, int height) {
		fontRenderer.drawString(text, x, y + height / 2 - fontRenderer.FONT_HEIGHT / 2, color);
	}

	/**
	 * Draw a text box on the screen
	 * 
	 * @since 2.1
	 */
	public static void drawTextBox(FontRenderer fontRenderer, int x, int y, int parentWidth, int parentHeight,
			float zLevel, String... args) {
		List<String> text = Arrays.asList(args);
		int width = text.size() == 0 ? 0 : text.stream().mapToInt(fontRenderer::getStringWidth).max().getAsInt();
		int height = text.size() * (1 + fontRenderer.FONT_HEIGHT);
		Tuple<Integer, Integer> pos = getRelativeBoxPos(x, y, width, height, parentWidth, parentHeight);
		drawBox(pos.a, pos.b, width, height, zLevel);
		text.forEach(l -> {
			fontRenderer.drawString(l, pos.a, pos.b, 0xffffffff);
			pos.b += (1 + fontRenderer.FONT_HEIGHT);
		});
	}

	/**
	 * Get a respectively a green or a red integer color for true or false boolean
	 * 
	 * @since 2.0
	 */
	public static int getRedGreen(boolean value) {
		return value ? 0xff77ff77 : 0xffff7777;
	}

	/**
	 * get a tuple of (x,y) location on the screen for a box to put it without
	 * loosing it at borders
	 * 
	 * @since 2.0
	 */
	public static Tuple<Integer, Integer> getRelativeBoxPos(int x, int y, int width, int height, int parentWidth,
			int parentHeight) {
		if (x + width > parentWidth) {
			x -= width + 5;
			if (x < 0)
				x = 0;
		} else
			x += 12;
		if (y + height > parentHeight) {
			y -= height + 5;
			if (y < 0)
				y = 0;
		} else
			y += 12;
		return new Tuple<Integer, Integer>(x, y);
	}

	/**
	 * Check if a {@link GuiButton} is hover by a location (mouse)
	 * 
	 * @return true if the button is hover
	 * @see #isHover(GuiTextField, int, int)
	 * @see #isHover(int, int, int, int, int, int)
	 * @since 2.0
	 */
	public static boolean isHover(Widget button, int mouseX, int mouseY) {
		return isHover(button.x, button.y, button.getWidth(), button.getHeight(), mouseX, mouseY);
	}

	/**
	 * Check if a box is hover by a location (mouse)
	 * 
	 * @return true if the field is hover
	 * @see #isHover(GuiButton, int, int)
	 * @see #isHover(GuiTextField, int, int)
	 * @since 2.0
	 */
	public static boolean isHover(int X, int Y, int sizeX, int sizeY, int mouseX, int mouseY) {
		return mouseX >= X && mouseX <= X + sizeX && mouseY >= Y && mouseY <= Y + sizeY;
	}

	/**
	 * Check if a {@link GuiTextField} is hover by a location (mouse)
	 * 
	 * @return true if the field is hover
	 * @see #isHover(GuiButton, int, int)
	 * @see #isHover(int, int, int, int, int, int)
	 * @since 2.0
	 */
	public static boolean isHover(TextFieldWidget field, int mouseX, int mouseY) {
		return isHover(field.x, field.y, field.getWidth(), field.getHeight(), mouseX, mouseY);
	}
}
