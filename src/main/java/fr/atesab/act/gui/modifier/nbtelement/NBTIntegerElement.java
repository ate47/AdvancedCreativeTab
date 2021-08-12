package fr.atesab.act.gui.modifier.nbtelement;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.gui.modifier.GuiColorModifier;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.TranslatableComponent;

public class NBTIntegerElement extends NBTNumericElement<Integer> {

	@SuppressWarnings("deprecation")
	public NBTIntegerElement(GuiListModifier<?> parent, String key, int value) {
		super("int", parent, key, 200, 42, value);
		buttonList.add(new Button(0, 21, 100, 20, new TranslatableComponent("gui.act.modifier.meta.setColor"), b -> {
			mc.setScreen(new GuiColorModifier(parent, c -> updateValue(c), getValue(), 0xffffff));
		}));
		buttonList.add(new Button(101, 21, 99, 20, new TranslatableComponent("gui.act.modifier.ench"), b -> {
			List<Tuple<String, Integer>> list = new ArrayList<>();
			Registry.ENCHANTMENT.forEach(e -> {
				int id = Registry.ENCHANTMENT.getId(e);
				list.add(new Tuple<>(I18n.get(e.getDescriptionId()) + " (" + id + ")", id));
			});
			mc.setScreen(new GuiButtonListSelector<Integer>(parent, new TranslatableComponent("gui.act.modifier.ench"),
					list, i -> {
						updateValue(i);
						return null;
					}));
			return;
		}));
	}

	@Override
	public Tag get(Integer value) {
		return IntTag.valueOf(value);
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
