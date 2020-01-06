package fr.atesab.act.gui.modifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

/**
 * Boolean value button setter with color and text (for color blind)
 */
public class GuiBooleanButton extends Button {
	private Consumer<Boolean> setter;
	private Supplier<Boolean> getter;
	private String text;

	public GuiBooleanButton(int x, int y, int widthIn, int heightIn, String buttonText,
			Consumer<Boolean> setter, Supplier<Boolean> getter) {
		super(x, y, widthIn, heightIn, buttonText, GuiUtils.EMPTY_PRESS);
		this.setter = setter;
		this.getter = getter;
		this.text = buttonText;
		updateDisplay();
	}

	public GuiBooleanButton(int x, int y, String buttonText, Consumer<Boolean> setter,
			Supplier<Boolean> getter) {
		super(x, y, 200, 20, buttonText, GuiUtils.EMPTY_PRESS);
		this.setter = setter;
		this.getter = getter;
		this.text = buttonText;
		updateDisplay();
	}

	private void updateDisplay() {
		boolean v = getter.get();
		packedFGColor = GuiUtils.getRedGreen(v);
		setMessage(text + " (" + I18n.format(v ? "gui.act.yes" : "gui.act.no") + ")");
	}

	@Override
	public void onPress() {
		boolean newValue = !getter.get();
		setter.accept(newValue);
		updateDisplay();
	}
}
