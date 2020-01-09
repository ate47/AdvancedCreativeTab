package fr.atesab.act.gui;

import fr.atesab.act.gui.ItemStackButtonWidget.ITooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiACT extends Screen implements ITooltipRenderer {
	public static void playClick() {
		Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	protected Screen parent;
	protected Minecraft mc;

	public GuiACT(Screen parent, String name) {
		super(new TranslationTextComponent(name));
		this.parent = parent;
		mc = Minecraft.getInstance();
	}

	/**
	 * @return the formatted title
	 */
	public String getStringTitle() {
		return super.getTitle().getFormattedText();
	}

	public Screen getParent() {
		return parent;
	}

	public void setParent(Screen parent) {
		this.parent = parent;
	}

	public float getZLevel() {
		return blitOffset;
	}

	@Override
	public Minecraft getMinecraft() {
		return mc;
	}

	@Override
	public void renderTooltip(ItemStack stack, int mouseX, int mouseY) {
		super.renderTooltip(stack, mouseX, mouseY);
	}
}
