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
		private GuiButton button;
		public SelectorListElement(GuiButtonListSelector<T> parent, Tuple<String, T> element) {
			super(201, 21);
			buttonList.add(button = new GuiValueButton<T>(0, 0, 0, element.a, element.b) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					parent.select(getValue());
					super.onClick(mouseX, mouseY);
				}
			});
		}

		@Override
		public boolean match(String search) {
			return button.displayString.toLowerCase().contains(search.toLowerCase());
		}

	}

	@SuppressWarnings("unchecked")
	public GuiButtonListSelector(GuiScreen parent, List<Tuple<String, T>> elements, Function<T, GuiScreen> setter) {
		super(parent, new ArrayList<>(), setter, false, new Tuple[0]);
		if (elements != null)
			setElements(elements);
	}

	public void setElements(List<Tuple<String, T>> elements) {
		elements.forEach(tuple -> this.elements.add(new SelectorListElement<T>(this, tuple)));
	}
}
