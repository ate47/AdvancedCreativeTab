package fr.atesab.act.utils;

import fr.atesab.act.ACTMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
	 * @param error error message
	 * @since 2.0
	 * @see ChatUtils#show(String)
	 */
	public static void error(String error) {
		send(getErrorPrefix().append(error));
	}

	/**
	 * create an error prefix
	 * 
	 * @return the prefix
	 * @since 2.0
	 * @see ChatUtils#getPrefix()
	 */
	public static IFormattableTextComponent getErrorPrefix() {
		return getPrefix(new TranslationTextComponent("gui.act.error").withStyle(TextFormatting.RED),
				TextFormatting.WHITE);
	}

	/**
	 * create a default prefix
	 * 
	 * @return the prefix
	 * @since 2.0
	 * @see ChatUtils#getErrorPrefix()
	 */
	public static IFormattableTextComponent getPrefix() {
		return getPrefix(null, null);
	}

	private static IFormattableTextComponent getPrefix(ITextComponent notif, TextFormatting endColor) {
		IFormattableTextComponent p = new StringTextComponent("")
				.withStyle(endColor != null ? endColor : TextFormatting.WHITE)
				.append(new StringTextComponent("[").withStyle(TextFormatting.RED)
						.append(new StringTextComponent(ACTMod.MOD_LITTLE_NAME).withStyle(TextFormatting.GOLD)));
		if (notif != null)
			p.append(new StringTextComponent("/").withStyle(TextFormatting.WHITE)).append(notif);
		return p.append(new StringTextComponent("]").withStyle(TextFormatting.RED))
				.append(new StringTextComponent(" "));
	}

	/**
	 * Send a message to say an {@link ItemStack} has been given in the inventory
	 * 
	 * @param itemStack the stack
	 * @since 2.0
	 */
	public static void itemStack(ItemStack itemStack) {
		if (itemStack != null) {
			CompoundNBT item = new CompoundNBT();
			item.putString("id", itemStack.getItem().getRegistryName().toString());
			item.putInt("Count", itemStack.getCount());
			if (itemStack.getTag() != null)
				item.put("tag", itemStack.getTag());
			send(getPrefix().append(new TranslationTextComponent("gui.act.give.msg").append(": ")
					.withStyle(TextFormatting.GOLD).append(itemStack.getDisplayName().copy().withStyle(style -> {
						style.withHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemHover(itemStack)));
						style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + ACTMod.ACT_COMMAND.getName() + " "
								+ ACTMod.ACT_COMMAND.SC_OPEN_GIVER.getName() + " " + ItemUtils.getGiveCode(itemStack)));
						return style;
					}))));
		} else
			error(I18n.get("gui.act.give.fail2"));
	}

	/**
	 * Send a {@link ITextComponent} to chat
	 * 
	 * @param message The {@link ITextComponent}
	 * @since 2.0
	 * @see ChatUtils#show(String)
	 * @see ChatUtils#error(String)
	 */
	public static void send(ITextComponent message) {
		ClientPlayerEntity plr = Minecraft.getInstance().player;
		if (plr != null)
			plr.sendMessage(message, plr.getUUID());
	}

	/**
	 * Send a text message
	 * 
	 * @param message The message
	 * @since 2.0
	 * @see ChatUtils#error(String)
	 */
	public static void show(String message) {
		send(getPrefix().append(message));
	}

	private ChatUtils() {
	}
}
