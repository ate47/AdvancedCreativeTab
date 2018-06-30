package fr.atesab.act;

import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;

@Mod(name = ACTMod.MOD_NAME, version = ACTMod.MOD_VERSION, canBeDeactivated = false, guiFactory = ACTMod.MOD_FACTORY, modid = ACTMod.MOD_ID, clientSideOnly = true)
public class ACTMod {
	public static final String MOD_ID = "act";
	public static final String MOD_NAME = "Advanced Creative 2";
	public static final String MOD_VERSION = "2.1";
	public static final String MOD_LITTLE_NAME = "ACT-Mod";
	/**
	 * Link to {@link ModGuiFactory}
	 */
	public static final String MOD_FACTORY = "fr.atesab.act.gui.ModGuiFactory";
	public static final ACTCommand ACT_COMMAND = new ACTCommand();
	public static final AdvancedItem ADVANCED_ITEM = (AdvancedItem) new AdvancedItem();
	public static final AdvancedCreativeTab ADVANCED_CREATIVE_TAB = new AdvancedCreativeTab();
	public static final String TEMPLATE_TAG_NAME = "TemplateData";
	public static final String[] DEFAULT_CUSTOM_ITEMS = {
			ItemUtils.getGiveCode(ItemUtils.buildStack(Blocks.WOOL, 42, 2, TextFormatting.LIGHT_PURPLE + "Pink verity",
					new String[] { "" + TextFormatting.GOLD + TextFormatting.BOLD + "42 is life",
							"" + TextFormatting.GOLD + TextFormatting.BOLD + "wait what ?" })) };
	private static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());
	private static KeyBinding giver, menu, edit;
	private static List<String> customItems = new ArrayList<>(Arrays.asList(DEFAULT_CUSTOM_ITEMS));
	private static List<ItemStack> templates = new ArrayList<>();
	private static HashSet<IAttribute> attributes = new HashSet<>();
	private static Map<String, Map<String, Consumer<StringModifier>>> stringModifier = new HashMap<>();
	private static Configuration config;

	private static <T> void forEachMatchIn(Object obj, Class<T> cls, Consumer<T> consumer) {
		for (Field field : obj.getClass().getDeclaredFields()) {
			if (field.getType().equals(cls)) {
				field.setAccessible(true);
				try {
					consumer.accept((T) field.get(obj));
				} catch (IllegalArgumentException e) {
					;
				} catch (IllegalAccessException e) {
					;
				}
			}
		}
	}

	/**
	 * Get all registered {@link IAttribute}
	 * <p>
	 * To add Attributes, use {@link HashSet#add(IAttribute)}
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
					TextFormatting.AQUA + (lang != null ? I18n.format(lang) : is.getDisplayName()));
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
			ev.getToolTip().add(TextFormatting.GOLD + "[" + TextFormatting.YELLOW + I18n.format("gui.act.leftClick")
					+ TextFormatting.GOLD + "] " + TextFormatting.YELLOW + I18n.format(
							Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? "gui.act.give.copy" : "gui.act.give.editor"));
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				ev.getToolTip()
						.add(TextFormatting.GOLD + "[" + TextFormatting.YELLOW + I18n.format("gui.act.rightClick")
								+ TextFormatting.GOLD + "] " + TextFormatting.YELLOW + I18n.format("gui.act.delete"));
			else if (mc.player != null && mc.player.isCreative())
				ev.getToolTip()
						.add(TextFormatting.GOLD + "[" + TextFormatting.YELLOW + I18n.format("gui.act.rightClick")
								+ TextFormatting.GOLD + "] " + TextFormatting.YELLOW
								+ I18n.format("gui.act.give.give"));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			NBTTagCompound compound = ev.getItemStack().getTagCompound();
			String s = (!ev.getFlags().isAdvanced() ? ev.getItemStack().getItem().getRegistryName().toString() : "");
			if (!(mc.currentScreen != null && mc.currentScreen instanceof GuiContainerCreative
					&& ((GuiContainerCreative) mc.currentScreen).getSelectedTabIndex() == CreativeTabs.SEARCH
							.getTabIndex())
					&& ev.getItemStack().getItem().getCreativeTab() != null)
				s += TextFormatting.WHITE + (s.isEmpty() ? "" : " ") + "("
						+ I18n.format(ev.getItemStack().getItem().getCreativeTab().getTranslatedTabLabel()) + ")";
			if (!s.isEmpty())
				ev.getToolTip().add(s);
			if (compound != null && compound.hasKey("CustomPotionColor", 99))
				ev.getToolTip()
						.add(TextFormatting.GOLD + net.minecraft.util.text.translation.I18n
								.translateToLocalFormatted("item.color", TextFormatting.YELLOW
										+ String.format("#%06X", compound.getInteger("CustomPotionColor"))));
			if (!ev.getFlags().isAdvanced()) {
				if (ev.getToolTip().size() != 0)
					ev.getToolTip().set(0,
							ev.getToolTip().get(0) + TextFormatting.WHITE + " (#"
									+ Item.getIdFromItem(ev.getItemStack().getItem())
									+ (ev.getItemStack().getMetadata() != 0 && !ev.getItemStack().isItemDamaged()
											? "/" + ev.getItemStack().getMetadata()
											: "")
									+ ")" + TextFormatting.WHITE);
				if (compound != null && compound.hasKey("display", 10)
						&& compound.getCompoundTag("display").hasKey("color", 99))
					ev.getToolTip().add(TextFormatting.GOLD + net.minecraft.util.text.translation.I18n
							.translateToLocalFormatted("item.color", TextFormatting.YELLOW
									+ String.format("#%06X", compound.getCompoundTag("display").getInteger("color"))));
				if (ev.getItemStack().isItemDamaged()) {
					int dmg = Math.abs(ev.getItemStack().getItemDamage() - ev.getItemStack().getMaxDamage()) + 1;
					double maxdmg = (double) (ev.getItemStack().getMaxDamage() + 1);
					ev.getToolTip()
							.add(TextFormatting.YELLOW + I18n.format("item.durability",
									(new StringBuilder().append('\247').append(dmg < (int) (1.0D * maxdmg) ? "2"
											: (dmg < (int) (0.75D * maxdmg) ? "a"
													: (dmg < (int) (0.5D * maxdmg) ? "6"
															: (dmg < (int) (0.25D * maxdmg) ? "c"
																	: (dmg < (int) (0.1D * maxdmg) ? "4" : "2")))))
											.toString()) + dmg,
									(ev.getItemStack().getMaxDamage() + 1)));
				}
			}
			if (compound != null && compound.getSize() != 0) {
				if (ev.getItemStack().getTagCompound().hasKey("RepairCost")) {
					int dmg = ev.getItemStack().getTagCompound().getInteger("RepairCost");
					double maxdmg = 31;
					ev.getToolTip()
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
				ev.getToolTip()
						.add(TextFormatting.GOLD + I18n.format("gui.act.tags") + "(" + compound.getSize() + "): "
								+ TextFormatting.YELLOW + compound.getKeySet().stream().collect(
										Collectors.joining(TextFormatting.GOLD + ", " + TextFormatting.YELLOW)));
			}
		}
		if (!(mc.currentScreen instanceof GuiGiver || mc.currentScreen instanceof GuiModifier)
				&& giver.getKeyCode() != 0 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (Keyboard.isKeyDown(giver.getKeyCode()))
				mc.displayGuiScreen(new GuiGiver(mc.currentScreen, ev.getItemStack()));
			ev.getToolTip()
					.add(TextFormatting.GOLD + "[" + TextFormatting.YELLOW + Keyboard.getKeyName(giver.getKeyCode())
							+ TextFormatting.GOLD + "] " + TextFormatting.YELLOW + I18n.format("cmd.act.opengiver"));
			if (menu.getKeyCode() != 0) {
				if (menu.getKeyCode() != 0) {
					if (Keyboard.isKeyDown(menu.getKeyCode())) {
						ACTMod.customItems.add(ItemUtils.getGiveCode(ev.getItemStack()));
						mc.displayGuiScreen(new GuiMenu(mc.currentScreen));
					}
					ev.getToolTip()
							.add(TextFormatting.GOLD + "[" + TextFormatting.YELLOW
									+ Keyboard.getKeyName(menu.getKeyCode()) + TextFormatting.GOLD + "] "
									+ TextFormatting.YELLOW + I18n.format("gui.act.save"));
				}
			}
		}
		if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			ev.getToolTip().add(TextFormatting.YELLOW + Keyboard.getKeyName(Keyboard.KEY_LSHIFT) + TextFormatting.GOLD
					+ " " + I18n.format("gui.act.shift"));
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent ev) {
		Item.REGISTRY.forEach(i -> {
			// Register modifier for every items
			registerStringModifier(i.getUnlocalizedName() + ".name", "registry.items",
					sm -> sm.setString(i.getRegistryName().toString()));
			// build cheat tab
			if (i.equals(ADVANCED_ITEM))
				return;
			else if (i.getCreativeTab() == null)
				ADVANCED_ITEM.addSubitem(i);
			else {
				NonNullList<ItemStack> sub = NonNullList.create();
				for (CreativeTabs tab : i.getCreativeTabs())
					i.getSubItems(tab, sub);
				if (!sub.stream()
						.filter(is -> is.getItem().equals(i) && is.getMetadata() == 0
								&& (is.getTagCompound() == null || is.getTagCompound().hasNoTags()))
						.findFirst().isPresent())
					ADVANCED_ITEM.addSubitem(i);
			}
		});
		/*
		 * Register modifier for every registries
		 */
		ForgeRegistries.BLOCKS.forEach(b -> registerStringModifier(b.getUnlocalizedName() + ".name", "registry.blocks",
				sm -> sm.setString(b.getRegistryName().toString())));
		ForgeRegistries.POTION_TYPES.forEach(p -> registerStringModifier(p.getNamePrefixed(""), "registry.potions",
				sm -> sm.setString(p.getRegistryName().toString())));
		ForgeRegistries.BIOMES.forEach(b -> registerStringModifier(b.getBiomeName(), "registry.biomes",
				sm -> sm.setString(b.getRegistryName().toString())));
		ForgeRegistries.SOUND_EVENTS.forEach(s -> registerStringModifier(s.getSoundName().toString(), "registry.sounds",
				sm -> sm.setString(s.getSoundName().toString())));
		ForgeRegistries.VILLAGER_PROFESSIONS.forEach(vp -> {
			registerStringModifier(vp.getRegistryName().toString(), "registry.villagerProfessions",
					sm -> sm.setString(vp.getRegistryName().toString()));
			// Register modifier for careers of profession
			forEachMatchIn(vp, List.class, list -> {
				try {
					((List<VillagerCareer>) list).forEach(
							vc -> registerStringModifier(vp.getRegistryName().toString() + " - " + vc.getName(),
									"registry.villagerProfessions", sm -> sm.setString(vc.getName())));
				} catch (Exception e) {
					; // if a non-VillagerCareer list is found
				}
			});
		});
		ForgeRegistries.ENTITIES.forEach(
				ee -> registerStringModifier(ee.getEgg() != null ? "entity." + ee.getName() + ".name" : ee.getName(),
						"registry.entities", sm -> sm.setString(ee.getRegistryName().toString())));

		attributes.forEach(at -> registerStringModifier("attribute.name." + at.getName(), "attributes",
				sm -> sm.setString(at.getName())));

		for (EntityEquipmentSlot slot : EntityEquipmentSlot.class.getEnumConstants())
			registerStringModifier("item.modifiers." + slot.getName(), "attributes.slot",
					sm -> sm.setString(slot.getName()));

		// giver

		registerStringModifier("gui.act.modifier.string.giver", "",
				sm -> sm.setNextScreen(new GuiGiver(sm.getNextScreen(), sm.getString(), sm::setString, false)));

		// NBT editor

		registerStringModifier("gui.act.modifier.string.nbt", "", sm -> {
			try {
				sm.setNextScreen(new GuiNBTModifier(sm.getNextScreen(), nbt -> sm.setString(nbt.toString()),
						JsonToNBT.getTagFromJson(sm.getString())));
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

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent ev) {

		config = new Configuration(ev.getSuggestedConfigurationFile());
		syncConfigs();

		try {
			ClientCommandHandler.instance.registerCommand(ACT_COMMAND);
			ClientCommandHandler.instance
					.registerCommand(new SimpleCommand("gm", "gm", args -> Minecraft.getMinecraft().player
							.sendChatMessage("/gamemode " + CommandBase.buildString(args, 0))));
			ClientCommandHandler.instance.registerCommand(new SimpleCommand("gmc", "gmc",
					args -> Minecraft.getMinecraft().player.sendChatMessage("/gamemode 1")));
			ClientCommandHandler.instance.registerCommand(new SimpleCommand("gms", "gms",
					args -> Minecraft.getMinecraft().player.sendChatMessage("/gamemode 0")));
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

		attributes.add(SharedMonsterAttributes.ARMOR);
		attributes.add(SharedMonsterAttributes.ARMOR_TOUGHNESS);
		attributes.add(SharedMonsterAttributes.ATTACK_DAMAGE);
		attributes.add(SharedMonsterAttributes.ATTACK_SPEED);
		attributes.add(SharedMonsterAttributes.FOLLOW_RANGE);
		attributes.add(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
		attributes.add(SharedMonsterAttributes.LUCK);
		attributes.add(SharedMonsterAttributes.MAX_HEALTH);
		attributes.add(SharedMonsterAttributes.MOVEMENT_SPEED);

		// register templates

		registerTemplate("gui.act.menu.template.empty", new ItemStack(Items.PAPER), "");
		registerTemplate("gui.act.menu.template.stone", new ItemStack(Blocks.STONE),
				ItemUtils.getGiveCode(new ItemStack(Blocks.STONE)));
		registerTemplate("gui.act.menu.template.potion", new ItemStack(Items.POTIONITEM),
				ItemUtils.getGiveCode(new ItemStack(Items.POTIONITEM)));
		registerTemplate("gui.act.menu.template.fireworks", new ItemStack(Items.FIREWORKS),
				ItemUtils.getGiveCode(new ItemStack(Items.FIREWORKS)));
		registerTemplate("item.skull.char.name", new ItemStack(Items.SKULL, 1, 3),
				ItemUtils.getGiveCode(new ItemStack(Items.SKULL, 1, 3)));
		registerTemplate("gui.act.menu.template.command", new ItemStack(Blocks.COMMAND_BLOCK),
				ItemUtils.getGiveCode(new ItemStack(Blocks.COMMAND_BLOCK)));
		registerTemplate("item.egg.name", new ItemStack(Items.SPAWN_EGG),
				ItemUtils.getGiveCode(new ItemStack(Items.SPAWN_EGG)));

	}

	@SubscribeEvent
	public void register(RegistryEvent.Register<Item> ev) {
		ev.getRegistry().register(ADVANCED_ITEM);
	}
}
