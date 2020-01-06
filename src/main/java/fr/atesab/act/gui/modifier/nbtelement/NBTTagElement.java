package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

public class NBTTagElement extends NBTElement {
	private CompoundNBT value;

	public NBTTagElement(GuiListModifier<?> parent, String key, CompoundNBT value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(new Button(0, 0, 200, 20, I18n.format("gui.act.modifier.tag.editor.tag"), b -> {
			mc.displayGuiScreen(new GuiNBTModifier(((GuiArrayModifierTitle) parent).getListModifierTitle() + key + "/", parent,
					tag -> NBTTagElement.this.value = tag, value.copy()));
		}));
	}

	@Override
	public NBTElement clone() {
		return new NBTTagElement(parent, key, value.copy());
	}

	@Override
	public INBT get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor.tag") + "[" + value.size() + "]";
	}

}