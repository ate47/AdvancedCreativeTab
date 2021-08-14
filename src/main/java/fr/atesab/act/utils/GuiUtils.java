package fr.atesab.act.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import org.lwjgl.opengl.GL11;

import fr.atesab.act.ACTMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlclient.EarlyLoaderGUI;

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

	public static final Button.OnPress EMPTY_PRESS = b -> {
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
	public static void drawBox(int x, int y, int width, int height, float zLevel) {
		zLevel -= 50F;
		drawGradientRect(x - 3, y - 4, x + width + 3, y - 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y + height + 3, x + width + 3, y + height + 4, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y - 3, x + width + 3, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 4, y - 3, x - 3, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x + width + 3, y - 3, x + width + 4, y + height + 3, -267386864, -267386864, zLevel);
		drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, 1347420415, 1344798847, zLevel);
		drawGradientRect(x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, 1347420415, 1344798847, zLevel);
		drawGradientRect(x - 3, y - 3, x + width + 3, y - 3 + 1, 1347420415, 1347420415, zLevel);
		drawGradientRect(x - 3, y + height + 2, x + width + 3, y + height + 3, 1344798847, 1344798847, zLevel);
	}

	/**
	 * set the current color
	 * 
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @deprecated will be removed in next version
	 */
	@Deprecated
	public static void color3f(float r, float g, float b) {
		GL11.glColor4f(r, g, b, 1.0f);
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
	 * @see #drawCenterString(Font, String, int, int, int, int)
	 * @see #drawRightString(Font, String, int, int, int)
	 */
	public static void drawCenterString(Font font, String text, int x, int y, int color) {
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
	 * @see #drawCenterString(Font, String, int, int, int)
	 * @see #drawString(Font, String, int, int, int, int)
	 */
	public static void drawCenterString(Font font, String text, int x, int y, int color, int height) {
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
		float alpha1 = (float) (startColor >> 24 & 255) / 255.0F;
		float red1 = (float) (startColor >> 16 & 255) / 255.0F;
		float green1 = (float) (startColor >> 8 & 255) / 255.0F;
		float blue1 = (float) (startColor & 255) / 255.0F;
		float alpha2 = (float) (endColor >> 24 & 255) / 255.0F;
		float red2 = (float) (endColor >> 16 & 255) / 255.0F;
		float green2 = (float) (endColor >> 8 & 255) / 255.0F;
		float blue2 = (float) (endColor & 255) / 255.0F;
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		// func_225582_a_ = pos func_227885_a_ = color
		bufferbuilder.vertex((double) right, (double) top, (double) zLevel).color(red1, green1, blue1, alpha1)
				.endVertex();
		bufferbuilder.vertex((double) left, (double) top, (double) zLevel).color(red1, green1, blue1, alpha1)
				.endVertex();
		bufferbuilder.vertex((double) left, (double) bottom, (double) zLevel).color(red2, green2, blue2, alpha2)
				.endVertex();
		bufferbuilder.vertex((double) right, (double) bottom, (double) zLevel).color(red2, green2, blue2, alpha2)
				.endVertex();
		tesselator.end();
		RenderSystem.disableBlend();
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
		float alphaLeft = (float) (leftColor >> 24 & 255) / 255.0F;
		float redLeft = (float) (leftColor >> 16 & 255) / 255.0F;
		float greenLeft = (float) (leftColor >> 8 & 255) / 255.0F;
		float blueLeft = (float) (leftColor & 255) / 255.0F;
		float alphaTop = (float) (topColor >> 24 & 255) / 255.0F;
		float redTop = (float) (topColor >> 16 & 255) / 255.0F;
		float greenTop = (float) (topColor >> 8 & 255) / 255.0F;
		float blueTop = (float) (topColor & 255) / 255.0F;
		float alphaRight = (float) (rightColor >> 24 & 255) / 255.0F;
		float redRight = (float) (rightColor >> 16 & 255) / 255.0F;
		float greenRight = (float) (rightColor >> 8 & 255) / 255.0F;
		float blueRight = (float) (rightColor & 255) / 255.0F;
		float alphaBottom = (float) (bottomColor >> 24 & 255) / 255.0F;
		float redBottom = (float) (bottomColor >> 16 & 255) / 255.0F;
		float greenBottom = (float) (bottomColor >> 8 & 255) / 255.0F;
		float blueBottom = (float) (bottomColor & 255) / 255.0F;
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		bufferbuilder.vertex((double) right, (double) top, (double) zLevel)
				.color(redLeft, greenLeft, blueLeft, alphaLeft).endVertex();
		bufferbuilder.vertex((double) left, (double) top, (double) zLevel).color(redTop, greenTop, blueTop, alphaTop)
				.endVertex();
		bufferbuilder.vertex((double) left, (double) bottom, (double) zLevel)
				.color(redRight, greenRight, blueRight, alphaRight).endVertex();
		bufferbuilder.vertex((double) right, (double) bottom, (double) zLevel)
				.color(redBottom, greenBottom, blueBottom, alphaBottom).endVertex();
		tesselator.end();
		RenderSystem.disableBlend();
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
	public static void drawRect(PoseStack stack, int left, int top, int right, int bottom, int color) {
		Gui.fill(stack, left, top, right, bottom, color);
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
	public static void drawRelative(PoseStack stack, AbstractWidget field, int offsetX, int offsetY, int mouseX,
			int mouseY, float partialTicks) {
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
	 * @param widget       the widget
	 * @param offsetX      the x offset
	 * @param offsetY      the y offset
	 * @param mouseX       the mouse X location
	 * @param mouseY       the mouse Y location
	 * @param partialTicks the partialTicks of the render
	 * 
	 * @since 2.0
	 */
	public static void drawRelativeToolTip(PoseStack stack, AbstractWidget widget, int offsetX, int offsetY, int mouseX,
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
	 * @see #drawCenterString(Font, String, int, int, int)
	 * @see #drawRightString(Font, String, int, int, int, int)
	 */

	public static void drawRightString(Font font, String text, int x, int y, int color) {
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
	 * @see #drawString(Font, String, int, int, int, int)
	 * @see #drawCenterString(Font, String, int, int, int, int)
	 */
	public static void drawRightString(Font font, String text, int x, int y, int color, int height) {
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
	 * @see #drawRightString(Font, String, int, int, int)
	 * @see #drawRightString(Font, String, int, int, int, int)
	 */
	public static void drawRightString(Font font, String text, AbstractWidget field, int color) {
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
	 * @see #drawRightString(Font, String, Widget, int)
	 */
	public static void drawRightString(Font font, String text, AbstractWidget field, int color, int offsetX,
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
		drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight, 0xffffff);
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
	 * @param color      tile color
	 */
	public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
			int height, float tileWidth, float tileHeight, int color) {
		drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight, color, false);
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
	 * @param color      tile color
	 * @param useAlpha   use the alpha of the color
	 */
	public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
			int height, float tileWidth, float tileHeight, int color, boolean useAlpha) {
		float scaleX = 1.0F / tileWidth;
		float scaleY = 1.0F / tileHeight;
		int red = (color >> 16) & 0xFF;
		int green = (color >> 8) & 0xFF;
		int blue = color & 0xFF;
		int alpha = useAlpha ? (color >> 24) : 0xff;
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferbuilder.vertex((double) x, (double) (y + height), 0.0D)
				.uv((float) (u * scaleX), (float) ((v + (float) vHeight) * scaleY)).color(red, green, blue, alpha)
				.endVertex();
		bufferbuilder.vertex((double) (x + width), (double) (y + height), 0.0D)
				.uv((float) ((u + (float) uWidth) * scaleX), (float) ((v + (float) vHeight) * scaleY))
				.color(red, green, blue, alpha).endVertex();
		bufferbuilder.vertex((double) (x + width), (double) y, 0.0D)
				.uv((float) ((u + (float) uWidth) * scaleX), (float) (v * scaleY)).color(red, green, blue, alpha)
				.endVertex();
		bufferbuilder.vertex((double) x, (double) y, 0.0D).uv((float) (u * scaleX), (float) (v * scaleY))
				.color(red, green, blue, alpha).endVertex();
		tesselator.end();
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
	 * @see #drawCenterString(Font, String, int, int, int, int)
	 * @see #drawRightString(Font, String, int, int, int, int)
	 */
	public static void drawString(Font font, String text, int x, int y, int color, int height) {
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
	public static void drawTextBox(Font font, int x, int y, int parentWidth, int parentHeight, float zLevel,
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
	public static boolean isHover(AbstractWidget widget, int mouseX, int mouseY) {
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

	public static float clamp(float v, float min, float max) {
		return v < min ? min : v > max ? max : v;
	}

	public static int clamp(int v, int min, int max) {
		return v < min ? min : v > max ? max : v;
	}
}
