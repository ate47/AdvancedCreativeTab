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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * A set of tools to help to create and modify {@link ItemStack}
 * 
 * @author ATE47
 * @since 2.0
 */
public class ItemUtils {
	/**
	 * Information about an explosion
	 * 
	 * @author ATE47
	 * @since 2.0
	 */
	public static final class ExplosionInformation implements Cloneable {
		private int[] colors;
		private int[] fadeColors;
		private boolean trail, flicker;
		private int type;

		/**
		 * Create a random legit explosion {@link ExplosionInformation}
		 * 
		 * @since 2.1
		 */
		public ExplosionInformation() {
			this(random.nextInt(5), random.nextBoolean(), random.nextBoolean(), new int[random.nextInt(6) + 1],
					new int[random.nextInt(7)]);
			colors = new int[random.nextInt(6 - ((trail ? 1 : 0) + (flicker ? 1 : 0))) + 1];
			for (int i = 0; i < colors.length; i++)
				colors[i] = ItemDye.DYE_COLORS[random.nextInt(ItemDye.DYE_COLORS.length)];
			for (int i = 0; i < fadeColors.length; i++)
				fadeColors[i] = ItemDye.DYE_COLORS[random.nextInt(ItemDye.DYE_COLORS.length)];

		}

		/**
		 * Create an {@link ExplosionInformation}
		 * 
		 * @since 2.0
		 */
		public ExplosionInformation(int type, boolean trail, boolean flicker, int[] colors, int[] fadeColors) {
			this.type = type;
			this.trail = trail;
			this.flicker = flicker;
			this.colors = colors;
			this.fadeColors = fadeColors;
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

		/**
		 * Get Explosion tag for this explosion
		 * 
		 * @return the tag
		 * @since 2.0
		 */
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

	/**
	 * Information about a potion
	 * 
	 * @author ATE47
	 * @since 2.0
	 */
	public static final class PotionInformation {
		private int customColor;
		private List<PotionEffect> customEffects;
		private PotionType main;

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

	private static final Random random = new Random();
	/*
	 * Caches are made to avoid the waiting time between requests to Mojang API
	 */
	private static final Map<String, Tuple<Long, NBTTagCompound>> SKIN_CACHE = new HashMap<>();
	private static final Map<String, Tuple<Long, String>> UUID_CACHE = new HashMap<>();

	private static String addHyphen(String uuid) {
		if (uuid.length() < 20)
			return uuid;
		return uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-"
				+ uuid.substring(16, 20) + "-" + uuid.substring(20, uuid.length());
	}

	/**
	 * Create a custom stack
	 * 
	 * @param block
	 * @param count
	 * @param meta
	 * @param name
	 * @param lore
	 * @param enchantments
	 *            Array of {@link Tuple} of {@link Enchantment} with there level
	 * @return your stack
	 * @see #buildStack(Item, int, int, String, String[], Tuple...)
	 */
	public static ItemStack buildStack(Block block, int count, int meta, @Nullable String name, @Nullable String[] lore,
			@Nullable Tuple<Enchantment, Integer>... enchantments) {
		return buildStack(Item.getItemFromBlock(block), count, meta, name, lore, enchantments);
	}

	/**
	 * Create a custom stack
	 * 
	 * @param item
	 * @param count
	 * @param meta
	 * @param name
	 * @param lore
	 * @param enchantments
	 *            Array of {@link Tuple} of {@link Enchantment} with there level
	 * @return your stack
	 * @see #buildStack(Block, int, int, String, String[], Tuple...)
	 * @since 2.0
	 */
	public static ItemStack buildStack(Item item, int count, int meta, @Nullable String name, @Nullable String[] lore,
			@Nullable Tuple<Enchantment, Integer>... enchantments) {
		ItemStack is = new ItemStack(item, count, meta);
		if (name != null)
			is.setStackDisplayName(name);
		if (lore != null)
			setLore(is, lore);
		if (enchantments != null)
			setEnchantments(Arrays.asList(enchantments), is, is.getItem().equals(Items.ENCHANTED_BOOK));
		return is;
	}

	/**
	 * Check if the player have a clear slot in his hotbar
	 * 
	 * @return true if a slot is clear
	 * @since 2.0
	 */
	public static boolean canGive(Minecraft mc) {
		for (int i = 0; i < 9; i++)
			if (mc.thePlayer.inventory.getStackInSlot(i) == null
					|| mc.thePlayer.inventory.getStackInSlot(i).getItem().equals(Item.getItemFromBlock(Blocks.AIR)))
				return true;
		return false;
	}

	/**
	 * Get a list of tuples of Slot / Attribute of a stack
	 * 
	 * @since 2.0
	 * @see #setAttributes(List, ItemStack)
	 */
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
			if (tag.getKeySet().contains("Slot"))
				try {
					slot = EntityEquipmentSlot.fromString(tag.getString("Slot"));
				} catch (Exception e) {
				}
			result.add(new Tuple<EntityEquipmentSlot, AttributeModifier>(slot,
					SharedMonsterAttributes.readAttributeModifierFromNBT(tag)));
		}
		return result;
	}

	/**
	 * Get Leather armor integer color (10511680 if no color has been found)
	 */
	public static int getColor(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return (tag == null || !tag.getKeySet().contains("display")
				|| !tag.getCompoundTag("display").getKeySet().contains("color")) ? 10511680
						: tag.getCompoundTag("display").getInteger("color");

	}

	/**
	 * Get a custom String tag at the nbt root of a stack or a default value
	 * 
	 * @since 2.0
	 * @see #setCustomTag(ItemStack, String, String)
	 */
	public static String getCustomTag(ItemStack stack, String key, String defaultValue) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return defaultValue;
		return tag.getKeySet().contains(key) ? tag.getString(key) : defaultValue;
	}

	/**
	 * Get {@link Enchantment}s of an {@link ItemStack}
	 * 
	 * @see #setEnchantments(List, ItemStack)
	 * @see #getEnchantments(ItemStack)
	 * @since 2.0
	 */
	public static List<Tuple<Enchantment, Integer>> getEnchantments(ItemStack stack) {
		return getEnchantments(stack, false);
	}

	/**
	 * Get {@link Enchantment}s of an {@link ItemStack} with checking if it is a
	 * book
	 * 
	 * @param stack
	 * @param book
	 *            if this stack is a book or not
	 * @see #setEnchantments(List, ItemStack, boolean)
	 * @see #getEnchantments(ItemStack)
	 * @since 2.0
	 */
	public static List<Tuple<Enchantment, Integer>> getEnchantments(ItemStack stack, boolean book) {
		LinkedTreeMap<Enchantment, Integer> map = new LinkedTreeMap<>(
				(e1, e2) -> e2.getName().compareToIgnoreCase(e1.getName()));
		Enchantment.REGISTRY.forEach(e -> map.put((Enchantment) e, 0));
		String key = book ? "StoredEnchantments" : "ench";
		NBTTagList list = stack.getTagCompound() != null && stack.getTagCompound().getKeySet().contains(key)
				? stack.getTagCompound().getTagList(key, 10)
				: new NBTTagList();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if (tag.getKeySet().contains("id")) {
				Enchantment ench = Enchantment.getEnchantmentByID(tag.getInteger("id"));
				if (ench != null)
					map.put(ench, tag.getKeySet().contains("lvl") ? tag.getInteger("lvl") : 0);
			}

		}
		List<Tuple<Enchantment, Integer>> result = new ArrayList<>();
		map.keySet().forEach(e -> result.add(new Tuple<>(e, map.get(e))));
		return result;
	}

	/**
	 * Get {@link ExplosionInformation} from a tag, if the tag is null a
	 * legit-random explosion will be return
	 * 
	 * @since 2.1
	 */
	public static ExplosionInformation getExplosionInformation(@Nullable NBTTagCompound explosion) {
		return explosion == null ? new ExplosionInformation()
				: new ExplosionInformation(explosion.getKeySet().contains("Type") ? explosion.getInteger("Type") : 0,
						explosion.getKeySet().contains("Trail") && explosion.getByte("Trail") == (byte) 1,
						explosion.getKeySet().contains("Flicker") && explosion.getByte("Flicker") == (byte) 1,
						explosion.getKeySet().contains("Colors") ? explosion.getIntArray("Colors") : new int[0],
						explosion.getKeySet().contains("FadeColors") ? explosion.getIntArray("FadeColors")
								: new int[0]);
	}

	/**
	 * Get an {@link ItemStack} from a give code (like after a /give player (code))
	 * 
	 * @since 2.0
	 * @see #getGiveCode(ItemStack)
	 */
	public static ItemStack getFromGiveCode(String code) {
		if (code == null || code.isEmpty())
			return null;
		String[] args = code.split(" ", 4);
		Item item = Item.REGISTRY.getObject(new ResourceLocation(args[0]));
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
			ItemStack itemstack = new ItemStack(item, i, j);

			if (args.length == 4) {
				try {
					itemstack.setTagCompound(
							ItemUtils.toJson(args[3].replaceAll("&", String.valueOf(ChatUtils.MODIFIER)) + ""));
				} catch (NBTException e) {
				}
			}
			return itemstack;
		}
		return null;
	}

	/**
	 * Get a give code from a {@link ItemStack} (like after a /give player (code))
	 * 
	 * @since 2.0
	 * @see #getFromGiveCode(String)
	 */
	public static String getGiveCode(ItemStack itemStack) {
		boolean a = itemStack.getTagCompound() != null && !itemStack.getTagCompound().hasNoTags();
		boolean b = itemStack.getMetadata() == 0 && !a;
		return itemStack == null ? ""
				: Item.REGISTRY.getNameForObject(itemStack.getItem()).toString()
						+ ((itemStack.stackSize == 1 || itemStack.stackSize == 0) && b ? ""
								: " " + itemStack.stackSize
										+ (b ? ""
												: " " + itemStack.getMetadata()
														+ (a ? " " + itemStack.getTagCompound().toString() : "")));
	}

	/**
	 * Change a head with new skin information given by name
	 * 
	 * @since 2.0
	 * @see #getHead(String)
	 * @see #getHead(String, String, String)
	 * @see #getHead(ItemStack, String, String, String)
	 * @see #getHeads(String...)
	 */
	public static ItemStack getHead(ItemStack is, String name)
			throws IOException, NBTException, NoSuchElementException {
		NBTTagCompound skullOwner = getOrCreateSubCompound(is, "SkullOwner");
		String uuid = getUUIDByNames(name).stream().findFirst().get().b;
		skullOwner.merge(getSkinInformationFromUUID(uuid));
		skullOwner.setString("Name", name);
		skullOwner.setString("Id", addHyphen(uuid));
		return is;
	}

	/**
	 * Change a head with new skin information given by uuid, url and name
	 * 
	 * @since 2.0
	 * @see #getHead(String)
	 * @see #getHead(ItemStack, String)
	 * @see #getHead(ItemStack, String, String, String)
	 * @see #getHeads(String...)
	 */
	public static ItemStack getHead(ItemStack is, String uuid, String url, String name) {
		NBTTagCompound skullOwner = getOrCreateSubCompound(is, "SkullOwner");
		skullOwner.setString("Id", addHyphen(uuid.replaceAll("-", "")));
		NBTTagList textures = new NBTTagList();
		NBTTagCompound texture = new NBTTagCompound();
		texture.setString("Value",
				Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes()));
		textures.appendTag(texture);
		getOrCreateSubCompound(skullOwner, "Properties").setTag("textures", textures);
		if (name != null)
			skullOwner.setString("Name", name);
		else if (skullOwner.getKeySet().contains("Name"))
			skullOwner.removeTag("Name");
		return is;
	}

	/**
	 * Create a head with skin information given by name
	 * 
	 * @since 2.0
	 * @see #getHead(ItemStack, String)
	 * @see #getHead(String, String, String)
	 * @see #getHead(ItemStack, String, String, String)
	 * @see #getHeads(String...)
	 */
	public static ItemStack getHead(String name) throws IOException, NBTException, NoSuchElementException {
		return getHead(new ItemStack(Items.SKULL, 1, 3), name);
	}

	/**
	 * Create a head with skin information given by uuid, url and name
	 * 
	 * @since 2.0
	 * @see #getHead(String)
	 * @see #getHead(ItemStack, String)
	 * @see #getHead(ItemStack, String, String, String)
	 * @see #getHeads(String...)
	 */
	public static ItemStack getHead(String uuid, String url, String name) {
		return getHead(new ItemStack(Items.SKULL, 1, 3), uuid, url, name);
	}

	/**
	 * Create a heads with skin information given by names
	 * 
	 * @since 2.0
	 * @see #getHead(String)
	 * @see #getHead(ItemStack, String)
	 * @see #getHead(String, String, String)
	 * @see #getHead(ItemStack, String, String, String)
	 */
	public static List<ItemStack> getHeads(String... names) throws IOException, NBTException, NoSuchElementException {
		List<ItemStack> stacks = new ArrayList<>();
		getUUIDByNames(names).stream().forEach(tuple -> {
			try {
				ItemStack stack = new ItemStack(Items.SKULL, 1, 3);
				NBTTagCompound skullOwner = getOrCreateSubCompound(stack, "SkullOwner");
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

	/**
	 * Get lore(description) of an {@link ItemStack}
	 * 
	 * @since 2.0
	 * @see #setLore(ItemStack, String[])
	 */
	public static String[] getLore(ItemStack stack) {
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		NBTTagList nbtTagList = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
		String[] array = new String[nbtTagList.tagCount()];
		for (int i = 0; i < array.length; i++)
			array[i] = nbtTagList.getStringTagAt(i);
		return array;
	}

	public static NBTTagCompound getOrCreateSubCompound(ItemStack stack, String key) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		if (tag.hasKey(key, 10))
			return tag.getCompoundTag(key);
		NBTTagCompound sTag = new NBTTagCompound();
		tag.setTag(key, sTag);
		return sTag;
	}

	private static NBTTagCompound getOrCreateSubCompound(NBTTagCompound compound, String key) {
		if (compound.getKeySet().contains(key))
			return compound.getCompoundTag(key);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		compound.setTag(key, nbttagcompound);
		return nbttagcompound;

	}

	/**
	 * Get {@link PotionInformation} of stack
	 * 
	 * @param stack
	 *            your potion
	 * @return potion information
	 * @see #setPotionInformation(ItemStack, PotionInformation)
	 * @since 2.0
	 */
	public static PotionInformation getPotionInformation(ItemStack stack) {
		List<PotionEffect> customEffects = new ArrayList<>();
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.getKeySet().contains("CustomPotionEffects")) {
			NBTTagList list = tag.getTagList("CustomPotionEffects", 10);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound c = list.getCompoundTagAt(i);
				if (c.getKeySet().contains("Id")) {
					Potion pot = Potion.getPotionById(c.getInteger("Id"));
					if (pot != null)
						customEffects.add(
								new PotionEffect(pot, c.getKeySet().contains("Duration") ? c.getInteger("Duration") : 0,
										c.getKeySet().contains("Amplifier") ? c.getInteger("Amplifier") : 0,
										c.getKeySet().contains("Ambient") && c.getInteger("Ambient") == 1,
										c.getKeySet().contains("ShowParticles") && c.getInteger("ShowParticles") == 1));
				}
			}
		}
		return new PotionInformation(
				tag != null && tag.getKeySet().contains("CustomPotionColor") ? tag.getInteger("CustomPotionColor") : -1,
				PotionType.getPotionTypeForName(
						tag != null && tag.getKeySet().contains("Potion") ? tag.getString("Potion") : "empty"),
				customEffects);
	}

	/**
	 * Get a random legitimate fireworks
	 * 
	 * @return your fireworks
	 * @since 2.1
	 */
	public static ItemStack getRandomFireworks() {
		NBTTagCompound fwt = new NBTTagCompound();
		int flight = random.nextInt(3); // a number between 0 and 2 (number of additional gunpowder)
		fwt.setInteger("Flight", flight + 1);
		NBTTagList explosions = new NBTTagList();
		int exp = random.nextInt(7 - flight) + 1; // a number between 1 and 7 - flight (number of additional
													// gunpowder) -> (number of explosion)
		for (int i = 0; i < exp; i++)
			explosions.appendTag(new ExplosionInformation().getTag());
		fwt.setTag("Explosions", explosions);
		ItemStack fw = new ItemStack(Items.FIREWORKS);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("Fireworks", fwt);
		fw.setTagCompound(tag);
		// create a random name and description because "why not ?"
		List<String> list = EntityList.getEntityNameList().stream().filter(ee -> EntityList.ENTITY_EGGS.get(ee) != null)
				.collect(Collectors.toList());
		fw.setStackDisplayName(ChatUtils.MODIFIER + Integer.toHexString(random.nextInt(6) + 9)
				+ I18n.format("entity." + list.get(random.nextInt(list.size())) + ".name") + " "
				+ (new char[] { 'X', 'Y', 'M', 'Z' })[random.nextInt(4)] + random.nextInt(1000));
		setLore(fw, new String[] { TextFormatting.YELLOW + "" + TextFormatting.ITALIC + I18n.format("cmd.act.rfw") });
		return fw;
	}

	/**
	 * Request {@link NBTTagCompound} of Skin Information with the Mojang API
	 * 
	 * @param uuid
	 *            Player's UUID
	 * @return skin information
	 * @throws IOException
	 * @throws NBTException
	 * @since 2.0
	 */
	public static NBTTagCompound getSkinInformationFromUUID(String uuid) throws IOException, NBTException {
		// check if a skin is find in the cache (1min : the minimum time between
		// request)
		if (SKIN_CACHE.containsKey(uuid) && SKIN_CACHE.get(uuid).a + 60000 > System.currentTimeMillis())
			return SKIN_CACHE.get(uuid).b.copy();
		LinkedTreeMap<String, Object> query = new Gson().fromJson(
				sendRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replaceAll("-", ""),
						null, null, null),
				LinkedTreeMap.class);
		NBTTagCompound newTag = new NBTTagCompound();
		if (query.containsKey("properties")) {
			List<LinkedTreeMap<String, Object>> properties = (List<LinkedTreeMap<String, Object>>) query
					.get("properties");
			NBTTagList textures = new NBTTagList();
			properties.forEach(prop -> {
				NBTTagCompound newTex = new NBTTagCompound();
				if (prop.containsKey("value")) {
					newTex.setString("Value", (String) prop.get("value"));
				}
				textures.appendTag(newTex);
			});
			newTag.setTag("Properties", new NBTTagCompound());
			newTag.setString("Id", addHyphen(uuid.replaceAll("-", "")));
			newTag.getCompoundTag("Properties").setTag("textures", textures);
		}
		SKIN_CACHE.put(uuid, new Tuple<Long, NBTTagCompound>(System.currentTimeMillis(), newTag.copy()));
		return newTag;
	}

	/**
	 * Request {@link Tuple} of name / UUID of users by there name with the Mojang
	 * API
	 * 
	 * @param names
	 *            Players' names
	 * @return List of name find
	 * @throws IOException
	 * @throws NBTException
	 * @since 2.0
	 */
	public static List<Tuple<String, String>> getUUIDByNames(String... names) throws IOException, NBTException {
		List<Tuple<String, String>> list = new ArrayList<>();
		String query = Arrays.stream(names).map(n -> {
			// Check in cache for UUID (1min : the minimum time between request)
			if (UUID_CACHE.containsKey(n) && UUID_CACHE.get(n).a + 60000 > System.currentTimeMillis()) {
				list.add(new Tuple<>(n, UUID_CACHE.get(n).b));
				return null;
			}
			return '"' + n + '"';
		})
				// remove find elements
				.filter(s -> s != null)
				// create the query
				.collect(Collectors.joining(","));
		// request only if names aren't in cache
		if (!query.isEmpty()) {
			List<LinkedTreeMap<String, Object>> data = new Gson()
					.fromJson(sendRequest("https://api.mojang.com/profiles/minecraft", "POST", "application/json",
							"[" + query + "]"), List.class);
			data.forEach(map -> {
				if (map.containsKey("id") && map.containsKey("name")) {
					String name = (String) map.get("name");
					String id = (String) map.get("id");
					list.add(new Tuple<>(name, id));
					UUID_CACHE.put(name, new Tuple<>(System.currentTimeMillis(), id));
				}
			});
		}
		return list;

	}

	/**
	 * Try to give an item in the hotbar
	 * 
	 * @param stack
	 * @since 2.0
	 * @see #give(ItemStack, int)
	 */
	public static void give(ItemStack stack) {
		give(Minecraft.getMinecraft(), stack);
	}

	/**
	 * Try to give an item in a slot
	 * 
	 * @since 2.1
	 * @see #give(ItemStack)
	 */
	public static void give(ItemStack stack, int slot) {
		give(Minecraft.getMinecraft(), stack, slot);
	}

	/**
	 * Try to give an item in the hotbar
	 * 
	 * @since 2.0
	 * @see #give(ItemStack)
	 */
	@Deprecated
	public static void give(Minecraft mc, ItemStack stack) {
		if (mc.thePlayer != null && mc.thePlayer.capabilities.isCreativeMode) {
			if (stack != null) {
				for (int i = 0; i < 9; i++) {
					ItemStack is = mc.thePlayer.inventory.mainInventory[i];
					if (is == null || is.getItem().equals(Item.getItemFromBlock(Blocks.AIR))) {
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

	/**
	 * Try to give an item in a slot
	 * 
	 * @since 2.0
	 * @see #give(ItemStack, int)
	 */
	@Deprecated
	public static void give(Minecraft mc, ItemStack stack, int slot) {
		if (mc.thePlayer.isCreative())
			mc.playerController.sendSlotPacket(stack, slot);
		else
			ChatUtils.error(I18n.format("gui.act.nocreative"));
	}

	/**
	 * Check if a {@link ItemStack} is unbreakable
	 * 
	 * @param stack
	 *            your stack
	 * @return if the stack is unbreakable
	 * @see #setUnbreakable(ItemStack, boolean)
	 * @since 2.0
	 */
	public static boolean isUnbreakable(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return false;
		return tag.getKeySet().contains("Unbreakable") ? tag.getInteger("Unbreakable") == 1 : false;
	}

	private static String sendRequest(String url, String method, String contentType, String content)
			throws IOException {
		Proxy proxy = Minecraft.getMinecraft() == null ? null : Minecraft.getMinecraft().getProxy();
		if (proxy == null)
			proxy = Proxy.NO_PROXY;
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(proxy);
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

	/**
	 * Set a list of tuples of Slot / Attribute to a stack
	 * 
	 * @since 2.0
	 * @see #getAttributes(ItemStack)
	 */
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

	/**
	 * Change the color of a leather armor
	 * 
	 * @param stack
	 *            leather armor
	 * @param color
	 *            integer color
	 * @return your stack
	 * @since 2.0
	 */
	public static ItemStack setColor(ItemStack stack, int color) {
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = stack.getTagCompound();
		if (!tag.getKeySet().contains("display"))
			tag.setTag("display", new NBTTagCompound());
		NBTTagCompound display = tag.getCompoundTag("display");
		if (color == 10511680 && display.getKeySet().contains("color"))
			display.removeTag("color");
		else
			display.setInteger("color", color);
		tag.setTag("display", display);
		stack.setTagCompound(tag);
		return stack;
	}

	/**
	 * Set or create a custom String tag at the nbt root of a stack
	 * 
	 * @since 2.0
	 * @see #getCustomTag(ItemStack, String, String)
	 */
	public static ItemStack setCustomTag(ItemStack stack, String key, String value) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			stack.setTagCompound(tag = new NBTTagCompound());
		tag.setString(key, value);
		return stack;
	}

	/**
	 * Set {@link Enchantment}s of an {@link ItemStack}
	 * 
	 * @see #setEnchantments(List, ItemStack, boolean)
	 * @see #getEnchantments(ItemStack)
	 * @since 2.0
	 */
	public static ItemStack setEnchantments(List<Tuple<Enchantment, Integer>> enchantments, ItemStack stack) {
		return setEnchantments(enchantments, stack, false);
	}

	/**
	 * Set {@link Enchantment}s of an {@link ItemStack} with checking if it is a
	 * book
	 * 
	 * @param enchantments
	 * @param stack
	 * @param book
	 *            if this stack is a book or not
	 * @see #setEnchantments(List, ItemStack)
	 * @see #getEnchantments(ItemStack, boolean)
	 * @since 2.0
	 */
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

	/**
	 * Create a new {@link ItemStack} with the same count, metadata and
	 * {@link NBTTagCompound} than the given stack but with a new {@link Item} type
	 * 
	 * @param item
	 *            New {@link Item} type
	 * @param stack
	 *            Your stack to copy
	 * @return Your new stack
	 * @since 2.0
	 */
	public static ItemStack setItem(Item item, ItemStack stack) {
		ItemStack is = new ItemStack(item, stack.stackSize, stack.getMetadata());
		is.setTagCompound(stack.getTagCompound());
		return is;
	}

	/**
	 * Set lore(description) of a {@link ItemStack}
	 * 
	 * @since 2.0
	 * @see #getLore(ItemStack)
	 */
	public static ItemStack setLore(ItemStack stack, String[] lore) {
		if (stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		NBTTagList nbtTagList = new NBTTagList();
		for (String l : lore)
			nbtTagList.appendTag(new NBTTagString(l));
		NBTTagCompound tag = stack.getTagCompound();
		if (!tag.getKeySet().contains("display"))
			tag.setTag("display", new NBTTagCompound());
		NBTTagCompound display = tag.getCompoundTag("display");
		display.setTag("Lore", nbtTagList);
		tag.setTag("display", display);
		return stack;
	}

	/**
	 * Set {@link PotionInformation} to a {@link ItemStack}
	 * 
	 * @param stack
	 *            your stack to change
	 * @param info
	 *            {@link PotionInformation} to set
	 * @return your stack
	 * @see #getPotionInformation(ItemStack)
	 * @since 2.0
	 */
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
		else if (tag.getKeySet().contains("CustomPotionColor"))
			tag.removeTag("CustomPotionColor");
		tag.setTag("CustomPotionEffects", nbttaglist);
		return stack;
	}

	/**
	 * Set a {@link ItemStack} Unbreakable
	 * 
	 * @param stack
	 *            Your stack
	 * @param value
	 *            Unbreakable or not
	 * @return Your stack
	 * @see #isUnbreakable(ItemStack)
	 * @since 2.0
	 */
	public static ItemStack setUnbreakable(ItemStack stack, boolean value) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null || (tag == null && value)) {
			stack.setTagCompound(tag = new NBTTagCompound());
			if (tag.getKeySet().contains("Unbreakable") && !value)
				tag.removeTag("Unbreakable");
			else
				tag.setInteger("Unbreakable", 1);
		}
		return stack;
	}

	/**
	 * Translate a {@link String} to a {@link NBTTagCompound} and
	 * {@link #validate(NBTTagCompound)} it
	 */
	public static NBTTagCompound toJson(String json) throws NBTException {
		return validate(JsonToNBT.getTagFromJson(json));
	}

	/**
	 * Remove " of keys
	 */
	public static NBTTagCompound validate(NBTTagCompound tag) throws NBTException {
		NBTTagCompound newTag = new NBTTagCompound();
		tag.getKeySet().forEach(key -> {
			NBTBase base = tag.getTag(key);
			if (base instanceof NBTTagCompound)
				try {
					base = validate((NBTTagCompound) base);
				} catch (NBTException e) {
				}
			else if (base instanceof NBTTagList) {
				base = validate((NBTTagList) base);
			}
			newTag.setTag(key.startsWith("\"") && key.endsWith("\"") ? key.substring(1, key.length() - 1) : key, base);
		});
		return newTag;
	}

	private static NBTTagList validate(NBTTagList tag) {
		if (tag.getTagType() == 9) { // list
			NBTTagList nlist = new NBTTagList();
			forEachInNBTTagList(tag, base -> nlist.appendTag(validate((NBTTagList) base)));
			return nlist;
		} else if (tag.getTagType() == 10) { // tag
			NBTTagList nlist = new NBTTagList();
			forEachInNBTTagList(tag, base -> {
				try {
					nlist.appendTag(validate((NBTTagCompound) base));
				} catch (NBTException e) {
					nlist.appendTag(base);
				}
			});
			return nlist;
		} else
			return tag;
	}

	public static void forEachInNBTTagList(NBTTagList list, Consumer<NBTBase> consumer) {
		for (int i = 0; i < list.tagCount(); i++)
			consumer.accept(list.get(i));
	}
}
