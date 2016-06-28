package com.ATE.ATEHUD;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public class Chat {
	public static String prefixError="\u00a74[\u00a7c"+ModMain.ModLittleName+"\u00a7f/\u00a74ERROR \u00a74]",
			prefixShow="\u00a74[\u00a7c"+ModMain.ModLittleName+"\u00a74]";
	public static void error(String error){
		Minecraft mc=Minecraft.getMinecraft();
		mc.thePlayer.addChatComponentMessage(new ChatComponentText(prefixError+" \u00a7c"+error));
	}
	public static void show(String message){
		Minecraft mc=Minecraft.getMinecraft();
		mc.thePlayer.addChatComponentMessage(new ChatComponentText(prefixShow+" \u00a76"+message));
	}
	public static void itemStack(ItemStack itemStack){
		Minecraft mc=Minecraft.getMinecraft();
		String tag="";
		if(itemStack.getTagCompound()!=null){tag=",tag:"+itemStack.getTagCompound().toString();}
		IChatComponent cc=new ChatComponentText(""+prefixShow+" \u00a76"+I18n.format("gui.act.give.msg"));
		ChatStyle style = cc.getChatStyle();
		
		HoverEvent he=new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText("{id:"+((ResourceLocation)Item.itemRegistry.getNameForObject(itemStack.getItem())).toString()+tag+"}"));
		style.setChatHoverEvent(he);
		cc.setChatStyle(style);
		mc.thePlayer.addChatComponentMessage(cc);
	}
}
