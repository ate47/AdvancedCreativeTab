package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBT;

public class NBTUnknownElement extends NBTElement {
	private INBT value;

	public NBTUnknownElement(GuiListModifier<?> parent, String key, INBT value) {
		super(parent, key, 200, 21);
		this.value = value;
	}

	@Override
	public NBTElement clone() {
		return new NBTUnknownElement(parent, key, value.copy());
	}

	@Override
	public INBT get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.get("gui.act.modifier.tag.editor.unknown");
	}

}
