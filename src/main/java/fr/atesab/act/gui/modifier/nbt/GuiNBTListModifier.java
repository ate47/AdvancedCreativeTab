package fr.atesab.act.gui.modifier.nbt;

import java.util.ArrayList;
import java.util.function.Consumer;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement.GuiNBTList;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagList;

@GuiNBTList
public class GuiNBTListModifier extends GuiListModifier<NBTTagList> implements GuiArrayModifierTitle {
	private NBTTagList list;
	private String title;

	@SuppressWarnings("unchecked")
	public GuiNBTListModifier(String title, GuiScreen parent, Consumer<NBTTagList> setter, NBTTagList list) {
		super(parent, new ArrayList<>(), setter, new Tuple[0]);
		this.title = title;
		this.list = list.copy();
		String k = "...";
		for (INBTBase base : list) {
			elements.add(NBTElement.getElementByBase(this, k, base));
		}
		elements.add(new AddElementList(this, () -> {
			if (this.list.getTagType() != 0) {
				INBTBase base = GuiNBTModifier.getDefaultElement(this.list.getTagType());
				if (base != null)
					addListElement(elements.size() - 1, NBTElement.getElementByBase(this, k, base));
			} else
				mc.displayGuiScreen(GuiNBTModifier.addElement(elements.size() - 1, this, k));
			return null;
		}));
		setPaddingLeft(5);
		setPaddingTop(13 + Minecraft.getInstance().fontRenderer.FONT_HEIGHT);
		setNoAdaptativeSize(true);
	}

	@Override
	public void addListElement(int i, ListElement elem) {
		if (elem instanceof NBTElement)
			list.add(((NBTElement) elem).get());
		super.addListElement(i, elem);
	}

	@Override
	protected NBTTagList get() {
		NBTTagList list = new NBTTagList();
		elements.stream().filter(le -> le instanceof NBTElement).forEach(le -> list.add(((NBTElement) le).get()));
		return list;
	}

	@Override
	public String getTitle() {
		return title;
	}

}