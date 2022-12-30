package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.components.ACTButton;
import fr.atesab.act.gui.modifier.GuiColorModifier;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class NBTIntegerElement extends NBTNumericElement<Integer> {

    @SuppressWarnings("deprecation")
    public NBTIntegerElement(GuiListModifier<?> parent, String key, int value) {
        super("int", parent, key, 200, 42, value);
        buttonList.add(new ACTButton(0, 21, 100, 20, Component.translatable("gui.act.modifier.meta.setColor"), b -> mc.setScreen(new GuiColorModifier(parent, this::updateValue, getValue(), 0xffffff))));
        buttonList.add(new ACTButton(101, 21, 99, 20, Component.translatable("gui.act.modifier.ench"), b -> {
            List<Tuple<String, Integer>> list = new ArrayList<>();
            Registry.ENCHANTMENT.forEach(e -> {
                int id = Registry.ENCHANTMENT.getId(e);
                list.add(new Tuple<>(I18n.get(e.getDescriptionId()) + " (" + id + ")", id));
            });
            mc.setScreen(new GuiButtonListSelector<>(parent, Component.translatable("gui.act.modifier.ench"),
                    list, i -> {
                updateValue(i);
                return null;
            }));
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
