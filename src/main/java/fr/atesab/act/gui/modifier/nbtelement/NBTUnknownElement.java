package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBTBase;

public class NBTUnknownElement extends NBTElement {
	private INBTBase value;

	public NBTUnknownElement(GuiListModifier<?> parent, String key, INBTBase value) {
		super(parent, key, 200, 21);
		this.value = value;
	}

	@Override
	public NBTElement clone() {
		return new NBTUnknownElement(parent, key, value.copy());
	}

	@Override
	public INBTBase get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor.unknown");
	}

}
