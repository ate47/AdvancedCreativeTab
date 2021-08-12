package fr.atesab.act.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.gui.ItemStackButtonWidget.ITooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class GuiACT extends Screen implements ITooltipRenderer {
	public static void playClick() {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	protected Screen parent;
	protected Minecraft mc;

	public GuiACT(Screen parent, Component name) {
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
	public void renderTooltip1(PoseStack matrixStack, ItemStack stack, int mouseX, int mouseY) {
		super.renderTooltip(matrixStack, stack, mouseX, mouseY);
	}
}
