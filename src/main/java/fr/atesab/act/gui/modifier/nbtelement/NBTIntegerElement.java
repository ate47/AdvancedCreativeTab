package fr.atesab.act.gui.modifier.nbtelement;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.gui.modifier.GuiColorModifier;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.registry.IRegistry;

public class NBTIntegerElement extends NBTNumericElement<Integer> {

	public NBTIntegerElement(GuiListModifier<?> parent, String key, int value) {
		super("int", parent, key, 200, 42, value);
		buttonList.add(new GuiButton(0, 0, 21, 100, 20, I18n.format("gui.act.modifier.meta.setColor")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new GuiColorModifier(parent, c -> updateValue(c), getValue(), 0xffffff));
				super.onClick(mouseX, mouseY);
			}
		});
		buttonList.add(new GuiButton(1, 101, 21, 99, 20, I18n.format("gui.act.modifier.ench")) {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(double mouseX, double mouseY) {
				List<Tuple<String, Integer>> list = new ArrayList<>();
				IRegistry.field_212628_q.forEach(e -> { // Enchantment.REGISTRY
					int id = IRegistry.field_212628_q.getId(e);
					list.add(new Tuple<>(I18n.format(e.getName()) + " (" + id + ")", id));
				});
				mc.displayGuiScreen(new GuiButtonListSelector<Integer>(parent, list, i -> {
					updateValue(i);
					return null;
				}));
				super.onClick(mouseX, mouseY);
			}
		});
	}

	@Override
	public INBTBase get(Integer value) {
		return new NBTTagInt(value);
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
