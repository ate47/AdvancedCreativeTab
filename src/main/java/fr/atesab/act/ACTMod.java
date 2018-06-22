package fr.atesab.act;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.input.Keyboard;

import fr.atesab.act.command.ACTCommand;
import fr.atesab.act.command.SimpleCommand;
import fr.atesab.act.gui.GuiGiver;
import fr.atesab.act.gui.GuiMenu;
import fr.atesab.act.gui.modifier.GuiModifier;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.ItemUtils;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@Mod(name = ACTMod.MOD_NAME, version = ACTMod.MOD_VERSION, canBeDeactivated = false, guiFactory = ACTMod.MOD_FACTORY, modid = ACTMod.MOD_ID, clientSideOnly = true)
public class ACTMod {
	public static final String MOD_ID = "act";
	public static final String MOD_NAME = "Advanced Creative 2";
	public static final String MOD_VERSION = "2.0";
	public static final String MOD_LITTLE_NAME = "ACT-Mod";
	public static final String MOD_FACTORY = "fr.atesab.act.gui.ModGuiFactory";
	private static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());
	private static KeyBinding giver, menu, edit;
	public static ACTCommand theCommand;
	public static String[] defaultCustomItems = {
			ItemUtils.getGiveCode(ItemUtils.buildStack(Blocks.WOOL, 42, 2, TextFormatting.LIGHT_PURPLE + "Pink verity",
					new String[] { "" + TextFormatting.GOLD + TextFormatting.BOLD + "42 is life",
							"" + TextFormatting.GOLD + TextFormatting.BOLD + "wait what ?" })) };
	public static List<String> customItems = new ArrayList<>(Arrays.asList(defaultCustomItems));
	private static List<ItemStack> templates = new ArrayList<>();
	public static HashSet<IAttribute> attributes = new HashSet<>();
	private static Configuration config;

	public static final String TEMPLATE_TAG_NAME = "TemplateData";

	public static String getTemplateData(ItemStack template) {
		return ItemUtils.getCustomTag(template, TEMPLATE_TAG_NAME, null);
	}

	public static Stream<ItemStack> getTemplates() {
		return templates.stream().map(is -> {
			String lang = ItemUtils.getCustomTag(is, TEMPLATE_TAG_NAME + "Lang", null);
			return is.copy().setStackDisplayName(
					TextFormatting.AQUA + (lang != null ? I18n.format(lang) : is.getDisplayName()));
		});
	}

	public static void registerTemplate(String lang, ItemStack icon, String data) {
		templates.add(ItemUtils.setCustomTag(ItemUtils.setCustomTag(icon.copy(), TEMPLATE_TAG_NAME, data),
				TEMPLATE_TAG_NAME + "Lang", lang));
	}

	public static void saveConfig() {
		config.get(Configuration.CATEGORY_GENERAL, "customItems", defaultCustomItems)
				.set(customItems.toArray(new String[customItems.size()]));
		config.save();
	}

	public static void syncConfig() {
		customItems = new ArrayList<>(Arrays.asList(config.getStringList("customItems", Configuration.CATEGORY_GENERAL,
				defaultCustomItems, "", null, "config.act.custom")));
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
				theCommand.edit.processSubCommand(null, null, null);
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
	public void preInit(FMLPreInitializationEvent ev) {
		config = new Configuration(ev.getSuggestedConfigurationFile());
		syncConfig();
		try {
			ClientCommandHandler.instance.registerCommand(theCommand = new ACTCommand());
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
		attributes.add(SharedMonsterAttributes.ARMOR);
		attributes.add(SharedMonsterAttributes.ARMOR_TOUGHNESS);
		attributes.add(SharedMonsterAttributes.ATTACK_DAMAGE);
		attributes.add(SharedMonsterAttributes.ATTACK_SPEED);
		attributes.add(SharedMonsterAttributes.FOLLOW_RANGE);
		attributes.add(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
		attributes.add(SharedMonsterAttributes.LUCK);
		attributes.add(SharedMonsterAttributes.MAX_HEALTH);
		attributes.add(SharedMonsterAttributes.MOVEMENT_SPEED);

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

}
