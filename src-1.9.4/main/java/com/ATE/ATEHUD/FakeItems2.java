package com.ATE.ATEHUD;

import java.util.List;

import com.ATE.ATEHUD.superclass.Head;
import com.ATE.ATEHUD.utils.ItemStackGenHelper;
import com.sun.imageio.plugins.common.I18N;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;

public class FakeItems2 extends Item {
	@Deprecated
	public FakeItems2() {
		setCreativeTab(ModMain.ATEcreativeTAB2);
		setHasSubtypes(true);
	}
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
		if (tab != CreativeTabs.SEARCH) {
			ItemStackGenHelper.addTitle(subItems, I18n.format("act.devfriend"));

			subItems.add(ItemStackGenHelper.setMeta(new ItemStack(Blocks.STAINED_GLASS_PANE), 0," "));
			subItems.add(new Head("ATE47", "(Dev)").getHead());
			subItems.add(new Head("DarkArthurCT", "(El Tardos)").getHead());
			subItems.add(new Head("PikSel42", "(Babycraft Owner)").getHead());
			subItems.add(new Head("AlphaSeven278").getHead());
			subItems.add(new Head("Paralogos").getHead());
			subItems.add(new Head("theluckier59","(Noob)").getHead());
			subItems.add(new Head(Minecraft.getMinecraft().getSession().getUsername()).getHead());
			subItems.add(ItemStackGenHelper.setMeta(new ItemStack(Blocks.STAINED_GLASS_PANE), 0," "));

			ItemStackGenHelper.addTitle(subItems, I18n.format("act.mhf"));
			int i;
			for (i = 0; i < Head.MHF_list.length; i++) {
				subItems.add(new Head(Head.MHF_list[i],"MHF").getHead());
			}
			ItemStackGenHelper.addBlank(subItems, (int) MathHelper.abs(i%9-9));
			ItemStackGenHelper.addTitle(subItems, I18n.format("act.vipdev"));
			
			for (i = 0; i < Head.VIP_list.length; i++) {
				String[] str=Head.VIP_list[i].split(":");
				if(str.length>2){
					for (int j = 2; j < str.length; j++) {
						str[1]=":"+str[j];
					}
				}
				if(str.length==1){
					subItems.add(new Head(str[0]).getHead());
				}else{
					subItems.add(new Head(str[0],str[1]).getHead());
				}
			}
			ItemStackGenHelper.addBlank(subItems, (int) MathHelper.abs(i%9-9));
			ItemStackGenHelper.addTitle(subItems, I18n.format("act.list"));
			
			for (i = 0; i < Head.BickerCraft_list.length; i++) {
				String[] str=Head.BickerCraft_list[i].split(":");
				if(str.length>2){
					for (int j = 2; j < str.length; j++) {
						str[1]=":"+str[j];
					}
				}
				if(str.length==1){
					subItems.add(new Head(str[0]).getHead());
				}else{
					subItems.add(new Head(str[0],str[1]).getHead());
				}
			}
			ItemStackGenHelper.addBlank(subItems, (int) MathHelper.abs(i%9-9));
		}
	}

}
