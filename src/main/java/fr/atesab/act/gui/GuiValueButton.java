package fr.atesab.act.gui;

import java.util.function.Consumer;

import net.minecraft.client.gui.widget.button.Button;

public class GuiValueButton<T> extends Button {
	private T Value;

	@SuppressWarnings("unchecked")
	public GuiValueButton(int x, int y, int widthIn, int heightIn, String buttonText, T value,
			Consumer<GuiValueButton<T>> action) {
		super(x, y, widthIn, heightIn, buttonText, b -> action.accept((GuiValueButton<T>) b));
		Value = value;
	}

	@SuppressWarnings("unchecked")
	public GuiValueButton(int x, int y, String buttonText, T value, Consumer<GuiValueButton<T>> action) {
		super(x, y, 200, 20, buttonText, b -> action.accept((GuiValueButton<T>) b));
		Value = value;
	}

	public T getValue() {
		return Value;
	}

	public void setValue(T value) {
		Value = value;
	}
}