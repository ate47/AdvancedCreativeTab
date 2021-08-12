package fr.atesab.act.gui.modifier;

import java.util.function.Consumer;

import fr.atesab.act.gui.GuiACT;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GuiModifier<T> extends GuiACT {

	protected Consumer<T> setter;

	public GuiModifier(Screen parent, Component name, Consumer<T> setter) {
		super(parent, name);
		this.setter = setter;
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	public void set(T value) {
		setter.accept(value);
	}

	public void setSetter(Consumer<T> setter) {
		this.setter = setter;
	}
}
