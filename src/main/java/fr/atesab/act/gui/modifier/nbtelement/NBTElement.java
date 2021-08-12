package fr.atesab.act.gui.modifier.nbtelement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.GuiListModifier.AddElementButton;
import fr.atesab.act.gui.modifier.GuiListModifier.ListElement;
import fr.atesab.act.gui.modifier.GuiListModifier.RemoveElementButton;
import fr.atesab.act.gui.modifier.GuiListModifier.RunElementButton;
import fr.atesab.act.gui.modifier.GuiStringModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

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
				new TextComponent("+").withStyle(ChatFormatting.GREEN), this, i -> {
					GuiNBTModifier.ADD_ELEMENT.accept(i, parent);
					return null;
				}));
		buttonList.add(new AddElementButton(parent, sizeX + 43, 0, 37, 20,
				new TranslatableComponent("gui.act.give.copy"), this, i -> {
					parent.addListElement(i, getElementByBase(parent, key, this.get()));
					return null;
				}));
		if (!isList(parent))
			buttonList
					.add(new RunElementButton(sizeX + 1, 21, 79, 20, new TranslatableComponent("gui.act.config.name"),
							() -> mc.setScreen(new GuiStringModifier(parent,
									new TranslatableComponent("gui.act.config.name"), getKey(), nk -> this.key = nk)),
							null));
	}

	@Override
	public abstract NBTElement clone();

	@Override
	public void draw(PoseStack matrixStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
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
	 * Create a {@link Tag} from this {@link NBTElement}
	 * 
	 * @return the base
	 */
	public abstract Tag get();

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
	 * @param key    the key of the tag
	 * @param base   the tag
	 * @return the {@link NBTElement} from this tag
	 * @since 2.1
	 */
	public static NBTElement getElementByBase(GuiListModifier<?> parent, String key, Tag base) {
		switch (base.getId()) {
			case 0:
				return new NBTTagElement(parent, key, new CompoundTag());
			case 1:
				return new NBTByteElement(parent, key, ((ByteTag) base).getAsByte());
			case 2:
				return new NBTShortElement(parent, key, ((ShortTag) base).getAsShort());
			case 3:
				return new NBTIntegerElement(parent, key, ((IntTag) base).getAsInt());
			case 4:
				return new NBTLongElement(parent, key, ((LongTag) base).getAsLong());
			case 5:
				return new NBTFloatElement(parent, key, ((FloatTag) base).getAsFloat());
			case 6:
				return new NBTDoubleElement(parent, key, ((DoubleTag) base).getAsDouble());
			case 8:
				return new NBTStringElement(parent, key, ((StringTag) base).getAsString());
			case 9:
				return new NBTListElement(parent, key, ((ListTag) base));
			case 10:
				return new NBTTagElement(parent, key, (CompoundTag) base);
			case 11:
				return new NBTIntArrayElement(parent, key, (IntArrayTag) base);
			case 12:
				return new NBTLongArrayElement(parent, key, (LongArrayTag) base);
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