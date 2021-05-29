package fr.atesab.act;

import java.util.ArrayList;
import java.util.Collection;

import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * A creative tab to add items
 */
public class AdvancedCreativeTab extends ItemGroup {

	private Collection<ItemStack> subItems = new ArrayList<>();

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
				new Tuple[] { new Tuple<Enchantment, Integer>(Enchantments.BLOCK_FORTUNE, 1) });
	}

	@Override
	public void fillItemList(NonNullList<ItemStack> items) {
		items.addAll(subItems);
		ACTMod.getCustomItems().stream().map(ItemUtils::getFromGiveCode).forEach(items::add);
		super.fillItemList(items);
	}

}
