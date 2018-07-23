package fr.atesab.act;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiScreen;

public class StringModifier {
	private String string;
	private Consumer<String> setter;
	private GuiScreen nextScreen;

	public String getString() {
		return string;
	}

	public void setString(String string) {
		setter.accept(string);
		this.string = string;
	}

	public GuiScreen getNextScreen() {
		return nextScreen;
	}

	public void setNextScreen(GuiScreen nextScreen) {
		this.nextScreen = nextScreen;
	}

	public StringModifier(String string, GuiScreen nextScreen, Consumer<String> setter) {
		this.string = string;
		this.nextScreen = nextScreen;
		this.setter = setter;
	}

}
