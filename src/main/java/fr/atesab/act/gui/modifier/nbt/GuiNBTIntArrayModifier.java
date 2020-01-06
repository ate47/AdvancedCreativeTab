package fr.atesab.act.gui.modifier.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbtelement.NBTIntegerElement;
import fr.atesab.act.utils.Tuple;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement.GuiNBTList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagIntArray;

@GuiNBTList
public class GuiNBTIntArrayModifier extends GuiListModifier<NBTTagIntArray> implements GuiArrayModifierTitle {
	private List<Integer> list;
	private String title;

	@SuppressWarnings("unchecked")
	public GuiNBTIntArrayModifier(String title, GuiScreen parent, Consumer<NBTTagIntArray> setter,
			NBTTagIntArray array) {
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
	protected NBTTagIntArray get() {
		return new NBTTagIntArray(list);
	}

	@Override
	public String getTitle() {
		return title;
	}
}
