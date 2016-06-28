package com.ATE.ATEHUD;


import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.ATE.ATEHUD.superclass.Head;
import com.ATE.ATEHUD.utils.ItemStackGenHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class FakeItems3 extends Item {

	@Deprecated
    public FakeItems3(){
		setCreativeTab(ModMain.ATEcreativeTAB3);
		setHasSubtypes(true);
	}
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
    {
    	if(ModMain.AdvancedModActived)System.out.println("Adding fake item");
    	for (int i = 0; i < ModMain.HeadNames.length; i++) {
			subItems.add(new Head(ModMain.HeadNames[i]).getHead());
			if(ModMain.AdvancedModActived)System.out.println("Add "+ModMain.HeadNames[i]+"'s head");
		}
    	if(ModMain.AdvancedModActived)System.out.println("Loading custom command block ...");
    	for (int i = 0; i < ModMain.CustomCommandBlock.length; i++) {
    		String cmd=ModMain.CustomCommandBlock[i].replaceFirst("\"", "\\\"");
        	cmd=ModMain.CustomCommandBlock[i].replaceAll("&&", "\u00a7")+"";
			subItems.add(ItemStackGenHelper.getCMD(cmd,I18n.format("act.cstcommand")+i));
			if(ModMain.AdvancedModActived)System.out.println("Add Custom CommandBlock #"+i+" with :\""+ModMain.CustomCommandBlock[i]+"\"");
		}
    	if(ModMain.AdvancedModActived)System.out.println("Loading custom firework ...");
    	for (int i = 0; i < ModMain.CustomFirework.length; i++) {
        	String nbt=ModMain.CustomFirework[i].replaceAll("&&", "\u00a7")+"";
			subItems.add(ItemStackGenHelper.setName(ItemStackGenHelper.getNBT(Items.FIREWORKS,nbt),I18n.format("act.cstfirework")+i));
			if(ModMain.AdvancedModActived)System.out.println("Add Custom Firework #"+i+" with :\""+ModMain.CustomFirework[i]+"\"");
		}
    	if(ModMain.AdvancedModActived)System.out.println("Loading advanced item ...");
    	for (int i = 0; i < ModMain.AdvancedItem.length; i++) {
    		if(ModMain.AdvancedModActived)System.out.println("Add Advanced Item #"+i+"...");
			try {
				if(ItemStackGenHelper.getGive(ModMain.AdvancedItem[i])==null){
					if(ModMain.AdvancedModActived)System.out.println("Failed add Advanced Item #"+i);
				}else{
					subItems.add(ItemStackGenHelper.getGive(ModMain.AdvancedItem[i]));
				}
			} catch (NumberInvalidException e) {
				if(ModMain.AdvancedModActived)System.out.println("Failed add Advanced Item #"+i);
			}
			
		}
    	if(ModMain.AdvancedModActived)System.out.println("Finish add Item");
    }
}
