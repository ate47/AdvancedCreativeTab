package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTIntArrayModifier;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagIntArray;

public class NBTIntArrayElement extends NBTElement {
	private NBTTagIntArray value;

	public NBTIntArrayElement(GuiListModifier<?> parent, String key, NBTTagIntArray value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(new GuiButton(0, 0, 0, I18n.format("gui.act.modifier.tag.editor.intArray")));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0)
			mc.displayGuiScreen(new GuiNBTIntArrayModifier(((GuiArrayModifierTitle) parent).getTitle() + key + "/",
					parent, tag -> value = tag, value.copy()));
		super.actionPerformed(button);
	}

	@Override
	public NBTElement clone() {
		return new NBTIntArrayElement(parent, key, value.copy());
	}

	@Override
	public NBTBase get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor.intArray") + "[" + value.getIntArray().length + "]";
	}

}
