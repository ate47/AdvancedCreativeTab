package fr.atesab.act.gui.selector;

import java.util.List;
import java.util.function.Function;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.screen.Screen;

public class GuiListSelector<T> extends GuiListModifier<T> {
	private Function<T, Screen> selector;

	public GuiListSelector(Screen parent, String name, List<ListElement> elements, Function<T, Screen> setter, boolean doneButton,
			boolean cancelButton, Tuple<String, Tuple<Runnable, Runnable>>[] buttons) {
		super(parent, name, elements, t -> setter.apply(t), doneButton, cancelButton, buttons);
		this.selector = setter;
	}

	public GuiListSelector(Screen parent, String name, List<ListElement> elements, Function<T, Screen> setter, boolean doneButton,
			Tuple<String, Tuple<Runnable, Runnable>>[] buttons) {
		super(parent, name, elements, t -> setter.apply(t), doneButton, buttons);
		this.selector = setter;
	}

	public GuiListSelector(Screen parent, String name, List<ListElement> elements, Function<T, Screen> setter,
			Tuple<String, Tuple<Runnable, Runnable>>[] buttons) {
		super(parent, name, elements, t -> setter.apply(t), buttons);
		this.selector = setter;
	}

	@Override
	protected T get() {
		return null;
	}

	public void select(T t) {
		playClick();
		Screen screen = selector.apply(t);
		getMinecraft().displayGuiScreen(screen == null ? parent : screen);
	}

}
