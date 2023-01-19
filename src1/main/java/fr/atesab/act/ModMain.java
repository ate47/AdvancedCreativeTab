package fr.atesab.act;

import fr.atesab.act.superclass.Effect;
import fr.atesab.act.superclass.Enchantments;
import fr.atesab.act.superclass.Head;
import fr.atesab.act.utils.ItemStackGenHelper;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = ModMain.MOD_ID, version = ModMain.MOD_VERSION, name = ModMain.MOD_NAME, guiFactory = ModMain.MOD_GUI_FACTORY, clientSideOnly = true)
@SideOnly(Side.CLIENT)
public class ModMain {
	public static final String MOD_ID = "act";
	public static final String MOD_LITTLE_NAME = "ACT-Mod";
	public static final String MOD_VERSION = "1.4";
	public static final String MOD_NAME = "Advanced Creative Tab";
	public static final String MOD_GUI_FACTORY = "fr.atesab.act.gui.GuiFactory";
	public static final Minecraft mc = Minecraft.getMinecraft();
	public static Item fakeItem;
	public static Item fakeItem2;
	public static Item fakeItem3;

	public static CreativeTabs ATEcreativeTAB = new ActCreativeTab(CreativeTabs.getNextID(), "ateTab", ItemStackGenHelper.setMaxEnchant(new ItemStack(Items.BLAZE_ROD), ""));

	public static CreativeTabs ATEcreativeTAB2 = new ActCreativeTab(CreativeTabs.getNextID(), "ateTab2", ItemStackGenHelper.setMaxEnchant(new ItemStack(Items.SKULL), ""), true);
	public static CreativeTabs ATEcreativeTAB3 = new ActCreativeTab(CreativeTabs.getNextID(), "ateTab3", ItemStackGenHelper.setMaxEnchant(new ItemStack(Items.CAKE), ""), true);
	public static String[] HeadNames = { "ATE47" };
	public static String[] AdvancedItem = {
			"wool 42 2 {display:{Name:\"Pink verity\",Lore:[\"42 is life\",\"wait what ?\"]}}" };
	public static String[] CustomFirework = {
			"{Fireworks:{Flight:1,Explosions:[{Type:1,Colors:[7995154,16252672,15615],FadeColors:[16777215]}]}}" };
	public static String[] CustomCommandBlock = { "/tellraw @a \"<SuperCommandBlock> Hello world !\"" };
	public static int MaxLevelEnch = 127;
	public static int MaxLevelPotion = 127;
	public static Configuration configFile;
	public static Configuration headList;
	public static Configuration builder;
	public static KeyBinding guifactory;

	public static KeyBinding nbtitem;
	@Mod.Instance(ModMain.MOD_ID)
	public static ModMain instance;
	public static boolean AdvancedModActived = false;

	public static boolean GenFakeSub1 = true;

	public static boolean GenFakeSub2 = true;

	public static boolean GenFakeSub3 = true;

	public static void addConfig(String type, String value) {
		if (type == "AdvancedItem") {
			AdvancedItem = (String[]) copyOf(AdvancedItem, AdvancedItem.length + 1, String[].class);
			AdvancedItem[AdvancedItem.length - 1] = value;
		}
		if (type == "HeadNames") {
			HeadNames = (String[]) copyOf(HeadNames, HeadNames.length + 1, String[].class);
			HeadNames[HeadNames.length - 1] = value;
		}
		if (type == "CustomFirework") {
			CustomFirework = (String[]) copyOf(CustomFirework, CustomFirework.length + 1, String[].class);
			CustomFirework[CustomFirework.length - 1] = value;
		}
		if (type == "CustomCommandBlock") {
			CustomCommandBlock = (String[]) copyOf(CustomCommandBlock, CustomCommandBlock.length + 1, String[].class);
			CustomCommandBlock[CustomCommandBlock.length - 1] = value;
		}
		configFile.get(Configuration.CATEGORY_GENERAL, "Give command (Item/Block) (number) (meta) (nbt)", AdvancedItem)
				.setValues(AdvancedItem);
		configFile.get(Configuration.CATEGORY_GENERAL, "Custom Head", HeadNames).setValues(HeadNames);
		configFile.get(Configuration.CATEGORY_GENERAL, "Custom Firework (NBT)", CustomFirework)
				.setValues(CustomFirework);
		configFile.get(Configuration.CATEGORY_GENERAL, "Custom commandBlock (command)", CustomCommandBlock)
				.setValues(CustomCommandBlock);
		configFile.save();
	}
	public static Object[] copyOf(Object[] original, int newLength, Class newType) {
		Object[] arr = (newType == Object[].class) ? new Object[newLength]
				: (Object[]) Array.newInstance(newType.getComponentType(), newLength);
		int len = (original.length < newLength ? original.length : newLength);
		System.arraycopy(original, 0, arr, 0, len);
		return arr;
	}
	public static <T> ArrayList<T> getArray(T[] array) {
		ArrayList<T> a = new ArrayList<T>();
		for (T t: array) {
			a.add(t);
		}
		return a;
	}

	public static String[] getStringList(List<String> strList) {
		String[] a = new String[strList.size()];
		for (int i = 0; i < strList.size(); i++) {
			a[i] = strList.get(i);
		}
		return a;
	}

	public static void syncConfig() {
		MaxLevelEnch = configFile.getInt("MaxLevelEnch", Configuration.CATEGORY_GENERAL, MaxLevelEnch,
				Integer.MIN_VALUE, Integer.MAX_VALUE, "");
		MaxLevelPotion = configFile.getInt("MaxLevelPotion", Configuration.CATEGORY_GENERAL, MaxLevelPotion, -127, 127,
				"");
		AdvancedModActived = configFile.getBoolean("Advanced Mode", Configuration.CATEGORY_GENERAL, AdvancedModActived,
				"Open access to advanced options");
		AdvancedItem = configFile.getStringList("Give command (Item/Block) (number) (meta) (nbt)",
				Configuration.CATEGORY_GENERAL, (String[]) AdvancedItem, I18n.format("act.advlist"));
		HeadNames = configFile.getStringList("Custom Head", Configuration.CATEGORY_GENERAL, HeadNames,
				I18n.format("act.headlist"));
		CustomFirework = configFile.getStringList("Custom Firework (NBT)", Configuration.CATEGORY_GENERAL,
				CustomFirework, I18n.format("act.cstfirework"));
		CustomCommandBlock = configFile.getStringList("Custom commandBlock (command)", Configuration.CATEGORY_GENERAL,
				CustomCommandBlock, I18n.format("act.cstcommand"));
		GenFakeSub1 = configFile.getBoolean("Generate Fake Item", Configuration.CATEGORY_GENERAL, GenFakeSub1, "");
		GenFakeSub2 = configFile.getBoolean("Generate Head", Configuration.CATEGORY_GENERAL, GenFakeSub2, "");
		GenFakeSub3 = configFile.getBoolean("Generate Customs Item", Configuration.CATEGORY_GENERAL, GenFakeSub3, "");

		Head.VIP_list = headList.getStringList("VIPList", Configuration.CATEGORY_GENERAL, Head.VIP_list,
				"Vip from ATE");
		Head.MHF_list = headList.getStringList("MHFList", Configuration.CATEGORY_GENERAL, Head.MHF_list,
				"Do not touch");
		Head.BickerCraft_list = headList.getStringList("BickerCraftList", Configuration.CATEGORY_GENERAL,
				Head.BickerCraft_list, "BickerCraft");
		Enchantments.setEnchantmentList(builder.getStringList("LIST", "ENCHANTMENT", Enchantments.getEnchantmentList(),
				"TYPE,NAME,ID,CLASS,FIELD"));
		Enchantments.setTypeList(builder.getStringList("TYPE", "ENCHANTMENT", Enchantments.getTypeList(), ""));
		Effect.effects = builder.getStringList("LIST", "EFFECT", Effect.effects, "NAME,STRONGABLE,LONGABLE");
		Effect.skin = builder.getStringList("SKIN", "EFFECT", Effect.skin, "NAME,ID");
		Effect.type = builder.getStringList("TYPE", "EFFECT", Effect.type, "CLASS,FIELD");
		configFile.save();
		headList.save();
		builder.save();
	}
	@Mod.EventHandler
	public void modDisabled(FMLModDisabledEvent event) {
		System.out.println("You disabled me, why ? :c");
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (eventArgs.getModID().equals(ModMain.MOD_ID))
			syncConfig();
	}
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		configFile = new Configuration(new File(event.getModConfigurationDirectory(), MOD_LITTLE_NAME), "config.cfg");
		headList = new Configuration(
				new File(new File(event.getModConfigurationDirectory(), MOD_LITTLE_NAME), "headList.cfg"));
		Enchantments.defineEnchantments();
		Effect.defineEffects();
		builder = new Configuration(new File(new File(event.getModConfigurationDirectory(), MOD_LITTLE_NAME), "builder.cfg"));
		syncConfig();

		ClientRegistry.registerKeyBinding(
				ModMain.guifactory = new KeyBinding("key.atehud.guifactory", 49, "key.atehud.categories"));
		ClientRegistry.registerKeyBinding(
				ModMain.nbtitem = new KeyBinding("key.atehud.nbtitem", 21, "key.atehud.categories"));
		FMLCommonHandler.instance().bus().register(instance);
		FMLCommonHandler.instance().bus().register(new ActEventHandler());
	}
}
