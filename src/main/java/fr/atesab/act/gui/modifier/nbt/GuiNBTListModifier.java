package fr.atesab.act.gui.modifier.nbt;

import java.util.ArrayList;
import java.util.function.Consumer;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement.GuiNBTList;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

@GuiNBTList
public class GuiNBTListModifier extends GuiListModifier<NBTTagList> implements GuiArrayModifierTitle {
	private NBTTagList list;
	private String title;

	public GuiNBTListModifier(String title, GuiScreen parent, Consumer<NBTTagList> setter, NBTTagList list) {
		super(parent, new ArrayList<>(), setter);
		this.title = title;
		this.list = list.copy();
		String k = "...";
		ItemUtils.forEachInNBTTagList(list, base -> elements.add(NBTElement.getElementByBase(this, k, base)));
		elements.add(new AddElementList(this, () -> {
			if (this.list.getTagType() != 0) {
				NBTBase base = GuiNBTModifier.getDefaultElement(this.list.getTagType());
				if (base != null)
					addListElement(elements.size() - 1, NBTElement.getElementByBase(this, k, base));
			} else
				mc.displayGuiScreen(GuiNBTModifier.addElement(elements.size() - 1, this, k));
			return null;
		}));
		setPaddingLeft(5);
		setPaddingTop(13 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT);
		setNoAdaptativeSize(true);
	}

	@Override
	public void addListElement(int i, ListElement elem) {
		if (elem instanceof NBTElement)
			list.appendTag(((NBTElement) elem).get());
		super.addListElement(i, elem);
	}

	@Override
	protected NBTTagList get() {
		NBTTagList list = new NBTTagList();
		elements.stream().filter(le -> le instanceof NBTElement).forEach(le -> list.appendTag(((NBTElement) le).get()));
		return list;
	}

	@Override
	public String getTitle() {
		return title;
	}

}