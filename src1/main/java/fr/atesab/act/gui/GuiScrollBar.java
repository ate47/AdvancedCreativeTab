package fr.atesab.act.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiScrollBar extends Gui {
	protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
	public int ValueMin;
	public int ValueMax;
	public int Value;
	public int height;
	public int width;
	public int xPosition;
	public int yPosition;
	public boolean visible;
	public int id;
	public boolean enabled;
	protected boolean hovered;
	public boolean dragging;

	public GuiScrollBar(int id, int xPosition, int yPosition, int width, int height) {
		this(id, xPosition, yPosition, width, height, 0, 100, 0);
	}

	public GuiScrollBar(int id, int xPosition, int yPosition, int width, int height, int value) {
		this(id, xPosition, yPosition, width, height, 0, 100, value);
	}

	public GuiScrollBar(int id, int xPosition, int yPosition, int width, int height, int min, int max) {
		this(id, xPosition, yPosition, width, height, min, max, min);
	}

	public GuiScrollBar(int id, int xPosition, int yPosition, int width, int height, int min, int max, int value) {
		this.id = id;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.height = height;
		this.width = width;
		ValueMin = min;
		ValueMax = max;
	}

	public void drawScrollBar(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			if (dragging) {
				Value = ((mouseX - (xPosition + 4)) / (width - 8));
				Value = MathHelper.clamp(Value, ValueMin, ValueMax);
			}
			mc.getTextureManager().bindTexture(buttonTextures);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(this.xPosition + (int) (this.Value * (float) (this.width - 8)), this.yPosition,
					0, 66, 4, 20);
			this.drawTexturedModalRect(this.xPosition + (int) (this.Value * (float) (this.width - 8)) + 4,
					this.yPosition, 196, 66, 4, 20);
		}
	}

	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if ((enabled) && (visible) && (mouseX >= xPosition) && (mouseY >= yPosition) && (mouseX < xPosition + width)
				&& (mouseY < yPosition + height)) {
			Value = ((mouseX - (xPosition + 4)) / (width - 8));
			Value = MathHelper.clamp(Value, ValueMin, ValueMax);
			dragging = true;
			return true;
		}

		return false;
	}

	public void mouseReleased(int mouseX, int mouseY) {
		dragging = false;
	}
}
