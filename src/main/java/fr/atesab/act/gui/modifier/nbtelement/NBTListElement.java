package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTListModifier;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

public class NBTListElement extends NBTElement {
	private NBTTagList value;

	public NBTListElement(GuiListModifier<?> parent, String key, NBTTagList value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(new GuiButton(0, 0, 0, I18n.format("gui.act.modifier.tag.editor.list")));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0)
			mc.displayGuiScreen(new GuiNBTListModifier(((GuiArrayModifierTitle) parent).getTitle() + key + "/", parent,
					tag -> value = tag, value.copy()));
		super.actionPerformed(button);
	}

	@Override
	public NBTElement clone() {
		return new NBTListElement(parent, key, value.copy());
	}

	@Override
	public NBTBase get() {
		return value.copy();
	}

	public static String getTagTypeName(int i) {
		return i >= 0 && i < NBTBase.NBT_TYPES.length ? NBTBase.NBT_TYPES[i] : "UNKNOWN";
	}

	@Override
	public String getType() {
		return getTagTypeName(value.getTagType()) + " " + I18n.format("gui.act.modifier.tag.editor.list") + "["
				+ value.tagCount() + "]";
	}

}