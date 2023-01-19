package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTTagElement extends NBTElement {
	private NBTTagCompound value;

	public NBTTagElement(GuiListModifier<?> parent, String key, NBTTagCompound value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(new GuiButton(0, 0, 0, I18n.format("gui.act.modifier.tag.editor.tag")));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0)
			mc.displayGuiScreen(new GuiNBTModifier(((GuiArrayModifierTitle) parent).getTitle() + key + "/", parent,
					tag -> value = tag, (NBTTagCompound) value.copy()));
		super.actionPerformed(button);
	}

	@Override
	public NBTElement clone() {
		return new NBTTagElement(parent, key, (NBTTagCompound) value.copy());
	}

	@Override
	public NBTBase get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor.tag") + "[" + value.getKeySet().size() + "]";
	}

}