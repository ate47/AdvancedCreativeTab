package fr.atesab.act.gui.modifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * Boolean value button setter with color and text (for color blind)
 */
public class GuiBooleanButton extends Button {
	private static final Component YES = new TranslatableComponent("gui.act.yes");
	private static final Component NO = new TranslatableComponent("gui.act.no");
	private Consumer<Boolean> setter;
	private Supplier<Boolean> getter;
	private Component textYes;
	private Component textNo;

	public GuiBooleanButton(int x, int y, int widthIn, int heightIn, Component buttonText, Consumer<Boolean> setter,
			Supplier<Boolean> getter) {
		super(x, y, widthIn, heightIn, buttonText, GuiUtils.EMPTY_PRESS);
		this.setter = setter;
		this.getter = getter;
		this.textYes = buttonText.copy().append(" (").append(YES).append(")");
		this.textNo = buttonText.copy().append(" (").append(NO).append(")");
		updateDisplay();
	}

	public GuiBooleanButton(int x, int y, Component buttonText, Consumer<Boolean> setter, Supplier<Boolean> getter) {
		super(x, y, 200, 20, buttonText, GuiUtils.EMPTY_PRESS);
		this.setter = setter;
		this.getter = getter;
		this.textYes = buttonText.copy().append(" (").append(YES).append(")");
		this.textNo = buttonText.copy().append(" (").append(NO).append(")");
		updateDisplay();
	}

	private void updateDisplay() {
		boolean v = getter.get();
		packedFGColor = GuiUtils.getRedGreen(v);
		if (v)
			setMessage(textYes);
		else
			setMessage(textNo);
	}

	@Override
	public void onPress() {
		boolean newValue = !getter.get();
		setter.accept(newValue);
		updateDisplay();
	}
}
