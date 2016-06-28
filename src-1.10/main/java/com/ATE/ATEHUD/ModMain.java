package com.ATE.ATEHUD;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale.LanguageRange;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;

import com.ATE.ATEHUD.superclass.Effect;
import com.ATE.ATEHUD.superclass.Enchantments;
import com.ATE.ATEHUD.superclass.Head;

import scala.tools.nsc.backend.icode.Members.Local;


@Mod(modid=ModMain.ModId, version = ModMain.Version, name=ModMain.Name, guiFactory = ModMain.GuiFactory, clientSideOnly=true)
@SideOnly(Side.CLIENT)
public class ModMain
{
	public static final String ModId="ATEHUD",ModLittleName="ACT-Mod", Version="1.2.2", Name="Advanced Creative Tab", GuiFactory="com.ATE.ATEHUD.gui.GuiFactory";
    public static Item fakeItem,fakeItem2,fakeItem3,iconItem; //Fake item to add subitems
    public static CreativeTabs ATEcreativeTAB = new ATEcreativeTAB(CreativeTabs.getNextID(), "ateTab"),
    	ATEcreativeTAB2 = new ATEcreativeTAB2(CreativeTabs.getNextID(), "ateTab2"),
    	ATEcreativeTAB3 = new ATEcreativeTAB3(CreativeTabs.getNextID(), "ateTab3"); //creativeTab
	public static String[] HeadNames={"ATE47"},
		AdvancedItem={"wool 42 2 {display:{Name:\"Pink verity\",Lore:[\"42 is life\",\"wait what ?\"]}}"},
		CustomFirework={"{Fireworks:{Flight:1,Explosions:[{Type:1,Colors:[7995154,16252672,15615],FadeColors:[16777215]}]}}"},
		CustomCommandBlock={"/tellraw @a \"<SuperCommandBlock> Hello world !\""};  //config
    public static int MaxLevelEnch=127,MaxLevelPotion=127;
	public static Configuration configFile,headList,builder;
	public static KeyBinding guifactory,nbtitem;
	@Mod.Instance(ModId)
    public static ModMain instance;

    public static Object[] copyOf(Object[] original, int newLength, Class newType) {
        Object[] arr = (newType == Object[].class) ? new Object[newLength] :
            (Object[])Array.newInstance(newType.getComponentType(), newLength);
        int len  = (original.length < newLength ? original.length : newLength);
        System.arraycopy(original, 0, arr, 0, len);
        return arr;
    }
	public static boolean AdvancedModActived=false;
	/**
	 * Add a specific config (Yes I useless but I like it ^^)
	 * @param type type=["AdvancedItem","HeadNames","CustomFirework","CustomCommandBlock"]
	 * @param value
	 */
	
	public static void addConfig(String type,String value){
		if(type=="AdvancedItem"){
			AdvancedItem=(String[]) copyOf(AdvancedItem, AdvancedItem.length+1, String[].class);
			AdvancedItem[AdvancedItem.length-1]=value;
		}
		if(type=="HeadNames"){
			HeadNames=(String[]) copyOf(HeadNames, HeadNames.length+1, String[].class);
			HeadNames[HeadNames.length-1]=value;
		}
		if(type=="CustomFirework"){
			CustomFirework=(String[]) copyOf(CustomFirework, CustomFirework.length+1, String[].class);
			CustomFirework[CustomFirework.length-1]=value;
		}
		if(type=="CustomCommandBlock"){
			CustomCommandBlock=(String[]) copyOf(CustomCommandBlock, CustomCommandBlock.length+1, String[].class);
			CustomCommandBlock[CustomCommandBlock.length-1]=value;
		}
		configFile.get(Configuration.CATEGORY_GENERAL,"Give command (Item/Block) (number) (meta) (nbt)",AdvancedItem).setValues(AdvancedItem);
		configFile.get(Configuration.CATEGORY_GENERAL,"Custom Head",HeadNames).setValues(HeadNames);
		configFile.get(Configuration.CATEGORY_GENERAL,"Custom Firework (NBT)",CustomFirework).setValues(CustomFirework);
		configFile.get(Configuration.CATEGORY_GENERAL,"Custom commandBlock (command)",CustomCommandBlock).setValues(CustomCommandBlock);
		configFile.save();
	}
    public static void syncConfig() {
    	MaxLevelEnch=configFile.getInt("MaxLevelEnch",Configuration.CATEGORY_GENERAL,MaxLevelEnch,Integer.MIN_VALUE,Integer.MAX_VALUE, "");
    	MaxLevelPotion=configFile.getInt("MaxLevelPotion",Configuration.CATEGORY_GENERAL,MaxLevelPotion,-127,127, "");
    	AdvancedModActived=configFile.getBoolean("Advanced Mode", Configuration.CATEGORY_GENERAL, AdvancedModActived, "Open access to advanced options");
    	AdvancedItem=configFile.getStringList("Give command (Item/Block) (number) (meta) (nbt)",Configuration.CATEGORY_GENERAL, (String[]) AdvancedItem, I18n.format("act.advlist"));
    	HeadNames = configFile.getStringList("Custom Head", Configuration.CATEGORY_GENERAL, HeadNames, I18n.format("act.headlist"));
    	CustomFirework = configFile.getStringList("Custom Firework (NBT)", Configuration.CATEGORY_GENERAL, CustomFirework, I18n.format("act.cstfirework"));
    	CustomCommandBlock = configFile.getStringList("Custom commandBlock (command)", Configuration.CATEGORY_GENERAL, CustomCommandBlock, I18n.format("act.cstcommand"));
        Head.VIP_list = headList.getStringList("VIPList", Configuration.CATEGORY_GENERAL, Head.VIP_list, "Vip from ATE");
        Head.MHF_list = headList.getStringList("MHFList", Configuration.CATEGORY_GENERAL, Head.MHF_list, "Do not touch");
        Head.BickerCraft_list = headList.getStringList("BickerCraftList", Configuration.CATEGORY_GENERAL, Head.BickerCraft_list, "BickerCraft");
        Enchantments.EnchantmentList = builder.getStringList("LIST","ENCHANTMENT", Enchantments.EnchantmentList, "TYPE,NAME,ID,CLASS,FIELD");
        Enchantments.TypeList = builder.getStringList("TYPE","ENCHANTMENT", Enchantments.TypeList, "");
        Effect.effects=builder.getStringList("LIST", "EFFECT", Effect.effects, "NAME,STRONGABLE,LONGABLE");
        Effect.skin=builder.getStringList("SKIN", "EFFECT", Effect.skin, "NAME,ID");
        Effect.type=builder.getStringList("TYPE", "EFFECT", Effect.type, "CLASS,FIELD");
        configFile.save();
        headList.save();
        builder.save();
    }
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        configFile = new Configuration(event.getSuggestedConfigurationFile());
        headList=new Configuration(new File(new File(event.getModConfigurationDirectory(),"ACT-Mod"), "headList.cfg"));
    	Enchantments.defineEnchantments();
    	Effect.defineEffects();
        builder=new Configuration(new File(new File(event.getModConfigurationDirectory(),"ACT-Mod"), "builder.cfg"));
        syncConfig();
        GameRegistry.register(fakeItem = new FakeItems().setRegistryName("fakeItem"));//Item in the 1st tab
        GameRegistry.register(fakeItem2 = new FakeItems2().setRegistryName("fakeItem2"));//2nd tab
        GameRegistry.register(fakeItem3 = new FakeItems3().setRegistryName("fakeItem3"));//....
        GameRegistry.register(iconItem = new Item().setRegistryName("potionselector").setUnlocalizedName("potionselector"));//Icon
        ClientRegistry.registerKeyBinding(guifactory = new KeyBinding("key.atehud.guifactory", Keyboard.KEY_N, "key.atehud.categories"));
        ClientRegistry.registerKeyBinding(nbtitem = new KeyBinding("key.atehud.nbtitem", Keyboard.KEY_Y, "key.atehud.categories"));
    	FMLCommonHandler.instance().bus().register(instance);
    	FMLCommonHandler.instance().bus().register(new ATEEventHandler());
    }
    @EventHandler
    public void init(FMLInitializationEvent event){
    	registerRender(iconItem);
        
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
    }
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
      if(eventArgs.getModID().equals(ModMain.ModId))
        syncConfig();
    }
    @EventHandler
    public void modDisabled(FMLModDisabledEvent event) {
    	System.out.println("You disabled me, why ? :c");
    }
    public void registerRender(Item item){
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
        .register(item, 0, new ModelResourceLocation(ModId+":"+item.getUnlocalizedName().substring(5), "inventory"));
    }

}
