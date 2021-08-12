package fr.atesab.act.utils;

import fr.atesab.act.ACTMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
	public static MutableComponent getErrorPrefix() {
		return getPrefix(new TranslatableComponent("gui.act.error").withStyle(ChatFormatting.RED),
				ChatFormatting.WHITE);
	}

	/**
	 * create a default prefix
	 * 
	 * @return the prefix
	 * @since 2.0
	 * @see ChatUtils#getErrorPrefix()
	 */
	public static MutableComponent getPrefix() {
		return getPrefix(null, null);
	}

	private static MutableComponent getPrefix(Component notif, ChatFormatting endColor) {
		MutableComponent p = new TextComponent("").withStyle(endColor != null ? endColor : ChatFormatting.WHITE)
				.append(new TextComponent("[").withStyle(ChatFormatting.RED)
						.append(new TextComponent(ACTMod.MOD_LITTLE_NAME).withStyle(ChatFormatting.GOLD)));
		if (notif != null)
			p.append(new TextComponent("/").withStyle(ChatFormatting.WHITE)).append(notif);
		return p.append(new TextComponent("]").withStyle(ChatFormatting.RED)).append(new TextComponent(" "));
	}

	/**
	 * Send a message to say an {@link ItemStack} has been given in the inventory
	 * 
	 * @param itemStack the stack
	 * @since 2.0
	 */
	public static void itemStack(ItemStack itemStack) {
		if (itemStack != null) {
			CompoundTag item = new CompoundTag();
			item.putString("id", itemStack.getItem().getRegistryName().toString());
			item.putInt("Count", itemStack.getCount());
			if (itemStack.getTag() != null)
				item.put("tag", itemStack.getTag());
			send(getPrefix().append(new TranslatableComponent("gui.act.give.msg").append(": ")
					.withStyle(ChatFormatting.GOLD).append(itemStack.getDisplayName().copy().withStyle(style -> {
						style.withHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(itemStack)));
						style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/" + ACTMod.ACT_COMMAND.getName() + " " + ACTMod.ACT_COMMAND.SC_OPEN_GIVER.getName()
										+ " " + ItemUtils.getGiveCode(itemStack)));
						return style;
					}))));
		} else
			error(I18n.get("gui.act.give.fail2"));
	}

	/**
	 * Send a {@link Component} to chat
	 * 
	 * @param message The {@link Component}
	 * @since 2.0
	 * @see ChatUtils#show(String)
	 * @see ChatUtils#error(String)
	 */
	public static void send(Component message) {
		Player plr = Minecraft.getInstance().player;
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
