package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTIntArrayModifier;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagIntArray;

public class NBTIntArrayElement extends NBTElement {
	private NBTTagIntArray value;

	public NBTIntArrayElement(GuiListModifier<?> parent, String key, NBTTagIntArray value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(new GuiButton(0, 0, 0, I18n.format("gui.act.modifier.tag.editor.intArray")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new GuiNBTIntArrayModifier(
						parent instanceof GuiArrayModifierTitle ? ((GuiArrayModifierTitle) parent).getTitle()
								: "" + key + "/",
						parent, tag -> NBTIntArrayElement.this.value = tag, value.copy()));
				super.onClick(mouseX, mouseY);
			};
		});
	}

	@Override
	public NBTElement clone() {
		return new NBTIntArrayElement(parent, key, value.copy());
	}

	@Override
	public INBTBase get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor.intArray") + "[" + value.getIntArray().length + "]";
	}

}
