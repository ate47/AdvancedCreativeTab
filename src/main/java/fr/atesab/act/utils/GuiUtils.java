package fr.atesab.act.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL13.GL_SAMPLE_ALPHA_TO_COVERAGE;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * A set of tools to help to draw, show and modify {@link GuiScreen}
 * 
 * @author ATE47
 * @since 2.0
 */
public class GuiUtils {
	private static class DelayScreen {
		private GuiScreen screen;
		private long delay;

		DelayScreen(GuiScreen screen, long delay) {
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
	 * Display a {@link GuiScreen} with delay if in chat (to avoid screen close)
	 * 
	 * @param screen
	 * @see GuiUtils#displayScreen(GuiScreen, boolean)
	 * @since 2.0
	 */
	public static void displayScreen(GuiScreen screen) {
		displayScreen(screen, false);
	}

	/**
	 * Display a {@link GuiScreen} with delay if in chat (to avoid screen close)
	 * 
	 * @param screen
	 * @param forceDelay
	 *            force the delay if the currentScreen isn't a {@link GuiChat}
	 * @see GuiUtils#displayScreen(GuiScreen)
	 * @since 2.0
	 */
	public static void displayScreen(GuiScreen screen, boolean forceDelay) {
		Minecraft mc = Minecraft.getMinecraft();
		if (forceDelay || mc.currentScreen instanceof GuiChat)
			new DelayScreen(screen, 20);
		else
			mc.displayGuiScreen(screen);
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
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.func_181668_a(7, DefaultVertexFormats.BLOCK);
		worldrenderer.func_181662_b((double) right, (double) top, (double) zLevel).func_181666_a(f1, f2, f3, f)
				.func_181675_d();
		worldrenderer.func_181662_b((double) left, (double) top, (double) zLevel).func_181666_a(f1, f2, f3, f)
				.func_181675_d();
		worldrenderer.func_181662_b((double) left, (double) bottom, (double) zLevel).func_181666_a(f5, f6, f7, f4)
				.func_181675_d();
		worldrenderer.func_181662_b((double) right, (double) bottom, (double) zLevel).func_181666_a(f5, f6, f7, f4)
				.func_181675_d();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
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
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
		worldrenderer.func_181662_b((double) right, (double) top, (double) zLevel).func_181666_a(f1, f2, f3, f)
				.func_181675_d();
		worldrenderer.func_181662_b((double) left, (double) top, (double) zLevel).func_181666_a(f5, f6, f7, f4)
				.func_181675_d();
		worldrenderer.func_181662_b((double) left, (double) bottom, (double) zLevel).func_181666_a(f9, f10, f11, f8)
				.func_181675_d();
		worldrenderer.func_181662_b((double) right, (double) bottom, (double) zLevel).func_181666_a(f13, f14, f15, f12)
				.func_181675_d();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	/**
	 * Draw an {@link ItemStack} on a {@link GuiScreen}
	 * 
	 * @since 2.0
	 */
	public static void drawItemStack(RenderItem itemRender, float zLevel, GuiScreen gui, ItemStack itemstack, int x,
			int y) {
		if (itemstack == null || itemstack.getItem().equals(Item.getItemFromBlock(Blocks.air)))
			return;
		GlStateManager.enableDepth();
		itemRender.renderItemAndEffectIntoGUI(itemstack, x, y);
		itemRender.renderItemOverlayIntoGUI(gui.mc.fontRendererObj, itemstack, x, y, null);
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
	}

	/**
	 * Draw relatively a {@link GuiTextField}
	 * 
	 * @see #drawRelative(Minecraft, GuiButton, int, int, int, int, float)
	 * @since 2.0
	 */
	public static void drawRelative(GuiTextField field, int offsetX, int offsetY) {
		field.xPosition += offsetX;
		field.yPosition += offsetY;
		field.drawTextBox();
		field.xPosition -= offsetX;
		field.yPosition -= offsetY;
	}

	/**
	 * Draw relatively a {@link GuiButton}
	 * 
	 * @see #drawRelative(GuiTextField, int, int)
	 * @since 2.0
	 */
	public static void drawRelative(Minecraft mc, GuiButton button, int offsetX, int offsetY, int mouseX, int mouseY,
			float partialTicks) {
		button.xPosition += offsetX;
		button.yPosition += offsetY;
		button.drawButton(mc, mouseX + offsetX, mouseY + offsetY);
		button.xPosition -= offsetX;
		button.yPosition -= offsetY;
	}

	/**
	 * Draw a String to the right of a {@link GuiTextField}
	 * 
	 * @since 2.0
	 * @see #drawRightString(FontRenderer, String, GuiTextField, int, int, int)
	 */
	public static void drawRightString(FontRenderer fontRenderer, String text, GuiTextField field, int color) {
		drawRightString(fontRenderer, text, field.xPosition, field.yPosition, color, field.height);
	}

	/**
	 * Draw a String to the right of a {@link GuiTextField} with offsets
	 * 
	 * @since 2.0
	 * @see #drawRightString(FontRenderer, String, GuiTextField, int)
	 */
	public static void drawRightString(FontRenderer fontRenderer, String text, GuiTextField field, int color,
			int offsetX, int offsetY) {
		drawRightString(fontRenderer, text, field.xPosition + offsetX, field.yPosition + offsetY, color, field.height);
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

	public static void drawBox(int left, int top, int right, int bottom, int color) {
		float r = (color >> 16 & 0xFF) / 255F;
		float g = (color >> 8 & 0xFF) / 255F;
		float b = (color & 0xFF) / 255F;
		GlStateManager.color(r, g, b);
		glColor4f(r, g, b, 0.9F);
		Gui.drawRect(left, top, right, top + 1, color);
		Gui.drawRect(left, top, left + 1, bottom, color);
		Gui.drawRect(right - 1, top, right, bottom, color);
		Gui.drawRect(left, bottom - 1, right, bottom, color);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
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

	/**
	 * Get a respectively a green or a red integer color for true or false boolean
	 * 
	 * @since 2.0
	 */
	public static int getRedGreen(boolean value) {
		return value ? 0xff00ff00 : 0xffff0000;
	}

	/**
	 * Check if a {@link GuiButton} is hover by a location (mouse)
	 * 
	 * @return true if the button is hover
	 * @see #isHover(GuiTextField, int, int)
	 * @see #isHover(int, int, int, int, int, int)
	 * @since 2.0
	 */
	public static boolean isHover(GuiButton button, int mouseX, int mouseY) {
		return isHover(button.xPosition, button.yPosition, button.width, button.height, mouseX, mouseY);
	}

	/**
	 * Check if a {@link GuiTextField} is hover by a location (mouse)
	 * 
	 * @return true if the field is hover
	 * @see #isHover(GuiButton, int, int)
	 * @see #isHover(int, int, int, int, int, int)
	 * @since 2.0
	 */
	public static boolean isHover(GuiTextField field, int mouseX, int mouseY) {
		return isHover(field.xPosition, field.yPosition, field.width, field.height, mouseX, mouseY);
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

}
