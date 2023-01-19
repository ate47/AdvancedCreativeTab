package fr.atesab.act.utils;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.lwjgl.input.Keyboard;

import fr.atesab.act.FakeItems;
import fr.atesab.act.ModMain;
import fr.atesab.act.superclass.Firework;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ItemStackGenHelper {
	public static ItemStack dl = setMeta(new ItemStack(Blocks.STAINED_GLASS_PANE), 3, " ");

	public static ItemStack dl1 = setMeta(new ItemStack(Blocks.STAINED_GLASS_PANE), 0, " ");
	public static NonNullList<ItemStack> addBlank(NonNullList<ItemStack> subItems) {
		return addBlank(subItems, 1);
	}

	public static NonNullList<ItemStack> addBlank(NonNullList<ItemStack> subItems, int blanks) {
		for (int i = 0; i < blanks; i++) {
			subItems.add(dl1);
		}
		return subItems;
	}

	public static NonNullList<ItemStack> addTitle(NonNullList<ItemStack> subItems, String text) {
		for (int i = 0; i < 4; i++) {
			subItems.add(dl);
		}
		subItems.add(setMeta(new ItemStack(Blocks.STAINED_GLASS_PANE), 4, text)); // 5
		for (int i = 0; i < 4; i++) {
			subItems.add(dl);
		}
		return subItems;
	}

	public static ItemStack getChestNBT(String name) {
		ItemStack is = new ItemStack(Items.FIREWORKS);
		if (is.getTagCompound() == null) {
			is.setTagCompound(new NBTTagCompound());
		}
		if (is.getTagCompound() != null)
			is.getTagCompound().setString("Items", "");
		is.setStackDisplayName(name);
		return is;
	}

	public static ItemStack getCMD(String cmd, String name) {
		String cmdmod = cmd.replaceFirst("\\\\", "\\\\");
		cmdmod = cmd.replaceFirst("\"", "\\\"");
		ItemStack is = new ItemStack(Blocks.COMMAND_BLOCK);
		if (is.getTagCompound() == null) {
			is.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound cmd2 = new NBTTagCompound();
		cmd2.setString("Command", cmdmod);
		if (is.getTagCompound() != null)
			is.getTagCompound().setTag("BlockEntityTag", cmd2);
		is.setStackDisplayName(name);
		return is;
	}

	/**
	 * Not implemented yet / Add custom head
	 * 
	 * @param url
	 *            link to your skin image :
	 *            skins.minecraft.net/MinecraftSkins/USERNAME.png
	 * @param name
	 *            Your Username
	 * @return
	 */
	public static ItemStack getCustomSkull(String url, String name) {
		return getCustomSkull(url, name, "");
	}

	/**
	 * Not implemented yet / Add custom head
	 * 
	 * @param url
	 *            link to your skin image :
	 *            skins.minecraft.net/MinecraftSkins/USERNAME.png
	 * @param name
	 *            Your Username
	 * @param description
	 *            Description the head
	 * @return
	 */
	public static ItemStack getCustomSkull(String url, String name, String description) {

		String uuid = UUID.randomUUID().toString();
		byte[] encodedData = Base64.getEncoder().encode(String.format("{\"timestamp\":1429453091873,\"profileId\":\""
				+ uuid.replaceAll("-", "") + "\",textures:{SKIN:{url:\"" + url + "\"}}}", url).getBytes());
		ItemStack head = getNBT(new ItemStack(Items.SKULL, 1, (short) 3), "{SkullOwner:{Id:\"" + uuid
				+ "\",Properties:{textures:[{Value:\"" + (new String(encodedData)) + "\"}]}}}");
		String str = I18n.format("act.head");
		str = str.replaceFirst("PLAYERNAME", name);
		str = str.replaceFirst("ADDTEXT", description);
		head.setStackDisplayName(str);
		return head;
	}

	public static ItemStack getFireWork(Firework fw) {
		ItemStack is = getNBT(Items.FIREWORKS, fw.getNBTFirework());
		is.addEnchantment(Enchantments.UNBREAKING, 10);
		System.out.println(is.getTagCompound().toString());
		return is;
	}

	public static ItemStack getGive(String code) throws NumberInvalidException {
		ItemStack itemstack = null;
		String[] args = code.split(" ");
		ResourceLocation resourcelocation = new ResourceLocation(args[0]);
		Item item = Item.REGISTRY.getObject(resourcelocation);
		if (item != null) {
			int i = args.length >= 2 ? CommandBase.parseInt(args[1]) : 1;
			int j = args.length >= 3 ? CommandBase.parseInt(args[2]) : 0;
			itemstack = new ItemStack(item, i, j);

			if (args.length >= 4) {
				String arg5 = args[3];
				if (args.length > 4) {
					for (int i1 = 4; i1 < args.length; i1++) {
						arg5 = arg5 + " " + args[i1];
					}
				}
				String arg5b = arg5.replaceAll("&&", "\u00a7") + "";
				itemstack = getNBT(itemstack, arg5b);
			}
		}
		return itemstack;
	}

	public static ItemStack getNBT(ItemStack is, String nbt) {
		try {
			is.setTagCompound(JsonToNBT.getTagFromJson(nbt));
		} catch (NBTException e) {}
		return is;
	}

	public static ItemStack getNBT(Object item, String nbt) {
		ItemStack is = null;
		if (item instanceof Item)
			is = new ItemStack((Item) item);
		if (item instanceof Block)
			is = new ItemStack((Block) item);
		return getNBT(is, nbt);
	}

	public static ItemStack getNoSubItem(String config) {
		return getNBT(new ItemStack(Blocks.BARRIER),
				"{display:{Name:\""
						+ I18n.format("item.act.noSubItem")
								.replaceAll("MENUKEY", Keyboard.getKeyName(ModMain.guifactory.getKeyCode()))
								.replaceAll("CONFIGNAME", config)
						+ "\",Lore:[\""
						+ I18n.format("item.act.noSubItem.lore.1")
								.replaceAll("MENUKEY", Keyboard.getKeyName(ModMain.guifactory.getKeyCode()))
								.replaceAll("CONFIGNAME", config)
						+ "\",\""
						+ I18n.format("item.act.noSubItem.lore.2")
								.replaceAll("MENUKEY", Keyboard.getKeyName(ModMain.guifactory.getKeyCode()))
								.replaceAll("CONFIGNAME", config)
						+ "\"]}}");
	}

	public static ItemStack setHyperProtectionEnchant(ItemStack itst, String name, boolean Thorns, boolean cheat) {
		int lv = 10;
		if (cheat) {
			lv = ModMain.MaxLevelEnch;
		} else {
			lv = 10;
		}
		itst.setStackDisplayName(name);
		itst.addEnchantment(Enchantments.UNBREAKING, lv);
		itst.addEnchantment(Enchantments.PROTECTION, lv);
		itst.addEnchantment(Enchantments.PROJECTILE_PROTECTION, lv);
		itst.addEnchantment(Enchantments.FEATHER_FALLING, lv);
		itst.addEnchantment(Enchantments.FIRE_PROTECTION, lv);
		itst.addEnchantment(Enchantments.RESPIRATION, 3);
		itst.addEnchantment(Enchantments.AQUA_AFFINITY, lv);
		itst.addEnchantment(Enchantments.BLAST_PROTECTION, lv);
		itst.addEnchantment(Enchantments.DEPTH_STRIDER, lv);
		if (Thorns) {
			itst.addEnchantment(Enchantments.THORNS, 42);
		}
		return itst;
	}

	public static ItemStack setMaxEnchant(ItemStack itst, String name) {
		for (int i = 0; i < fr.atesab.act.superclass.Enchantments.getEnchantments().length; i++) {
			itst.addEnchantment(fr.atesab.act.superclass.Enchantments.getEnchantments()[i].getEnchantment(),
					ModMain.MaxLevelEnch);
		}
		itst.setStackDisplayName(name);

		return itst;
	}

	public static ItemStack setMeta(ItemStack is, int meta) {
		return setMeta(is, meta, null);
	}

	public static ItemStack setMeta(ItemStack is, int meta, String name) {
		is.setItemDamage(meta);
		if (name != null)
			is.setStackDisplayName(name);
		return is;
	}

	public static ItemStack setName(ItemStack is, String name) {
		is.setStackDisplayName(name);
		return is;
	}

	public static ItemStack setPushUpEnchant(ItemStack itst, String name) {
		itst = getNBT(itst, "{display:{Name:\"" + name + "\",Lore:[\"" + I18n.format("act.minigame.pu.kopeople")
				+ "\"]},AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:0,Operation:1,UUIDLeast:8000,UUIDMost:4000},{AttributeName:\"generic.movementSpeed\",Name:\"generic.movementSpeed\",Amount:0.5,Operation:1,UUIDLeast:8000,UUIDMost:4000}]}");
		itst.addEnchantment(Enchantments.PUNCH, 5);
		itst.addEnchantment(Enchantments.KNOCKBACK, 5);
		itst.addEnchantment(Enchantments.INFINITY, 1);
		itst.getTagCompound().setInteger("Unbreakable", 1);
		return itst;
	}
}
