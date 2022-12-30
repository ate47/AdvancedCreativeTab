package fr.atesab.act;

import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A creative tab to add items
 */
public class AdvancedCreativeTab extends CreativeModeTab {

    private final Collection<ItemStack> subItems = new ArrayList<>();

    public AdvancedCreativeTab() {
        super("act");
    }

    /**
     * add a block to this tab
     *
     * @param sub the block
     */
    public void addSubitem(Block sub) {
        addSubitem(new ItemStack(sub, 1));
    }

    /**
     * add a item to this tab
     *
     * @param sub the item
     */
    public void addSubitem(Item sub) {
        addSubitem(new ItemStack(sub, 1));
    }

    /**
     * add a stack to this tab
     *
     * @param sub the stack
     */
    public void addSubitem(ItemStack sub) {
        subItems.add(sub);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ItemStack makeIcon() {
        return ItemUtils.buildStack(Blocks.STRUCTURE_BLOCK, 1, null, null,
                new Tuple[]{new Tuple<>(Enchantments.BLOCK_FORTUNE, 1)});
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> items) {
        items.addAll(subItems);
        ACTMod.getCustomItems().stream()
                .map(s -> s.replaceAll("&", "" + ChatUtils.MODIFIER))
                .map(ItemUtils::getFromGiveCode)
                .forEach(items::add);
        super.fillItemList(items);
    }

}
