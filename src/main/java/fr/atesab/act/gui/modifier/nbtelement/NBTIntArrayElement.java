package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTIntArrayModifier;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class NBTIntArrayElement extends NBTElement {
	private IntArrayNBT value;

	public NBTIntArrayElement(GuiListModifier<?> parent, String key, IntArrayNBT value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(
				new Button(0, 0, 200, 20, new TranslationTextComponent("gui.act.modifier.tag.editor.intArray"), b -> {
					mc.setScreen(
							new GuiNBTIntArrayModifier(new StringTextComponent(parent.getStringTitle() + key + "/"),
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
		return I18n.get("gui.act.modifier.tag.editor.intArray") + "[" + value.size() + "]";
	}

}
