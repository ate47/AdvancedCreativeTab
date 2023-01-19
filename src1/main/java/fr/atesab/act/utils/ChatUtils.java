package fr.atesab.act.utils;

import fr.atesab.act.ModMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.ResourceLocation;

public class ChatUtils {
	public static final char MODIFIER = '\u00a7';
	
	public static void error(String error) {
		ModMain.mc.player.sendStatusMessage(getErrorPrefix().appendText(error), false);
	}
	public static ITextComponent getErrorPrefix() {
		return getPrefix(new TextComponentString(I18n.format("gui.act.error")).setStyle(new Style().setColor(TextFormatting.DARK_RED)), 
				TextFormatting.RED);
	}
	public static ITextComponent getPrefix() {
		return getPrefix(null, null);
	}
	
	private static ITextComponent getPrefix(ITextComponent notif, TextFormatting endColor) {
		Style dark_red = new Style().setColor(TextFormatting.DARK_RED);
		ITextComponent p = new TextComponentString("[").setStyle(dark_red.createDeepCopy()).appendText(ModMain.MOD_LITTLE_NAME); 
		if(notif!=null)p.appendText("/").setStyle(new Style().setColor(TextFormatting.WHITE)).appendSibling(notif);
		return p.appendText("]").setStyle(dark_red.createDeepCopy()).appendText(" ")
				.setStyle(new Style().setColor(endColor!=null?endColor:TextFormatting.GOLD));
	}

	public static void itemStack(ItemStack itemStack) {
		if(itemStack != null) ModMain.mc.player.sendStatusMessage(getPrefix().appendText(I18n.format("gui.act.give.msg"))
					.setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponentString("{\"id\":\""
							+ Item.REGISTRY.getNameForObject(itemStack.getItem()).toString()+"\""+
							(itemStack.getTagCompound() != null?",\"tag\":" + itemStack.getTagCompound().toString():"")+"}")))), false);
		else error(I18n.format("gui.act.give.fail2") + " (itemStack=null)");
	}

	public static void show(String message) {
		ModMain.mc.player.sendStatusMessage(getErrorPrefix().appendText(message), false);
	}
}
