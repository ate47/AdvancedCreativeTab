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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.TextFormatting;

public class GuiNBTModifier extends GuiListModifier<CompoundNBT> implements GuiArrayModifierTitle {

	public static final BiConsumer<Integer, GuiListModifier<?>> ADD_ELEMENT = (i, lm) -> {
		final GuiStringModifier modifier = new GuiStringModifier(lm, "", null);
		modifier.setSetter(key -> {
			if (!key.isEmpty())
				modifier.setParent(addElement(i == null ? lm.getElements().size() - 1 : i, lm, key));
		});
		lm.getMinecraft().displayGuiScreen(modifier);
	};

	public static GuiButtonListSelector<INBT> addElement(int i, GuiListModifier<?> lm, String key) {
		return new GuiButtonListSelector<INBT>(lm, Arrays.asList(
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.tag"), new CompoundNBT()),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.string"), new StringNBT()),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.int"), new IntNBT(0)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.long"), new LongNBT(0L)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.float"), new FloatNBT(0F)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.double"), new DoubleNBT(0D)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.short"), new ShortNBT((short) 0)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.intArray"),
						new IntArrayNBT(new int[0])),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.longArray"),
						new LongArrayNBT(new long[0])),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.byte"), new ByteNBT((byte) 0)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.list"), new ListNBT())), base -> {
					lm.addListElement(i, NBTElement.getElementByBase(lm, key, base));
					return null;
				});
	}

	public static INBT getDefaultElement(int id) {
		switch (id) {
		case 1:
			new ByteNBT((byte) 0);
		case 2:
			return new ShortNBT((short) 0);
		case 3:
			return new IntNBT(0);
		case 4:
			return new LongNBT(0L);
		case 5:
			return new FloatNBT(0F);
		case 6:
			return new DoubleNBT(0D);
		case 8:
			return new StringNBT("");
		case 9:
			return new ListNBT();
		case 10:
			return new CompoundNBT();
		case 11:
			return new IntArrayNBT(new int[0]);
		case 12:
			return new LongArrayNBT(new long[0]);
		default:
			return null;
		}
	}

	private String title = "";

	public GuiNBTModifier(Screen parent, Consumer<CompoundNBT> setter, CompoundNBT tag) {
		this("/", parent, setter, tag);
	}

	@SuppressWarnings("unchecked")
	public GuiNBTModifier(String title, Screen parent, Consumer<CompoundNBT> setter, CompoundNBT tag) {
		super(parent, new ArrayList<>(), setter, true, true, new Tuple[0]);
		elements.add(new ButtonElementList(200, 21, 200, 20, TextFormatting.GREEN + "+",
				() -> ADD_ELEMENT.accept(null, this), null));
		tag.keySet().forEach(key -> addElement(key, tag.get(key)));
		setPaddingLeft(5);
		setPaddingTop(13 + Minecraft.getInstance().fontRenderer.FONT_HEIGHT);
		setNoAdaptativeSize(true);
		this.title = title;
	}

	private void addElement(int i, String key, INBT base) {
		if (elements.stream().filter(le -> le instanceof NBTElement && ((NBTElement) le).getKey().equals(key))
				.findFirst().isPresent()) {
			addElement(key + "_", base);
			return;
		}
		addListElement(i, NBTElement.getElementByBase(this, key, base));

	}

	private void addElement(String key, INBT base) {
		addElement(elements.size() - 1, key, base);
	}

	@Override
	protected CompoundNBT get() {
		CompoundNBT tag = new CompoundNBT();
		elements.stream().filter(le -> le instanceof NBTElement).forEach(le -> {
			NBTElement elem = ((NBTElement) le);
			tag.put(elem.getKey(), elem.get());
		});
		return tag;
	}

	@Override
	public String getListModifierTitle() {
		return title;
	}

}
