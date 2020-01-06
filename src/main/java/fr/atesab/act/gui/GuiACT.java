package fr.atesab.act.gui;

import fr.atesab.act.ACTMod;
import fr.atesab.act.command.ModdedCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;

public class GuiACT extends Screen {
	public static void playClick() {
		Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	protected Screen parent;
	protected Minecraft mc;

	public GuiACT(Screen parent) {
		super(ModdedCommand.createText(ACTMod.MOD_NAME, TextFormatting.RED));
		this.parent = parent;
		mc = Minecraft.getInstance();
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
}
