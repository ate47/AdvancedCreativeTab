package fr.atesab.act.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;

public class GuiACT extends GuiScreen {
	public static void playClick() {
		Minecraft.getInstance().getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	protected GuiScreen parent;

	public GuiACT(GuiScreen parent) {
		this.parent = parent;
	}

	public GuiScreen getParent() {
		return parent;
	}

	public void setParent(GuiScreen parent) {
		this.parent = parent;
	}

}
