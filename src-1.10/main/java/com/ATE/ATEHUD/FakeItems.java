package com.ATE.ATEHUD;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.superclass.EnchantmentInfo;
import com.ATE.ATEHUD.superclass.Explosion;
import com.ATE.ATEHUD.superclass.Firework;
import com.ATE.ATEHUD.utils.ItemStackGenHelper;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
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
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ChatColorEntry;

public class FakeItems extends Item {
    //LE PUTAIN DE BOUQUIN DES FAMILLES
	public static final String book_nbt="{pages:[\"[\\\"\\\",{\\\"text\\\":\\\""+I18n.format("act.book.command")+" :\\\",\\\"bold\\\":true,\\\"underlined\\\":true},{\\\"text\\\":\\\"\\n\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command"+
	        "\\\",\\\"value\\\":\\\"/home\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\""+I18n.format("act.book.home")+"\\n\\\",\\\"hoverEvent"+
	        "\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\\",\\\"color\\\":\\\"dark_gray\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_com"+
	        "mand\\\",\\\"value\\\":\\\"/plot home\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\""+I18n.format("act.book.plothome")+"\\\","+
	        "\\\"color\\\":\\\"dark_gray\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent"+
	        "\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/spawn\\\"},"+
	        "\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\""+I18n.format("act.book.spawn")+"\\n\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\","+
	        "\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\""+I18n.format("act.book.helloworld")+"\\\",\\\"color\\\":\\\"dark_gray\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/tellraw"+
	        " @a {\\\\\\\"text\\\\\\\":\\\\\\\""+I18n.format("act.book.helloworld")+"\\\\\\\",\\\\\\\"bold\\\\\\\":true,\\\\\\\"color\\\\\\\":\\\\\\\"dark_red\\\\\\\"}\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+"\\\",\\\"bold\\\":t"+
	        "rue,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"te"+
	        "xt\\\":\\\""+I18n.format("act.book.givehead")+"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/give @p minecraft:skull 1 3 {SkullOwner: "+Minecraft.getMinecraft().getSession().getUsername()+"}\\\"},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.executecommand")+""+
	        "e\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}}},{\\\"text\\\":\\\"\\n\\n\\\"},{\\\"text\\\":\\\""+I18n.format("act.book.page2")+"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":2},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.gopage")+
	        "\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"}}},{\\\"text\\\":\\\"\\n\\\",\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.gopage")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_gr"+
	        "een\\\"}}},{\\\"text\\\":\\\""+I18n.format("act.book.page3")+"\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"change_page\\\",\\\"value\\\":3},\\\"hoverEvent\\\":{\\\"action\\\":\\\"show_text\\\",\\\"value\\\":{\\\"text\\\":\\\""+I18n.format("act.book.gopage")+"\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_gree"+
	        "n\\\"}}},{\\\"text\\\":\\\"\\n \\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\""+I18n.format("act.book.format")+" :\\\",\\\"bold\\\":true,\\\"underlined\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"\\n\\n"+I18n.format("act.book.colors")+" :\\\",\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"te"+
	        "xt\\\":\\\"&0 \\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&1\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_blue\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&2\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" \\\",\\\"colo"+
	        "r\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&3\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_aqua\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&4\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"\\n\\\",\\\"color"+
	        "\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&5\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_purple\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&6\\\",\\\"bold\\\":true,\\\"color\\\":\\\"gold\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bo"+
	        "ld\\\":true},{\\\"text\\\":\\\"&7\\\",\\\"bold\\\":true,\\\"color\\\":\\\"gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&8\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bo"+
	        "ld\\\":true},{\\\"text\\\":\\\"&9\\\",\\\"bold\\\":true,\\\"color\\\":\\\"blue\\\"},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&a\\\",\\\"bold\\\":true,\\\"color\\\":\\\"green\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"t"+
	        "ext\\\":\\\"&b\\\",\\\"bold\\\":true,\\\"color\\\":\\\"aqua\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&c\\\",\\\"bold\\\":true,\\\"color\\\":\\\"red\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\""+
	        ":\\\"&d\\\",\\\"bold\\\":true,\\\"color\\\":\\\"light_purple\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"&e\\\",\\\"bold\\\":true,\\\"color\\\":\\\"yellow\\\"},{\\\"text\\\":\\\"\\n\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\""+
	        ""+I18n.format("act.book.formating")+"\\\",\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&n\\\",\\\"underlined\\\":true},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&m\\\",\\\"strikethrough\\\":true},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\""+
	        "reset\\\"},{\\\"text\\\":\\\"&l\\\",\\\"bold\\\":true},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"k\\\",\\\"obfuscated\\\":true},{\\\"text\\\":\\\"&k \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"&o\\\",\\\"italic\\\":true},{\\\"text\\\":\\\"\\n\\n \\\",\\\"c"+
	        "olor\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"ACT - Advanced Creative Tab\\\",\\\"bold\\\":true,\\\"underlined\\\":true,\\\"color\\\":\\\"dark_aqua\\\"},{\\\"text\\\":\\\"\\n\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\""+I18n.format("act.book.author")+"\\\",\\\"bold\\\":true},{\\\"text\\\":\\\": ATE"+
	        "47\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\""+I18n.format("act.book.version")+"\\\",\\\"bold\\\":true},{\\\"text\\\":\\\": "+ModMain.Version+"\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Minecraft\\\",\\\"bold\\\":true},{\\\"text\\\":\\\": 1.8\\n\\nBorried about the old minecraft stuff ? This mod is made for you ! it"+
	        " add new unreal Item in your creative mode\\\",\\\"color\\\":\\\"reset\\\"}]\"],title:\""+I18n.format("act.book.title")+"\",author:ATE47,display:{Lore:[\""+I18n.format("act.book.lore")+"\"]}}"; 
	@Deprecated
    public FakeItems(){
		setCreativeTab(ModMain.ATEcreativeTAB);
		setHasSubtypes(true);
	}
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
    {
        subItems=ItemStackGenHelper.addTitle(subItems, I18n.format("act.urealobjet"));
        
        subItems.add(new ItemStack(Blocks.BARRIER));				//1
        subItems.add(new ItemStack(Blocks.RED_MUSHROOM_BLOCK));		//2
        subItems.add(new ItemStack(Blocks.BROWN_MUSHROOM_BLOCK));	//3
        subItems.add(new ItemStack(Blocks.COMMAND_BLOCK));			//4
        subItems.add(new ItemStack(Blocks.DRAGON_EGG));				//5
        subItems.add(new ItemStack(Blocks.MOB_SPAWNER));			//6
        //subItems.add(new ItemStack(Blocks.FARMLAND));				Remove because bug
        //subItems.add(new ItemStack(Blocks.LIT_FURNACE));			Remove because bug
        subItems.add(new ItemStack(Items.ENCHANTED_BOOK));			//9
        subItems.add(ItemStackGenHelper.setMeta(new ItemStack(Items.SPAWN_EGG),0));				//1
        subItems.add(new ItemStack(Items.COMMAND_BLOCK_MINECART));
        
        subItems.add(ItemStackGenHelper.getCMD("/tellraw @a \"\u00a7f<\u00a74SuperCommandBlock\u00a7f> \u00a74H\u00a73e\u00a7dl\u00a72l\u00a7eo \u00a7fW\u00a7ao\u00a76r\u00a7bl\u00a79d\"", "\u00a74H\u00a73e\u00a7dl\u00a72l\u00a7eo \u00a7fW\u00a7ao\u00a76r\u00a7bl\u00a79d \u00a7b(Solo Only)"));	//2
        subItems.add(ItemStackGenHelper.getNBT(Items.WRITTEN_BOOK,book_nbt));	//3
        subItems=ItemStackGenHelper.addBlank(subItems,7);
        
        subItems=ItemStackGenHelper.addTitle(subItems, I18n.format("act.cheatobjet"));
        
        subItems.add(ItemStackGenHelper.setMaxEnchant(new ItemStack(Items.BLAZE_ROD),I18n.format("act.cheat.wand")));
        subItems.add(ItemStackGenHelper.setMaxEnchant(ItemStackGenHelper.getNBT(Items.DIAMOND_SWORD,"{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000000,Operation:200,UUIDLeast:8000,UUIDMost:4000}]}"),I18n.format("act.cheat.sword")));
        subItems.add(ItemStackGenHelper.setMaxEnchant(new ItemStack(Items.BOW),I18n.format("act.cheat.bow")));
        subItems.add(ItemStackGenHelper.setMaxEnchant(new ItemStack(Items.ENCHANTED_BOOK),I18n.format("act.cheat.book")));
        subItems.add(ItemStackGenHelper.setMaxEnchant(new ItemStack(Items.DIAMOND_PICKAXE),I18n.format("act.cheat.pickaxe")));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(ItemStackGenHelper.getNBT(Items.DIAMOND_BOOTS, "{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.maxHealth\",Name:\"generic.maxHealth\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:2,Operation:1,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.knockbackResistance\",Name:\"generic.knockbackResistance\",Amount:1,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}"),I18n.format("act.cheat.boots"),true,true));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(ItemStackGenHelper.getNBT(Items.DIAMOND_LEGGINGS, "{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.maxHealth\",Name:\"generic.maxHealth\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:2,Operation:1,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.knockbackResistance\",Name:\"generic.knockbackResistance\",Amount:1,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}"),I18n.format("act.cheat.leggins"),true,true));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(ItemStackGenHelper.getNBT(Items.DIAMOND_CHESTPLATE, "{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.maxHealth\",Name:\"generic.maxHealth\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:2,Operation:1,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.knockbackResistance\",Name:\"generic.knockbackResistance\",Amount:1,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}"),I18n.format("act.cheat.chestplate"),true,true));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(ItemStackGenHelper.getNBT(Items.DIAMOND_HELMET, "{Unbreakable:1,AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.maxHealth\",Name:\"generic.maxHealth\",Amount:2000,Operation:20,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:2,Operation:1,UUIDLeast:8000,UUIDMost:4000},"+
        "{AttributeName:\"generic.knockbackResistance\",Name:\"generic.knockbackResistance\",Amount:1,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}"),I18n.format("act.cheat.helmet"),true,true));
        subItems=ItemStackGenHelper.addTitle(subItems, I18n.format("act.hyperprotectobjet"));
        
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(new ItemStack(Items.DIAMOND_BOOTS),I18n.format("act.hyper.boots"),false,false));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(new ItemStack(Items.DIAMOND_LEGGINGS),I18n.format("act.hyper.leggins"),false,false));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(new ItemStack(Items.DIAMOND_CHESTPLATE),I18n.format("act.hyper.chestplate"),false,false));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(new ItemStack(Items.DIAMOND_HELMET),I18n.format("act.hyper.helmet"),false,false));
        subItems.add(ItemStackGenHelper.dl);	//5
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(new ItemStack(Items.CHAINMAIL_BOOTS),I18n.format("act.hyperthorns.boots"),true,false));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(new ItemStack(Items.CHAINMAIL_LEGGINGS),I18n.format("act.hyperthorns.leggins"),true,false));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(new ItemStack(Items.CHAINMAIL_CHESTPLATE),I18n.format("act.hyperthorns.chestplate"),true,false));
        subItems.add(ItemStackGenHelper.setHyperProtectionEnchant(new ItemStack(Items.CHAINMAIL_HELMET),I18n.format("act.hyperthorns.helmet"),true,false));
        
        subItems=ItemStackGenHelper.addTitle(subItems, I18n.format("act.minigameobjet"));
        
        subItems.add(ItemStackGenHelper.setPushUpEnchant(new ItemStack(Items.DIAMOND_HOE),I18n.format("act.minigame.pu.hoe")));
        subItems.add(ItemStackGenHelper.setPushUpEnchant(new ItemStack(Items.STICK),I18n.format("act.minigame.pu.stick")));
        subItems.add(ItemStackGenHelper.setPushUpEnchant(new ItemStack(Items.BOW),I18n.format("act.minigame.pu.bow")));        
        subItems.add(ItemStackGenHelper.setName(new ItemStack(Items.ARROW),I18n.format("act.minigame.pu.arrow")));
        
        subItems=ItemStackGenHelper.addBlank(subItems,5);
        subItems=ItemStackGenHelper.addTitle(subItems, I18n.format("act.fireworks"));

        subItems.add(ItemStackGenHelper.setName(new Firework(new Explosion[] {
	        new Explosion(1, 1, 2, new int[]{Explosion.getColorWithRGB(255, 0, 0)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(170, 0, 0)}, new int[]{}),
	        new Explosion(0, 0, 4, new int[]{Explosion.getColorWithRGB(255, 85, 255)}, new int[]{}),
	        new Explosion(1, 1, 0, new int[]{Explosion.getColorWithRGB(255, 255, 85)}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Explosion.getColorWithRGB(170, 0, 170)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(85, 255, 255)}, new int[]{}),
	        new Explosion(0, 1, 2, new int[]{Explosion.getColorWithRGB(255, 70, 0)}, new int[]{})
        }, 1).getItemStack(),"\u00a76Orange life"));
        subItems.add(ItemStackGenHelper.setName(new Firework(new Explosion[] {
	        new Explosion(1, 1, 2, new int[]{Explosion.getColorWithRGB(255, 0, 0)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(255, 255, 0)}, new int[]{}),
	        new Explosion(0, 1, 1, new int[]{Explosion.getColorWithRGB(255, 0, 255)}, new int[]{}),
	        new Explosion(1, 1, 0, new int[]{Explosion.getColorWithRGB(0, 255, 255)}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Explosion.getColorWithRGB(255, 255, 128)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(255, 0, 128)}, new int[]{}),
	        new Explosion(0, 1, 2, new int[]{Explosion.getColorWithRGB(128, 0, 255)}, new int[]{}),
	        new Explosion(1, 1, 1, new int[]{Explosion.getColorWithRGB(255, 128, 0)}, new int[]{})
        }, -20).getItemStack(),"\u00a7dMulticolors boom"));
		
        subItems.add(ItemStackGenHelper.setName(new Firework(new Explosion[] {
	        new Explosion(1, 1, 2, new int[]{Colors.RED}, new int[]{}),
	        new Explosion(1, 1, 4, new int[]{Colors.GOLD}, new int[]{}),
	        new Explosion(0, 1, 1, new int[]{Colors.DARK_RED}, new int[]{}),
	        new Explosion(1, 1, 0, new int[]{Colors.YELLOW}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Colors.WHITE}, new int[]{}),
	        new Explosion(1, 1, 3, new int[]{Colors.LIGHT_PURPLE}, new int[]{})
        }, 1).getItemStack(),"\u00a76Go\u00a7cld\u00a76en\u00a7c Re\u00a76d"));
        subItems.add(ItemStackGenHelper.setName(new Firework(new Explosion[] {
	        new Explosion(1, 1, 2, new int[]{Colors.azure1}, new int[]{}),
	        new Explosion(1, 1, 4, new int[]{Colors.aquamarine4}, new int[]{}),
	        new Explosion(0, 1, 1, new int[]{Colors.blue1}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Colors.BLUE}, new int[]{}),
	        new Explosion(1, 0, 0, new int[]{Colors.DarkSeaGreen1}, new int[]{}),
	        new Explosion(0, 1, 4, new int[]{Colors.DARK_BLUE}, new int[]{}),
	        new Explosion(1, 1, 2, new int[]{Colors.DARK_AQUA}, new int[]{})
        }, 1).getItemStack(),"\u00a79Blue Fire"));
        subItems.add(ItemStackGenHelper.setName(new Firework().getItemStack(),"\u00a7fB\u00a77asic"));
    }

}
