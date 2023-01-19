package fr.atesab.act;

import fr.atesab.act.superclass.Head;
import fr.atesab.act.utils.ItemStackGenHelper;

import java.io.PrintStream;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeItems3 extends Item {
	@Deprecated
	public FakeItems3() {
		setCreativeTab(ModMain.ATEcreativeTAB3);
		setHasSubtypes(true);
	}

	@SideOnly(Side.CLIENT)
	public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (tab.equals(ModMain.ATEcreativeTAB3)) {
			if (ModMain.GenFakeSub3) {
				if (ModMain.AdvancedModActived)
					System.out.println("Adding fake item");
				for (int i = 0; i < ModMain.HeadNames.length; i++) {
					subItems.add(new Head(ModMain.HeadNames[i]).getHead());
					if (ModMain.AdvancedModActived)
						System.out.println("Add " + ModMain.HeadNames[i] + "'s head");
				}
				if (ModMain.AdvancedModActived)
					System.out.println("Loading custom command block ...");
				for (int i = 0; i < ModMain.CustomCommandBlock.length; i++) {
					String cmd = ModMain.CustomCommandBlock[i].replaceFirst("\"", "\\\"");
					cmd = ModMain.CustomCommandBlock[i].replaceAll("&&", "\u00a7") + "";
					subItems.add(ItemStackGenHelper.getCMD(cmd, I18n.format("act.cstcommand", new Object[0]) + i));
					if (ModMain.AdvancedModActived)
						System.out.println(
								"Add Custom CommandBlock #" + i + " with :\"" + ModMain.CustomCommandBlock[i] + "\"");
				}
				if (ModMain.AdvancedModActived)
					System.out.println("Loading custom firework ...");
				for (int i = 0; i < ModMain.CustomFirework.length; i++) {
					String nbt = ModMain.CustomFirework[i].replaceAll("&&", "\u00a7") + "";
					subItems.add(ItemStackGenHelper.setName(ItemStackGenHelper.getNBT(Items.FIREWORKS, nbt),
							I18n.format("act.cstfirework", new Object[0]) + i));
					if (ModMain.AdvancedModActived)
						System.out
								.println("Add Custom Firework #" + i + " with :\"" + ModMain.CustomFirework[i] + "\"");
				}
				if (ModMain.AdvancedModActived)
					System.out.println("Loading advanced item ...");
				for (int i = 0; i < ModMain.AdvancedItem.length; i++) {
					if (ModMain.AdvancedModActived)
						System.out.println("Add Advanced Item #" + i + "...");
					try {
						if (ItemStackGenHelper.getGive(ModMain.AdvancedItem[i]) == null) {
							if (ModMain.AdvancedModActived)
								System.out.println("Failed add Advanced Item #" + i);
						} else {
							subItems.add(ItemStackGenHelper.getGive(ModMain.AdvancedItem[i]));
						}
					} catch (NumberInvalidException e) {
						if (ModMain.AdvancedModActived) {
							System.out.println("Failed add Advanced Item #" + i);
						}
					}
				}
				if (ModMain.AdvancedModActived)
					System.out.println("Finish add Item");
			} else {
				subItems.add(ItemStackGenHelper.getNoSubItem("Generate Customs Item"));
			}
		}
	}

	public boolean func_77614_k() {
		return true;
	}
}
