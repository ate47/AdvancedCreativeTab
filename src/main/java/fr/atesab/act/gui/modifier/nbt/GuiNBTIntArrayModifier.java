package fr.atesab.act.gui.modifier.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbtelement.NBTIntegerElement;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement.GuiNBTList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagIntArray;

@GuiNBTList
public class GuiNBTIntArrayModifier extends GuiListModifier<NBTTagIntArray> implements GuiArrayModifierTitle {
	private String title;

	public GuiNBTIntArrayModifier(String title, GuiScreen parent, Consumer<NBTTagIntArray> setter,
			NBTTagIntArray array) {
		super(parent, new ArrayList<>(), setter);
		this.title = title;
		String k = "...";
		for (int i : array.getIntArray()) {
			elements.add(new NBTIntegerElement(this, k, i));
		}
		elements.add(new AddElementList(this, () -> {
			return new NBTIntegerElement(this, k, 0);
		}));
		setPaddingLeft(5);
		setPaddingTop(13 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT);
		setNoAdaptativeSize(true);
	}

	@Override
	protected NBTTagIntArray get() {
		List<Integer> list = elements.stream().filter(le -> le instanceof NBTIntegerElement)
				.map(e -> ((NBTIntegerElement) e).getValue()).collect(Collectors.toList());
		int[] i = new int[list.size()];
		for (int j = 0; j < i.length; j++) {
			i[j] = list.get(j);
		}
		return new NBTTagIntArray(i);
	}

	@Override
	public String getTitle() {
		return title;
	}
}
