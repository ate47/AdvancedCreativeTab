package fr.atesab.act.utils;

import fr.atesab.act.ACTMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;

/**
 * A set of tools to help to communicate in chat with the player
 * 
 * @author ATE47
 * @since 2.0
 */
public class ChatUtils {
	public static final char MODIFIER = '\u00a7';

	/**
	 * Send an error
	 * 
	 * @param error
	 *            error message
	 * @since 2.0
	 * @see ChatUtils#show(String)
	 */
	public static void error(String error) {
		send(getErrorPrefix().appendText(error));
	}

	/**
	 * create an error prefix
	 * 
	 * @return the prefix
	 * @since 2.0
	 * @see ChatUtils#getPrefix()
	 */
	public static ITextComponent getErrorPrefix() {
		return getPrefix(new StringTextComponent(I18n.format("gui.act.error"))
				.setStyle(new Style().setColor(TextFormatting.RED)), TextFormatting.WHITE);
	}

	/**
	 * create a default prefix
	 * 
	 * @return the prefix
	 * @since 2.0
	 * @see ChatUtils#getErrorPrefix()
	 */
	public static ITextComponent getPrefix() {
		return getPrefix(null, null);
	}

	private static ITextComponent getPrefix(ITextComponent notif, TextFormatting endColor) {
		ITextComponent p = new StringTextComponent("")
				.setStyle(new Style().setColor(endColor != null ? endColor : TextFormatting.WHITE))
				.appendSibling(new StringTextComponent("[").setStyle(new Style().setColor(TextFormatting.RED))
						.appendSibling(new StringTextComponent(ACTMod.MOD_LITTLE_NAME)
								.setStyle(new Style().setColor(TextFormatting.GOLD))));
		if (notif != null)
			p.appendSibling(new StringTextComponent("/").setStyle(new Style().setColor(TextFormatting.WHITE)))
					.appendSibling(notif);
		return p.appendSibling(new StringTextComponent("]").setStyle(new Style().setColor(TextFormatting.RED)))
				.appendSibling(new StringTextComponent(" "));
	}

	/**
	 * Send a message to say an {@link ItemStack} has been given in the inventory
	 * 
	 * @param itemStack
	 *            the stack
	 * @since 2.0
	 */
	public static void itemStack(ItemStack itemStack) {
		if (itemStack != null) {
			CompoundNBT item = new CompoundNBT();
			item.putString("id", itemStack.getItem().getRegistryName().toString());
			item.putInt("Count", itemStack.getCount());
			if (itemStack.getTag() != null)
				item.put("tag", itemStack.getTag());
			send(getPrefix().appendSibling(new TranslationTextComponent("gui.act.give.msg").appendText(": ")
					.applyTextStyle(s -> s.setColor(TextFormatting.GOLD))
					.appendSibling(itemStack.getDisplayName().deepCopy().applyTextStyle(style -> {
						style.setHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_ITEM, new StringTextComponent(item.toString())));
						style.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + ACTMod.ACT_COMMAND.getName() + " "
								+ ACTMod.ACT_COMMAND.SC_OPEN_GIVER.getName() + " " + ItemUtils.getGiveCode(itemStack)));
					}))));
		} else
			error(I18n.format("gui.act.give.fail2"));
	}

	/**
	 * Send a {@link ITextComponent} to chat
	 * 
	 * @param message
	 *            The {@link ITextComponent}
	 * @since 2.0
	 * @see ChatUtils#show(String)
	 * @see ChatUtils#error(String)
	 */
	public static void send(ITextComponent message) {
		if (Minecraft.getInstance().player != null)
			Minecraft.getInstance().player.sendMessage(message);
	}

	/**
	 * Send a text message
	 * 
	 * @param message
	 *            The message
	 * @since 2.0
	 * @see ChatUtils#error(String)
	 */
	public static void show(String message) {
		send(getPrefix().appendText(message));
	}
}
