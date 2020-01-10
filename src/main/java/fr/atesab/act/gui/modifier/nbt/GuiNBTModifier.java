package fr.atesab.act.gui.modifier.nbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

public class GuiNBTModifier extends GuiListModifier<CompoundNBT> {

	public static final BiConsumer<Integer, GuiListModifier<?>> ADD_ELEMENT = (i, lm) -> {
		final GuiStringModifier modifier = new GuiStringModifier(lm, "gui.act.modifier.name", "", null);
		modifier.setSetter(key -> {
			if (!key.isEmpty())
				modifier.setParent(addElement(i == null ? lm.getElements().size() - 1 : i, lm, key));
		});
		lm.getMinecraft().displayGuiScreen(modifier);
	};

	public static GuiButtonListSelector<INBT> addElement(int i, GuiListModifier<?> lm, String key) {
		return new GuiButtonListSelector<INBT>(lm, "gui.act.modifier.tag.editor", Arrays.asList(
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.tag"), new CompoundNBT()),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.string"), StringNBT.func_229705_a_("")),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.int"), IntNBT.func_229692_a_(0)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.long"), LongNBT.func_229698_a_(0L)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.float"), FloatNBT.func_229689_a_(0F)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.double"), DoubleNBT.func_229684_a_(0D)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.short"), ShortNBT.func_229701_a_((short) 0)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.intArray"),
						new IntArrayNBT(new int[0])),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.longArray"),
						new LongArrayNBT(new long[0])),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.byte"), ByteNBT.func_229671_a_((byte) 0)),
				new Tuple<String, INBT>(I18n.format("gui.act.modifier.tag.editor.list"), new ListNBT())), base -> {
					lm.addListElement(i, NBTElement.getElementByBase(lm, key, base));
					return null;
				});
	}

	public static INBT getDefaultElement(int id) {
		switch (id) {
		case 1:
			return ByteNBT.func_229671_a_((byte) 0);
		case 2:
			return ShortNBT.func_229701_a_((short) 0);
		case 3:
			return IntNBT.func_229692_a_(0);
		case 4:
			return LongNBT.func_229698_a_(0L);
		case 5:
			return FloatNBT.func_229689_a_(0F);
		case 6:
			return DoubleNBT.func_229684_a_(0D);
		case 8:
			return StringNBT.func_229705_a_("");
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

	public GuiNBTModifier(Screen parent, Consumer<CompoundNBT> setter, CompoundNBT tag) {
		this("/", parent, setter, tag);
	}

	@SuppressWarnings("unchecked")
	public GuiNBTModifier(String title, Screen parent, Consumer<CompoundNBT> setter, CompoundNBT tag) {
		super(parent, title, new ArrayList<>(), setter, true, true, new Tuple[0]);
		addListElement(new ButtonElementList(200, 21, 200, 20, TextFormatting.GREEN + "+",
				() -> ADD_ELEMENT.accept(null, this), null));
		tag.keySet().forEach(key -> addElement(key, tag.get(key)));
		setPaddingLeft(5);
		setPaddingTop(13 + Minecraft.getInstance().fontRenderer.FONT_HEIGHT);
		setNoAdaptativeSize(true);
	}

	private void addElement(int i, String key, INBT base) {
		if (getElements().stream().filter(le -> le instanceof NBTElement && ((NBTElement) le).getKey().equals(key))
				.findFirst().isPresent()) {
			addElement(key + "_", base);
			return;
		}
		addListElement(i, NBTElement.getElementByBase(this, key, base));

	}

	private void addElement(String key, INBT base) {
		addElement(getElements().size() - 1, key, base);
	}

	@Override
	protected CompoundNBT get() {
		CompoundNBT tag = new CompoundNBT();
		getElements().stream().filter(le -> le instanceof NBTElement).forEach(le -> {
			NBTElement elem = ((NBTElement) le);
			tag.put(elem.getKey(), elem.get());
		});
		return tag;
	}

}
