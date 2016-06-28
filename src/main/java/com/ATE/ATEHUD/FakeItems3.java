package com.ATE.ATEHUD;


import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

public class FakeItems3 extends Item {

	@Deprecated
    public FakeItems3(){
		setCreativeTab(ModMain.ATEcreativeTAB3);
		setHasSubtypes(true);
	}
	/**
	 * Not implemented yet / Add custom head
	 * @param url link to your skin image : skins.minecraft.net/MinecraftSkins/USERNAME.png
	 * @param name Your Username
	 * @return
	 */
    public static ItemStack getCustomSkull(String url,String name) {
    	
    	String uuid=UUID.randomUUID().toString();
        byte[] encodedData = Base64.getEncoder().encode(String.format("{\"timestamp\":1429453091873,\"profileId\":\""+uuid.replaceAll("-", "")+"\",textures:{SKIN:{url:\""+url+"\"}}}", url).getBytes());
        ItemStack head=FakeItems.getNBT(new ItemStack(Items.skull, 1, (short) 3), "{SkullOwner:{Id:\""+uuid+"\",Properties:{textures:[{Value:\""+(new String(encodedData))+"\"}]}}}");
        return head;
    }
    public static ItemStack getGive(String code) throws NumberInvalidException {
        ItemStack itemstack = null;
    	String[] args=code.split(" ");
        ResourceLocation resourcelocation = new ResourceLocation(args[0]);
        Item itema = (Item)Item.itemRegistry.getObject(resourcelocation);
        Item item = null;
        if (itema == null)
        {
	    	System.out.println("Bad item id : "+args[0]);
	    }
        else
        {
            item=itema;
            int i = args.length >= 2 ? CommandBase.parseInt(args[1]) : 1;
            int j = args.length >= 3 ? CommandBase.parseInt(args[2]) : 0;
            itemstack = new ItemStack(item, i, j);

            if (args.length >= 4)
            {
            	String arg5=args[3];
            	if (args.length > 4){
            		for (int i1 = 4; i1 < args.length; i1++) {
            			arg5=arg5+" "+args[i1];
            		}
            	}
            	String arg5b=arg5.replaceAll("&&", "\u00a7")+"";
            	System.out.println("NBT : "+arg5b);
            	itemstack=FakeItems.getNBT(itemstack, arg5b);
            }
        }
		return itemstack;
    }
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
    {
    	
    	System.out.println();
    	for (int i = 0; i < ModMain.HeadNames.length; i++) {
			subItems.add(FakeItems2.getHead(ModMain.HeadNames[i]));
			//System.out.println("Add "+ModMain.HeadNames[i]+"'s head");
		}
    	for (int i = 0; i < ModMain.CustomCommandBlock.length; i++) {
    		String cmd=ModMain.CustomCommandBlock[i].replaceFirst("\"", "\\\"");
        	cmd=ModMain.CustomCommandBlock[i].replaceAll("&&", "\u00a7")+"";
			subItems.add(FakeItems.getCMD(cmd,LanguageRegistry.instance().getStringLocalization("act.cstcommand")+i));
			//System.out.println("Add Custom CommandBlock #"+i+" with :\""+ModMain.CustomCommandBlock[i]+"\"");
		}
    	for (int i = 0; i < ModMain.CustomFirework.length; i++) {
        	String nbt=ModMain.CustomFirework[i].replaceAll("&&", "\u00a7")+"";
			subItems.add(FakeItems.setName(FakeItems.getNBT(Items.fireworks,nbt),LanguageRegistry.instance().getStringLocalization("act.cstfirework")+i));
			//System.out.println("Add Custom Firework #"+i+" with :\""+ModMain.CustomFirework[i]+"\"");
		}
    	for (int i = 0; i < ModMain.AdvancedItem.length; i++) {
    		//System.out.println("Add Advanced Item #"+i+"...");
			try {
				if(getGive(ModMain.AdvancedItem[i])==null){
					//System.out.println("Failed add Advanced Item #"+i);
				}else{
					subItems.add(getGive(ModMain.AdvancedItem[i]));
				}
			} catch (NumberInvalidException e) {
				//System.out.println("Failed add Advanced Item #"+i);
			}
			
		}
    }
}
