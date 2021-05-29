package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.INBT;

public class NBTByteElement extends NBTNumericElement<Byte> {

	public NBTByteElement(GuiListModifier<?> parent, String key, Byte value) {
		this(parent, key, 200, 21, value);
	}

	public NBTByteElement(GuiListModifier<?> parent, String key, int sizeX, int sizeY, Byte value) {
		super("byte", parent, key, sizeX, sizeY, value);
	}

	@Override
	public INBT get(Byte value) {
		return ByteNBT.valueOf(value);
	}

	@Override
	public void setNull() {
		setValue((byte) 0);
	}

	@Override
	public Byte parseValue(String text) {
		return text.isEmpty() ? 0 : Byte.parseByte(text);
	}

}