package fr.atesab.act.gui.selector;

import java.util.List;
import java.util.function.Consumer;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;

public class GuiListSelector<T> extends GuiListModifier<T> {

	public GuiListSelector(GuiScreen parent, List<ListElement> elements, Consumer<T> setter, boolean doneButton,
			boolean cancelButton, Tuple<String, Tuple<Runnable, Runnable>>... buttons) {
		super(parent, elements, setter, doneButton, cancelButton, buttons);
	}

	public GuiListSelector(GuiScreen parent, List<ListElement> elements, Consumer<T> setter, boolean doneButton,
			Tuple<String, Tuple<Runnable, Runnable>>... buttons) {
		super(parent, elements, setter, doneButton, buttons);
	}

	public GuiListSelector(GuiScreen parent, List<ListElement> elements, Consumer<T> setter,
			Tuple<String, Tuple<Runnable, Runnable>>... buttons) {
		super(parent, elements, setter, buttons);
	}

	@Override
	protected T get() {
		return null;
	}

	public void select(T t) {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		set(t);
		mc.displayGuiScreen(parent);
	}

}
