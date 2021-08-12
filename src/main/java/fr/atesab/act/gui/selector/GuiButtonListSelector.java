package fr.atesab.act.gui.selector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import fr.atesab.act.gui.GuiValueButton;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class GuiButtonListSelector<T> extends GuiListSelector<T> {

	static class SelectorListElement<T> extends ListElement {
		private Button button;

		public SelectorListElement(GuiButtonListSelector<T> parent, Tuple<String, T> element) {
			super(201, 21);
			buttonList.add(button = new GuiValueButton<T>(0, 0, new TextComponent(element.a), element.b,
					b -> parent.select(b.getValue())));
		}

		@Override
		public boolean match(String search) {
			return button.getMessage().getString().toLowerCase().contains(search.toLowerCase());
		}

	}

	@SuppressWarnings("unchecked")
	public GuiButtonListSelector(Screen parent, Component name, List<Tuple<String, T>> elements,
			Function<T, Screen> setter) {
		super(parent, name, new ArrayList<>(), setter, false, new Tuple[0]);
		if (elements != null)
			setElements(elements);
	}

	public void setElements(List<Tuple<String, T>> elements) {
		elements.forEach(tuple -> addListElement(new SelectorListElement<T>(this, tuple)));
	}
}
