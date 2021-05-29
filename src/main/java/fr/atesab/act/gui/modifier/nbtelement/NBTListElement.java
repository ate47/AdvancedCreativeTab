package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTListModifier;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class NBTListElement extends NBTElement {
	private ListNBT value;

	public NBTListElement(GuiListModifier<?> parent, String key, ListNBT value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList
				.add(new Button(0, 0, 200, 20, new TranslationTextComponent("gui.act.modifier.tag.editor.list"), b -> {
					mc.setScreen(new GuiNBTListModifier(new StringTextComponent(parent.getStringTitle() + key + "/"),
							parent, tag -> NBTListElement.this.value = tag, value.copy()));
				}));
	}

	@Override
	public NBTElement clone() {
		return new NBTListElement(parent, key, value.copy());
	}

	@Override
	public INBT get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return value.getType().getName().toLowerCase() + " " + I18n.get("gui.act.modifier.tag.editor.list") + "["
				+ value.size() + "]";
	}

}