package fr.atesab.act;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AdvancedItem extends Item {
	private Collection<ItemStack> subItems = new ArrayList<>();

	public AdvancedItem() {
		setCreativeTab(ACTMod.ADVANCED_CREATIVE_TAB);
		setHasSubtypes(true);
	}

	@Override
	public CreativeTabs[] getCreativeTabs() {
		return new CreativeTabs[] { ACTMod.ADVANCED_CREATIVE_TAB };
	}

	public void addSubitem(Item sub) {
		if (sub.equals(Item.getItemFromBlock(Blocks.air)))
			return;
		if (sub.getHasSubtypes()) {
			List<ItemStack> list = new ArrayList<>();
			sub.getSubItems(sub, getCreativeTab(), list);
			if (!list.stream()
					.filter(is -> is.getItem().equals(sub) && is.getMetadata() == 0
							&& (is.getTagCompound() == null || is.getTagCompound().hasNoTags()))
					.findFirst().isPresent())
				list.add(new ItemStack(sub));
			subItems.addAll(list);
		} else
			addSubitemStack(new ItemStack(sub));
	}

	public void addSubitemStack(ItemStack sub) {
		subItems.add(sub);
	}

	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
		//fixed crash with TMI
		if(tab == (ACTMod.ADVANCED_CREATIVE_TAB)){
		items.addAll(subItems);}
	}
}
