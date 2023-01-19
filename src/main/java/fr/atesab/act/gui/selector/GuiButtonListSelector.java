package fr.atesab.act.gui.selector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import fr.atesab.act.gui.GuiValueButton;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiButtonListSelector<T> extends GuiListSelector<T> {

	static class SelectorListElement<T> extends ListElement {
		private GuiButtonListSelector parent;
		private GuiButton button;

		public SelectorListElement(GuiButtonListSelector parent, Tuple<String, T> element) {
			super(201, 21);
			this.parent = parent;
			buttonList.add(button = new GuiValueButton<T>(0, 0, 0, element.a, element.b));
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			parent.select(((GuiValueButton<T>) button).getValue());
			super.actionPerformed(button);
		}

		@Override
		public boolean match(String search) {
			return button.displayString.toLowerCase().contains(search.toLowerCase());
		}

	}

	public GuiButtonListSelector(GuiScreen parent, List<Tuple<String, T>> elements, Function<T, GuiScreen> setter) {
		super(parent, new ArrayList<>(), setter, false);
		if (elements != null)
			setElements(elements);
	}

	public void setElements(List<Tuple<String, T>> elements) {
		elements.forEach(tuple -> this.elements.add(new SelectorListElement<T>(this, tuple)));
	}
}
