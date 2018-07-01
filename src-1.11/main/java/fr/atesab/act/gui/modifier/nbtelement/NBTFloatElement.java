package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagFloat;

public class NBTFloatElement extends NBTNumericElement<Float> {

	public NBTFloatElement(GuiListModifier<?> parent, String key, Float value) {
		this(parent, key, 200, 21, value);
	}

	public NBTFloatElement(GuiListModifier<?> parent, String key, int sizeX, int sizeY, Float value) {
		super("double", parent, key, sizeX, sizeY, value);
	}

	@Override
	public NBTBase get(Float value) {
		return new NBTTagFloat(value);
	}

	@Override
	public Float parseValue(String text) {
		return text.isEmpty() ? 0 : Float.parseFloat(text);
	}

}