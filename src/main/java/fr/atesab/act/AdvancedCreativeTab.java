package fr.atesab.act;

import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;

public class AdvancedCreativeTab extends CreativeTabs {

	public AdvancedCreativeTab() {
		super("act");
	}

	@Override
	public ItemStack getTabIconItem() {
		return ItemUtils.buildStack(Blocks.STRUCTURE_BLOCK, 1, 0, null, null,
				new Tuple<Enchantment, Integer>(Enchantments.FORTUNE, 1));
	}

}
