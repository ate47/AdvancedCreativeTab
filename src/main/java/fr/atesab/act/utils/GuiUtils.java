package fr.atesab.act.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL11;

import fr.atesab.act.ACTMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
				Minecraft.getInstance().setScreen(screen);
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
	 * @param text the text to set
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
	 * @param screen the screen to show
	 * @see GuiUtils#displayScreen(Screen, boolean)
	 * @since 2.0
	 */
	public static void displayScreen(Screen screen) {
		displayScreen(screen, false);
	}

	/**
	 * Display a {@link Screen} with delay if in chat (to avoid screen close)
	 * 
	 * @param screen     the screen to show
	 * @param forceDelay force the delay if the currentScreen isn't a
	 *                   {@link ChatScreen}
	 * @see GuiUtils#displayScreen(Screen)
	 * @since 2.0
	 */
	public static void displayScreen(Screen screen, boolean forceDelay) {
		Minecraft mc = Minecraft.getInstance();
		if (forceDelay || mc.screen instanceof ChatScreen)
			new DelayScreen(screen, 20);
		else
			mc.setScreen(screen);
	}

	/**
	 * Draw a box on the screen
	 * 
	 * @param x      x tl location
	 * @param y      y tl location
	 * @param width  box width
	 * @param height box height
	 * @param zLevel zlevel of the screen
	 * 
	 * @since 2.0
	 */
	@SuppressWarnings("deprecation")
	public static void drawBox(int x, int y, int width, int height, float zLevel) {
		zLevel -= 50F;
		RenderSystem.disableRescaleNormal();
		RenderHelper.turnOff();
		RenderSystem.disableLighting();
		RenderSystem.disableDepthTest();
		drawGradientRect(x - 3, y - 4, x + width + 3, y - 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y + height + 3, x + width + 3, y + height + 4, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y - 3, x + width + 3, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 4, y - 3, x - 3, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x + width + 3, y - 3, x + width + 4, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, 1347420415, 1344798847, zLevel);
		drawGradientRect(x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, 1347420415, 1344798847, zLevel);
		drawGradientRect(x - 3, y - 3, x + width + 3, y - 3 + 1, 1347420415, 1347420415, zLevel);
		drawGradientRect(x - 3, y + height + 2, x + width + 3, y + height + 3, 1344798847, 1344798847, zLevel);
		RenderSystem.enableLighting();
		RenderSystem.enableDepthTest();
		RenderHelper.turnBackOn();
		RenderSystem.enableRescaleNormal();
	}

	/**
	 * set the current color
	 * 
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	public static void color3f(float r, float g, float b) {
		GL11.glColor3f(r, g, b);
	}

	/**
	 * Draw a String centered
	 * 
	 * @param font  font renderer
	 * @param text  the text
	 * @param x     x text location
	 * @param y     y text location
	 * @param color text color
	 * @since 2.0
	 * @see #drawCenterString(FontRenderer, String, int, int, int, int)
	 * @see #drawRightString(FontRenderer, String, int, int, int)
	 */
	public static void drawCenterString(FontRenderer font, String text, int x, int y, int color) {
		drawCenterString(font, text, x, y, color, font.lineHeight);
	}

	/**
	 * Draw a String centered of a vertical segment
	 * 
	 * @param font   font renderer
	 * @param text   the text
	 * @param x      x text location
	 * @param y      y text location
	 * @param color  text color
	 * @param height segment length
	 * @since 2.0
	 * @see #drawCenterString(FontRenderer, String, int, int, int)
	 * @see #drawString(FontRenderer, String, int, int, int, int)
	 */
	public static void drawCenterString(FontRenderer font, String text, int x, int y, int color, int height) {
		drawString(font, text, x - font.width(text) / 2, y, color, height);
	}

	/**
	 * Draw a rectangle with a vertical gradient
	 * 
	 * @param left       left location
	 * @param top        top location
	 * @param right      right location
	 * @param bottom     bottom location
	 * @param startColor startColor color
	 * @param endColor   endColor color
	 * @param zLevel     zLevel of the screen
	 * 
	 * @see #drawGradientRect(int, int, int, int, int, int, int, int, float)
	 * @since 2.0
	 */
	@SuppressWarnings("deprecation")
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
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		// func_225582_a_ = pos func_227885_a_ = color
		bufferbuilder.vertex((double) right, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
		bufferbuilder.vertex((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
		bufferbuilder.vertex((double) left, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
		bufferbuilder.vertex((double) right, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
		tessellator.end();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	/**
	 * Draw a gradient rectangle
	 * 
	 * @param left        left location
	 * @param top         top location
	 * @param right       right location
	 * @param bottom      bottom location
	 * @param leftColor   leftColor color
	 * @param topColor    topColor color
	 * @param rightColor  rightColor color
	 * @param bottomColor bottomColor color
	 * @param zLevel      zLevel of the screen
	 * 
	 * @see #drawGradientRect(int, int, int, int, int, int, float)
	 * @since 2.0
	 */
	@SuppressWarnings("deprecation")
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
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.vertex((double) right, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
		bufferbuilder.vertex((double) left, (double) top, (double) zLevel).color(f5, f6, f7, f4).endVertex();
		bufferbuilder.vertex((double) left, (double) bottom, (double) zLevel).color(f9, f10, f11, f8).endVertex();
		bufferbuilder.vertex((double) right, (double) bottom, (double) zLevel).color(f13, f14, f15, f12).endVertex();
		tessellator.end();
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	/**
	 * Draw an {@link ItemStack} on a {@link Screen}
	 * 
	 * @param itemRender the renderer
	 * @param itemstack  the stack
	 * @param x          the x location
	 * @param y          the y location
	 * 
	 * @since 2.1.1
	 */
	@SuppressWarnings("deprecation")
	public static void drawItemStack(ItemRenderer itemRender, ItemStack itemstack, int x, int y) {
		if (itemstack == null || itemstack.isEmpty())
			return;
		RenderSystem.enableDepthTest();
		itemRender.renderAndDecorateItem(itemstack, x, y);
		itemRender.renderGuiItemDecorations(Minecraft.getInstance().font, itemstack, x, y, null);
		RenderSystem.disableBlend();
		RenderSystem.disableLighting();
	}

	/**
	 * Draw an {@link ItemStack} on a {@link Screen}
	 * 
	 * @param itemRender the renderer
	 * @param screen     the screen
	 * @param itemstack  the stack
	 * @param x          the x location
	 * @param y          the y location
	 * 
	 * @since 2.0
	 */
	@SuppressWarnings("deprecation")
	public static void drawItemStack(ItemRenderer itemRender, Screen screen, ItemStack itemstack, int x, int y) {
		if (itemstack == null || itemstack.isEmpty())
			return;
		RenderSystem.enableDepthTest();
		itemRender.renderAndDecorateItem(itemstack, x, y);
		itemRender.renderGuiItemDecorations(screen.getMinecraft().font, itemstack, x, y, null);
		RenderSystem.disableBlend();
		RenderSystem.disableLighting();
	}

	/**
	 * Draws a solid color rectangle with the specified coordinates and color.
	 * 
	 * @param stack  the matrix stack
	 * @param left   left location
	 * @param top    top location
	 * @param right  right location
	 * @param bottom bottom location
	 * @param color  the color
	 */
	public static void drawRect(MatrixStack stack, int left, int top, int right, int bottom, int color) {
		AbstractGui.fill(stack, left, top, right, bottom, color);
	}

	/**
	 * Draw relatively a {@link Widget}
	 * 
	 * @param stack        the matrix stack
	 * @param field        the field
	 * @param offsetX      the x offset
	 * @param offsetY      the y offset
	 * @param mouseX       the mouse X location
	 * @param mouseY       the mouse Y location
	 * @param partialTicks the partialTicks of the render
	 * 
	 * @since 2.0
	 */
	public static void drawRelative(MatrixStack stack, Widget field, int offsetX, int offsetY, int mouseX, int mouseY,
			float partialTicks) {
		field.x += offsetX; // x
		field.y += offsetY; // y
		field.render(stack, mouseX + offsetX, mouseY + offsetY, partialTicks);
		field.x -= offsetX;
		field.y -= offsetY;
	}

	/**
	 * Draw relatively a {@link Widget}
	 * 
	 * @param stack        the matrix stack
	 * @param widget        the widget
	 * @param offsetX      the x offset
	 * @param offsetY      the y offset
	 * @param mouseX       the mouse X location
	 * @param mouseY       the mouse Y location
	 * @param partialTicks the partialTicks of the render
	 * 
	 * @since 2.0
	 */
	public static void drawRelativeToolTip(MatrixStack stack, Widget widget, int offsetX, int offsetY, int mouseX,
			int mouseY, float partialTicks) {
		widget.x += offsetX; // x
		widget.y += offsetY; // y
		widget.renderToolTip(stack, mouseX + offsetX, mouseY + offsetY); // renderToolTip
		widget.x -= offsetX;
		widget.y -= offsetY;
	}

	/**
	 * Draw a String to the the right of a location
	 * 
	 * @param font  the renderer
	 * @param text  the string to render
	 * @param x     the x location
	 * @param y     the y location
	 * @param color the color of the text
	 * 
	 * @since 2.0
	 * @see #drawCenterString(FontRenderer, String, int, int, int)
	 * @see #drawRightString(FontRenderer, String, int, int, int, int)
	 */

	public static void drawRightString(FontRenderer font, String text, int x, int y, int color) {
		drawCenterString(font, text, x, y, color, font.lineHeight);
	}

	/**
	 * Draw a String on the screen at middle of an height to the right of location
	 * 
	 * @param font   the renderer
	 * @param text   the string to render
	 * @param x      the x location
	 * @param y      the y location
	 * @param color  the color of the text
	 * @param height the height of the text
	 * 
	 * @since 2.0
	 * @see #drawString(FontRenderer, String, int, int, int, int)
	 * @see #drawCenterString(FontRenderer, String, int, int, int, int)
	 */
	public static void drawRightString(FontRenderer font, String text, int x, int y, int color, int height) {
		drawString(font, text, x - font.width(text), y, color, height);
	}

	/**
	 * Draw a String to the right of a {@link Widget}
	 * 
	 * @param font  the renderer
	 * @param text  the string to render
	 * @param field the widget
	 * @param color the color of the text
	 * 
	 * @since 2.0
	 * @see #drawRightString(FontRenderer, String, int, int, int)
	 * @see #drawRightString(FontRenderer, String, int, int, int, int)
	 */
	public static void drawRightString(FontRenderer font, String text, Widget field, int color) {
		drawRightString(font, text, field.x, field.y, color, field.getHeight());
	}

	/**
	 * Draw a String to the right of a {@link Widget} with offsets
	 * 
	 * @param font    the renderer
	 * @param text    the string to render
	 * @param field   the widget
	 * @param color   the color of the text
	 * @param offsetX the x offset
	 * @param offsetY the y offset
	 * 
	 * @since 2.0
	 * @see #drawRightString(FontRenderer, String, Widget, int)
	 */
	public static void drawRightString(FontRenderer font, String text, Widget field, int color, int offsetX,
			int offsetY) {
		drawRightString(font, text, field.x + offsetX, field.y + offsetY, color, field.getHeight());
	}

	/**
	 * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used
	 * anywhere in vanilla code.
	 * 
	 * @param x          x location
	 * @param y          y location
	 * @param u          x uv location
	 * @param v          y uv location
	 * @param uWidth     uv width
	 * @param vHeight    uv height
	 * @param width      width
	 * @param height     height
	 * @param tileWidth  tile width
	 * @param tileHeight tile height
	 */
	public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
			int height, float tileWidth, float tileHeight) {
		float scaleX = 1.0F / tileWidth;
		float scaleY = 1.0F / tileHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.vertex((double) x, (double) (y + height), 0.0D)
				.uv((float) (u * scaleX), (float) ((v + (float) vHeight) * scaleY)).endVertex();
		bufferbuilder.vertex((double) (x + width), (double) (y + height), 0.0D)
				.uv((float) ((u + (float) uWidth) * scaleX), (float) ((v + (float) vHeight) * scaleY)).endVertex();
		bufferbuilder.vertex((double) (x + width), (double) y, 0.0D)
				.uv((float) ((u + (float) uWidth) * scaleX), (float) (v * scaleY)).endVertex();
		bufferbuilder.vertex((double) x, (double) y, 0.0D).uv((float) (u * scaleX), (float) (v * scaleY)).endVertex();
		tessellator.end();
	}

	/**
	 * Draw a String on the screen at middle of an height
	 * 
	 * @param font   the renderer
	 * @param text   the string to render
	 * @param x      the x location
	 * @param y      the y location
	 * @param color  the color of the text
	 * @param height the height of the text
	 * 
	 * @since 2.0
	 * @see #drawCenterString(FontRenderer, String, int, int, int, int)
	 * @see #drawRightString(FontRenderer, String, int, int, int, int)
	 */
	public static void drawString(FontRenderer font, String text, int x, int y, int color, int height) {
		ACTMod.drawString(font, text, x, y + height / 2 - font.lineHeight / 2, color);
	}

	/**
	 * Draw a text box on the screen
	 * 
	 * @param font         the renderer
	 * @param x            the x location
	 * @param y            the y location
	 * @param parentWidth  the parent width
	 * @param parentHeight the parent height
	 * @param zLevel       the zLevel of the screen
	 * @param args         the lines to show
	 * 
	 * @since 2.1
	 */
	public static void drawTextBox(FontRenderer font, int x, int y, int parentWidth, int parentHeight, float zLevel,
			String... args) {
		List<String> text = Arrays.asList(args);
		int width = text.isEmpty() ? 0 : text.stream().mapToInt(font::width).max().getAsInt();
		int height = text.size() * (1 + font.lineHeight);
		Tuple<Integer, Integer> pos = getRelativeBoxPos(x, y, width, height, parentWidth, parentHeight);
		drawBox(pos.a, pos.b, width, height, zLevel);
		text.forEach(l -> {
			ACTMod.drawString(font, l, pos.a, pos.b, 0xffffffff);
			pos.b += (1 + font.lineHeight);
		});
	}

	/**
	 * Get a respectively a green or a red integer color for true or false boolean
	 * 
	 * @param value the boolean value
	 * @return the color
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
	 * @param x            the x location
	 * @param y            the y location
	 * @param width        the width
	 * @param height       the height
	 * @param parentWidth  the parent width
	 * @param parentHeight the parent height
	 * 
	 * @return (x,y) location
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
		return new Tuple<>(x, y);
	}

	/**
	 * Check if a {@link Widget} is hover by a location (mouse)
	 * 
	 * @param widget the widget
	 * @param mouseX the mouse x location
	 * @param mouseY the mouse y location
	 * @return true if the button is hover, false otherwise
	 * @see #isHover(int, int, int, int, int, int)
	 * @since 2.0
	 */
	public static boolean isHover(Widget widget, int mouseX, int mouseY) {
		return isHover(widget.x, widget.y, widget.getWidth(), widget.getHeight(), mouseX, mouseY);
	}

	/**
	 * Check if a box is hover by a location (mouse)
	 * 
	 * @param x      the x location
	 * @param y      the y location
	 * @param sizeX  the width
	 * @param sizeY  the height
	 * @param mouseX the mouse x location
	 * @param mouseY the mouse y location
	 * @return true if the field is hover
	 * @see #isHover(Widget, int, int)
	 * @since 2.0
	 */
	public static boolean isHover(int x, int y, int sizeX, int sizeY, int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + sizeX && mouseY >= y && mouseY <= y + sizeY;
	}

}
