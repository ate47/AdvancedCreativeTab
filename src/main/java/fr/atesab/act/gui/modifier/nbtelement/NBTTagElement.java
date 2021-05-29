package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class NBTTagElement extends NBTElement {
	private CompoundNBT value;

	public NBTTagElement(GuiListModifier<?> parent, String key, CompoundNBT value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(new Button(0, 0, 200, 20, new TranslationTextComponent("gui.act.modifier.tag.editor.tag"), b -> {
			mc.setScreen(new GuiNBTModifier(new StringTextComponent(parent.getStringTitle() + key + "/"), parent,
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
		return I18n.get("gui.act.modifier.tag.editor.tag") + "[" + value.size() + "]";
	}

}