package fr.atesab.act;

import fr.atesab.act.utils.ItemStackGenHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ActCreativeTab extends CreativeTabs {
	
	private ItemStack iconItem;
	private boolean searchBar;

	public ActCreativeTab(int index, String label, ItemStack iconItem) {
		this(index, label, iconItem, false);
	}
	public ActCreativeTab(int index, String label, ItemStack iconItem, boolean searchBar) {
		super(index, label);
		this.iconItem = iconItem;
		this.searchBar = searchBar;
	}
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		return iconItem;
	}
	@Override
	public boolean hasSearchBar() {
		return searchBar;
	}
}
