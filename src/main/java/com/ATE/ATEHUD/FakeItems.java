package com.ATE.ATEHUD;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.superclass.Explosion;
import com.ATE.ATEHUD.superclass.Firework;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ChatColorEntry;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

public class FakeItems extends Item {

	public static final String book_nbt="{pages:[\"[\\\"\\\",{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.command")+" :\\\",\\\"bold\\\":true,\\\"underlined\\\":true},{\\\"text\\\":\\\"\\n\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command"+
	        "\\\",\\\"value\\\":\\\"/home\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.home")+"\\n\\\",\\\"hoverEvent"+
	        "\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\\",\\\"color\\\":\\\"dark_gray\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_com"+
	        "mand\\\",\\\"value\\\":\\\"/plot home\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.plothome")+"\\\","+
	        "\\\"color\\\":\\\"dark_gray\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent"+
	        "\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/spawn\\\"},"+
	        "\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.spawn")+"\\n\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\","+
	        "\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.helloworld")+"\\\",\\\"color\\\":\\\"dark_gray\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/tellraw"+
	        " @a {\\\\\\\"text\\\\\\\":\\\\\\\""+LanguageRegistry.instance().getStringLocalization("act.book.helloworld")+"\\\\\\\",\\\\\\\"bold\\\\\\\":true,\\\\\\\"color\\\\\\\":\\\\\\\"dark_red\\\\\\\"}\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+"\\\",\\\"bold\\\":t"+
	        "rue,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"te"+
	        "xt\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.givehead")+"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/give @p minecraft:skull 1 3 {SkullOwner: "+Minecraft.getMinecraft().getSession().getUsername()+"}\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.executecommand")+""+
	        "e\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\n\\n\\\"},{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.page2")+"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":2},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.gopage")+
	        "\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"}}},{\\\"text\\\":\\\"\\n\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.gopage")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_gr"+
	        "een\\\"}}},{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.page3")+"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":3},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.gopage")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_gree"+
	        "n\\\"}}},{\\\"text\\\":\\\"\\n \\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.format")+" :\\\",\\\"bold\\\":true,\\\"underlined\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"\\n\\n"+LanguageRegistry.instance().getStringLocalization("act.book.colors")+" :\\\",\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"te"+
	        "xt\\\":\\\"&0 \\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&1\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_blue\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&2\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" \\\",\\\"colo"+
	        "r\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&3\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_aqua\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&4\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"\\n\\\",\\\"color"+
	        "\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&5\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_purple\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&6\\\",\\\"bold\\\":true,\\\"color\\\":\\\"gold\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bo"+
	        "ld\\\":true},{\\\"text\\\":\\\"&7\\\",\\\"bold\\\":true,\\\"color\\\":\\\"gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&8\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bo"+
	        "ld\\\":true},{\\\"text\\\":\\\"&9\\\",\\\"bold\\\":true,\\\"color\\\":\\\"blue\\\"},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&a\\\",\\\"bold\\\":true,\\\"color\\\":\\\"green\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"t"+
	        "ext\\\":\\\"&b\\\",\\\"bold\\\":true,\\\"color\\\":\\\"aqua\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&c\\\",\\\"bold\\\":true,\\\"color\\\":\\\"red\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\""+
	        ":\\\"&d\\\",\\\"bold\\\":true,\\\"color\\\":\\\"light_purple\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&e\\\",\\\"bold\\\":true,\\\"color\\\":\\\"yellow\\\"},{\\\"text\\\":\\\"\\n\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\""+
	        ""+LanguageRegistry.instance().getStringLocalization("act.book.formating")+"\\\",\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&n\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&m\\\",\\\"strikethrough\\\":true},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\""+
	        "reset\\\"},{\\\"text\\\":\\\"&l\\\",\\\"bold\\\":true},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"k\\\",\\\"obfuscated\\\":true},{\\\"text\\\":\\\"&k \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&o\\\",\\\"italic\\\":true},{\\\"text\\\":\\\"\\n\\n \\\",\\\"c"+
	        "olor\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"ACT - Advanced Creative Tab\\\",\\\"bold\\\":true,\\\"underlined\\\":true,\\\"color\\\":\\\"dark_aqua\\\"},{\\\"text\\\":\\\"\\n\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.author")+"\\\",\\\"bold\\\":true},{\\\"text\\\":\\\": ATE"+
	        "47\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\""+LanguageRegistry.instance().getStringLocalization("act.book.version")+"\\\",\\\"bold\\\":true},{\\\"text\\\":\\\": "+ModMain.Version+"\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Minecraft\\\",\\\"bold\\\":true},{\\\"text\\\":\\\": 1.8\\n\\nBorried about the old minecraft stuff ? This mod is made for you ! it"+
	        " add new unreal Item in your creative mode\\\",\\\"color\\\":\\\"reset\\\"}]\"],title:\""+LanguageRegistry.instance().getStringLocalization("act.book.title")+"\",author:ATE47,display:{Lore:[\""+LanguageRegistry.instance().getStringLocalization("act.book.lore")+"\"]}}"; 
	@Deprecated
    public FakeItems(){
		setCreativeTab(ModMain.ATEcreativeTAB);
		setHasSubtypes(true);
	}
    public static ItemStack setMaxEnchant(ItemStack itst, String name) {
    	itst.addEnchantment(Enchantment.fireAspect, 127);
    	itst.addEnchantment(Enchantment.aquaAffinity, 127);
    	itst.addEnchantment(Enchantment.baneOfArthropods, 127);
    	itst.addEnchantment(Enchantment.blastProtection, 127);
    	itst.addEnchantment(Enchantment.depthStrider, 127);
    	itst.addEnchantment(Enchantment.efficiency, 127);
    	itst.addEnchantment(Enchantment.featherFalling, 127);
    	itst.addEnchantment(Enchantment.fireProtection, 127);
    	itst.addEnchantment(Enchantment.flame, 127);
    	itst.addEnchantment(Enchantment.sharpness, 127);
    	itst.addEnchantment(Enchantment.silkTouch, 127);
    	itst.addEnchantment(Enchantment.smite, 127);
    	itst.addEnchantment(Enchantment.fortune, 127);
    	itst.addEnchantment(Enchantment.infinity, 127);
    	itst.addEnchantment(Enchantment.knockback, 127);
    	itst.addEnchantment(Enchantment.looting, 127);
    	itst.addEnchantment(Enchantment.luckOfTheSea, 127);
    	itst.addEnchantment(Enchantment.lure, 127);
    	itst.addEnchantment(Enchantment.power, 127);
    	itst.addEnchantment(Enchantment.thorns, 127);
    	itst.addEnchantment(Enchantment.projectileProtection, 127);
    	itst.addEnchantment(Enchantment.unbreaking, 127);
    	itst.addEnchantment(Enchantment.respiration, 127);
    	itst.addEnchantment(Enchantment.punch, 127);
    	itst.addEnchantment(Enchantment.protection, 127);
    	itst.setStackDisplayName(name);
    	
    	return itst;
	}
    public static ItemStack setPushUpEnchant(ItemStack itst, String name) {
    	itst=getNBT(itst,"{display:{Name:\""+name+"\",Lore:[\""+LanguageRegistry.instance().getStringLocalization("act.minigame.pu.kopeople")+"\"]},AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:0,Operation:1,UUIDLeast:8000,UUIDMost:4000},{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:0.5,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}");
    	itst.addEnchantment(Enchantment.unbreaking, 10);
    	itst.addEnchantment(Enchantment.punch, 5);
    	itst.addEnchantment(Enchantment.knockback, 5);
    	itst.addEnchantment(Enchantment.infinity, 1);
    	return itst;
    }
    public static ItemStack setHyperProtectionEnchant(ItemStack itst, String name, boolean Thorns, boolean cheat) {
    	int lv=10;
    	if(cheat){lv=127;}else{lv=10;}
    	itst.setStackDisplayName(name);
    	itst.addEnchantment(Enchantment.unbreaking, lv);
    	itst.addEnchantment(Enchantment.protection, lv);
    	itst.addEnchantment(Enchantment.projectileProtection, lv);
    	itst.addEnchantment(Enchantment.featherFalling, lv);
    	itst.addEnchantment(Enchantment.fireProtection, lv);
    	itst.addEnchantment(Enchantment.respiration, lv);
    	itst.addEnchantment(Enchantment.aquaAffinity,1);
    	itst.addEnchantment(Enchantment.blastProtection, lv);
    	itst.addEnchantment(Enchantment.depthStrider, lv);
    	if(Thorns) {
        	itst.addEnchantment(Enchantment.thorns, 42);
    	}
    	return itst;
    }
    public static ItemStack setMeta(ItemStack is, int meta,String name) {
    	is.setItemDamage(meta);
    	if(name!=null)is.setStackDisplayName(name);
    	return is;
    }

    public static ItemStack setMeta(ItemStack is, int meta) {
    	return setMeta(is, meta, null);
    }
    public static ItemStack getChestNBT(String name) {
    	ItemStack is = new ItemStack(Items.fireworks);
    	if (is.getTagCompound() == null)
        {
            is.setTagCompound(new NBTTagCompound());
        }
    	if (is.getTagCompound() != null) is.getTagCompound().setString("Items", "");
    	is.setStackDisplayName(name);
    	return is;
    }
    public static String createNBTFirework() {
    	
    	return null;
    }
    public static ItemStack getFireWork(Firework fw) {
    	ItemStack is=getNBT(Items.fireworks,fw.getNBTFirework());
    	is.addEnchantment(Enchantment.unbreaking, 10);
		System.out.println(is.getTagCompound().toString());
    	return is;
    }
    public static ItemStack setName(ItemStack is,String name) {
    	is.setStackDisplayName(name);
    	return is;
    }

    public static ItemStack getCMD(String cmd, String name) {
    	String cmdmod=cmd.replaceFirst("\\\\", "\\\\");
    	cmdmod=cmd.replaceFirst("\"", "\\\"");
    	ItemStack is=new ItemStack(Blocks.command_block);
    	if (is.getTagCompound() == null)
        {
            is.setTagCompound(new NBTTagCompound());
        }
    	NBTTagCompound cmd2=new NBTTagCompound();
    	cmd2.setString("Command", cmdmod);
    	if (is.getTagCompound() != null) is.getTagCompound().setTag("BlockEntityTag", cmd2);
    	System.out.println("Create commandblock with cmd="+cmd);
		System.out.println(is.getTagCompound().toString());
    	is.setStackDisplayName(name);
    	return is;
    }
    public static ItemStack getNBT(Item item, String nbt) {
        ItemStack is = new ItemStack(item);
    	return getNBT(is, nbt);
    }
    public static ItemStack getNBT(ItemStack is, String nbt) {
    	try {
			is.setTagCompound(JsonToNBT.func_180713_a(nbt));
		} catch (NBTException e) {
			e.printStackTrace();
		}
    	return is;
    }
    private ArrayList<AttributeModifier> modifiers = new ArrayList();
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
    {
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//1
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//2
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//3
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//4
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),4,LanguageRegistry.instance().getStringLocalization("act.urealobjet"))); //5
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//6
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//7
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//8
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//9
        
        subItems.add(new ItemStack(Blocks.barrier));				//1
        subItems.add(new ItemStack(Blocks.red_mushroom_block));		//2
        subItems.add(new ItemStack(Blocks.brown_mushroom_block));	//3
        subItems.add(new ItemStack(Blocks.command_block));			//4
        subItems.add(new ItemStack(Blocks.dragon_egg));				//5
        subItems.add(new ItemStack(Blocks.mob_spawner));			//6
        subItems.add(new ItemStack(Blocks.farmland));				//7
        subItems.add(new ItemStack(Blocks.lit_furnace));			//8
        subItems.add(new ItemStack(Items.enchanted_book));			//9
        subItems.add(setMeta(new ItemStack(Items.spawn_egg),0));				//1
        subItems.add(new ItemStack(Items.command_block_minecart));
        subItems.add(getCMD("/tellraw @a \"\u00a7f<\u00a74SuperCommandBlock\u00a7f> \u00a74H\u00a73e\u00a7dl\u00a72l\u00a7eo \u00a7fW\u00a7ao\u00a76r\u00a7bl\u00a79d\"", "\u00a74H\u00a73e\u00a7dl\u00a72l\u00a7eo \u00a7fW\u00a7ao\u00a76r\u00a7bl\u00a79d \u00a7b(Solo Only)"));	//2
        //LE PUTAIN DE BOUQUIN DES FAMILLES
        subItems.add(getNBT(Items.written_book,book_nbt));	//3
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//5
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//6
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//7
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//8
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//9

        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//1
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//2
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//3
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//4
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),4,LanguageRegistry.instance().getStringLocalization("act.cheatobjet"))); //5
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//6
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//7
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//8
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//9
        
        subItems.add(setMaxEnchant(new ItemStack(Items.blaze_rod),LanguageRegistry.instance().getStringLocalization("act.cheat.wand")));
        subItems.add(setMaxEnchant(getNBT(Items.diamond_sword,"{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000000,Operation:200,UUIDLeast:8000,UUIDMost:4000}]}"),LanguageRegistry.instance().getStringLocalization("act.cheat.sword")));
        subItems.add(setMaxEnchant(new ItemStack(Items.bow),LanguageRegistry.instance().getStringLocalization("act.cheat.bow")));
        subItems.add(setMaxEnchant(new ItemStack(Items.enchanted_book),LanguageRegistry.instance().getStringLocalization("act.cheat.book")));
        subItems.add(setMaxEnchant(new ItemStack(Items.diamond_pickaxe),LanguageRegistry.instance().getStringLocalization("act.cheat.pickaxe")));
        subItems.add(setHyperProtectionEnchant(getNBT(Items.diamond_boots, "{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.maxHealth\",Name:\"generic.maxHealth\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:2,Operation:1,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.knockbackResistance\",Name:\"generic.knockbackResistance\",Amount:1,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}"),LanguageRegistry.instance().getStringLocalization("act.cheat.boots"),true,true));
        subItems.add(setHyperProtectionEnchant(getNBT(Items.diamond_leggings, "{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.maxHealth\",Name:\"generic.maxHealth\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:2,Operation:1,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.knockbackResistance\",Name:\"generic.knockbackResistance\",Amount:1,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}"),LanguageRegistry.instance().getStringLocalization("act.cheat.leggins"),true,true));
        subItems.add(setHyperProtectionEnchant(getNBT(Items.diamond_chestplate, "{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.maxHealth\",Name:\"generic.maxHealth\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:2,Operation:1,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.knockbackResistance\",Name:\"generic.knockbackResistance\",Amount:1,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}"),LanguageRegistry.instance().getStringLocalization("act.cheat.chestplate"),true,true));
        subItems.add(setHyperProtectionEnchant(getNBT(Items.diamond_helmet, "{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.maxHealth\",Name:\"generic.maxHealth\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:2,Operation:1,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.knockbackResistance\",Name:\"generic.knockbackResistance\",Amount:1,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}"),LanguageRegistry.instance().getStringLocalization("act.cheat.helmet"),true,true));

        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//1
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//2
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//3
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//4
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),4,LanguageRegistry.instance().getStringLocalization("act.hyperprotectobjet") )); //5
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//6
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//7
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//8
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//9
        
        subItems.add(setHyperProtectionEnchant(new ItemStack(Items.diamond_boots),LanguageRegistry.instance().getStringLocalization("act.hyper.boots"),false,false));
        subItems.add(setHyperProtectionEnchant(new ItemStack(Items.diamond_leggings),LanguageRegistry.instance().getStringLocalization("act.hyper.leggins"),false,false));
        subItems.add(setHyperProtectionEnchant(new ItemStack(Items.diamond_chestplate),LanguageRegistry.instance().getStringLocalization("act.hyper.chestplate"),false,false));
        subItems.add(setHyperProtectionEnchant(new ItemStack(Items.diamond_helmet),LanguageRegistry.instance().getStringLocalization("act.hyper.helmet"),false,false));
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//5
        subItems.add(setHyperProtectionEnchant(new ItemStack(Items.chainmail_boots),LanguageRegistry.instance().getStringLocalization("act.hyperthorns.boots"),true,false));
        subItems.add(setHyperProtectionEnchant(new ItemStack(Items.chainmail_leggings),LanguageRegistry.instance().getStringLocalization("act.hyperthorns.leggins"),true,false));
        subItems.add(setHyperProtectionEnchant(new ItemStack(Items.chainmail_chestplate),LanguageRegistry.instance().getStringLocalization("act.hyperthorns.chestplate"),true,false));
        subItems.add(setHyperProtectionEnchant(new ItemStack(Items.chainmail_helmet),LanguageRegistry.instance().getStringLocalization("act.hyperthorns.helmet"),true,false));
        
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//1
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//2
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//3
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//4
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),4,LanguageRegistry.instance().getStringLocalization("act.minigameobjet") )); //5
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//6
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//7
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//8
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//9

        subItems.add(setPushUpEnchant(new ItemStack(Items.diamond_hoe),LanguageRegistry.instance().getStringLocalization("act.minigame.pu.hoe")));
        subItems.add(setPushUpEnchant(new ItemStack(Items.stick),LanguageRegistry.instance().getStringLocalization("act.minigame.pu.stick")));
        subItems.add(setPushUpEnchant(new ItemStack(Items.bow),LanguageRegistry.instance().getStringLocalization("act.minigame.pu.bow")));        
        subItems.add(setName(new ItemStack(Items.arrow),LanguageRegistry.instance().getStringLocalization("act.minigame.pu.arrow")));
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//5
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//6
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//7
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//8
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),0, " "));	//9

        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//1
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//2
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//3
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//4
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),4,LanguageRegistry.instance().getStringLocalization("act.fireworks") )); //5
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//6
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//7
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//8
        subItems.add(setMeta(new ItemStack(Blocks.stained_glass_pane),3, " "));	//9

        subItems.add(setName(new Firework(new Explosion[] {
	        new Explosion(1, 1, 2, new int[]{Explosion.getColorWithRGB(255, 0, 0)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(170, 0, 0)}, new int[]{}),
	        new Explosion(0, 0, 4, new int[]{Explosion.getColorWithRGB(255, 85, 255)}, new int[]{}),
	        new Explosion(1, 1, 0, new int[]{Explosion.getColorWithRGB(255, 255, 85)}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Explosion.getColorWithRGB(170, 0, 170)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(85, 255, 255)}, new int[]{}),
	        new Explosion(0, 1, 2, new int[]{Explosion.getColorWithRGB(255, 70, 0)}, new int[]{})
        }, 1).getItemStack(),"\u00a76Orange life"));
        subItems.add(setName(new Firework(new Explosion[] {
	        new Explosion(1, 1, 2, new int[]{Explosion.getColorWithRGB(255, 0, 0)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(255, 255, 0)}, new int[]{}),
	        new Explosion(0, 1, 1, new int[]{Explosion.getColorWithRGB(255, 0, 255)}, new int[]{}),
	        new Explosion(1, 1, 0, new int[]{Explosion.getColorWithRGB(0, 255, 255)}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Explosion.getColorWithRGB(255, 255, 128)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(255, 0, 128)}, new int[]{}),
	        new Explosion(0, 1, 2, new int[]{Explosion.getColorWithRGB(128, 0, 255)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(255, 128, 0)}, new int[]{})
        }, -20).getItemStack(),"\u00a7dMulticolors boom"));
		
        subItems.add(setName(new Firework(new Explosion[] {
	        new Explosion(1, 1, 2, new int[]{Colors.RED}, new int[]{}),
	        new Explosion(1, 1, 4, new int[]{Colors.GOLD}, new int[]{}),
	        new Explosion(0, 1, 1, new int[]{Colors.DARK_RED}, new int[]{}),
	        new Explosion(1, 1, 0, new int[]{Colors.YELLOW}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Colors.WHITE}, new int[]{}),
	        new Explosion(1, 1, 3, new int[]{Colors.LIGHT_PURPLE}, new int[]{})
        }, 1).getItemStack(),"\u00a76Go\u00a7cld\u00a76en\u00a7c Re\u00a76d"));
        subItems.add(setName(new Firework(new Explosion[] {
	        new Explosion(1, 1, 2, new int[]{Colors.azure1}, new int[]{}),
	        new Explosion(1, 1, 4, new int[]{Colors.aquamarine4}, new int[]{}),
	        new Explosion(0, 1, 1, new int[]{Colors.blue1}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Colors.BLUE}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Colors.DarkSeaGreen1}, new int[]{}),
	        new Explosion(0, 1, 4, new int[]{Colors.DARK_BLUE}, new int[]{}),
	        new Explosion(1, 1, 2, new int[]{Colors.DARK_AQUA}, new int[]{})
        }, 1).getItemStack(),"\u00a79Blue Fire"));
        subItems.add(setName(new Firework().getItemStack(),"\u00a7fB\u00a77asic"));
    }

}
