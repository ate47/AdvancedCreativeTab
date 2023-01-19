package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLongArray;

public class NBTLongArrayElement extends NBTElement {
	private NBTTagLongArray value;

	public NBTLongArrayElement(GuiListModifier<?> parent, String key, NBTTagLongArray value) {
		super(parent, key, 200, 21);
		this.value = value;
	}

	@Override
	public NBTElement clone() {
		return new NBTLongArrayElement(parent, key, value.copy());
	}

	@Override
	public NBTBase get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor.longArray");
	}

}