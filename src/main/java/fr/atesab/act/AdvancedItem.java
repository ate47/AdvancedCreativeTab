package fr.atesab.act;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class AdvancedItem extends Item {
	private Collection<ItemStack> subItems = new ArrayList<>();

	public AdvancedItem() {
		setRegistryName(ACTMod.MOD_ID, "adv_it");
		setCreativeTab(ACTMod.ADVANCED_CREATIVE_TAB);
		setHasSubtypes(true);
	}

	@Override
	public CreativeTabs[] getCreativeTabs() {
		return new CreativeTabs[] { ACTMod.ADVANCED_CREATIVE_TAB };
	}

	public void addSubitem(Item sub) {
		if (sub.equals(Item.getItemFromBlock(Blocks.AIR)))
			return;
		if (sub.getHasSubtypes()) {
			NonNullList<ItemStack> list = NonNullList.func_191196_a();
			sub.getSubItems(sub, getCreativeTab(), list);
			if (!list.stream()
					.filter(is -> is.getItem().equals(sub) && is.getMetadata() == 0
							&& (is.getTagCompound() == null || is.getTagCompound().hasNoTags()))
					.findFirst().isPresent())
				list.add(new ItemStack(sub));
			subItems.addAll(list);
		} else
			subItems.add(new ItemStack(sub));
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> items) {
		if (tab.equals(ACTMod.ADVANCED_CREATIVE_TAB))
			items.addAll(subItems);
	}
}
