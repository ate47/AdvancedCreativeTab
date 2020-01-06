package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagDouble;

public class NBTDoubleElement extends NBTNumericElement<Double> {

	public NBTDoubleElement(GuiListModifier<?> parent, String key, Double value) {
		this(parent, key, 200, 21, value);
	}

	public NBTDoubleElement(GuiListModifier<?> parent, String key, int sizeX, int sizeY, Double value) {
		super("double", parent, key, sizeX, sizeY, value);
	}

	@Override
	public INBTBase get(Double value) {
		return new NBTTagDouble(value);
	}

	@Override
	public Double parseValue(String text) {
		return text.isEmpty() ? 0 : Double.parseDouble(text);
	}

	@Override
	public void setNull() {
		setValue(0.0);
	}

}