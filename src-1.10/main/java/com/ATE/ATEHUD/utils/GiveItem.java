package com.ATE.ATEHUD.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import com.ATE.ATEHUD.FakeItems3;

public class GiveItem {
	public static void give(Minecraft mc,ItemStack stack){
		if(mc.thePlayer.capabilities.isCreativeMode){
			for(int i = 0; i < 36; i++)
				if(mc.thePlayer.inventory.getStackInSlot(i) == null)
				{
					mc.thePlayer.inventory.setInventorySlotContents(i, stack);
					Chat.itemStack(stack);
					return;
				}
			Chat.error(I18n.format("gui.act.give.fail"));
		}else{
			Chat.error(I18n.format("gui.act.nocreative"));
		}
	}
	public static boolean canGive(Minecraft mc){
		for(int i = 0; i < 9; i++)
			if(mc.thePlayer.inventory.getStackInSlot(i) == null)
			{
				return true;
			}
		return false;
	}
}
