package fr.atesab.act.gui;

import net.minecraft.client.gui.GuiButton;

public class GuiValueButton<T> extends GuiButton {
	private T value;

	public GuiValueButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, T value) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.value = value;
	}

	public GuiValueButton(int buttonId, int x, int y, String buttonText, T value) {
		super(buttonId, x, y, buttonText);
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
