package fr.atesab.act.gui;

import java.util.function.Consumer;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class GuiValueButton<T> extends Button {
	private T value;

	@SuppressWarnings("unchecked")
	public GuiValueButton(int x, int y, int widthIn, int heightIn, ITextComponent buttonText, T value,
			Consumer<GuiValueButton<T>> action) {
		super(x, y, widthIn, heightIn, buttonText, b -> action.accept((GuiValueButton<T>) b));
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public GuiValueButton(int x, int y, ITextComponent buttonText, T value, Consumer<GuiValueButton<T>> action) {
		super(x, y, 200, 20, buttonText, b -> action.accept((GuiValueButton<T>) b));
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}