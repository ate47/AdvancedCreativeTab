package fr.atesab.act;

import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A creative tab to add items
 */
public class AdvancedCreativeTab {
    private record ItemStackInfo(ItemStack stack, CreativeModeTab tab) {
    }

    private static final Field SELECT_TAB_FIELD;
    private static final Field DISPLAY_ITEMS_GENERATOR_FIELD;
    // CreativeModeTab.DisplayItemsGenerator

    static {
        Field selectTabField = null;
        Class<CreativeModeInventoryScreen> creativeModeInventoryScreenClass = CreativeModeInventoryScreen.class;
        for (Field field : creativeModeInventoryScreenClass.getDeclaredFields()) {
            if (field.getType().equals(CreativeModeTab.class) && (field.getModifiers() & Modifier.STATIC) != 0) {
                field.setAccessible(true);
                selectTabField = field;
                break;
            }
        }
        SELECT_TAB_FIELD = Optional.ofNullable(selectTabField)
                .orElseThrow(() -> new RuntimeException("Can't find selectTabField for " + creativeModeInventoryScreenClass));

        Field displayItemsGeneratorField = null;
        Class<CreativeModeTab> creativeModeTabClass = CreativeModeTab.class;
        for (Field field : creativeModeTabClass.getDeclaredFields()) {
            if (field.getType().equals(CreativeModeTab.DisplayItemsGenerator.class)) {
                field.setAccessible(true);
                displayItemsGeneratorField = field;
                break;
            }
        }
        DISPLAY_ITEMS_GENERATOR_FIELD = Optional.ofNullable(displayItemsGeneratorField)
                .orElseThrow(() -> new RuntimeException("Can't find selectTabField for " + creativeModeTabClass));
    }

    /**
     * @return get the current selected tab in the creative mode menu
     */
    public static CreativeModeTab getCurrentSelectedTab() {
        try {
            return (CreativeModeTab) SELECT_TAB_FIELD.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get the generator for a creative tab
     *
     * @param tab the tab
     * @return generator
     */
    public static CreativeModeTab.DisplayItemsGenerator getGeneratorForTab(CreativeModeTab tab) {
        try {
            return (CreativeModeTab.DisplayItemsGenerator) DISPLAY_ITEMS_GENERATOR_FIELD.get(tab);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static final ResourceLocation ADVANCED_CREATIVE_TAB_LOCATION = new ResourceLocation(ACTMod.MOD_ID, "act_tab");

    private final Collection<ItemStack> subItems = new ArrayList<>();
    private CreativeModeTab tab;

    public void buildSubItems() {
        List<CreativeModeTab> tabs = CreativeModeTabs.allTabs();
        Set<ItemStack> known = ItemStackLinkedSet.createTypeAndTagSet();

        CreativeModeTabs.allTabs()
                .forEach(tab -> {
                    CreativeModeTab.DisplayItemsGenerator displayItemsGenerator = AdvancedCreativeTab.getGeneratorForTab(tab);

                    // use false to avoid the generation of op items in the know set
                    int oldCount = known.size();
                    displayItemsGenerator.accept(FeatureFlagSet.of(), (is, per) -> known.add(is), false);
                    ACTMod.LOGGER.info(
                            "loaded tab {} with {} new element(s)",
                            tab.getDisplayName().getString(),
                            known.size() - oldCount
                    );
                });

        ForgeRegistries.ITEMS.getValues()
                .stream()
                .map(ItemStack::new)
                .filter(stack -> !stack.isEmpty() && !known.contains(stack))
                .forEach(this::addSubitem);
    }

    /**
     * add a block to this tab
     *
     * @param sub the item
     */
    public void addSubitem(ItemLike sub) {
        addSubitem(new ItemStack(sub, 1));
    }


    /**
     * add a stack to this tab
     *
     * @param sub the stack
     */
    public void addSubitem(ItemStack sub) {
        // can't have stacks with more than 1 element (1.19.3??)
        subItems.add(sub.copyWithCount(1));
    }

    @SuppressWarnings("unchecked")
    public ItemStack makeIcon() {
        return ItemUtils.buildStack(Blocks.STRUCTURE_BLOCK, 1, null, null,
                new Tuple[]{new Tuple<>(Enchantments.BLOCK_FORTUNE, 1)});
    }

    private void accept(FeatureFlagSet flagSet, CreativeModeTab.Output output, boolean hasPermisions) {
        output.acceptAll(subItems);
        ACTMod.getCustomItems().stream()
                .map(s -> s.replaceAll("&", "" + ChatUtils.MODIFIER))
                .map(ItemUtils::getFromGiveCode)
                .peek(s -> s.setCount(1))
                .forEach(output::accept);
    }

    public void register(CreativeModeTabEvent.Register ev) {
        tab = ev.registerCreativeModeTab(
                ADVANCED_CREATIVE_TAB_LOCATION,
                bld -> bld
                        .title(Component.translatable("itemGroup.act"))
                        .icon(this::makeIcon)
                        .displayItems(this::accept)
        );
    }

    public CreativeModeTab getTab() {
        return Optional.ofNullable(tab).orElseThrow(() -> new RuntimeException("tab wasn't built yet!"));
    }

    public boolean isTabRegistered() {
        return tab != null;
    }
}
