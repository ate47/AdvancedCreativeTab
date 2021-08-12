package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.LongTag;

public class NBTLongElement extends NBTNumericElement<Long> {

	public NBTLongElement(GuiListModifier<?> parent, String key, int sizeX, int sizeY, Long value) {
		super("long", parent, key, sizeX, sizeY, value);
	}

	public NBTLongElement(GuiListModifier<?> parent, String key, Long value) {
		this(parent, key, 200, 21, value);
	}

	@Override
	public Tag get(Long value) {
		return LongTag.valueOf(value);
	}

	@Override
	public void setNull() {
		setValue(0L);
	}

	@Override
	public Long parseValue(String text) {
		return text.isEmpty() ? 0 : Long.parseLong(text);
	}

}