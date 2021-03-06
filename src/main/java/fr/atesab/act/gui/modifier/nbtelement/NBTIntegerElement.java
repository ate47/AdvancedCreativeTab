package fr.atesab.act.gui.modifier.nbtelement;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.gui.modifier.GuiColorModifier;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class NBTIntegerElement extends NBTNumericElement<Integer> {

	@SuppressWarnings("deprecation")
	public NBTIntegerElement(GuiListModifier<?> parent, String key, int value) {
		super("int", parent, key, 200, 42, value);
		buttonList.add(new Button(0, 21, 100, 20, new TranslationTextComponent("gui.act.modifier.meta.setColor"), b -> {
			mc.setScreen(new GuiColorModifier(parent, c -> updateValue(c), getValue(), 0xffffff));
		}));
		buttonList.add(new Button(101, 21, 99, 20, new TranslationTextComponent("gui.act.modifier.ench"), b -> {
			List<Tuple<String, Integer>> list = new ArrayList<>();
			Registry.ENCHANTMENT.forEach(e -> {
				int id = Registry.ENCHANTMENT.getId(e);
				list.add(new Tuple<>(I18n.get(e.getDescriptionId()) + " (" + id + ")", id));
			});
			mc.setScreen(new GuiButtonListSelector<Integer>(parent,
					new TranslationTextComponent("gui.act.modifier.ench"), list, i -> {
						updateValue(i);
						return null;
					}));
			return;
		}));
	}

	@Override
	public INBT get(Integer value) {
		return IntNBT.valueOf(value);
	}

	@Override
	public void setNull() {
		setValue(0);
	}

	@Override
	public Integer parseValue(String text) {
		return text.isEmpty() ? 0 : Integer.parseInt(text);
	}
}
