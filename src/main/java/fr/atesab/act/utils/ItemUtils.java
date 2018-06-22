package fr.atesab.act.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.gson.internal.LinkedTreeMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;

public class ItemUtils {
	private static final Random random = new Random();

	public static final class ExplosionInformation implements Cloneable {
		private int type;
		private boolean trail, flicker;
		private int[] colors;
		private int[] fadeColors;

		public ExplosionInformation(int type, boolean trail, boolean flicker, int[] colors, int[] fadeColors) {
			this.type = type;
			this.trail = trail;
			this.flicker = flicker;
			this.colors = colors;
			this.fadeColors = fadeColors;
		}

		public NBTTagCompound getTag() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("Type", type);
			if (trail)
				tag.setByte("Trail", (byte) 1);
			if (flicker)
				tag.setByte("Flicker", (byte) 1);
			if (colors.length != 0)
				tag.setIntArray("Colors", colors);
			if (fadeColors.length != 0)
				tag.setIntArray("FadeColors", fadeColors);
			return tag;
		}

		@Override
		public ExplosionInformation clone() {
			return new ExplosionInformation(type, trail, flicker, colors, fadeColors);
		}

		public int[] getColors() {
			return colors;
		}

		public int[] getFadeColors() {
			return fadeColors;
		}

		public int getType() {
			return type;
		}

		public boolean isFlicker() {
			return flicker;
		}

		public boolean isTrail() {
			return trail;
		}

		public void setColors(int[] colors) {
			this.colors = colors;
		}

		public void setFadeColors(int[] fadeColors) {
			this.fadeColors = fadeColors;
		}

		public void setFlicker(boolean flicker) {
			this.flicker = flicker;
		}

		public void setTrail(boolean trail) {
			this.trail = trail;
		}

		public void setType(int type) {
			this.type = type;
		}
	}

	public static final class PotionInformation {
		private int customColor;
		private PotionType main;
		private List<PotionEffect> customEffects;

		public PotionInformation(int customColor, PotionType main, List<PotionEffect> customEffects) {
			this.customColor = customColor;
			this.main = main;
			this.customEffects = customEffects;
		}

		public int getCustomColor() {
			return customColor;
		}

		public List<PotionEffect> getCustomEffects() {
			return customEffects;
		}

		public PotionType getMain() {
			return main;
		}

		public void setCustomColor(int customColor) {
			this.customColor = customColor;
		}

		public void setCustomEffects(List<PotionEffect> customEffects) {
			this.customEffects = customEffects;
		}

		public void setMain(PotionType main) {
			this.main = main;
		}

	}

	private static final Map<String, Tuple<Long, String>> UUID_CACHE = new HashMap<>();
	private static final Map<String, Tuple<Long, NBTTagCompound>> SKIN_CACHE = new HashMap<>();

	public static String addHyphen(String uuid) {
		if (uuid.length() < 20)
			return uuid;
		return uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-"
				+ uuid.substring(16, 20) + "-" + uuid.substring(20, uuid.length());
	}

	public static ItemStack buildStack(Block block, int count, int meta, String name, String[] lore,
			Tuple<Enchantment, Integer>... enchantments) {
		return buildStack(Item.getItemFromBlock(block), count, meta, name, lore, enchantments);
	}

	public static ItemStack buildStack(Item item, int count, int meta, String name, String[] lore,
			Tuple<Enchantment, Integer>... enchantments) {
		ItemStack is = new ItemStack(item, count, meta);
		if (name != null)
			is.setStackDisplayName(name);
		if (lore != null)
			setLore(is, lore);
		if (enchantments.length != 0)
			setEnchantments(Arrays.asList(enchantments), is, is.getItem().equals(Items.ENCHANTED_BOOK));
		return is;
	}

	public static boolean canGive(Minecraft mc) {
		for (int i = 0; i < 9; i++)
			if (mc.player.inventory.getStackInSlot(i) == null) {
				return true;
			}
		return false;
	}

	public static List<Tuple<EntityEquipmentSlot, AttributeModifier>> getAttributes(ItemStack stack) {
		LinkedTreeMap<Enchantment, Integer> map = new LinkedTreeMap<>(
				(e1, e2) -> e2.getName().compareToIgnoreCase(e1.getName()));
		NBTTagCompound compound = stack.getTagCompound();
		compound = compound == null ? new NBTTagCompound() : compound;
		NBTTagList list = compound.getTagList("AttributeModifiers", 10);
		List<Tuple<EntityEquipmentSlot, AttributeModifier>> result = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			EntityEquipmentSlot slot = null;
			if (tag.hasKey("Slot"))
				try {
					slot = EntityEquipmentSlot.fromString(tag.getString("Slot"));
				} catch (Exception e) {
				}
			result.add(new Tuple<EntityEquipmentSlot, AttributeModifier>(slot,
					SharedMonsterAttributes.readAttributeModifierFromNBT(tag)));
		}
		return result;
	}

	public static int getColor(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return (tag == null || !tag.hasKey("display") || !tag.getCompoundTag("display").hasKey("color")) ? 10511680
				: tag.getCompoundTag("display").getInteger("color");

	}

	public static ItemStack getCommandBlock(String cmd, String name) {
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
		if (name != null)
			is.setStackDisplayName(name);
		return is;
	}

	public static String getCustomTag(ItemStack stack, String key, String defaultValue) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		return tag.hasKey(key) ? tag.getString(key) : defaultValue;
	}

	public static ExplosionInformation getExplosionInformation(NBTTagCompound explosion) {
		return explosion == null
				? new ExplosionInformation(random.nextInt(5), random.nextBoolean(), random.nextBoolean(),
						new int[] { random.nextInt(0x1000000) + 0xff000000 }, new int[0])
				: new ExplosionInformation(explosion.hasKey("Type") ? explosion.getInteger("Type") : 0,
						explosion.hasKey("Trail") && explosion.getByte("Trail") == (byte) 1,
						explosion.hasKey("Flicker") && explosion.getByte("Flicker") == (byte) 1,
						explosion.hasKey("Colors") ? explosion.getIntArray("Colors") : new int[0],
						explosion.hasKey("FadeColors") ? explosion.getIntArray("FadeColors") : new int[0]);
	}

	public static List<Tuple<Enchantment, Integer>> getEnchantments(ItemStack stack) {
		return getEnchantments(stack, false);
	}

	public static List<Tuple<Enchantment, Integer>> getEnchantments(ItemStack stack, boolean book) {
		LinkedTreeMap<Enchantment, Integer> map = new LinkedTreeMap<>(
				(e1, e2) -> e2.getName().compareToIgnoreCase(e1.getName()));
		Enchantment.REGISTRY.forEach(e -> map.put(e, 0));
		String key = book ? "StoredEnchantments" : "ench";
		NBTTagList list = stack.getTagCompound() != null && stack.getTagCompound().hasKey(key)
				? stack.getTagCompound().getTagList(key, 10)
				: new NBTTagList();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if (tag.hasKey("id")) {
				Enchantment ench = Enchantment.getEnchantmentByID(tag.getInteger("id"));
				if (ench != null)
					map.put(ench, tag.hasKey("lvl") ? tag.getInteger("lvl") : 0);
			}

		}
		List<Tuple<Enchantment, Integer>> result = new ArrayList<>();
		map.keySet().forEach(e -> result.add(new Tuple<>(e, map.get(e))));
		return result;
	}

	public static ItemStack getFromGiveCode(String code) {
		ItemStack itemstack = null;
		if (code == null)
			return itemstack;
		String[] args = code.split(" ");
		ResourceLocation resourcelocation = new ResourceLocation(args[0]);
		Item item = Item.REGISTRY.getObject(resourcelocation);
		if (item != null) {
			int i;
			try {
				i = args.length >= 2 ? CommandBase.parseInt(args[1]) : 1;
			} catch (NumberInvalidException e1) {
				i = 1;
			}
			int j;
			try {
				j = args.length >= 3 ? CommandBase.parseInt(args[2]) : 0;
			} catch (NumberInvalidException e1) {
				j = 0;
			}
			itemstack = new ItemStack(item, i, j);

			if (args.length >= 4) {
				String arg5 = args[3];
				if (args.length > 4) {
					for (int i1 = 4; i1 < args.length; i1++) {
						arg5 = arg5 + " " + args[i1];
					}
				}
				try {
					itemstack.setTagCompound(
							JsonToNBT.getTagFromJson(arg5.replaceAll("&", String.valueOf(ChatUtils.MODIFIER)) + ""));
				} catch (NBTException e) {
				}
			}
		}
		return itemstack;
	}

	public static String getGiveCode(ItemStack itemStack) {
		boolean a = itemStack.getTagCompound() != null && !itemStack.getTagCompound().hasNoTags();
		boolean b = itemStack.getMetadata() == 0 && !a;
		return itemStack == null ? ""
				: Item.REGISTRY.getNameForObject(itemStack.getItem()).toString()
						+ ((itemStack.getCount() == 1 || itemStack.getCount() == 0) && b ? ""
								: " " + itemStack.getCount()
										+ (b ? ""
												: " " + itemStack.getMetadata()
														+ (a ? " " + itemStack.getTagCompound().toString() : "")));
	}

	public static ItemStack getHead(ItemStack is, String name)
			throws IOException, NBTException, NoSuchElementException {
		NBTTagCompound skullOwner = is.getOrCreateSubCompound("SkullOwner");
		String uuid = getUUIDByNames(name).stream().findFirst().get().b;
		skullOwner.merge(getSkinInformationFromUUID(uuid));
		skullOwner.setString("Name", name);
		skullOwner.setString("Id", addHyphen(uuid));
		return is;
	}

	public static ItemStack getHead(ItemStack is, String uuid, String url, String name) {
		NBTTagCompound skullOwner = is.getOrCreateSubCompound("SkullOwner");
		skullOwner.setString("Id", addHyphen(uuid.replaceAll("-", "")));
		NBTTagList textures = new NBTTagList();
		NBTTagCompound texture = new NBTTagCompound();
		texture.setString("Value",
				Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes()));
		textures.appendTag(texture);
		getOrCreateSubCompound(skullOwner, "Properties").setTag("textures", textures);
		if (name != null)
			skullOwner.setString("Name", name);
		else if (skullOwner.hasKey("Name"))
			skullOwner.removeTag("Name");
		return is;
	}

	public static ItemStack getHead(String name) throws IOException, NBTException, NoSuchElementException {
		return getHead(new ItemStack(Items.SKULL, 1, 3), name);
	}

	public static ItemStack getHead(String uuid, String url, String name) {
		return getHead(new ItemStack(Items.SKULL, 1, 3), uuid, url, name);
	}

	public static List<ItemStack> getHeads(String... names) throws IOException, NBTException, NoSuchElementException {
		List<ItemStack> stacks = new ArrayList<>();
		getUUIDByNames(names).stream().forEach(tuple -> {
			try {
				ItemStack stack = new ItemStack(Items.SKULL, 1, 3);
				NBTTagCompound skullOwner = stack.getOrCreateSubCompound("SkullOwner");
				String uuid = tuple.b;
				skullOwner.merge(getSkinInformationFromUUID(uuid));
				skullOwner.setString("Name", tuple.a);
				skullOwner.setString("Id", addHyphen(uuid));
				stacks.add(stack);
			} catch (Exception e) {
			}
		});
		return stacks;
	}

	public static String[] getLore(ItemStack stack) {
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		NBTTagList nbtTagList = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
		String[] array = new String[nbtTagList.tagCount()];
		for (int i = 0; i < array.length; i++)
			array[i] = nbtTagList.getStringTagAt(i);
		return array;
	}

	private static NBTTagCompound getOrCreateSubCompound(NBTTagCompound compound, String key) {
		if (compound.hasKey(key, 10))
			return compound.getCompoundTag(key);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		compound.setTag(key, nbttagcompound);
		return nbttagcompound;

	}

	public static PotionInformation getPotionInformation(ItemStack stack) {
		List<PotionEffect> customEffects = new ArrayList<>();
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("CustomPotionEffects")) {
			NBTTagList list = tag.getTagList("CustomPotionEffects", 10);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound c = list.getCompoundTagAt(i);
				if (c.hasKey("Id")) {
					Potion pot = Potion.getPotionById(c.getInteger("Id"));
					if (pot != null)
						customEffects.add(new PotionEffect(pot, c.hasKey("Duration") ? c.getInteger("Duration") : 0,
								c.hasKey("Amplifier") ? c.getInteger("Amplifier") : 0,
								c.hasKey("Ambient") && c.getInteger("Ambient") == 1,
								c.hasKey("ShowParticles") && c.getInteger("ShowParticles") == 1));
				}
			}
		}
		return new PotionInformation(
				tag != null && tag.hasKey("CustomPotionColor") ? tag.getInteger("CustomPotionColor") : -1, PotionType
						.getPotionTypeForName(tag != null && tag.hasKey("Potion") ? tag.getString("Potion") : "empty"),
				customEffects);
	}

	public static NBTTagCompound getSkinInformationFromUUID(String uuid) throws IOException, NBTException {

		if (SKIN_CACHE.containsKey(uuid) && SKIN_CACHE.get(uuid).a + 60000 > System.currentTimeMillis())
			return SKIN_CACHE.get(uuid).b.copy();
		NBTTagCompound requestCompound = JsonToNBT.getTagFromJson(
				sendRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replaceAll("-", ""),
						null, null, null));
		NBTTagCompound newTag = new NBTTagCompound();
		if (requestCompound.hasKey("properties", 9)) {
			NBTTagList properties = requestCompound.getTagList("properties", 10);
			NBTTagList textures = new NBTTagList();
			properties.forEach(base -> {
				NBTTagCompound tex = (NBTTagCompound) base;
				NBTTagCompound newTex = new NBTTagCompound();
				if (tex.hasKey("value", 8))
					newTex.setString("Value", tex.getString("value"));
				textures.appendTag(newTex);
			});
			newTag.setTag("Properties", new NBTTagCompound());
			newTag.setString("Id", addHyphen(uuid.replaceAll("-", "")));
			newTag.getCompoundTag("Properties").setTag("textures", textures);
		}
		SKIN_CACHE.put(uuid, new Tuple<Long, NBTTagCompound>(System.currentTimeMillis(), newTag.copy()));
		return newTag;
	}

	public static List<Tuple<String, String>> getUUIDByNames(String... names) throws IOException, NBTException {
		List<Tuple<String, String>> list = new ArrayList<>();
		String query = Arrays.stream(names).map(n -> {
			if (UUID_CACHE.containsKey(n) && UUID_CACHE.get(n).a + 60000 > System.currentTimeMillis()) {
				list.add(new Tuple<>(n, UUID_CACHE.get(n).b));
				return null;
			}
			return '"' + n + '"';
		}).filter(s -> s != null).collect(Collectors.joining(","));
		if (!query.isEmpty()) {
			NBTTagCompound tag = JsonToNBT
					.getTagFromJson("{data:" + sendRequest("https://api.mojang.com/profiles/minecraft", "POST",
							"application/json", "[" + query + "]") + "}");
			if (tag.hasKey("data", 9))
				tag.getTagList("data", 10).forEach(base -> {
					NBTTagCompound data = (NBTTagCompound) base;
					if (data.hasKey("id", 8) && data.hasKey("name", 8)) {
						String name = data.getString("name");
						String id = data.getString("id");
						list.add(new Tuple<>(name, id));
						UUID_CACHE.put(name, new Tuple<>(System.currentTimeMillis(), id));
					}
				});
		}
		return list;
	}

	public static void give(ItemStack stack) {
		give(Minecraft.getMinecraft(), stack);
	}

	public static void give(Minecraft mc, ItemStack stack) {
		if (mc.player != null && mc.player.capabilities.isCreativeMode) {
			if (stack != null) {
				for (int i = 0; i < 9; i++) {
					if (mc.player.inventory.mainInventory.get(i).isEmpty()) {
						give(mc, stack, 36 + i);
						ChatUtils.itemStack(stack);
						return;
					}
				}
			}
			ChatUtils.error(I18n.format("gui.act.give.fail"));
		} else
			ChatUtils.error(I18n.format("gui.act.nocreative"));
	}

	public static void give(Minecraft mc, ItemStack stack, int slot) {
		if (mc.player.isCreative())
			mc.playerController.sendSlotPacket(stack, slot);
		else
			ChatUtils.error(I18n.format("gui.act.nocreative"));
	}

	public static boolean isUnbreakable(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return false;
		return tag.hasKey("Unbreakable") ? tag.getInteger("Unbreakable") == 1 : false;
	}

	private static String sendRequest(String url, String method, String contentType, String content)
			throws IOException {
		Proxy proxy = Minecraft.getMinecraft() == null ? null : Minecraft.getMinecraft().getProxy();
		if (proxy == null)
			proxy = Proxy.NO_PROXY;
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		if (method != null)
			connection.setRequestMethod(method);
		if (contentType != null)
			connection.setRequestProperty("Content-Type", contentType);
		connection.setRequestProperty("Content-Language", "en-US");
		connection.setDoOutput(true);
		if (content != null) {
			connection.setRequestProperty("Content-Length", "" + content.getBytes().length);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			DataOutputStream output = new DataOutputStream(connection.getOutputStream());
			output.writeBytes(content);
			output.flush();
			output.close();
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			buffer.append("\n" + line);
		}
		return buffer.length() > 0 ? buffer.toString().substring(1) : "";
	}

	public static ItemStack setAttributes(List<Tuple<EntityEquipmentSlot, AttributeModifier>> attributes,
			ItemStack stack) {
		NBTTagList nbttaglist = new NBTTagList();
		attributes.forEach(tuple -> {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString("Name", tuple.b.getName());
			nbttagcompound.setString("AttributeName", tuple.b.getName());
			nbttagcompound.setDouble("Amount", tuple.b.getAmount());
			nbttagcompound.setInteger("Operation", tuple.b.getOperation());
			nbttagcompound.setUniqueId("UUID", tuple.b.getID());
			if (tuple.a != null)
				nbttagcompound.setString("Slot", tuple.a.getName());
			nbttaglist.appendTag(nbttagcompound);
		});
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		tag.setTag("AttributeModifiers", nbttaglist);
		return stack;
	}

	public static ItemStack setColor(ItemStack stack, int color) {
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = stack.getTagCompound();
		if (!tag.hasKey("display"))
			tag.setTag("display", new NBTTagCompound());
		NBTTagCompound display = tag.getCompoundTag("display");
		if (color == 10511680 && display.hasKey("color"))
			display.removeTag("color");
		else
			display.setInteger("color", color);
		tag.setTag("display", display);
		stack.setTagCompound(tag);
		return stack;
	}

	public static ItemStack setCustomTag(ItemStack stack, String key, String value) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		tag.setString(key, value);
		return stack;
	}

	public static ItemStack setEnchantments(List<Tuple<Enchantment, Integer>> enchantments, ItemStack stack) {
		return setEnchantments(enchantments, stack, false);
	}

	public static ItemStack setEnchantments(List<Tuple<Enchantment, Integer>> enchantments, ItemStack stack,
			boolean book) {
		NBTTagList nbttaglist = new NBTTagList();
		enchantments.forEach(tuple -> {
			if (tuple.b == 0)
				return;
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("id", Enchantment.getEnchantmentID(tuple.a));
			nbttagcompound.setInteger("lvl", tuple.b);
			nbttaglist.appendTag(nbttagcompound);
		});
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		tag.setTag(book ? "StoredEnchantments" : "ench", nbttaglist);
		return stack;
	}

	public static ItemStack setItem(Item item, ItemStack stack) {
		ItemStack is = new ItemStack(item, stack.getCount(), stack.getMetadata());
		is.setTagCompound(stack.getTagCompound());
		return is;
	}

	public static ItemStack setLore(ItemStack stack, String[] lore) {
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		NBTTagList nbtTagList = new NBTTagList();
		for (String l : lore)
			nbtTagList.appendTag(new NBTTagString(l));
		NBTTagCompound tag = stack.getTagCompound();
		if (!tag.hasKey("display"))
			tag.setTag("display", new NBTTagCompound());
		NBTTagCompound display = tag.getCompoundTag("display");
		display.setTag("Lore", nbtTagList);
		tag.setTag("display", display);
		return stack;
	}

	public static ItemStack setPotionInformation(ItemStack stack, PotionInformation info) {
		NBTTagList nbttaglist = new NBTTagList();
		info.getCustomEffects().forEach(effect -> {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("Id", Potion.getIdFromPotion(effect.getPotion()));
			compound.setInteger("Amplifier", effect.getAmplifier());
			compound.setInteger("Duration", effect.getDuration());
			compound.setInteger("Ambient", effect.getIsAmbient() ? 1 : 0);
			compound.setInteger("ShowParticles", effect.doesShowParticles() ? 1 : 0);
			nbttaglist.appendTag(compound);
		});
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		tag.setString("Potion", info.getMain().getRegistryName().toString());
		if (info.customColor != -1)
			tag.setInteger("CustomPotionColor", info.customColor);
		else if (tag.hasKey("CustomPotionColor"))
			tag.removeTag("CustomPotionColor");
		tag.setTag("CustomPotionEffects", nbttaglist);
		return stack;
	}

	public static ItemStack setUnbreakable(ItemStack stack, boolean value) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		tag.setInteger("Unbreakable", value ? 1 : 0);
		return stack;
	}
}
