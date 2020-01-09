package fr.atesab.act.gui.modifier.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement.GuiNBTList;
import fr.atesab.act.gui.modifier.nbtelement.NBTIntegerElement;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.IntArrayNBT;

@GuiNBTList
public class GuiNBTIntArrayModifier extends GuiListModifier<IntArrayNBT> {
	private List<Integer> list;

	@SuppressWarnings("unchecked")
	public GuiNBTIntArrayModifier(String title, Screen parent, Consumer<IntArrayNBT> setter, IntArrayNBT array) {
		super(parent, title, new ArrayList<>(), setter, new Tuple[0]);
		this.list = new ArrayList<>();
		String k = "...";
		for (int i : array.getIntArray()) {
			addListElement(new NBTIntegerElement(this, k, i));
			list.add(i);
		}
		addListElement(new AddElementList(this, () -> {
			return new NBTIntegerElement(this, k, 0);
		}));
		setPaddingLeft(5);
		setPaddingTop(13 + Minecraft.getInstance().fontRenderer.FONT_HEIGHT);
		setNoAdaptativeSize(true);
	}

	@Override
	protected IntArrayNBT get() {
		return new IntArrayNBT(list);
	}

}
