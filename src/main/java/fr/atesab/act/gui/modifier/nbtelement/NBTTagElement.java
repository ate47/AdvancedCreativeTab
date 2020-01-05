package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTTagElement extends NBTElement {
	private NBTTagCompound value;

	public NBTTagElement(GuiListModifier<?> parent, String key, NBTTagCompound value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(new GuiButton(0, 0, 0, I18n.format("gui.act.modifier.tag.editor.tag")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new GuiNBTModifier(((GuiArrayModifierTitle) parent).getTitle() + key + "/", parent,
						tag -> NBTTagElement.this.value = tag, value.copy()));
				super.onClick(mouseX, mouseY);
			}
		});
	}

	@Override
	public NBTElement clone() {
		return new NBTTagElement(parent, key, value.copy());
	}

	@Override
	public INBTBase get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor.tag") + "[" + value.size() + "]";
	}

}