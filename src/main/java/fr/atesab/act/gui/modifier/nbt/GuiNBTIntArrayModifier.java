package fr.atesab.act.gui.modifier.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement.GuiNBTList;
import fr.atesab.act.gui.modifier.nbtelement.NBTIntegerElement;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.IntArrayNBT;

@GuiNBTList
public class GuiNBTIntArrayModifier extends GuiListModifier<IntArrayNBT> implements GuiArrayModifierTitle {
	private List<Integer> list;
	private String title;

	@SuppressWarnings("unchecked")
	public GuiNBTIntArrayModifier(String title, Screen parent, Consumer<IntArrayNBT> setter,
			IntArrayNBT array) {
		super(parent, new ArrayList<>(), setter, new Tuple[0]);
		this.title = title;
		this.list = new ArrayList<>();
		String k = "...";
		for (int i : array.getIntArray()) {
			elements.add(new NBTIntegerElement(this, k, i));
			list.add(i);
		}
		elements.add(new AddElementList(this, () -> {
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

	@Override
	public String getListModifierTitle() {
		return title;
	}
}
