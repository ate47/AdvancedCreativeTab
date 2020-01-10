package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTListModifier;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public class NBTListElement extends NBTElement {
	private static final String[] NBT_TYPES = new String[] { "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE",
			"BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]" };

	private static String getNBTType(int id) {
		return id >= 0 && id < NBT_TYPES.length ? NBT_TYPES[id] : NBT_TYPES[0];
	}

	private ListNBT value;

	public NBTListElement(GuiListModifier<?> parent, String key, ListNBT value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList.add(new Button(0, 0, 200, 20, I18n.format("gui.act.modifier.tag.editor.list"), b -> {
			mc.displayGuiScreen(new GuiNBTListModifier(parent.getStringTitle() + key + "/", parent,
					tag -> NBTListElement.this.value = tag, value.copy()));
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
		return getNBTType(value.getTagType()).toLowerCase() + " " + I18n.format("gui.act.modifier.tag.editor.list")
				+ "[" + value.size() + "]";
	}

}