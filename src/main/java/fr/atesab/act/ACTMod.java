package fr.atesab.act;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import fr.atesab.act.command.ModdedCommand;
import fr.atesab.act.command.ModdedCommandACT;
import fr.atesab.act.command.ModdedCommandGamemode;
import fr.atesab.act.command.ModdedCommandGamemodeQuick;
import fr.atesab.act.config.Configuration;
import fr.atesab.act.gui.GuiACT;
import fr.atesab.act.gui.GuiGiver;
import fr.atesab.act.gui.GuiMenu;
import fr.atesab.act.gui.modifier.GuiItemStackModifier;
import fr.atesab.act.gui.modifier.GuiModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTModifier;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.CommandUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.gui.ModListScreen;
import net.minecraftforge.fml.client.gui.ModListWidget;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

@OnlyIn(Dist.CLIENT)
@Mod(ACTMod.MOD_ID)
public class ACTMod {
	public enum ACTState {
		RELEASE(null), BETA(TextFormatting.GOLD), ALPHA(TextFormatting.DARK_RED);

		private TextFormatting color;

		private ACTState(TextFormatting color) {
			this.color = color;
		}

		/**
		 * @return the color in the warning
		 * @see #isShow()
		 */
		public TextFormatting getColor() {
			return color;
		}

		/**
		 * @return if a warning is showed in menus
		 */
		public boolean isShow() {
			return color != null;
		}
	}

	public static final ACTState MOD_STATE = ACTState.RELEASE;

	public static final String MOD_ID = "act";

	public static final String MOD_NAME = "Advanced Creative 2";

	public static final String MOD_VERSION = "2.3";

	public static final String MOD_LITTLE_NAME = "ACT-Mod";

	/**
	 * Link to {@link ModGuiFactory}
	 */
	public static final String MOD_FACTORY = "fr.atesab.act.gui.ModGuiFactory";
	public static final ModdedCommandACT ACT_COMMAND = new ModdedCommandACT();
	public static final AdvancedCreativeTab ADVANCED_CREATIVE_TAB = new AdvancedCreativeTab();
	public static final String TEMPLATE_TAG_NAME = "TemplateData";
	public static final Random RANDOM = new Random();
	@SuppressWarnings("unchecked")
	public static final String[] DEFAULT_CUSTOM_ITEMS = {
			ItemUtils
					.getGiveCode(
							ItemUtils.buildStack(Blocks.PINK_WOOL, 42, TextFormatting.LIGHT_PURPLE + "Pink verity",
									new String[] { "" + TextFormatting.GOLD + TextFormatting.BOLD + "42 is life",
											"" + TextFormatting.GOLD + TextFormatting.BOLD + "wait what ?" },
									new Tuple[0])) };
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());
	private static KeyBinding giver, menu, edit;
	private static List<ItemStack> templates = new ArrayList<>();
	private static HashSet<IAttribute> attributes = new HashSet<>();
	private static Map<String, Map<String, Consumer<StringModifier>>> stringModifier = new HashMap<>();
	private static Configuration config;
	private static CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
	private static Set<String> commandSet = new HashSet<>();
	private static CommandDispatcher<ISuggestionProvider> suggestionProvider;

	public static boolean doesDisableToolTip() {
		return config.doesDisableToolTip();
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
		return config.getCustomitems();
	}

	/**
	 * @return the {@link CommandDispatcher} of the act API
	 */
	public static CommandDispatcher<CommandSource> getDispatcher() {
		return dispatcher;
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
			ITextComponent display = (lang != null ? new TranslationTextComponent(lang) : is.getDisplayName());
			display.getStyle().setColor(TextFormatting.AQUA);
			return is.copy().setDisplayName(display);
		});
	}

	/**
	 * Quick version of {@link InputMappings#isKeyDown(long, int)} without the
	 * window pointer argument
	 * 
	 * @param key
	 *            the keycode
	 * @return if this key is pressed
	 */
	public static boolean isKeyDown(int key) {
		return InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), key); // getMainWindow()
	}

	/**
	 * open the giver with the inhand item
	 * 
	 * @since 2.1
	 * 
	 * @throws NullPointerException
	 *             if the game isn't started
	 */
	public static void openGiver() {
		Minecraft mc = Minecraft.getInstance();
		final int slot = mc.player.inventory.currentItem;
		GuiUtils.displayScreen(new GuiItemStackModifier(null, mc.player.getHeldItemMainhand().copy(),
				is -> ItemUtils.give(is, 36 + slot)));
	}

	/**
	 * register a creative command to the act command dispatcher (unless we can't
	 * create client command)
	 * 
	 * @param command
	 *            the command
	 * @since 2.0
	 */
	public static void registerCommand(ModdedCommand command) {
		commandSet.addAll(command.getAliases());
		command.register(dispatcher);
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
		config.save();
	}

	/**
	 * Save an item from the code
	 * 
	 * @param code
	 *            the code
	 */
	public static void saveItem(String code) {
		LOGGER.info("Adding : " + code);
		config.getCustomitems().add(0, code);
	}

	public static void setDoesDisableToolTip(boolean doesDisableToolTip) {
		config.setDoesDisableToolTip(doesDisableToolTip);
		;
	}

	public ACTMod() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		config = new Configuration();
		config.sync(FMLPaths.CONFIGDIR.get().resolve(MOD_ID + ".toml").toFile());
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void checkModList(Screen screen) {
		// enabling the config button
		if (screen != null && screen instanceof ModListScreen) {
			/* GuiSlotModList.ModEntry */ Object entry = getFirstFieldOfTypeInto(
					ModListWidget.class.getDeclaredClasses()[0], screen);
			if (entry != null) {
				ModInfo info = getFirstFieldOfTypeInto(ModInfo.class, entry);
				if (info != null) {
					Optional<? extends ModContainer> op = ModList.get().getModContainerById(info.getModId());
					if (op.isPresent()) {
						boolean value = op.get().getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY).isPresent();
						String config = I18n.format("fml.menu.mods.config");
						for (IGuiEventListener b : screen.children())
							if (b instanceof Button && ((Button) b).getMessage().equals(config))
								((Button) b).active = value;
					}
				}
			}
		}
	}

	private void commandSetup() {
		// /act
		registerCommand(new ModdedCommandACT());

		// /gm
		registerCommand(new ModdedCommandGamemode("gm"));
		registerCommand(new ModdedCommandGamemodeQuick("gmc", GameType.CREATIVE));
		registerCommand(new ModdedCommandGamemodeQuick("gma", GameType.ADVENTURE));
		registerCommand(new ModdedCommandGamemodeQuick("gms", GameType.SURVIVAL));
		registerCommand(new ModdedCommandGamemodeQuick("gmsp", GameType.SPECTATOR));

		LOGGER.info("Commands registered.");
	}

	@SuppressWarnings("deprecation")
	private void commonSetup(FMLCommonSetupEvent ev) {

		ModList.get().getModContainerById(MOD_ID).ifPresent(con -> {
			con.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, parent) -> new GuiMenu(parent));
		});

		commandSetup();

		ClientRegistry.registerKeyBinding(ACTMod.giver = new KeyBinding("key.act.giver", GLFW.GLFW_KEY_Y, MOD_NAME));
		ClientRegistry.registerKeyBinding(ACTMod.menu = new KeyBinding("key.act.menu", GLFW.GLFW_KEY_N, MOD_NAME));
		ClientRegistry.registerKeyBinding(ACTMod.edit = new KeyBinding("key.act.edit", GLFW.GLFW_KEY_H, MOD_NAME));

		// register attributes
		attributes.add(SharedMonsterAttributes.ARMOR);
		attributes.add(SharedMonsterAttributes.ARMOR_TOUGHNESS);
		attributes.add(SharedMonsterAttributes.ATTACK_DAMAGE);
		attributes.add(SharedMonsterAttributes.ATTACK_SPEED);
		attributes.add(SharedMonsterAttributes.FLYING_SPEED);
		attributes.add(SharedMonsterAttributes.FOLLOW_RANGE);
		attributes.add(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
		attributes.add(SharedMonsterAttributes.LUCK);
		attributes.add(SharedMonsterAttributes.MAX_HEALTH);
		attributes.add(SharedMonsterAttributes.MOVEMENT_SPEED);

		// register templates

		registerTemplate("gui.act.menu.template.empty", new ItemStack(Items.PAPER), "");
		registerTemplate("gui.act.menu.template.stone", new ItemStack(Blocks.STONE),
				ItemUtils.getGiveCode(new ItemStack(Blocks.STONE)));
		registerTemplate("gui.act.menu.template.potion", new ItemStack(Items.POTION),
				ItemUtils.getGiveCode(new ItemStack(Items.POTION)));
		registerTemplate("gui.act.menu.template.fireworks", new ItemStack(Items.FIREWORK_ROCKET),
				ItemUtils.getGiveCode(new ItemStack(Items.FIREWORK_ROCKET)));
		registerTemplate(Items.PLAYER_HEAD.getTranslationKey(), new ItemStack(Items.PLAYER_HEAD),
				ItemUtils.getGiveCode(new ItemStack(Items.PLAYER_HEAD)));
		registerTemplate("gui.act.menu.template.command", new ItemStack(Blocks.COMMAND_BLOCK),
				ItemUtils.getGiveCode(new ItemStack(Blocks.COMMAND_BLOCK)));
		registerTemplate(Items.EGG.getTranslationKey(), new ItemStack(Items.EGG),
				ItemUtils.getGiveCode(new ItemStack(Items.EGG)));

		// Item.REGISTRY
		Registry.ITEM.forEach(i -> {
			// Register modifier for every items
			registerStringModifier(i.getTranslationKey() + ".name", "registry.items",
					sm -> sm.setString(i.getRegistryName().toString()));
			// build cheat tab
			if (i.getCreativeTabs() == null || i.getCreativeTabs().isEmpty() || i.getGroup() == null)
				ADVANCED_CREATIVE_TAB.addSubitem(i);
			else {
				NonNullList<ItemStack> sub = NonNullList.create();
				i.fillItemGroup(ItemGroup.SEARCH, sub);
				if (!sub.stream()
						.filter(is -> is.getItem().equals(i) && (is.getTag() == null || is.getTag().isEmpty()
								|| !(is.getTag().size() == 1 && is.getTag().contains("damage"))))
						.findFirst().isPresent())
					ADVANCED_CREATIVE_TAB.addSubitem(i);
			}
		});
		/*
		 * Register modifier for every registries
		 */
		// ForgeRegistries.BLOCKS
		Registry.BLOCK
				.forEach(b -> registerStringModifier(b.getNameTextComponent().getUnformattedComponentText() + ".name",
						"registry.blocks", sm -> sm.setString(b.getRegistryName().toString())));
		// ForgeRegistries.POTION_TYPES
		Registry.POTION.forEach(p -> registerStringModifier(p.getNamePrefixed(""), "registry.potions",
				sm -> sm.setString(p.getRegistryName().toString())));
		// ForgeRegistries.BIOMES
		Registry.BIOME.forEach(b -> registerStringModifier(b.getDisplayName().getUnformattedComponentText(),
				"registry.biomes", sm -> sm.setString(b.getRegistryName().toString())));
		// ForgeRegistries.SOUND_EVENTS
		Registry.SOUND_EVENT.forEach(s -> registerStringModifier(s.getName().toString(), "registry.sounds",
				sm -> sm.setString(s.getName().toString())));
		// ForgeRegistries.VILLAGER_PROFESSIONS
		Registry.VILLAGER_PROFESSION.forEach(vp -> {
			registerStringModifier(vp.getRegistryName().toString(), "registry.villagerProfessions",
					sm -> sm.setString(vp.getRegistryName().toString()));
		});
		// ForgeRegistries.ENTITIES
		Registry.ENTITY_TYPE.forEach(ee -> registerStringModifier(ee.getTranslationKey(), "registry.entities",
				sm -> sm.setString(ee.getRegistryName().toString())));

		attributes.forEach(at -> registerStringModifier("attribute.name." + at.getName(), "attributes",
				sm -> sm.setString(at.getName())));

		for (EquipmentSlotType slot : EquipmentSlotType.values())
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
				plr.add(Minecraft.getInstance().getSession().getUsername());
			}
			List<Tuple<String, String>> btn = new ArrayList<>();
			plr.forEach(pn -> btn.add(new Tuple<String, String>(pn, pn)));
			sm.setNextScreen(
					new GuiButtonListSelector<String>(sm.getNextScreen(), "gui.act.modifier.string.players", btn, s -> {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createSuggestion(CommandNode<CommandSource> dispatcher,
			CommandNode<ISuggestionProvider> rootCommandNode, CommandSource player,
			Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> suggestions) {
		for (CommandNode<CommandSource> child : dispatcher.getChildren()) {
			ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = (ArgumentBuilder) child.createBuilder();
			argumentbuilder.requires((ctx) -> true);
			if (argumentbuilder.getCommand() != null)
				argumentbuilder.executes((ctx) -> 0);

			if (argumentbuilder instanceof RequiredArgumentBuilder) {
				RequiredArgumentBuilder<ISuggestionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder) argumentbuilder;
				if (requiredargumentbuilder.getSuggestionsProvider() != null) {
					requiredargumentbuilder.suggests(
							SuggestionProviders.ensureKnown(requiredargumentbuilder.getSuggestionsProvider()));
				}
			}

			if (argumentbuilder.getRedirect() != null) {
				argumentbuilder.redirect(suggestions.get(argumentbuilder.getRedirect()));
			}

			CommandNode<ISuggestionProvider> commandnode1 = argumentbuilder.build();
			suggestions.put(child, commandnode1);
			rootCommandNode.addChild(commandnode1);
			if (!child.getChildren().isEmpty()) {
				this.createSuggestion(child, commandnode1, player, suggestions);
			}
		}

	}

	@SuppressWarnings("unchecked")
	private <T> T getFirstFieldOfTypeInto(Class<T> cls, Object obj) {
		for (Field f : obj.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			if (f.getType() == cls)
				try {
					return (T) f.get(obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					return null;
				}
		}
		return null;
	}

	private void injectSuggestions() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && mc.player.connection != null) {
			CommandDispatcher<ISuggestionProvider> current = mc.player.connection.func_195515_i(); // getSuggestionProvider()
			if (current != suggestionProvider) {
				suggestionProvider = current;
				if (current != null) {
					Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> map = Maps.newHashMap();
					RootCommandNode<ISuggestionProvider> root = suggestionProvider.getRoot();
					map.put(dispatcher.getRoot(), root);
					createSuggestion(dispatcher.getRoot(), root, mc.player.getCommandSource(), map);
				}
			}
		}
	}

	@SubscribeEvent
	public void onChatMessage(ClientChatEvent ev) {
		String msg = ev.getMessage();
		// check if it is a command
		if (!msg.startsWith("/"))
			return;

		final String command = msg.substring(1);

		// check if we know it
		if (!commandSet.contains(command.split(" ", 2)[0]))
			return;

		// we know it, we remove it
		ev.setCanceled(true);
		Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(msg);

		StringReader reader = new StringReader(msg);
		if (reader.canRead())
			reader.skip(); // remove the '/'
		CommandSource source = Minecraft.getInstance().player.getCommandSource();

		try {
			ParseResults<CommandSource> parse = dispatcher.parse(reader, source);
			dispatcher.execute(parse);
		} catch (CommandException e) {
			source.sendErrorMessage(e.getComponent());
		} catch (CommandSyntaxException e) {
			source.sendErrorMessage(TextComponentUtils.toTextComponent(e.getRawMessage()));
			if (e.getInput() != null && e.getCursor() >= 0) {
				int messageSize = Math.min(e.getInput().length(), e.getCursor());
				ITextComponent error = (new StringTextComponent("")).applyTextStyle(TextFormatting.GRAY)
						.applyTextStyle((style) -> {
							style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, msg));
						});
				if (messageSize > 10) {
					error.appendText("...");
				}

				error.appendText(e.getInput().substring(Math.max(0, messageSize - 10), messageSize));
				if (messageSize < e.getInput().length()) {
					ITextComponent itextcomponent2 = (new StringTextComponent(e.getInput().substring(messageSize)))
							.applyTextStyles(new TextFormatting[] { TextFormatting.RED, TextFormatting.UNDERLINE });
					error.appendSibling(itextcomponent2);
				}

				error.appendSibling((new TranslationTextComponent("command.context.here"))
						.applyTextStyles(new TextFormatting[] { TextFormatting.RED, TextFormatting.ITALIC }));
				source.sendErrorMessage(error);
			}
		} catch (Exception e) {
			ITextComponent error = new StringTextComponent(
					e.getMessage() == null ? e.getClass().getName() : e.getMessage());
			if (LOGGER.isDebugEnabled()) {
				StackTraceElement[] trace = e.getStackTrace();

				for (int j = 0; j < Math.min(trace.length, 3); ++j) {
					error.appendText("\n\n").appendText(trace[j].getMethodName()).appendText("\n ")
							.appendText(trace[j].getFileName()).appendText(":")
							.appendText(String.valueOf(trace[j].getLineNumber()));
				}
			}

			source.sendErrorMessage((new TranslationTextComponent("command.failed")).applyTextStyle((style) -> {
				style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error));
			}));
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent ev) {
		if (ev.phase == TickEvent.Phase.END)
			checkModList(Minecraft.getInstance().currentScreen);
	}

	@SubscribeEvent
	public void onDrawScreen(DrawScreenEvent.Post ev) {
		if (ev.getGui() instanceof GuiACT && MOD_STATE.isShow()) {
			Minecraft.getInstance().fontRenderer
					.drawString("Warning! Currently in " + MOD_STATE.getColor() + MOD_STATE.name(), 5, 5, 0xffffffff);
		}
	}

	@SubscribeEvent
	public void onInitGui(InitGuiEvent.Post ev) {
		injectSuggestions();
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent ev) {
		if (Minecraft.getInstance().currentScreen != null)
			return;
		if (giver.isPressed()) {
			GuiUtils.displayScreen(new GuiGiver(null));
		} else if (menu.isPressed()) {
			GuiUtils.displayScreen(new GuiMenu(null));
		} else if (edit.isPressed()) {
			openGiver();
		}
	}

	@SubscribeEvent
	public void onRenderTooltip(ItemTooltipEvent ev) {
		Minecraft mc = Minecraft.getInstance();
		if (!(mc.currentScreen instanceof GuiGiver || mc.currentScreen instanceof GuiModifier)
				&& giver.getKey().getKeyCode() != 0 && isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			;
		} else if (mc.currentScreen instanceof GuiMenu) {
			ev.getToolTip()
					.add(ModdedCommand
							.createPrefix(I18n.format("gui.act.leftClick"), TextFormatting.YELLOW, TextFormatting.GOLD)
							.appendSibling(ModdedCommand.createText(I18n.format(
									isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) ? "gui.act.give.copy" : "gui.act.give.editor"),
									TextFormatting.YELLOW)));
			if (isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
				ev.getToolTip().add(ModdedCommand
						.createPrefix(I18n.format("gui.act.rightClick"), TextFormatting.YELLOW, TextFormatting.GOLD)
						.appendSibling(ModdedCommand.createText(I18n.format("gui.act.delete"), TextFormatting.YELLOW)));
			else if (mc.player != null && mc.player.isCreative())
				ev.getToolTip().add(ModdedCommand
						.createPrefix(I18n.format("gui.act.rightClick"), TextFormatting.YELLOW, TextFormatting.GOLD)
						.appendSibling(
								ModdedCommand.createText(I18n.format("gui.act.give.give"), TextFormatting.YELLOW)));
		}

		// remove if not in advanced game mode
		if (config.doesDisableToolTip() && !ev.getFlags().isAdvanced())
			return;

		if (isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			CompoundNBT compound = ev.getItemStack().getTag();
			String s = (!ev.getFlags().isAdvanced() ? ev.getItemStack().getItem().getRegistryName().toString() : "");
			if (!(mc.currentScreen != null && mc.currentScreen instanceof CreativeScreen
					&& ((CreativeScreen) mc.currentScreen).getSelectedTabIndex() == ItemGroup.SEARCH.getIndex())
					&& ev.getItemStack().getItem().getCreativeTabs() != null)
				s += TextFormatting.WHITE + (s.isEmpty() ? "" : " ")
						+ ev.getItemStack().getItem().getCreativeTabs().stream().filter(Objects::nonNull)
								.map(i -> I18n.format(i.getTranslationKey())).collect(Collectors.joining(", "));
			if (!s.isEmpty())
				ev.getToolTip().add(new StringTextComponent(s));
			if (compound != null && compound.contains("CustomPotionColor", 99))
				ev.getToolTip()
						.add(ModdedCommand.createText(
								I18n.format("item.color",
										TextFormatting.YELLOW
												+ String.format("#%06X", compound.getInt("CustomPotionColor"))),
								TextFormatting.GOLD));
			if (!ev.getFlags().isAdvanced()) {
				if (ev.getToolTip().size() != 0)
					ev.getToolTip().set(0,
							ev.getToolTip().get(0)
									.appendText(" (#" + Item.getIdFromItem(ev.getItemStack().getItem())
											+ (ev.getItemStack().getDamage() != 0 && !ev.getItemStack().isDamaged()
													? "/" + ev.getItemStack().getDamage()
													: "")
											+ ")")
									.setStyle(new Style().setColor(TextFormatting.WHITE)));
				if (compound != null && compound.contains("display", 10)
						&& compound.getCompound("display").contains("color", 99))
					ev.getToolTip()
							.add(ModdedCommand.createText(
									I18n.format("item.color",
											TextFormatting.YELLOW + String.format("#%06X",
													compound.getCompound("display").getInt("color"))),
									TextFormatting.GOLD));
				if (ev.getItemStack().isDamaged()) {
					int dmg = Math.abs(ev.getItemStack().getDamage() - ev.getItemStack().getMaxDamage()) + 1;
					double maxdmg = (double) (ev.getItemStack().getMaxDamage() + 1);
					ev.getToolTip()
							.add(ModdedCommand.createText("RepairCost: ", TextFormatting.YELLOW)
									.appendSibling(ModdedCommand.createText(String.valueOf(dmg),
											dmg < (int) (.1D * maxdmg) ? TextFormatting.DARK_GREEN
													: (dmg < (int) (0.25D * maxdmg) ? TextFormatting.GREEN
															: (dmg < (int) (0.5D * maxdmg) ? TextFormatting.GOLD
																	: (dmg < (int) (0.75D * maxdmg) ? TextFormatting.RED
																			: (dmg < (int) (1D * maxdmg)
																					? TextFormatting.DARK_RED
																					: TextFormatting.DARK_GREEN)))))));
					ev.getToolTip().add(ModdedCommand.createText(I18n.format("item.durability",
							(dmg < (int) (.1D * maxdmg) ? TextFormatting.DARK_GREEN
									: (dmg < (int) (0.25D * maxdmg) ? TextFormatting.GREEN
											: (dmg < (int) (0.5D * maxdmg) ? TextFormatting.GOLD
													: (dmg < (int) (0.75D * maxdmg) ? TextFormatting.RED
															: (dmg < (int) (1D * maxdmg) ? TextFormatting.DARK_RED
																	: TextFormatting.DARK_GREEN))))).toString()
									+ dmg,
							(ev.getItemStack().getMaxDamage() + 1)), TextFormatting.YELLOW));
				}
			}
			if (compound != null && compound.size() != 0) {
				if (ev.getItemStack().getTag().contains("RepairCost")) {
					int dmg = ev.getItemStack().getTag().getInt("RepairCost");
					double maxdmg = 31;
					ev.getToolTip()
							.add(ModdedCommand.createText("RepairCost: ", TextFormatting.YELLOW)
									.appendSibling(ModdedCommand.createText(String.valueOf(dmg),
											dmg < (int) (.1D * maxdmg) ? TextFormatting.DARK_GREEN
													: (dmg < (int) (0.25D * maxdmg) ? TextFormatting.GREEN
															: (dmg < (int) (0.5D * maxdmg) ? TextFormatting.GOLD
																	: (dmg < (int) (0.75D * maxdmg) ? TextFormatting.RED
																			: (dmg < (int) (1D * maxdmg)
																					? TextFormatting.DARK_RED
																					: TextFormatting.DARK_GREEN)))))));
				}

				ITextComponent tags = ModdedCommand
						.createText(I18n.format("gui.act.tags") + "(" + compound.size() + "): ", TextFormatting.GOLD);
				int count = 0;
				for (String tag : compound.keySet()) {
					if (count != 0)
						tags.appendSibling(ModdedCommand.createText(", ", TextFormatting.GOLD));

					tags.appendSibling(ModdedCommand.createText("" + tag, TextFormatting.YELLOW));
					count++;
				}

				ev.getToolTip().add(tags);
			}
			if (!(mc.currentScreen instanceof GuiGiver || mc.currentScreen instanceof GuiModifier)) {
				if (giver.getKey().getKeyCode() != 0 && isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
					if (isKeyDown(giver.getKey().getKeyCode()))
						mc.displayGuiScreen(new GuiGiver(mc.currentScreen, ev.getItemStack()));
					ev.getToolTip().add(ModdedCommand
							.createPrefix(giver.getLocalizedName(), TextFormatting.YELLOW, TextFormatting.GOLD)
							.appendSibling(
									ModdedCommand.createTranslatedText("cmd.act.opengiver", TextFormatting.YELLOW)));
				}
				if (menu.getKey().getKeyCode() != 0) {
					if (isKeyDown(menu.getKey().getKeyCode())) {
						String code = ItemUtils.getGiveCode(ev.getItemStack()).replace(ChatUtils.MODIFIER, '&');
						ACTMod.saveItem(code);
						mc.displayGuiScreen(new GuiMenu(mc.currentScreen));
					}
					ev.getToolTip().add(ModdedCommand
							.createPrefix(menu.getLocalizedName(), TextFormatting.YELLOW, TextFormatting.GOLD)
							.appendSibling(ModdedCommand.createTranslatedText("gui.act.save", TextFormatting.YELLOW)));
				}
			}
		}
		if (!isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			ev.getToolTip()
					.add(new StringTextComponent("SHIFT").setStyle(new Style().setColor(TextFormatting.YELLOW))
							.appendText(" " + I18n.format("gui.act.shift"))
							.setStyle(new Style().setColor(TextFormatting.GOLD)));

	}

}
