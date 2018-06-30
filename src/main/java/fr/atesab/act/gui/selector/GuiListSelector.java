package fr.atesab.act.gui.selector;

import java.util.List;
import java.util.function.Function;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;

public class GuiListSelector<T> extends GuiListModifier<T> {
	private Function<T, GuiScreen> selector;

	public GuiListSelector(GuiScreen parent, List<ListElement> elements, Function<T, GuiScreen> setter,
			boolean doneButton, boolean cancelButton, Tuple<String, Tuple<Runnable, Runnable>>... buttons) {
		super(parent, elements, t -> setter.apply(t), doneButton, cancelButton, buttons);
		this.selector = setter;
	}

	public GuiListSelector(GuiScreen parent, List<ListElement> elements, Function<T, GuiScreen> setter,
			boolean doneButton, Tuple<String, Tuple<Runnable, Runnable>>... buttons) {
		super(parent, elements, t -> setter.apply(t), doneButton, buttons);
		this.selector = setter;
	}

	public GuiListSelector(GuiScreen parent, List<ListElement> elements, Function<T, GuiScreen> setter,
			Tuple<String, Tuple<Runnable, Runnable>>... buttons) {
		super(parent, elements, t -> setter.apply(t), buttons);
		this.selector = setter;
	}

	@Override
	protected T get() {
		return null;
	}

	public void select(T t) {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		GuiScreen screen = selector.apply(t);
		mc.displayGuiScreen(screen == null ? parent : screen);
	}

}
