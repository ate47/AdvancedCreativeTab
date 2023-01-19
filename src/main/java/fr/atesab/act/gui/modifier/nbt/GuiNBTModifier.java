package fr.atesab.act.gui.modifier.nbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import fr.atesab.act.gui.modifier.GuiArrayModifierTitle;
import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.GuiStringModifier;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

public class GuiNBTModifier extends GuiListModifier<NBTTagCompound> implements GuiArrayModifierTitle {

	public static final BiConsumer<Integer, GuiListModifier<?>> ADD_ELEMENT = (i, lm) -> {
		final GuiStringModifier modifier = new GuiStringModifier(lm, "", null);
		modifier.setSetter(key -> {
			if (!key.isEmpty())
				modifier.setParent(addElement(i == null ? lm.getElements().size() - 1 : i, lm, key));
		});
		lm.mc.displayGuiScreen(modifier);
	};

	public static GuiButtonListSelector<NBTBase> addElement(int i, GuiListModifier<?> lm, String key) {
		return new GuiButtonListSelector<NBTBase>(lm, Arrays.asList(
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.tag"), new NBTTagCompound()),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.string"), new NBTTagString()),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.int"), new NBTTagInt(0)),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.long"), new NBTTagLong(0L)),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.float"), new NBTTagFloat(0F)),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.double"), new NBTTagDouble(0D)),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.short"),
						new NBTTagShort((short) 0)),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.intArray"),
						new NBTTagIntArray(new int[0])),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.longArray"),
						new NBTTagLongArray(new long[0])),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.byte"), new NBTTagByte((byte) 0)),
				new Tuple<String, NBTBase>(I18n.format("gui.act.modifier.tag.editor.list"), new NBTTagList())),
				base -> {
					lm.addListElement(i, NBTElement.getElementByBase(lm, key, base));
					return null;
				});
	}

	public static NBTBase getDefaultElement(int id) {
		switch (id) {
		case 1:
			new NBTTagByte((byte) 0);
		case 2:
			return new NBTTagShort((short) 0);
		case 3:
			return new NBTTagInt(0);
		case 4:
			return new NBTTagLong(0L);
		case 5:
			return new NBTTagFloat(0F);
		case 6:
			return new NBTTagDouble(0D);
		case 8:
			return new NBTTagString("");
		case 9:
			return new NBTTagList();
		case 10:
			return new NBTTagCompound();
		case 11:
			return new NBTTagIntArray(new int[0]);
		case 12:
			return new NBTTagLongArray(new long[0]);
		default:
			return null;
		}
	}

	private String title = "";

	public GuiNBTModifier(GuiScreen parent, Consumer<NBTTagCompound> setter, NBTTagCompound tag) {
		this("/", parent, setter, tag);
	}

	public GuiNBTModifier(String title, GuiScreen parent, Consumer<NBTTagCompound> setter, NBTTagCompound tag) {
		super(parent, new ArrayList<>(), setter, true, true);
		elements.add(new ButtonElementList(200, 21, 200, 20, TextFormatting.GREEN + "+",
				() -> ADD_ELEMENT.accept(null, this), null));
		tag.getKeySet().forEach(key -> addElement(key, tag.getTag(key)));
		setPaddingLeft(5);
		setPaddingTop(13 + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
		setNoAdaptativeSize(true);
		this.title = title;
	}

	private void addElement(int i, String key, NBTBase base) {
		if (elements.stream().filter(le -> le instanceof NBTElement && ((NBTElement) le).getKey().equals(key))
				.findFirst().isPresent()) {
			addElement(key + "_", base);
			return;
		}
		addListElement(i, NBTElement.getElementByBase(this, key, base));

	}

	private void addElement(String key, NBTBase base) {
		addElement(elements.size() - 1, key, base);
	}

	@Override
	protected NBTTagCompound get() {
		NBTTagCompound tag = new NBTTagCompound();
		elements.stream().filter(le -> le instanceof NBTElement).forEach(le -> {
			NBTElement elem = ((NBTElement) le);
			tag.setTag(elem.getKey(), elem.get());
		});
		return tag;
	}

	@Override
	public String getTitle() {
		return title;
	}

}
