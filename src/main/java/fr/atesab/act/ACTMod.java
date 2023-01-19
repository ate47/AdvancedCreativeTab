package fr.atesab.act;

import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.input.Keyboard;

import fr.atesab.act.command.ACTCommand;
import fr.atesab.act.command.SimpleCommand;
import fr.atesab.act.gui.GuiGiver;
import fr.atesab.act.gui.GuiMenu;
import fr.atesab.act.gui.ModGuiFactory;
import fr.atesab.act.gui.modifier.GuiModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.CommandUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

@Mod(name = ACTMod.MOD_NAME, version = ACTMod.MOD_VERSION, canBeDeactivated = false, guiFactory = ACTMod.MOD_FACTORY, modid = ACTMod.MOD_ID, clientSideOnly = true)
public class ACTMod {
	public static final String MOD_ID = "act";
	public static final String MOD_NAME = "Advanced Creative 2";
	public static final String MOD_VERSION = "2.1.1";
	public static final String MOD_LITTLE_NAME = "ACT-Mod";
	/**
	 * Link to {@link ModGuiFactory}
	 */
	public static final String MOD_FACTORY = "fr.atesab.act.gui.ModGuiFactory";
	public static final ACTCommand ACT_COMMAND = new ACTCommand();
	public static final AdvancedItem ADVANCED_ITEM = (AdvancedItem) new AdvancedItem();
	public static final AdvancedCreativeTab ADVANCED_CREATIVE_TAB = new AdvancedCreativeTab();
	public static final String TEMPLATE_TAG_NAME = "TemplateData";
	public static final String[] DEFAULT_CUSTOM_ITEMS = { ItemUtils
			.getGiveCode(ItemUtils.buildStack(Blocks.wool, 42, 2, EnumChatFormatting.LIGHT_PURPLE + "Pink verity",
					new String[] { "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "42 is life",
							"" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "wait what ?" })) };
	private static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());
	private static KeyBinding giver, menu, edit;
	private static List<String> customItems = new ArrayList<>(Arrays.asList(DEFAULT_CUSTOM_ITEMS));
	private static List<ItemStack> templates = new ArrayList<>();
	private static HashSet<IAttribute> attributes = new HashSet<>();
	private static long outdatedTime = 0;
	private static Map<String, Map<String, Consumer<StringModifier>>> stringModifier = new HashMap<>();
	private static Configuration config;

	private Minecraft mc;

	private static <T> void forEachMatchIn(Object obj, Class<T> cls, Consumer<T> consumer) {
		for (Field field : obj.getClass().getDeclaredFields()) {
			if (field.getType().equals(cls)) {
				field.setAccessible(true);
				try {
					consumer.accept((T) field.get(obj));
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Mod.Instance
	public static ACTMod instance;

	/**
	 * Get all registered {@link IAttribute}
	 * <p>
	 * To add Attributes, use @link HashSet#add(IAttribute)
	 * </p>
	 * 
	 * @since 2.1
	 */
	public static HashSet<IAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * Get saved custom items codes
	 * 
	 * @since 2.1
	 */
	public static List<String> getCustomItems() {
		return customItems;
	}

	/**
	 * Get the map by categories of modifiers
	 * 
	 * @since 2.1
	 * @see #registerStringModifier(String, String, Consumer)
	 */
	public static Map<String, Map<String, Consumer<StringModifier>>> getStringModifier() {
		return stringModifier;
	}

	/**
	 * Get template give code of a template {@link ItemStack}
	 * 
	 * @since 2.0
	 * @see #registerTemplate(String, ItemStack, String)
	 * @see #getTemplates()
	 */
	public static String getTemplateData(ItemStack template) {
		return ItemUtils.getCustomTag(template, TEMPLATE_TAG_NAME, null);
	}

	/**
	 * Create a {@link Stream} with {@link ItemStack} templates with translated name
	 * 
	 * @since 2.0
	 * @see #registerTemplate(String, ItemStack, String)
	 */
	public static Stream<ItemStack> getTemplates() {
		return templates.stream().map(is -> {
			String lang = ItemUtils.getCustomTag(is, TEMPLATE_TAG_NAME + "Lang", null);
			return is.copy().setStackDisplayName(
					EnumChatFormatting.AQUA + (lang != null ? I18n.format(lang) : is.getDisplayName()));
		});
	}

	/**
	 * Register a string modifier at root
	 * 
	 * @since 2.1
	 * @see #getStringModifier()
	 * @see #registerStringModifier(String, String, Consumer)
	 */
	public static void registerStringModifier(String name, Consumer<StringModifier> modifier) {
		registerStringModifier(name, "", modifier);
	}

	/**
	 * Register a string modifier in a category
	 * 
	 * @since 2.1
	 * @see #getStringModifier()
	 * @see #registerStringModifier(String, Consumer)
	 */
	public static void registerStringModifier(String name, String category, Consumer<StringModifier> modifier) {
		Map<String, Consumer<StringModifier>> map = stringModifier.get(category);
		if (map == null)
			stringModifier.put(category, map = new HashMap<>());
		map.put(name, modifier);
	}

	/**
	 * Register a new Template with a lang code, an icon an give data
	 * 
	 * @since 2.0
	 * @see #getTemplates()
	 * @see #getTemplateData(ItemStack)
	 */
	public static void registerTemplate(String lang, ItemStack icon, String data) {
		templates.add(ItemUtils.setCustomTag(ItemUtils.setCustomTag(icon.copy(), TEMPLATE_TAG_NAME, data),
				TEMPLATE_TAG_NAME + "Lang", lang));
	}

	/**
	 * Save mod configs
	 * 
	 * @since 2.0
	 */
	public static void saveConfigs() {
		config.get(Configuration.CATEGORY_GENERAL, "customItems", DEFAULT_CUSTOM_ITEMS)
				.set(customItems.toArray(new String[customItems.size()]));
		config.get("ihate18", "outdated", String.valueOf(outdatedTime)).set(String.valueOf(outdatedTime));
		config.save();
	}

	/**
	 * Load/Sync mod configs
	 * 
	 * @since 2.0
	 */
	public static void syncConfigs() {
		customItems = new ArrayList<>(Arrays.asList(config.getStringList("customItems", Configuration.CATEGORY_GENERAL,
				DEFAULT_CUSTOM_ITEMS, "", null, "config.act.custom")));
		try {
			outdatedTime = Long.parseLong(config.getString("outdated", "ihate18", String.valueOf(outdatedTime), ""));
		} catch (NumberFormatException e) {
			outdatedTime = 0;
		}
		config.save();
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent ev) {
		if (giver.isPressed())
			Minecraft.getMinecraft().displayGuiScreen(new GuiGiver(null));
		else if (menu.isPressed())
			Minecraft.getMinecraft().displayGuiScreen(new GuiMenu(null));
		else if (edit.isPressed()) {
			try {
				ACT_COMMAND.SC_EDIT.processSubCommand(null, null, null);
			} catch (CommandException e) {
				ChatUtils.error(e.getClass().getName() + ": " + e.getMessage());
			}
		}

	}

	@SubscribeEvent
	public void onRenderTooltip(ItemTooltipEvent ev) {
		Minecraft mc = Minecraft.getMinecraft();
		if (!(mc.currentScreen instanceof GuiGiver || mc.currentScreen instanceof GuiModifier)
				&& giver.getKeyCode() != 0 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			;
		} else if (mc.currentScreen instanceof GuiMenu) {
			ev.toolTip.add(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.YELLOW + I18n.format("gui.act.leftClick")
					+ EnumChatFormatting.GOLD + "] " + EnumChatFormatting.YELLOW + I18n.format(
							Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? "gui.act.give.copy" : "gui.act.give.editor"));
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				ev.toolTip.add(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.YELLOW
						+ I18n.format("gui.act.rightClick") + EnumChatFormatting.GOLD + "] " + EnumChatFormatting.YELLOW
						+ I18n.format("gui.act.delete"));
			else if (mc.thePlayer != null && mc.thePlayer.capabilities.isCreativeMode)
				ev.toolTip.add(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.YELLOW
						+ I18n.format("gui.act.rightClick") + EnumChatFormatting.GOLD + "] " + EnumChatFormatting.YELLOW
						+ I18n.format("gui.act.give.give"));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			NBTTagCompound compound = ev.itemStack.getTagCompound();
			String s = (!ev.showAdvancedItemTooltips ? ev.itemStack.getItem().getUnlocalizedName().toString() : "");
			if (!(mc.currentScreen != null && mc.currentScreen instanceof GuiContainerCreative
					&& ((GuiContainerCreative) mc.currentScreen).getSelectedTabIndex() == CreativeTabs.tabAllSearch
							.getTabIndex())
					&& ev.itemStack.getItem().getCreativeTab() != null)
				s += EnumChatFormatting.WHITE + (s.isEmpty() ? "" : " ") + "("
						+ I18n.format(ev.itemStack.getItem().getCreativeTab().getTranslatedTabLabel()) + ")";
			if (!s.isEmpty())
				ev.toolTip.add(s);
			if (compound != null && compound.hasKey("CustomPotionColor", 99))
				ev.toolTip.add(EnumChatFormatting.GOLD + I18n.format("item.color",
						EnumChatFormatting.YELLOW + String.format("#%06X", compound.getInteger("CustomPotionColor"))));
			if (!ev.showAdvancedItemTooltips) {
				if (ev.toolTip.size() != 0)
					ev.toolTip.set(0,
							ev.toolTip.get(0) + EnumChatFormatting.WHITE + " (#"
									+ Item.getIdFromItem(ev.itemStack.getItem())
									+ (ev.itemStack.getMetadata() != 0 && !ev.itemStack.isItemDamaged()
											? "/" + ev.itemStack.getMetadata()
											: "")
									+ ")" + EnumChatFormatting.WHITE);
				if (compound != null && compound.hasKey("display", 10)
						&& compound.getCompoundTag("display").hasKey("color", 99))
					ev.toolTip.add(EnumChatFormatting.GOLD + I18n.format("item.color", EnumChatFormatting.YELLOW
							+ String.format("#%06X", compound.getCompoundTag("display").getInteger("color"))));
				if (ev.itemStack.isItemDamaged()) {
					int dmg = Math.abs(ev.itemStack.getItemDamage() - ev.itemStack.getMaxDamage()) + 1;
					double maxdmg = (double) (ev.itemStack.getMaxDamage() + 1);
					ev.toolTip
							.add(EnumChatFormatting.YELLOW + I18n.format("item.durability",
									(new StringBuilder().append('\247').append(dmg < (int) (1.0D * maxdmg) ? "2"
											: (dmg < (int) (0.75D * maxdmg) ? "a"
													: (dmg < (int) (0.5D * maxdmg) ? "6"
															: (dmg < (int) (0.25D * maxdmg) ? "c"
																	: (dmg < (int) (0.1D * maxdmg) ? "4" : "2")))))
											.toString()) + dmg,
									(ev.itemStack.getMaxDamage() + 1)));
				}
			}
			if (compound != null && compound.getKeySet().size() != 0) {
				if (ev.itemStack.getTagCompound().hasKey("RepairCost")) {
					int dmg = ev.itemStack.getTagCompound().getInteger("RepairCost");
					double maxdmg = 31;
					ev.toolTip
							.add((new StringBuilder()).append("\247e")
									.append("RepairCost: "
											+ (new StringBuilder()).append('\247')
													.append(dmg < (int) (.1D * maxdmg) ? "2"
															: (dmg < (int) (0.25D * maxdmg) ? "a"
																	: (dmg < (int) (0.5D * maxdmg) ? "6"
																			: (dmg < (int) (0.75D * maxdmg) ? "c"
																					: (dmg < (int) (1D * maxdmg) ? "4"
																							: "2")))))
													.toString()
											+ String.valueOf(dmg))
									.toString());
				}
				ev.toolTip.add(EnumChatFormatting.GOLD + I18n.format("gui.act.tags") + "(" + compound.getKeySet().size()
						+ "): " + EnumChatFormatting.YELLOW + compound.getKeySet().stream().collect(
								Collectors.joining(EnumChatFormatting.GOLD + ", " + EnumChatFormatting.YELLOW)));
			}
		}
		if (!(mc.currentScreen instanceof GuiGiver || mc.currentScreen instanceof GuiModifier)
				&& giver.getKeyCode() != 0 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (Keyboard.isKeyDown(giver.getKeyCode()))
				mc.displayGuiScreen(new GuiGiver(mc.currentScreen, ev.itemStack));
			ev.toolTip.add(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.YELLOW
					+ Keyboard.getKeyName(giver.getKeyCode()) + EnumChatFormatting.GOLD + "] "
					+ EnumChatFormatting.YELLOW + I18n.format("cmd.act.opengiver"));
			if (menu.getKeyCode() != 0) {
				if (menu.getKeyCode() != 0) {
					if (Keyboard.isKeyDown(menu.getKeyCode())) {
						ACTMod.customItems.add(ItemUtils.getGiveCode(ev.itemStack));
						mc.displayGuiScreen(new GuiMenu(mc.currentScreen));
					}
					ev.toolTip.add(EnumChatFormatting.GOLD + "[" + EnumChatFormatting.YELLOW
							+ Keyboard.getKeyName(menu.getKeyCode()) + EnumChatFormatting.GOLD + "] "
							+ EnumChatFormatting.YELLOW + I18n.format("gui.act.save"));
				}
			}
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			ev.toolTip.add(EnumChatFormatting.YELLOW + Keyboard.getKeyName(Keyboard.KEY_LSHIFT)
					+ EnumChatFormatting.GOLD + " " + I18n.format("gui.act.shift"));
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent ev) {
		Item.itemRegistry.getKeys().forEach(c -> {
			Item i = (Item) Item.itemRegistry.getObject(c);
			// Register modifier for every items
			registerStringModifier(i.getUnlocalizedName() + ".name", "registry.items",
					sm -> sm.setString(c.toString()));
			// build cheat tab
			if (i.equals(ADVANCED_ITEM))
				return;
			else if (i.getCreativeTab() == null)
				ADVANCED_ITEM.addSubitem(i);
			else {
				List<ItemStack> sub = new ArrayList<>();
				for (CreativeTabs tab : i.getCreativeTabs())
					i.getSubItems(i, tab, sub);
				if (!sub.stream()
						.filter(is -> is.getItem().equals(i) && is.getMetadata() == 0
								&& (is.getTagCompound() == null || is.getTagCompound().hasNoTags()))
						.findFirst().isPresent())
					ADVANCED_ITEM.addSubitem(i);
			}
		});
		for (int i = 0; i < 32; i++) {
			ItemStack is = new ItemStack(Items.potionitem, 1, i);
			is.setTagCompound(new NBTTagCompound());
			is.getTagCompound().setTag("CustomPotionEffects", new NBTTagList());
			ADVANCED_ITEM.addSubitemStack(is);
		}
		/*
		 * Register modifier for every registries
		 */
		Block.blockRegistry.getKeys()
				.forEach(b -> registerStringModifier(Block.blockRegistry.getObject(b).getUnlocalizedName() + ".name",
						"registry.blocks", sm -> sm.setString(b.toString())));
		for (BiomeGenBase b : BiomeGenBase.getBiomeGenArray())
			if (b != null)
				registerStringModifier(b.biomeName, "registry.biomes", sm -> sm.setString(b.biomeName.toString()));
		SoundRegistry soundRegistry = getObject(SoundRegistry.class, Minecraft.getMinecraft().getSoundHandler());
		if (soundRegistry != null)
			soundRegistry.getKeys().forEach(rl -> {
				registerStringModifier(rl.toString(), "registry.sounds", sm -> sm.setString(rl.toString()));
			});
		FMLControlledNamespacedRegistry<VillagerProfession> professions = getObject(
				FMLControlledNamespacedRegistry.class, VillagerRegistry.instance());
		if (professions != null)
			professions.getKeys().forEach(rl -> {
				VillagerProfession vp = professions.getObject(rl);
				registerStringModifier(rl.toString(), "registry.villagerProfessions",
						sm -> sm.setString(rl.toString()));
				// Register modifier for careers of profession
				forEachMatchIn(vp, List.class, list -> {
					try {
						((List<VillagerCareer>) list).forEach(vc -> {
							String name = String.valueOf(getObject(String.class, vc));
							registerStringModifier(name, "registry.villagerProfessions", sm -> sm.setString(name));
						});
					} catch (Exception e) {
						; // if a non-VillagerCareer list is found
					}
				});
			});
		EntityList.getEntityNameList().forEach(ee -> registerStringModifier(
				EntityList.entityEggs.get(EntityList.getIDFromString(ee)) != null ? "entity." + ee + ".name" : ee,
				"registry.entities", sm -> sm.setString(ee)));

		attributes.forEach(at -> registerStringModifier("attribute.name." + at.getAttributeUnlocalizedName(),
				"attributes", sm -> sm.setString(at.getAttributeUnlocalizedName())));

		// giver

		registerStringModifier("gui.act.modifier.string.giver", "",
				sm -> sm.setNextScreen(new GuiGiver(sm.getNextScreen(), sm.getString(), sm::setString, false)));

		// NBT editor

		registerStringModifier("gui.act.modifier.string.nbt", "", sm -> {
			try {
				sm.setNextScreen(new GuiNBTModifier(sm.getNextScreen(), nbt -> sm.setString(nbt.toString()),
						ItemUtils.toJson(sm.getString())));
			} catch (Exception e) {
			}
		});

		// players names

		registerStringModifier("gui.act.modifier.string.players", "", sm -> {
			List<String> plr;
			try {
				plr = CommandUtils.getPlayerList();
			} catch (Exception e) {
				plr = new ArrayList<>();
				plr.add(Minecraft.getMinecraft().getSession().getUsername());
			}
			List<Tuple<String, String>> btn = new ArrayList<>();
			plr.forEach(pn -> btn.add(new Tuple<String, String>(pn, pn)));
			sm.setNextScreen(new GuiButtonListSelector<String>(sm.getNextScreen(), btn, s -> {
				sm.setString(s);
				return null;
			}));
		});

		// base 64 (en/de)coder

		registerStringModifier("gui.act.modifier.string.b64.encode", "b64", sm -> {
			try {
				sm.setString(new String(Base64.getEncoder().encode(sm.getString().getBytes())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		registerStringModifier("gui.act.modifier.string.b64.decode", "b64", sm -> {
			try {
				sm.setString(new String(Base64.getDecoder().decode(sm.getString().getBytes())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private <T> T getObject(Class<T> cls, Object object) {
		for (Field f : object.getClass().getDeclaredFields())
			try {
				f.setAccessible(true);
				if (f.getType().equals(cls)) {
					return (T) f.get(object);
				}
			} catch (Exception e) {
			}
		return null;
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent ev) {

		config = new Configuration(ev.getSuggestedConfigurationFile());
		syncConfigs();

		try {
			ClientCommandHandler.instance.registerCommand(ACT_COMMAND);
			ClientCommandHandler.instance
					.registerCommand(new SimpleCommand("gm", "gm", args -> Minecraft.getMinecraft().thePlayer
							.sendChatMessage("/gamemode " + CommandBase.buildString(args, 0))));
			ClientCommandHandler.instance.registerCommand(new SimpleCommand("gmc", "gmc",
					args -> Minecraft.getMinecraft().thePlayer.sendChatMessage("/gamemode 1")));
			ClientCommandHandler.instance.registerCommand(new SimpleCommand("gms", "gms",
					args -> Minecraft.getMinecraft().thePlayer.sendChatMessage("/gamemode 0")));
			LOGGER.info("Commands register.");
		} catch (Throwable e) {
			LOGGER.error("Can't register ATEHUD command\n" + e.getMessage());
		}

		ClientRegistry.registerKeyBinding(ACTMod.giver = new KeyBinding("key.act.giver", Keyboard.KEY_Y, MOD_NAME));
		ClientRegistry.registerKeyBinding(ACTMod.menu = new KeyBinding("key.act.menu", Keyboard.KEY_N, MOD_NAME));
		ClientRegistry.registerKeyBinding(ACTMod.edit = new KeyBinding("key.act.edit", Keyboard.KEY_H, MOD_NAME));

		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		// register attributes

		attributes.add(SharedMonsterAttributes.attackDamage);
		attributes.add(SharedMonsterAttributes.followRange);
		attributes.add(SharedMonsterAttributes.knockbackResistance);
		attributes.add(SharedMonsterAttributes.maxHealth);
		attributes.add(SharedMonsterAttributes.movementSpeed);

		// register templates

		registerTemplate("gui.act.menu.template.empty", new ItemStack(Items.paper), "");
		registerTemplate("gui.act.menu.template.stone", new ItemStack(Blocks.stone),
				ItemUtils.getGiveCode(new ItemStack(Blocks.stone)));
		registerTemplate("gui.act.menu.template.potion", new ItemStack(Items.potionitem),
				ItemUtils.getGiveCode(new ItemStack(Items.potionitem)));
		registerTemplate("gui.act.menu.template.fireworks", new ItemStack(Items.fireworks),
				ItemUtils.getGiveCode(new ItemStack(Items.fireworks)));
		registerTemplate("item.skull.char.name", new ItemStack(Items.skull, 1, 3),
				ItemUtils.getGiveCode(new ItemStack(Items.skull, 1, 3)));
		registerTemplate("gui.act.menu.template.command", new ItemStack(Blocks.command_block),
				ItemUtils.getGiveCode(new ItemStack(Blocks.command_block)));
		registerTemplate("item.egg.name", new ItemStack(Items.spawn_egg),
				ItemUtils.getGiveCode(new ItemStack(Items.spawn_egg)));

		GameRegistry.registerItem(ADVANCED_ITEM, "adv_it", MOD_ID);

	}

	@SubscribeEvent
	public void onInitGui(InitGuiEvent ev) {
		// I hate 1.8 (432000000 = 5 days (in ms))
		if (ev.gui instanceof GuiMainMenu && (System.currentTimeMillis() > outdatedTime + 432000000L
				|| System.currentTimeMillis() - 432000000L > outdatedTime
				// because it's fun to add random
				|| new Random().nextInt(50) == 42))
			ev.gui.mc.displayGuiScreen(new GuiScreen() {
				int y = 0;
				int psx = 0;

				@Override
				protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
					if (GuiUtils.isHover(width / 2 - psx / 2, y, psx, 20, mouseX, mouseY)) {
						outdatedTime = System.currentTimeMillis();
						saveConfigs();
						mc.displayGuiScreen(ev.gui);
					} else if (GuiUtils.isHover(width / 2 - psx / 2, y + 24, psx, 20, mouseX, mouseY))
						mc.shutdown();
					super.mouseClicked(mouseX, mouseY, mouseButton);
				}

				@Override
				public void drawScreen(int mouseX, int mouseY, float partialTicks) {
					drawDefaultBackground();
					int y = height / 2 - (fontRendererObj.FONT_HEIGHT + 1) * 4;
					String[] words = I18n.format("gui.act.bad", ACTMod.MOD_LITTLE_NAME).split(" ");
					int sx = 0;
					String line = "";
					for (int i = 0; i < words.length; i++) {
						if (!line.isEmpty())
							if (fontRendererObj.getStringWidth(line + " " + words[i]) > 400) {
								GuiUtils.drawCenterString(fontRendererObj, line, width / 2, y, 0xffdd2222);
								y += fontRendererObj.FONT_HEIGHT + 1;
								line = "";
							} else
								line += " ";
						line += words[i];
					}
					if (!line.isEmpty()) {
						GuiUtils.drawCenterString(fontRendererObj, line, width / 2, y, 0xffdd2222);
						y += fontRendererObj.FONT_HEIGHT + 1;
					}
					y += 4;
					GuiUtils.drawCenterString(fontRendererObj, "ATE47 " + EnumChatFormatting.LIGHT_PURPLE + "\u2764",
							width / 2, y, 0xffdd2222);
					y += fontRendererObj.FONT_HEIGHT + 1;
					this.y = (y += 10);
					String s = I18n.format("gui.act.bad.skip");
					psx = fontRendererObj.getStringWidth(s) + 10;
					int c = GuiUtils.isHover(width / 2 - psx / 2, y, psx, 20, mouseX, mouseY) ? 0xffff2222 : 0xffffffff;
					int c2 = GuiUtils.isHover(width / 2 - psx / 2, y + 24, psx, 20, mouseX, mouseY) ? 0xff22ff22
							: 0xffffffff;
					GuiUtils.drawBox(width / 2 - psx / 2, y, width / 2 + psx / 2, y + 20, c);
					GuiUtils.drawCenterString(fontRendererObj, s, width / 2, y, c, 20);
					GuiUtils.drawBox(width / 2 - psx / 2, y + 24, width / 2 + psx / 2, y + 44, c2);
					GuiUtils.drawCenterString(fontRendererObj, I18n.format("menu.quit"), width / 2, y + 24, c2, 20);
					super.drawScreen(mouseX, mouseY, partialTicks);
				}
			});
	}
}
