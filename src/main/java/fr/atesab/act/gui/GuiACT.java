package fr.atesab.act.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.atesab.act.gui.ItemStackButtonWidget.ITooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;

public class GuiACT extends Screen implements ITooltipRenderer {
	public static void playClick() {
		Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	protected Screen parent;
	protected Minecraft mc;

	public GuiACT(Screen parent, ITextComponent name) {
		super(name);
		this.parent = parent;
		mc = Minecraft.getInstance();
	}

	/**
	 * @return the formatted title
	 */
	public String getStringTitle() {
		return super.title.getString();
	}

	public Screen getParent() {
		return parent;
	}

	public void setParent(Screen parent) {
		this.parent = parent;
	}

	public float getZLevel() {
		return getBlitOffset();
	}

	@Override
	public Minecraft getMinecraft() {
		return mc;
	}

	@Override
	public void renderTooltip1(MatrixStack matrixStack, ItemStack stack, int mouseX, int mouseY) {
		super.renderTooltip(matrixStack, stack, mouseX, mouseY);
	}
}
