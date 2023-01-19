package fr.atesab.act.utils;

import fr.atesab.act.ACTMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

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
	public static IChatComponent getErrorPrefix() {
		return getPrefix(new ChatComponentText(I18n.format("gui.act.error"))
				.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)), EnumChatFormatting.WHITE);
	}

	/**
	 * create a default prefix
	 * 
	 * @return the prefix
	 * @since 2.0
	 * @see ChatUtils#getErrorPrefix()
	 */
	public static IChatComponent getPrefix() {
		return getPrefix(null, null);
	}

	private static IChatComponent getPrefix(IChatComponent notif, EnumChatFormatting endColor) {
		IChatComponent p = new ChatComponentText("")
				.setChatStyle(new ChatStyle().setColor(endColor != null ? endColor : EnumChatFormatting.WHITE))
				.appendSibling(new ChatComponentText("[").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))
						.appendSibling(new ChatComponentText(ACTMod.MOD_LITTLE_NAME)
								.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD))));
		if (notif != null)
			p.appendSibling(new ChatComponentText("/").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE)))
					.appendSibling(notif);
		return p.appendSibling(new ChatComponentText("]").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)))
				.appendSibling(new ChatComponentText(" "));
	}

	/**
	 * Send a message to say an {@link ItemStack} has been given in the inventory
	 * 
	 * @param itemStack
	 *            the stack
	 * @since 2.0
	 */
	public static void itemStack(ItemStack itemStack) {
		if (itemStack != null)
			send(getPrefix()
					.appendSibling(
							new ChatComponentText(I18n.format("gui.act.give.msg") + " (" + itemStack.getDisplayName()
									+ EnumChatFormatting.RESET + ")")
											.setChatStyle(
													new ChatStyle()
															.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
																	new ChatComponentText("{id:\""
																			+ Item.itemRegistry
																					.getNameForObject(
																							itemStack.getItem())
																					.toString()
																			+ "\",Count:" + itemStack.stackSize + "b"
																			+ (itemStack.getTagCompound() != null
																					? ",tag:" + itemStack
																							.getTagCompound().toString()
																					: "")
																			+ "}")))
															.setChatClickEvent(new ClickEvent(Action.RUN_COMMAND,
																	"/" + ACTMod.ACT_COMMAND.getCommandName() + " "
																			+ ACTMod.ACT_COMMAND.SC_OPEN_GIVER.getName()
																			+ " "
																			+ ItemUtils.getGiveCode(itemStack))))));
		else
			error(I18n.format("gui.act.give.fail2"));
	}

	/**
	 * Send a {@link ChatComponentText} to chat
	 * 
	 * @param iChatComponent
	 *            The {@link ChatComponentText}
	 * @since 2.0
	 * @see ChatUtils#show(String)
	 * @see ChatUtils#error(String)
	 */
	public static void send(IChatComponent iChatComponent) {
		if (Minecraft.getMinecraft().thePlayer != null)
			Minecraft.getMinecraft().thePlayer.addChatMessage(iChatComponent);
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
