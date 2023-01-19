package fr.atesab.act.utils;

import fr.atesab.act.FakeItems3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.util.ResourceLocation;

public class GiveUtils {
	public static boolean canGive(Minecraft mc) {
		for (int i = 0; i < 9; i++)
			if (mc.player.inventory.getStackInSlot(i) == null) {
				return true;
			}
		return false;
	}

	public static void give(Minecraft mc, ItemStack stack) {
		if (mc.player.capabilities.isCreativeMode) {
			for (int i = 0; i < 36; i++)
				if (mc.player.inventory.getStackInSlot(i).getItem().equals(new ItemStack(Blocks.AIR).getItem())) {
					mc.player.inventory.setInventorySlotContents(i, stack);
					mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(i, stack));
					ChatUtils.itemStack(stack);
					return;
				}
			ChatUtils.error(I18n.format("gui.act.give.fail"));
		} else ChatUtils.error(I18n.format("gui.act.nocreative"));
	}
	public static String getItemStack(ItemStack itemStack) {
		return itemStack==null?null:Item.REGISTRY.getNameForObject(itemStack.getItem()).toString() + " 1 "
		+ itemStack.getMetadata() + (itemStack.getTagCompound() != null?" " + itemStack.getTagCompound().toString():"");
	}
}
