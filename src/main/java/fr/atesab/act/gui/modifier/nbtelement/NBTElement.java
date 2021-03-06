package fr.atesab.act.gui.modifier.nbtelement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.GuiListModifier.AddElementButton;
import fr.atesab.act.gui.modifier.GuiListModifier.ListElement;
import fr.atesab.act.gui.modifier.GuiListModifier.RemoveElementButton;
import fr.atesab.act.gui.modifier.GuiListModifier.RunElementButton;
import fr.atesab.act.gui.modifier.GuiStringModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import fr.atesab.act.utils.GuiUtils;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class NBTElement extends ListElement implements Cloneable {
	private static boolean isList(Object object) {
		return object.getClass().isAnnotationPresent(GuiNBTList.class);
	}

	protected String key;

	protected GuiListModifier<?> parent;

	public NBTElement(GuiListModifier<?> parent, String key, int sizeX, int sizeY) {
		super(sizeX + 82, Math.max(isList(parent) ? 22 : 43, sizeY));
		this.key = key;
		this.parent = parent;
		buttonList.add(new RemoveElementButton(parent, sizeX + 1, 0, 20, 20, this));
		buttonList.add(new AddElementButton(parent, sizeX + 22, 0, 20, 20,
				new StringTextComponent("+").withStyle(TextFormatting.GREEN), this, i -> {
					GuiNBTModifier.ADD_ELEMENT.accept(i, parent);
					return null;
				}));
		buttonList.add(new AddElementButton(parent, sizeX + 43, 0, 37, 20,
				new TranslationTextComponent("gui.act.give.copy"), this, i -> {
					parent.addListElement(i, getElementByBase(parent, key, this.get()));
					return null;
				}));
		if (!isList(parent))
			buttonList.add(new RunElementButton(sizeX + 1, 21, 79, 20,
					new TranslationTextComponent("gui.act.config.name"),
					() -> mc.setScreen(new GuiStringModifier(parent,
							new TranslationTextComponent("gui.act.config.name"), getKey(), nk -> this.key = nk)),
					null));
	}

	@Override
	public abstract NBTElement clone();

	@Override
	public void draw(MatrixStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
		GuiUtils.drawGradientRect(offsetX - 2, offsetY - (6 + font.lineHeight), offsetX + getSizeX() - 1, offsetY - 2,
				0x88dddddd, 0x88aaaaaa, parent.getZLevel());
		GuiUtils.drawGradientRect(offsetX - 2, offsetY - 2, offsetX + getSizeX() - 1, offsetY + getSizeY() + 2,
				0x88000000, 0x88000000, parent.getZLevel());
		String s = getType();
		if (!isList(parent))
			s = key + " (" + s + ")";
		GuiUtils.drawString(font, s, offsetX + 2, offsetY - font.lineHeight - 4, 0xffffffff, font.lineHeight + 2);
		super.draw(matrixStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
	}

	/**
	 * Create a {@link INBT} from this {@link NBTElement}
	 * 
	 * @return the base
	 */
	public abstract INBT get();

	public String getKey() {
		return key;
	}

	/**
	 * Get the type of the NBTElement
	 * 
	 * @return the type name
	 * @since 2.1
	 */
	public abstract String getType();

	@Override
	public boolean match(String search) {
		return (key + " (" + I18n.get("gui.act.modifier.tag.editor." + getType()) + ")").toLowerCase()
				.contains(search.toLowerCase());
	}

	/**
	 * Get a {@link NBTElement} with a base
	 * 
	 * @param parent parent modifier
	 * @param key the key of the tag
	 * @param base the tag
	 * @return the {@link NBTElement} from this tag
	 * @since 2.1
	 */
	public static NBTElement getElementByBase(GuiListModifier<?> parent, String key, INBT base) {
		switch (base.getId()) {
			case 0:
				return new NBTTagElement(parent, key, new CompoundNBT());
			case 1:
				return new NBTByteElement(parent, key, ((ByteNBT) base).getAsByte());
			case 2:
				return new NBTShortElement(parent, key, ((ShortNBT) base).getAsShort());
			case 3:
				return new NBTIntegerElement(parent, key, ((IntNBT) base).getAsInt());
			case 4:
				return new NBTLongElement(parent, key, ((LongNBT) base).getAsLong());
			case 5:
				return new NBTFloatElement(parent, key, ((FloatNBT) base).getAsFloat());
			case 6:
				return new NBTDoubleElement(parent, key, ((DoubleNBT) base).getAsDouble());
			case 8:
				return new NBTStringElement(parent, key, ((StringNBT) base).getAsString());
			case 9:
				return new NBTListElement(parent, key, ((ListNBT) base));
			case 10:
				return new NBTTagElement(parent, key, (CompoundNBT) base);
			case 11:
				return new NBTIntArrayElement(parent, key, (IntArrayNBT) base);
			case 12:
				return new NBTLongArrayElement(parent, key, (LongArrayNBT) base);
			default:
				return new NBTUnknownElement(parent, key, base);
		}
	}

	/**
	 * Say to {@link NBTElement} in an {@link GuiListModifier} with this annotation
	 * that key isn't needed
	 * 
	 * @author ATE47
	 * @since 2.1
	 */
	@Retention(RUNTIME)
	@Target(TYPE)
	public static @interface GuiNBTList {
	}
}