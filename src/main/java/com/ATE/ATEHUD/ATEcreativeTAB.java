package com.ATE.ATEHUD;

import java.util.Iterator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ATEcreativeTAB extends CreativeTabs
{
	
    public ATEcreativeTAB(int par1, String par2Str)
    {
        super(par1, par2Str);
    }
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem()
    {
        return Items.blaze_rod;
    }
}