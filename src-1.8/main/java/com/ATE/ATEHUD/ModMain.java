package com.ATE.ATEHUD;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;


@Mod(modid=ModMain.ModId, version = ModMain.Version, name=ModMain.Name, guiFactory = ModMain.GuiFactory, clientSideOnly=true)
@SideOnly(Side.CLIENT)
public class ModMain
{
	public static final String ModId="ATEHUD",ModLittleName="ACT-Mod", Version="1.2", Name="Advanced Creative Tab", GuiFactory="com.ATE.ATEHUD.gui.ATEModGuiFactory";
    public static Item fakeItem,fakeItem2,fakeItem3; //Fake item to add subitems
    public static CreativeTabs ATEcreativeTAB = new ATEcreativeTAB(CreativeTabs.getNextID(), "ateTab"),
    	ATEcreativeTAB2 = new ATEcreativeTAB2(CreativeTabs.getNextID(), "ateTab2"),
    	ATEcreativeTAB3 = new ATEcreativeTAB3(CreativeTabs.getNextID(), "ateTab3"); //creativeTab
	public static String[] HeadNames={"ATE47"},
		AdvancedItem={"wool 42 2 {display:{Name:\"Pink verity\",Lore:[\"42 is life\",\"wait what ?\"]}}"},
		CustomFirework={"{Fireworks:{Flight:1,Explosions:[{Type:1,Colors:[7995154,16252672,15615],FadeColors:[16777215]}]}}"},
		CustomCommandBlock={"/tellraw @a \"<SuperCommandBlock> Hello world !\""};  //config
    public static Configuration configFile;
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
	public static boolean AdvancedModActived=true;
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
    	AdvancedModActived=configFile.getBoolean("Advanced Mode", Configuration.CATEGORY_GENERAL, false, "Open access to advanced options");
    	AdvancedItem=configFile.getStringList("Give command (Item/Block) (number) (meta) (nbt)",Configuration.CATEGORY_GENERAL, (String[]) AdvancedItem, LanguageRegistry.instance().getStringLocalization("act.advlist"));
    	HeadNames = configFile.getStringList("Custom Head", Configuration.CATEGORY_GENERAL, HeadNames, LanguageRegistry.instance().getStringLocalization("act.headlist"));
    	CustomFirework = configFile.getStringList("Custom Firework (NBT)", Configuration.CATEGORY_GENERAL, CustomFirework, LanguageRegistry.instance().getStringLocalization("act.cstfirework"));
    	CustomCommandBlock = configFile.getStringList("Custom commandBlock (command)", Configuration.CATEGORY_GENERAL, CustomCommandBlock, LanguageRegistry.instance().getStringLocalization("act.cstcommand"));
        configFile.save();
    }
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        configFile = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
        fakeItem = new FakeItems();
        fakeItem2 = new FakeItems2();
        fakeItem3 = new FakeItems3();
        GameRegistry.registerItem(fakeItem, "fakeItem");
        GameRegistry.registerItem(fakeItem2, "fakeItem2");
        GameRegistry.registerItem(fakeItem3, "fakeItem3");
        LanguageRegistry.instance().addStringLocalization("itemGroup.ateTab", "fr_FR","\u00a7bObjet Impossible");
        LanguageRegistry.instance().addStringLocalization("itemGroup.ateTab2","fr_FR" ,"\u00a7bTetes");
        LanguageRegistry.instance().addStringLocalization("itemGroup.ateTab3", "fr_FR","\u00a7bObjet perso");
        LanguageRegistry.instance().addStringLocalization("itemGroup.ateTab", "fr_CA","\u00a7bObjet Impossible");
        LanguageRegistry.instance().addStringLocalization("itemGroup.ateTab2","fr_CA" ,"\u00a7bTetes");
        LanguageRegistry.instance().addStringLocalization("itemGroup.ateTab3", "fr_CA","\u00a7bObjet perso");
        LanguageRegistry.instance().addStringLocalization("itemGroup.ateTab", "en_US","\u00a7bUnreal Items");
        LanguageRegistry.instance().addStringLocalization("itemGroup.ateTab2","en_US" ,"\u00a7bHeads");
        LanguageRegistry.instance().addStringLocalization("itemGroup.ateTab3", "en_US","\u00a7bCustom Item");
		
    	guifactory = new KeyBinding("key.atehud.guifactory", Keyboard.KEY_N, "key.atehud.categories");
    	nbtitem = new KeyBinding("key.atehud.nbtitem", Keyboard.KEY_Y, "key.atehud.categories");
        ClientRegistry.registerKeyBinding(guifactory);
        ClientRegistry.registerKeyBinding(nbtitem);
    	FMLCommonHandler.instance().bus().register(instance);
    	FMLCommonHandler.instance().bus().register(new ATEEventHandler());
    }
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }  
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
      if(eventArgs.modID.equals(ModMain.ModId))
        syncConfig();
    }
    @EventHandler
    public void modDisabled(FMLModDisabledEvent event) {
    	System.out.println("You disabled me, why ? :c");
    }

}
