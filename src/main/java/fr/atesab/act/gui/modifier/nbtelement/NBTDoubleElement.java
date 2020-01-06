package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.INBT;

public class NBTDoubleElement extends NBTNumericElement<Double> {

	public NBTDoubleElement(GuiListModifier<?> parent, String key, Double value) {
		this(parent, key, 200, 21, value);
	}

	public NBTDoubleElement(GuiListModifier<?> parent, String key, int sizeX, int sizeY, Double value) {
		super("double", parent, key, sizeX, sizeY, value);
	}

	@Override
	public INBT get(Double value) {
		return new DoubleNBT(value);
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