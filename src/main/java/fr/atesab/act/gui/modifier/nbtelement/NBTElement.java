package fr.atesab.act.gui.modifier.nbtelement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.GuiStringModifier;
import fr.atesab.act.gui.modifier.GuiListModifier.AddElementButton;
import fr.atesab.act.gui.modifier.GuiListModifier.ListElement;
import fr.atesab.act.gui.modifier.GuiListModifier.RemoveElementButton;
import fr.atesab.act.gui.modifier.GuiListModifier.RunElementButton;
import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import fr.atesab.act.utils.GuiUtils;
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
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

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
		buttonList.add(new AddElementButton(parent, sizeX + 22, 0, 20, 20, EnumChatFormatting.GREEN + "+", this, i -> {
			GuiNBTModifier.ADD_ELEMENT.accept(i, parent);
			return null;
		}));
		buttonList
				.add(new AddElementButton(parent, sizeX + 43, 0, 37, 20, I18n.format("gui.act.give.copy"), this, i -> {
					parent.addListElement(i, getElementByBase(parent, key, this.get()));
					return null;
				}));
		if (!isList(parent))
			buttonList.add(new RunElementButton(sizeX + 1, 21, 79, 20, I18n.format("gui.act.config.name"),
					() -> mc.displayGuiScreen(new GuiStringModifier(parent, getKey(), nk -> this.key = nk)), null));
	}

	@Override
	public abstract NBTElement clone();

	@Override
	public void draw(int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
		GuiUtils.drawGradientRect(offsetX - 2, offsetY - (6 + fontRenderer.FONT_HEIGHT), offsetX + getSizeX() - 1,
				offsetY - 2, 0x88dddddd, 0x88aaaaaa, parent.getZLevel());
		GuiUtils.drawGradientRect(offsetX - 2, offsetY - 2, offsetX + getSizeX() - 1, offsetY + getSizeY() + 2,
				0x88000000, 0x88000000, parent.getZLevel());
		String s = getType();
		if (!isList(parent))
			s = key + " (" + s + ")";
		GuiUtils.drawString(fontRenderer, s, offsetX + 2, offsetY - fontRenderer.FONT_HEIGHT - 4, 0xffffffff,
				fontRenderer.FONT_HEIGHT + 2);
		super.draw(offsetX, offsetY, mouseX, mouseY, partialTicks);
	}

	/**
	 * Create a {@link NBTBase} from this {@link NBTElement}
	 * 
	 * @return the base
	 */
	public abstract NBTBase get();

	public String getKey() {
		return key;
	}

	/**
	 * Get the type of the NBTElement
	 * 
	 * @since 2.1
	 */
	public abstract String getType();

	@Override
	public boolean match(String search) {
		return (key + " (" + I18n.format("gui.act.modifier.tag.editor." + getType()) + ")").toLowerCase()
				.contains(search.toLowerCase());
	}

	/**
	 * Get a {@link NBTElement} with a base
	 * 
	 * @param parent
	 * @param key
	 * @param base
	 * @return
	 * @since 2.1
	 */
	public static NBTElement getElementByBase(GuiListModifier<?> parent, String key, NBTBase base) {
		switch (base.getId()) {
		case 0:
			return new NBTTagElement(parent, key, new NBTTagCompound());
		case 1:
			return new NBTByteElement(parent, key, ((NBTTagByte) base).getByte());
		case 2:
			return new NBTShortElement(parent, key, ((NBTTagShort) base).getShort());
		case 3:
			return new NBTIntegerElement(parent, key, ((NBTTagInt) base).getInt());
		case 4:
			return new NBTLongElement(parent, key, ((NBTTagLong) base).getLong());
		case 5:
			return new NBTFloatElement(parent, key, ((NBTTagFloat) base).getFloat());
		case 6:
			return new NBTDoubleElement(parent, key, ((NBTTagDouble) base).getDouble());
		case 8:
			return new NBTStringElement(parent, key, ((NBTTagString) base).getString());
		case 9:
			return new NBTListElement(parent, key, ((NBTTagList) base));
		case 10:
			return new NBTTagElement(parent, key, (NBTTagCompound) base);
		case 11:
			return new NBTIntArrayElement(parent, key, (NBTTagIntArray) base);
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