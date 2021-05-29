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
import net.minecraft.util.text.ITextComponent;

@GuiNBTList
public class GuiNBTListModifier extends GuiListModifier<ListNBT> {
	private ListNBT list;

	@SuppressWarnings("unchecked")
	public GuiNBTListModifier(ITextComponent title, Screen parent, Consumer<ListNBT> setter, ListNBT list) {
		super(parent, title, new ArrayList<>(), setter, new Tuple[0]);
		this.list = list.copy();
		String k = "...";
		for (INBT base : list) {
			addListElement(NBTElement.getElementByBase(this, k, base));
		}
		addListElement(new AddElementList(this, () -> {
			if (this.list.getElementType() != 0) {
				INBT base = GuiNBTModifier.getDefaultElement(this.list.getElementType());
				if (base != null)
					addListElement(getElements().size() - 1, NBTElement.getElementByBase(this, k, base));
			} else
				getMinecraft().setScreen(GuiNBTModifier.addElement(getElements().size() - 1, this, k));
			return null;
		}));
		setPaddingLeft(5);
		setPaddingTop(13 + Minecraft.getInstance().font.lineHeight);
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
		getElements().stream().filter(le -> le instanceof NBTElement).forEach(le -> list.add(((NBTElement) le).get()));
		return list;
	}

}