package fr.atesab.act;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.datafixers.util.Either;
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
import fr.atesab.act.internalcommand.InternalCommandExecutor;
import fr.atesab.act.mixin.MinecraftMixin;
import fr.atesab.act.mixin.MultiPlayerGameModeMixin;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.client.gui.widget.ModListWidget;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
@Mod(ACTMod.MOD_ID)
public class ACTMod {
    public enum ACTState {
        RELEASE(null), BETA(ChatFormatting.GOLD), ALPHA(ChatFormatting.DARK_RED);

        private final ChatFormatting color;

        ACTState(ChatFormatting color) {
            this.color = color;
        }

        /**
         * @return the color in the warning
         * @see #isShow()
         */
        public ChatFormatting getColor() {
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

    private static String modName = null;

    private static String modVersion = null;

    private static final String modLittleName = "ACT-Mod";

    private static final String[] modAuthorsArray = {"ATE47"};

    private static final String modAuthors = String.join(", ", modAuthorsArray);

    private static final String modLicense = "GNU GPL 3";

    private static final String modLicenseLink = "https://www.gnu.org/licenses/gpl-3.0.en.html";

    private static final String modLink = "https://www.curseforge.com/minecraft/mc-mods/advanced-extended-creative-mode";

    // public static final ResourceLocation MOD_FILE_LOGO = new ResourceLocation(MOD_ID, "mod_file_logo"); // TODO: implement loadOnUsageBuffer if needed also in registerPickerImage

    /**
     * @deprecated removed option
     */
    @Deprecated
    public static final String MOD_FACTORY = "fr.atesab.act.gui.ModGuiFactory";
    private static ModdedCommandACT modCommand;
    private static boolean instantMineEnabled = false;
    private static boolean instantPlaceEnabled = false;
    private static final AdvancedCreativeTab ADVANCED_CREATIVE_TAB = new AdvancedCreativeTab();
    public static final String TEMPLATE_TAG_NAME = "TemplateData";
    public static final Random RANDOM = new Random();

    public static final RandomSource RANDOM_SOURCE = RandomSource.create();
    private static final PoseStack STACK = new PoseStack();
    @SuppressWarnings("unchecked")
    public static final String[] DEFAULT_CUSTOM_ITEMS = {
            ItemUtils
                    .getGiveCode(
                    ItemUtils.buildStack(Blocks.PINK_WOOL, 42, ChatFormatting.LIGHT_PURPLE + "Pink verity",
                            new String[]{"" + ChatFormatting.GOLD + ChatFormatting.BOLD + "42 is life",
                                    "" + ChatFormatting.GOLD + ChatFormatting.BOLD + "wait what ?"},
                            new Tuple[0]))};
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());
    private static KeyMapping giver, menu, edit;
    private static final List<ItemStack> templates = new ArrayList<>();
    private static final Map<String, Map<String, Consumer<StringModifier>>> stringModifier = new HashMap<>();
    private static final Configuration config = new Configuration();
    private static final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
    private static CommandDispatcher<SharedSuggestionProvider> SharedSuggestionProvider;
    private static final InternalCommandExecutor internalCommandExecutor = new InternalCommandExecutor();
    private static final Component HIDE_COMPONENT = Component.literal("%HIDE_COMPONENT%");

    /**
     * @return if the tool tip is disabled
     */
    public static boolean doesDisableToolTip() {
        return config.doesDisableToolTip();
    }

    /**
     * Get all registered {@link Attribute}
     * <p>
     * To add Attributes, use {@link Set#add(Object)}
     * </p>
     *
     * @return the attributes
     * @since 2.1
     * @deprecated not used anymore, use {@link ForgeRegistries#ATTRIBUTES} instead
     */
    @Deprecated
    public static Set<Attribute> getAttributes() {
        return Set.copyOf(ForgeRegistries.ATTRIBUTES.getValues());
    }

    /**
     * Get saved custom items codes
     *
     * @return the codes
     * @since 2.1
     */
    public static SyncList<String> getCustomItems() {
        return config.getCustomitems();
    }

    /**
     * @return the {@link CommandDispatcher} of the act API
     */
    public static CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }

    /**
     * Get the map by categories of modifiers
     *
     * @return the modifier
     * @see #registerStringModifier(String, String, Consumer)
     * @since 2.1
     */
    public static Map<String, Map<String, Consumer<StringModifier>>> getStringModifier() {
        return stringModifier;
    }

    /**
     * Get template give code of a template {@link ItemStack}
     *
     * @param template the template stack
     * @return the codes the code of the template
     * @see #registerTemplate(String, ItemStack, String)
     * @see #getTemplates()
     * @since 2.0
     */
    public static String getTemplateData(ItemStack template) {
        return ItemUtils.getCustomTag(template, TEMPLATE_TAG_NAME, null);
    }

    /**
     * find the tab of an item
     *
     * @param stack item stack
     * @return tab or null
     */
    public static CreativeModeTab findTabForItem(ItemStack stack) {
        // might be useful to use the search tree instead to reduce usage
        return CreativeModeTabs.allTabs().stream()
                .filter(tab -> tab.contains(stack))
                .findAny().orElse(null);
    }

    /**
     * Create a {@link Stream} with {@link ItemStack} templates with translated name
     *
     * @return the templates
     * @see #registerTemplate(String, ItemStack, String)
     * @since 2.0
     */
    public static Stream<ItemStack> getTemplates() {
        return templates.stream().map(is -> {
            String lang = ItemUtils.getCustomTag(is, TEMPLATE_TAG_NAME + "Lang", null);
            Component display = (lang != null ? Component.translatable(lang) : is.getDisplayName());
            display.copy().withStyle(ChatFormatting.AQUA);
            return is.copy().setHoverName(display);
        });
    }

    /**
     * get if a key is down
     *
     * @param key the keycode
     * @return if this key is pressed
     */
    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }

    /**
     * open the giver with the inhand item
     *
     * @throws NullPointerException if the game isn't started
     * @since 2.1
     */
    public static void openGiver() {
        Minecraft mc = Minecraft.getInstance();
        assert mc.player != null;
        final int slot = mc.player.getInventory().selected;
        GuiUtils.displayScreen(new GuiItemStackModifier(null, mc.player.getMainHandItem().copy(),
                is -> ItemUtils.give(is, 36 + slot)));
    }

    /**
     * Register a string modifier at root
     *
     * @param name     the modifier name
     * @param modifier the modifier
     * @see #getStringModifier()
     * @see #registerStringModifier(String, String, Consumer)
     * @since 2.1
     */
    public static void registerStringModifier(String name, Consumer<StringModifier> modifier) {
        registerStringModifier(name, "", modifier);
    }

    /**
     * Register a string modifier in a category
     *
     * @param name     the modifier name
     * @param category the modifier category
     * @param modifier the modifier
     * @see #getStringModifier()
     * @see #registerStringModifier(String, Consumer)
     * @since 2.1
     */
    public static void registerStringModifier(String name, String category, Consumer<StringModifier> modifier) {
        stringModifier.computeIfAbsent(category, k -> new HashMap<>()).put(name, modifier);
    }

    /**
     * Register a new Template with a lang code, an icon an give data
     *
     * @param lang the template description
     * @param icon the icon
     * @param data the code
     * @see #getTemplates()
     * @see #getTemplateData(ItemStack)
     * @since 2.0
     */
    public static void registerTemplate(String lang, ItemStack icon, String data) {
        templates.add(ItemUtils.setCustomTag(ItemUtils.setCustomTag(icon.copy(), TEMPLATE_TAG_NAME, data),
                TEMPLATE_TAG_NAME + "Lang", lang));
    }

    public static KeyMapping getGiverKeyMapping() {
        return giver;
    }

    public static KeyMapping getMenuKeyMapping() {
        return menu;
    }

    public static KeyMapping getEditKeyMapping() {
        return edit;
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
     * @param code the code
     */
    public static void saveItem(String code) {
        LOGGER.info("Adding : {}", code);
        config.getCustomitems().add(0, code);
    }

    /**
     * set doesDisableToolTip
     *
     * @param doesDisableToolTip the value
     */
    public static void setDoesDisableToolTip(boolean doesDisableToolTip) {
        config.setDoesDisableToolTip(doesDisableToolTip);
    }

    /**
     * Draw a string on the screen
     *
     * @param renderer the font renderer
     * @param str      the string
     * @param x        the y location
     * @param y        the x location
     * @param color    the text color
     */
    public static void drawString(Font renderer, String str, int x, int y, int color) {
        renderer.draw(STACK, str, x, y, color);
    }

    /**
     * send us to another player
     *
     * @param to the other player
     */
    public static void spectatorTeleport(PlayerInfo to) {
        spectatorTeleport(to.getProfile().getId());
    }

    /**
     * send us to another player
     *
     * @param to the other player uuid
     */
    public static void spectatorTeleport(UUID to) {
        var p = Minecraft.getInstance().player;
        if (p == null) {
            return;
        }
        var mode = Objects.requireNonNull(Objects.requireNonNull(Minecraft.getInstance().getConnection()).getPlayerInfo(p.getGameProfile().getId())).getGameMode();
        // ask for the mode
        if (mode != GameType.SPECTATOR) {
            ModdedCommand.sendSigned("/gamemode " + GameType.SPECTATOR.getName());
        }
        // send tp request
        p.connection.send(new ServerboundTeleportToEntityPacket(to));
        // reset our old mode
        if (mode != GameType.SPECTATOR) {
            ModdedCommand.sendSigned("/gamemode " + mode.getName());
        }
    }

    public static ACTState getModState() {
        return MOD_STATE;
    }

    public static String getModId() {
        return MOD_ID;
    }

    public static String getModName() {
        return Optional.ofNullable(modName).orElseThrow(() -> new RuntimeException("mod not init"));
    }

    public static String getModVersion() {
        return Optional.ofNullable(modVersion).orElseThrow(() -> new RuntimeException("mod not init"));
    }

    public static String getModLittleName() {
        return modLittleName;
    }

    public static String[] getModAuthorsArray() {
        return modAuthorsArray;
    }

    public static String getModAuthors() {
        return modAuthors;
    }

    public static String getModLicense() {
        return modLicense;
    }

    public static String getModLicenseLink() {
        return modLicenseLink;
    }

    public static String getModLink() {
        return modLink;
    }

    public static boolean isInstantMineEnabled() {
        return instantMineEnabled;
    }

    public static void setInstantMineEnabled(boolean instantMineEnabled) {
        ACTMod.instantMineEnabled = instantMineEnabled;
    }

    public static boolean isInstantPlaceEnabled() {
        return instantPlaceEnabled;
    }

    public static void setInstantPlaceEnabled(boolean instantPlaceEnabled) {
        ACTMod.instantPlaceEnabled = instantPlaceEnabled;
    }

    public static ModdedCommandACT getModCommand() {
        return modCommand;
    }

    public ACTMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::clientSetup);
        bus.addListener(this::commonSetup);
        bus.addListener(this::registerKeyBinding);
        bus.addListener(this::registerCreativeTab);
        config.sync(FMLPaths.CONFIGDIR.get().resolve(MOD_ID + ".toml"));
        config.addCustomItemsCallback(this::syncItemConfig);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void syncItemConfig(List<String> itemConfig) {
        // force the regeneration of the tabs
        CreativeModeTabs.CACHED_ENABLED_FEATURES = null;
        // if (ADVANCED_CREATIVE_TAB.isTabRegistered()) {
        //     LOGGER.info("Sync items config");
        //     // ADVANCED_CREATIVE_TAB.getTab().buildContents(FeatureFlagSet.of(), false);
        // } else {
        //     LOGGER.info("Tab not yet generated");
        // }
    }

    private void checkModList(Screen screen) {
        // enabling the config button
        if (screen instanceof ModListScreen) {
            ModListWidget.ModEntry entry = getFirstFieldOfTypeInto(ModListWidget.ModEntry.class, screen);
            if (entry != null) {
                ModInfo info = getFirstFieldOfTypeInto(ModInfo.class, entry);
                if (info != null) {
                    Optional<? extends ModContainer> op = ModList.get().getModContainerById(info.getModId());
                    if (op.isPresent()) {
                        boolean value = op.get().getCustomExtension(ConfigScreenHandler.ConfigScreenFactory.class).isPresent();
                        String config = I18n.get("fml.menu.mods.config");
                        for (Object b : screen.children()) {
                            if (b instanceof Button && ((Button) b).getMessage().getString().equals(config)) {
                                ((Button) b).active = value;
                            }
                        }
                    }
                }
            }
        }
    }

    private void registerKeyBinding(RegisterKeyMappingsEvent ev) {
        ev.register(ACTMod.giver = new KeyMapping("key.act.giver", GLFW.GLFW_KEY_Y, "key.act"));
        ev.register(ACTMod.menu = new KeyMapping("key.act.menu", GLFW.GLFW_KEY_N, "key.act"));
        ev.register(ACTMod.edit = new KeyMapping("key.act.edit", GLFW.GLFW_KEY_H, "key.act"));
    }

    private void registerCommandDispatcher(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        // /act
        new ModdedCommandACT().register(dispatcher, context);

        // /gm
        new ModdedCommandGamemode("gm").register(dispatcher, context);
        new ModdedCommandGamemodeQuick("gmc", GameType.CREATIVE).register(dispatcher, context);
        new ModdedCommandGamemodeQuick("gma", GameType.ADVENTURE).register(dispatcher, context);
        new ModdedCommandGamemodeQuick("gms", GameType.SURVIVAL).register(dispatcher, context);
        new ModdedCommandGamemodeQuick("gmsp", GameType.SPECTATOR).register(dispatcher, context);
    }

    private void registerCreativeTab(CreativeModeTabEvent.Register ev) {
        ADVANCED_CREATIVE_TAB.register(ev);
    }

    @SubscribeEvent
    public void registerCommand(RegisterClientCommandsEvent ev) {
        // register over the Forge and ACT dispatcher to have the suggestions
        registerCommandDispatcher(ev.getDispatcher(), ev.getBuildContext());
        registerCommandDispatcher(dispatcher, ev.getBuildContext());

        LOGGER.info("Commands registered.");
    }

    private void clientSetup(FMLClientSetupEvent ev) {
        ADVANCED_CREATIVE_TAB.buildSubItems();
    }

    private void commonSetup(FMLCommonSetupEvent ev) {
        internalCommandExecutor.registerModule(ACTUtils.class);
        internalCommandExecutor.registerModule(ChatUtils.class);
        internalCommandExecutor.registerModule(CommandUtils.class);
        internalCommandExecutor.registerModule(FileUtils.class);
        internalCommandExecutor.registerModule(GuiUtils.class);
        internalCommandExecutor.registerModule(ItemUtils.class);
        internalCommandExecutor.registerModule(ReflectionUtils.class);


        ModList.get().getModContainerById(MOD_ID).ifPresent(con -> {
            IModInfo nfo = con.getModInfo();
            modName = nfo.getDisplayName();
            modVersion = nfo.getVersion().toString();
            modCommand = new ModdedCommandACT();
            con.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new GuiMenu(parent)));
        });

        // register templates

        registerTemplate("gui.act.menu.template.empty", new ItemStack(Items.PAPER), "");
        registerTemplate("gui.act.menu.template.stone", new ItemStack(Blocks.STONE),
                ItemUtils.getGiveCode(new ItemStack(Blocks.STONE)));
        registerTemplate("gui.act.menu.template.potion", new ItemStack(Items.POTION),
                ItemUtils.getGiveCode(new ItemStack(Items.POTION)));
        registerTemplate("gui.act.menu.template.fireworks", new ItemStack(Items.FIREWORK_ROCKET),
                ItemUtils.getGiveCode(new ItemStack(Items.FIREWORK_ROCKET)));
        registerTemplate(Items.PLAYER_HEAD.getDescriptionId(), new ItemStack(Items.PLAYER_HEAD),
                ItemUtils.getGiveCode(new ItemStack(Items.PLAYER_HEAD)));
        registerTemplate("gui.act.menu.template.command", new ItemStack(Blocks.COMMAND_BLOCK),
                ItemUtils.getGiveCode(new ItemStack(Blocks.COMMAND_BLOCK)));
        registerTemplate(Items.EGG.getDescriptionId(), new ItemStack(Items.EGG),
                ItemUtils.getGiveCode(new ItemStack(Items.EGG)));

        // Item.REGISTRY

        ForgeRegistries.ITEMS.getEntries().forEach(e -> {
            var registryName = e.getKey().location();
            var i = e.getValue();
            // Register modifier for every items
            registerStringModifier(i.getDescriptionId() + ".name", "registry.items",
                    sm -> sm.setString(registryName.toString()));
        });
        /*
         * Register modifier for every registries
         */
        // ForgeRegistries.BLOCKS
        ForgeRegistries.BLOCKS.getEntries().forEach(e -> {
            var registryName = e.getKey().location();
            var b = e.getValue();
            registerStringModifier(b.getName().getString(), "registry.blocks",
                    sm -> sm.setString(registryName.toString()));
        });
        // ForgeRegistries.POTION_TYPES
        ForgeRegistries.POTIONS.getEntries().forEach(e -> {
            var registryName = e.getKey().location();
            var p = e.getValue();
            registerStringModifier(p.getName(""), "registry.potions",
                    sm -> sm.setString(registryName.toString()));
        });
        // ForgeRegistries.BIOMES
        ForgeRegistries.BIOMES.getKeys().forEach(k -> registerStringModifier(k.toString(), "registry.biomes",
                sm -> sm.setString(k.toString())));
        // ForgeRegistries.SOUND_EVENTS
        ForgeRegistries.SOUND_EVENTS.getKeys().forEach(s -> registerStringModifier(s.toString(), "registry.sounds",
                sm -> sm.setString(s.toString())));
        // ForgeRegistries.VILLAGER_PROFESSIONS
        ForgeRegistries.VILLAGER_PROFESSIONS.forEach(vp -> registerStringModifier(vp.name(), "registry.villagerProfessions",
                sm -> sm.setString(vp.toString())));
        // ForgeRegistries.ENTITIES
        ForgeRegistries.ENTITY_TYPES.getEntries().forEach(ee -> registerStringModifier(ee.getValue().getDescriptionId(), "registry.entities",
                sm -> sm.setString(ee.getKey().location().toString())));

        ForgeRegistries.ATTRIBUTES.getEntries().forEach(ee -> registerStringModifier(ee.getValue().getDescriptionId(), "attributes", // getName
                sm -> sm.setString(ee.getKey().location().toString()))); // getName

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            registerStringModifier("item.modifiers." + slot.getName(), "attributes.slot",
                    sm -> sm.setString(slot.getName()));
        }

        // giver

        registerStringModifier("gui.act.modifier.string.giver", "",
                sm -> sm.setNextScreen(new GuiGiver(sm.getNextScreen(), sm.getString(), sm::setString, false)));

        // NBT editor

        registerStringModifier("gui.act.modifier.string.nbt", "", sm -> {
            try {
                sm.setNextScreen(new GuiNBTModifier(sm.getNextScreen(), nbt -> sm.setString(nbt.toString()),
                        TagParser.parseTag(sm.getString())));
            } catch (Exception ignore) {
            }
        });

        // players names

        registerStringModifier("gui.act.modifier.string.players", "", sm -> {
            List<String> plr;
            try {
                plr = CommandUtils.getPlayerList();
            } catch (Exception e) {
                plr = new ArrayList<>();
                plr.add(Minecraft.getInstance().getUser().getName());
            }
            List<Tuple<String, String>> btn = new ArrayList<>();
            plr.forEach(pn -> btn.add(new Tuple<>(pn, pn)));
            sm.setNextScreen(new GuiButtonListSelector<>(sm.getNextScreen(),
                    Component.translatable("gui.act.modifier.string.players"), btn, s -> {
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

        // try {
        // 	GuiUtils.loadAndRegisterModImage(MOD_ID, MOD_FILE_LOGO, "logo.png");
        // } catch (IOException e) {
        // 	throw new RuntimeException("Can't find mod logo", e);
        // }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void createSuggestion(CommandNode<CommandSourceStack> dispatcher,
                                  CommandNode<SharedSuggestionProvider> rootCommandNode, CommandSourceStack player,
                                  Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> suggestions) {
        for (CommandNode<CommandSourceStack> child : dispatcher.getChildren()) {
            ArgumentBuilder<SharedSuggestionProvider, ?> argumentbuilder = (ArgumentBuilder) child.createBuilder();
            argumentbuilder.requires((ctx) -> true);
            if (argumentbuilder.getCommand() != null) {
                argumentbuilder.executes((ctx) -> 0);
            }

            if (argumentbuilder instanceof RequiredArgumentBuilder) {
                RequiredArgumentBuilder<SharedSuggestionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder) argumentbuilder;
                if (requiredargumentbuilder.getSuggestionsProvider() != null) {
                    requiredargumentbuilder
                            .suggests(SuggestionProviders.safelySwap(requiredargumentbuilder.getSuggestionsProvider()));
                }
            }

            if (argumentbuilder.getRedirect() != null) {
                argumentbuilder.redirect(suggestions.get(argumentbuilder.getRedirect()));
            }

            CommandNode<SharedSuggestionProvider> commandnode1 = argumentbuilder.build();
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
            if (f.getType() == cls) {
                try {
                    return (T) f.get(obj);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private void injectSuggestions() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            CommandDispatcher<SharedSuggestionProvider> current = mc.player.connection.getCommands();
            if (current != SharedSuggestionProvider) {
                SharedSuggestionProvider = current;

                Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> map = new HashMap<>();
                RootCommandNode<SharedSuggestionProvider> root = SharedSuggestionProvider.getRoot();
                map.put(dispatcher.getRoot(), root);
                createSuggestion(dispatcher.getRoot(), root, mc.player.createCommandSourceStack(), map);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent ev) {
        Minecraft mc = Minecraft.getInstance();
        if (ev.phase == TickEvent.Phase.END) {
            checkModList(mc.screen);
        }
        if (instantPlaceEnabled) {
            ((MinecraftMixin) mc).setRightClickDelay(0);
        }
        if (instantMineEnabled && mc.gameMode != null) {
            ((MultiPlayerGameModeMixin) mc.gameMode).setDestroyDelay(0);
        }
    }

    @SubscribeEvent
    public void onDrawScreen(ScreenEvent.Render.Post ev) {
        if (ev.getScreen() instanceof GuiACT && MOD_STATE.isShow()) {
            drawString(Minecraft.getInstance().font, "Warning! Currently in " + MOD_STATE.getColor() + MOD_STATE.name(),
                    5, 5, 0xffffffff);
        }
    }

    @SubscribeEvent
    public void onInitGui(ScreenEvent.Init.Post ev) {
        injectSuggestions();
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key ev) {
        if (Minecraft.getInstance().screen != null) {
            return;
        }
        if (giver.consumeClick()) {
            GuiUtils.displayScreen(new GuiGiver(null));
        } else if (menu.consumeClick()) {
            GuiUtils.displayScreen(new GuiMenu(null));
        } else if (edit.consumeClick()) {
            openGiver();
        }
    }

    @SubscribeEvent
    public void onRenderToopTipPost(RenderTooltipEvent.GatherComponents ev) {
        if (ev.getItemStack().isEmpty()) {
            return;
        }
        for (Either<FormattedText, TooltipComponent> e : ev.getTooltipElements()) {
            if (e.left().filter(c -> c == HIDE_COMPONENT).isPresent()) {
                ev.setCanceled(true);
                break;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderTooltip(ItemTooltipEvent ev) {
        Minecraft mc = Minecraft.getInstance();
        if (!(!(mc.screen instanceof GuiModifier) && giver.getKey().getValue() != 0
                && isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) && mc.screen instanceof GuiMenu) {
            ev.getToolTip().add(ModdedCommand
                    .createPrefix(I18n.get("gui.act.leftClick"), ChatFormatting.YELLOW, ChatFormatting.GOLD)
                    .append(ModdedCommand.createText(
                            I18n.get(isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) ? "gui.act.give.copy" : "gui.act.give.editor"),
                            ChatFormatting.YELLOW))); // appendSibling
            if (isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                ev.getToolTip()
                        .add(ModdedCommand
                                .createPrefix(I18n.get("gui.act.rightClick"), ChatFormatting.YELLOW,
                                        ChatFormatting.GOLD)
                                .append(ModdedCommand.createText(I18n.get("gui.act.delete"), ChatFormatting.YELLOW))); // appendSibling
            } else if (mc.player != null && mc.player.isCreative()) {
                ev.getToolTip().add(ModdedCommand
                        .createPrefix(I18n.get("gui.act.rightClick"), ChatFormatting.YELLOW, ChatFormatting.GOLD)
                        .append(ModdedCommand.createText(I18n.get("gui.act.give.give"), ChatFormatting.YELLOW))); // appendSibling
            }
        }

        // remove if not in advanced game mode
        if (config.doesDisableToolTip() && !ev.getFlags().isAdvanced()) {
            return;
        }

        var containerData = ItemUtils.getContainerSize(ev.getItemStack());
        if (containerData != null && Screen.hasControlDown() && Screen.hasShiftDown()) {
            if (isKeyDown(giver.getKey().getValue())) {
                mc.setScreen(new GuiGiver(mc.screen, ev.getItemStack()));
            }
            displayInventory(ev);
            ev.getToolTip().add(HIDE_COMPONENT);
            return; // cancel the tooltip
        }

        if (isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            CompoundTag compound = ev.getItemStack().getTag();
            if (!ev.getFlags().isAdvanced()) {
                ev.getToolTip().add(
                        Component.literal(ItemUtils.getRegistry(ev.getItemStack()).toString())
                                .withStyle(ChatFormatting.DARK_GRAY)
                );
            }
            if (!(mc.screen instanceof CreativeModeInventoryScreen
                    && AdvancedCreativeTab.getCurrentSelectedTab() == CreativeModeTabs.SEARCH)) {
                CreativeModeTab tab = findTabForItem(ev.getItemStack());
                if (tab != null) {
                    ev.getToolTip().add(tab.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
                }
            }
            if (compound != null && compound.contains("CustomPotionColor", 99)) {
                int color = compound.getInt("CustomPotionColor");
                ev.getToolTip()
                        .add(Component.translatable("item.color",
                                        Component.literal(String.format("#%06X", color)).withStyle(ChatFormatting.YELLOW))
                                .withStyle(ChatFormatting.GOLD)
                                .append(" ")
                                .append(Component.literal("\u25A0\u25A0\u25A0\u25A0").withStyle(s -> s.withColor(color | 0xff000000))));
            }
            if (!ev.getFlags().isAdvanced()) {
                if (ev.getToolTip().size() != 0) {
                    ev.getToolTip().set(0,
                            Component.literal("").append(ev.getToolTip().get(0))
                                    .append(" (#" + Item.getId(ev.getItemStack().getItem())
                                            + (ev.getItemStack().getDamageValue() != 0 && !ev.getItemStack().isDamaged()
                                            ? "/" + ev.getItemStack().getDamageValue()
                                            : "")
                                            + ")") // appendText
                                    .withStyle(ChatFormatting.WHITE));
                }
                if (compound != null && compound.contains("display", 10)
                        && compound.getCompound("display").contains("color", 99)) {
                    int color = compound.getCompound("display").getInt("color");
                    ev.getToolTip()
                            .add(Component.translatable("item.color",
                                            Component.literal(String.format("#%06X", color)).withStyle(ChatFormatting.YELLOW))
                                    .withStyle(ChatFormatting.GOLD)
                                    .append(" ")
                                    .append(Component.literal("\u25A0\u25A0\u25A0\u25A0").withStyle(s -> s.withColor(color | 0xff000000))));
                }
                if (ev.getItemStack().isDamaged()) {
                    int dmg = Math.abs(ev.getItemStack().getDamageValue() - ev.getItemStack().getMaxDamage()) + 1;
                    double maxdmg = ev.getItemStack().getMaxDamage() + 1;
                    ev.getToolTip()
                            .add(ModdedCommand.createText("RepairCost: ", ChatFormatting.YELLOW)
                                    .append(ModdedCommand.createText(String.valueOf(dmg),
                                            dmg < (int) (.1D * maxdmg) ? ChatFormatting.DARK_GREEN
                                                    : (dmg < (int) (0.25D * maxdmg) ? ChatFormatting.GREEN
                                                    : (dmg < (int) (0.5D * maxdmg) ? ChatFormatting.GOLD
                                                    : (dmg < (int) (0.75D * maxdmg) ? ChatFormatting.RED
                                                    : (dmg < (int) (maxdmg)
                                                    ? ChatFormatting.DARK_RED
                                                    : ChatFormatting.DARK_GREEN))))))); // appendSibling
                    ev.getToolTip().add(ModdedCommand.createText(I18n.get("item.durability",
                            (dmg < (int) (.1D * maxdmg) ? ChatFormatting.DARK_GREEN
                                    : (dmg < (int) (0.25D * maxdmg) ? ChatFormatting.GREEN
                                    : (dmg < (int) (0.5D * maxdmg) ? ChatFormatting.GOLD
                                    : (dmg < (int) (0.75D * maxdmg) ? ChatFormatting.RED
                                    : (dmg < (int) (maxdmg) ? ChatFormatting.DARK_RED
                                    : ChatFormatting.DARK_GREEN))))).toString()
                                    + dmg,
                            (ev.getItemStack().getMaxDamage() + 1)), ChatFormatting.YELLOW));
                }
            }
            if (compound != null && compound.size() != 0) {
                if (compound.contains("RepairCost")) {
                    int dmg = compound.getInt("RepairCost");
                    double maxdmg = 31;
                    ev.getToolTip()
                            .add(ModdedCommand.createText("RepairCost: ", ChatFormatting.YELLOW)
                                    .append(ModdedCommand.createText(String.valueOf(dmg),
                                            dmg < (int) (.1D * maxdmg) ? ChatFormatting.DARK_GREEN
                                                    : (dmg < (int) (0.25D * maxdmg) ? ChatFormatting.GREEN
                                                    : (dmg < (int) (0.5D * maxdmg) ? ChatFormatting.GOLD
                                                    : (dmg < (int) (0.75D * maxdmg) ? ChatFormatting.RED
                                                    : (dmg < (int) (maxdmg)
                                                    ? ChatFormatting.DARK_RED
                                                    : ChatFormatting.DARK_GREEN))))))); // appendSibling
                }

                MutableComponent tags = ModdedCommand
                        .createText(I18n.get("gui.act.tags") + "(" + compound.size() + "): ", ChatFormatting.GOLD);
                int count = 0;
                for (String tag : compound.getAllKeys()) {
                    if (count != 0) {
                        tags.append(ModdedCommand.createText(", ", ChatFormatting.GOLD)); // appendSibling
                    }

                    tags.append(ModdedCommand.createText("" + tag, ChatFormatting.YELLOW)); // appendSibling
                    count++;
                }

                ev.getToolTip().add(tags);
            }
            if (!(mc.screen instanceof GuiModifier)) {
                if (giver.getKey().getValue() != 0 && isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    if (isKeyDown(giver.getKey().getValue())) {
                        mc.setScreen(new GuiGiver(mc.screen, ev.getItemStack()));
                    }
                    ev.getToolTip().add(ModdedCommand
                            .createPrefix(giver.getKey().getDisplayName().getString(), ChatFormatting.YELLOW,
                                    ChatFormatting.GOLD)
                            .append(ModdedCommand.createTranslatedText("cmd.act.opengiver", ChatFormatting.YELLOW))); // appendSibling
                }
                if (menu.getKey().getValue() != 0) {
                    if (isKeyDown(menu.getKey().getValue())) {
                        String code = ItemUtils.getGiveCode(ev.getItemStack()).replace(ChatUtils.MODIFIER, '&');
                        ACTMod.saveItem(code);
                        mc.setScreen(new GuiMenu(mc.screen));
                    }
                    ev.getToolTip()
                            .add(ModdedCommand
                                    .createPrefix(menu.getKey().getDisplayName().getString(), ChatFormatting.YELLOW,
                                            ChatFormatting.GOLD)
                                    .append(ModdedCommand.createTranslatedText("gui.act.save", ChatFormatting.YELLOW)));
                }
            }
        } else {
            ev.getToolTip().add(Component.literal("SHIFT ").withStyle(ChatFormatting.YELLOW)
                    .append(Component.translatable("gui.act.shift").withStyle(ChatFormatting.GOLD)));
        }
        if (containerData != null) {
            ev.getToolTip().add(Component.literal("SHIFT + CTRL ").withStyle(ChatFormatting.YELLOW)
                    .append(Component.translatable("gui.act.shiftctrl").withStyle(ChatFormatting.GOLD)));
        }
    }

    public void displayInventory(ItemTooltipEvent ev) {
        var mc = Minecraft.getInstance();
        var stack = ev.getItemStack();

        var mh = mc.mouseHandler;
        var mouseX = (int) (mh.xpos() * (double) mc.getWindow().getGuiScaledWidth()
                / (double) mc.getWindow().getScreenWidth());
        var mouseY = (int) (mh.ypos() * (double) mc.getWindow().getGuiScaledHeight()
                / (double) mc.getWindow().getScreenHeight());

        var width = mc.getWindow().getGuiScaledWidth();
        var height = mc.getWindow().getGuiScaledHeight();

        GuiUtils.renderInventory(mc.font, mouseX, mouseY - 17, stack, width, height);
    }
}
