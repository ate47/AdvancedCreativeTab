package fr.atesab.act.gui.modifier.nbt;

import java.util.ArrayList;
import java.util.function.Consumer;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement.GuiNBTList;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

@GuiNBTList
public class GuiNBTListModifier extends GuiListModifier<ListNBT> {
	private ListNBT list;

	@SuppressWarnings("unchecked")
	public GuiNBTListModifier(String title, Screen parent, Consumer<ListNBT> setter, ListNBT list) {
		super(parent, title, new ArrayList<>(), setter, new Tuple[0]);
		this.list = list.copy();
		String k = "...";
		for (INBT base : list) {
			elements.add(NBTElement.getElementByBase(this, k, base));
		}
		elements.add(new AddElementList(this, () -> {
			if (this.list.getTagType() != 0) {
				INBT base = GuiNBTModifier.getDefaultElement(this.list.getTagType());
				if (base != null)
					addListElement(elements.size() - 1, NBTElement.getElementByBase(this, k, base));
			} else
				getMinecraft().displayGuiScreen(GuiNBTModifier.addElement(elements.size() - 1, this, k));
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
	protected ListNBT get() {
		ListNBT list = new ListNBT();
		elements.stream().filter(le -> le instanceof NBTElement).forEach(le -> list.add(((NBTElement) le).get()));
		return list;
	}

}