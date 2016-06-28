package com.ATE.ATEHUD.utils;

import com.ATE.ATEHUD.ModMain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.ResourceLocation;

public class Chat {
	public static String prefixError="\u00a74[\u00a7c"+ModMain.ModLittleName+"\u00a7f/\u00a74"+I18n.format("gui.act.error")+"\u00a74]",
			prefixShow="\u00a74[\u00a7c"+ModMain.ModLittleName+"\u00a74]";
	public static void error(String error){
		Minecraft mc=Minecraft.getMinecraft();
		mc.thePlayer.addChatComponentMessage(new TextComponentString(prefixError+" \u00a7c"+error));
	}
	public static void show(String message){
		Minecraft mc=Minecraft.getMinecraft();
		mc.thePlayer.addChatComponentMessage(new TextComponentString(prefixShow+" \u00a76"+message));
	}
	public static void itemStack(ItemStack itemStack){
		if(itemStack!=null){
			Minecraft mc=Minecraft.getMinecraft();
			String tag="";
			if(itemStack.getTagCompound()!=null){tag=",tag:"+itemStack.getTagCompound().toString();}
			ITextComponent cc=new TextComponentString(""+prefixShow+" \u00a76"+I18n.format("gui.act.give.msg"));
			Style style = cc.getStyle();
			
			HoverEvent he=new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponentString("{id:"+((ResourceLocation)Item.REGISTRY.getNameForObject(itemStack.getItem())).toString()+tag+"}"));
			style.setHoverEvent(he);
			cc.setStyle(style);
			mc.thePlayer.addChatComponentMessage(cc);
		}else{
			error(I18n.format("gui.act.give.fail2")+" (itemStack=null)");
		}
	}
}
