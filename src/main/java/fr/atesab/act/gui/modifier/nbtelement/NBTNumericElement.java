package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;

public abstract class NBTNumericElement<T extends Number> extends NBTElement {
	private T value;
	private GuiTextField field;
	private String type;

	public NBTNumericElement(String type, GuiListModifier<?> parent, String key, int sizeX, int sizeY, T value) {
		super(parent, key, Math.max(sizeX, 200), Math.max(sizeY, 21));
		this.value = value;
		this.type = type;
		fieldList.add(field = new GuiTextField(0, fontRenderer, 2, 2, 196, 16));
		field.setText(String.valueOf(value));
	}

	public NBTNumericElement(String type, GuiListModifier<?> parent, String key, T value) {
		this(type, parent, key, 200, 21, value);
	}

	@Override
	public NBTElement clone() {
		return new NBTNumericElement<T>(type, parent, key, value) {
			@Override
			public NBTBase get(T value) {
				return this.get(value);
			}

			@Override
			public T parseValue(String text) throws NumberFormatException {
				return this.parseValue(text);
			}
		};
	}

	@Override
	public NBTBase get() {
		return get(value);
	}

	public abstract NBTBase get(T value);

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor." + type);
	}

	public T getValue() {
		return value;
	}

	@Override
	public boolean match(String search) {
		return field.getText().toLowerCase().contains(search.toLowerCase()) || super.match(search);
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		super.keyTyped(typedChar, keyCode);
		try {
			if (field.getText().isEmpty())
				value = (T) ((Integer) 0);
			else
				value = parseValue(field.getText());
			field.setTextColor(0xffffffff);
		} catch (NumberFormatException e) {
			field.setTextColor(0xffff0000);
		}
	}

	public abstract T parseValue(String text) throws NumberFormatException;

	public void updateValue(T value) {
		this.value = value;
		field.setText("" + value);
		field.setTextColor(0xffffffff);
	}

}