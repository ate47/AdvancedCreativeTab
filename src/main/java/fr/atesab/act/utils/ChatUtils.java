package fr.atesab.act.utils;

import fr.atesab.act.ACTMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.ClickEvent.Action;

public class ChatUtils {
	public static final char MODIFIER = '\u00a7';

	public static void error(String error) {
		send(getErrorPrefix().appendText(error));
	}

	public static ITextComponent getErrorPrefix() {
		return getPrefix(new TextComponentString(I18n.format("gui.act.error"))
				.setStyle(new Style().setColor(TextFormatting.RED)), TextFormatting.WHITE);
	}

	public static ITextComponent getPrefix() {
		return getPrefix(null, null);
	}

	private static ITextComponent getPrefix(ITextComponent notif, TextFormatting endColor) {
		ITextComponent p = new TextComponentString("")
				.setStyle(new Style().setColor(endColor != null ? endColor : TextFormatting.WHITE))
				.appendSibling(new TextComponentString("[").setStyle(new Style().setColor(TextFormatting.RED))
						.appendSibling(new TextComponentString(ACTMod.MOD_LITTLE_NAME)
								.setStyle(new Style().setColor(TextFormatting.GOLD))));
		if (notif != null)
			p.appendSibling(new TextComponentString("/").setStyle(new Style().setColor(TextFormatting.WHITE)))
					.appendSibling(notif);
		return p.appendSibling(new TextComponentString("]").setStyle(new Style().setColor(TextFormatting.RED)))
				.appendSibling(new TextComponentString(" "));
	}

	public static void itemStack(ItemStack itemStack) {
		if (itemStack != null)
			send(getPrefix()
					.appendSibling(
							new TextComponentString(
									I18n.format("gui.act.give.msg"))
											.setStyle(
													new Style()
															.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
																	new TextComponentString("{id:\""
																			+ Item.REGISTRY
																					.getNameForObject(
																							itemStack.getItem())
																					.toString()
																			+ "\",Count:" + itemStack.getCount() + "b"
																			+ (itemStack.getTagCompound() != null
																					? ",tag:" + itemStack
																							.getTagCompound().toString()
																					: "")
																			+ "}")))
															.setClickEvent(new ClickEvent(Action.RUN_COMMAND,
																	"/" + ACTMod.theCommand.getName() + " "
																			+ ACTMod.theCommand.openGiver.getName()
																			+ " "
																			+ ItemUtils.getGiveCode(itemStack))))));
		else
			error(I18n.format("gui.act.give.fail2"));
	}

	public static void send(ITextComponent message) {
		if (Minecraft.getMinecraft().player != null)
			Minecraft.getMinecraft().player.sendMessage(message);
	}

	public static void show(String message) {
		send(getPrefix().appendText(message));
	}
}
