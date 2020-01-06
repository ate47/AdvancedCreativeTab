package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagShort;

public class NBTShortElement extends NBTNumericElement<Short> {

	public NBTShortElement(GuiListModifier<?> parent, String key, int sizeX, int sizeY, Short value) {
		super("short", parent, key, sizeX, sizeY, value);
	}

	public NBTShortElement(GuiListModifier<?> parent, String key, Short value) {
		this(parent, key, 200, 21, value);
	}

	@Override
	public INBTBase get(Short value) {
		return new NBTTagShort(value);
	}
	
	@Override
	public void setNull() {
		setValue((short) 0);
	}

	@Override
	public Short parseValue(String text) {
		return text.isEmpty() ? 0 : Short.parseShort(text);
	}

}