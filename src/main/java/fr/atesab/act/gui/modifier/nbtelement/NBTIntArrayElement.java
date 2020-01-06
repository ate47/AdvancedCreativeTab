package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTIntArrayModifier;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;

public class NBTIntArrayElement extends NBTElement {
	private IntArrayNBT value;

	public NBTIntArrayElement(GuiListModifier<?> parent, String key, IntArrayNBT value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(new Button(0, 0, 200, 20, I18n.format("gui.act.modifier.tag.editor.intArray"), b -> {
			mc.displayGuiScreen(
					new GuiNBTIntArrayModifier(
							parent instanceof GuiArrayModifierTitle ? ((GuiArrayModifierTitle) parent).getListModifierTitle()
									: "" + key + "/",
							parent, tag -> NBTIntArrayElement.this.value = tag, value.copy()));
		}));
	}

	@Override
	public NBTElement clone() {
		return new NBTIntArrayElement(parent, key, value.copy());
	}

	@Override
	public INBT get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor.intArray") + "[" + value.getIntArray().length + "]";
	}

}
