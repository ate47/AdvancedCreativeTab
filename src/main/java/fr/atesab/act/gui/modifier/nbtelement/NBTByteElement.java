package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;

public class NBTByteElement extends NBTNumericElement<Byte> {

	public NBTByteElement(GuiListModifier<?> parent, String key, Byte value) {
		this(parent, key, 200, 21, value);
	}

	public NBTByteElement(GuiListModifier<?> parent, String key, int sizeX, int sizeY, Byte value) {
		super("byte", parent, key, sizeX, sizeY, value);
	}

	@Override
	public NBTBase get(Byte value) {
		return new NBTTagByte(value);
	}

	@Override
	public Byte parseValue(String text) {
		return text.isEmpty() ? 0 : Byte.parseByte(text);
	}

}