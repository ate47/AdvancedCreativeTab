package fr.atesab.act;

import fr.atesab.act.superclass.Head;
import fr.atesab.act.utils.ItemStackGenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeItems2 extends Item {
	@Deprecated
	public FakeItems2() {
		setCreativeTab(ModMain.ATEcreativeTAB2);
		setHasSubtypes(true);
	}

	@SideOnly(Side.CLIENT)
	public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (tab.equals(ModMain.ATEcreativeTAB2)) {
			if (ModMain.GenFakeSub2) {
				if (tab != CreativeTabs.SEARCH) {
					ItemStackGenHelper.addTitle(subItems, I18n.format("act.devfriend", new Object[0]));

					subItems.add(ItemStackGenHelper.setMeta(new ItemStack(Blocks.STAINED_GLASS_PANE), 0, " "));
					subItems.add(new Head("ATE47", "(Dev)").getHead());
					subItems.add(new Head("DarkArthurCT", "(El Tardos)").getHead());
					subItems.add(new Head("PikSel42", "(Babycraft Owner)").getHead());
					subItems.add(new Head("AlphaSeven278").getHead());
					subItems.add(new Head("Paralogos").getHead());
					subItems.add(new Head("theluckier59", "(Noob)").getHead());
					subItems.add(new Head(Minecraft.getMinecraft().getSession().getUsername()).getHead());
					subItems.add(ItemStackGenHelper.setMeta(new ItemStack(Blocks.STAINED_GLASS_PANE), 0, " "));

					ItemStackGenHelper.addTitle(subItems, I18n.format("act.mhf", new Object[0]));
					int i;
					for (i = 0; i < Head.MHF_list.length; i++) {
						subItems.add(new Head(Head.MHF_list[i], "MHF").getHead());
					}
					ItemStackGenHelper.addBlank(subItems, MathHelper.abs(i % 9 - 9));
					ItemStackGenHelper.addTitle(subItems, I18n.format("act.vipdev", new Object[0]));

					for (i = 0; i < Head.VIP_list.length; i++) {
						String[] str = Head.VIP_list[i].split(":");
						if (str.length > 2) {
							for (int j = 2; j < str.length; j++) {
								str[1] = (":" + str[j]);
							}
						}
						if (str.length == 1) {
							subItems.add(new Head(str[0]).getHead());
						} else {
							subItems.add(new Head(str[0], str[1]).getHead());
						}
					}
					ItemStackGenHelper.addBlank(subItems, MathHelper.abs(i % 9 - 9));
					ItemStackGenHelper.addTitle(subItems, I18n.format("act.list", new Object[0]));

					for (i = 0; i < Head.BickerCraft_list.length; i++) {
						String[] str = Head.BickerCraft_list[i].split(":");
						if (str.length > 2) {
							for (int j = 2; j < str.length; j++) {
								str[1] = (":" + str[j]);
							}
						}
						if (str.length == 1) {
							subItems.add(new Head(str[0]).getHead());
						} else {
							subItems.add(new Head(str[0], str[1]).getHead());
						}
					}
					ItemStackGenHelper.addBlank(subItems, MathHelper.abs(i % 9 - 9));
				}
			} else {
				subItems.add(ItemStackGenHelper.getNoSubItem("Generate Head"));
			}
		}
	}

	public boolean func_77614_k() {
		return true;
	}
}
