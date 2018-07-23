package fr.atesab.act.gui.modifier.nbtelement;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.gui.modifier.GuiColorModifier;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;

public class NBTIntegerElement extends NBTNumericElement<Integer> {

	public NBTIntegerElement(GuiListModifier<?> parent, String key, int value) {
		super("int", parent, key, 200, 42, value);
		buttonList.add(new GuiButton(0, 0, 21, 100, 20, I18n.format("gui.act.modifier.meta.setColor")));
		buttonList.add(new GuiButton(1, 101, 21, 99, 20, I18n.format("gui.act.modifier.ench")));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			mc.displayGuiScreen(new GuiColorModifier(parent, c -> updateValue(c), getValue(), 0xffffff));
			break;
		case 1: {
			List<Tuple<String, Integer>> list = new ArrayList<>();
			Enchantment.REGISTRY.forEach(e -> {
				int id = Enchantment.getEnchantmentID((Enchantment) e);
				list.add(new Tuple(I18n.format(((Enchantment) e).getName()) + " (" + id + ")", id));
			});
			mc.displayGuiScreen(new GuiButtonListSelector<Integer>(parent, list, i -> {
				updateValue(i);
				return null;
			}));
		}
			break;
		}
		super.actionPerformed(button);
	}

	@Override
	public NBTBase get(Integer value) {
		return new NBTTagInt(value);
	}

	@Override
	public Integer parseValue(String text) {
		return text.isEmpty() ? 0 : Integer.parseInt(text);
	}
}
