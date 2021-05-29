package fr.atesab.act.gui.modifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Boolean value button setter with color and text (for color blind)
 */
public class GuiBooleanButton extends Button {
	private static final ITextComponent YES = new TranslationTextComponent("gui.act.yes");
	private static final ITextComponent NO = new TranslationTextComponent("gui.act.no");
	private Consumer<Boolean> setter;
	private Supplier<Boolean> getter;
	private ITextComponent textYes;
	private ITextComponent textNo;

	public GuiBooleanButton(int x, int y, int widthIn, int heightIn, ITextComponent buttonText,
			Consumer<Boolean> setter, Supplier<Boolean> getter) {
		super(x, y, widthIn, heightIn, buttonText, GuiUtils.EMPTY_PRESS);
		this.setter = setter;
		this.getter = getter;
		this.textYes = buttonText.copy().append(" (").append(YES).append(")");
		this.textNo = buttonText.copy().append(" (").append(NO).append(")");
		updateDisplay();
	}

	public GuiBooleanButton(int x, int y, ITextComponent buttonText, Consumer<Boolean> setter,
			Supplier<Boolean> getter) {
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
