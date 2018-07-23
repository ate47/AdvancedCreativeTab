package fr.atesab.act;

import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class AdvancedCreativeTab extends CreativeTabs {

	public AdvancedCreativeTab() {
		super("act");
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(Blocks.command_block);
	}

}
