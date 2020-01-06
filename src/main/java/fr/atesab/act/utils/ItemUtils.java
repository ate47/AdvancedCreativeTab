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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.gson.internal.LinkedTreeMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.atesab.act.ACTMod;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFireworkRocket.Shape;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.ForgeRegistries;

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
		private Shape type;

		/**
		 * Create a random legit explosion {@link ExplosionInformation}
		 * 
		 * @since 2.1
		 */
		public ExplosionInformation() {
			this(CommandUtils.getRandomElement(Shape.values()), RANDOM.nextBoolean(), RANDOM.nextBoolean(),
					new int[RANDOM.nextInt(6) + 1], new int[RANDOM.nextInt(7)]);
			colors = new int[RANDOM.nextInt(6 - ((trail ? 1 : 0) + (flicker ? 1 : 0))) + 1];
			for (int i = 0; i < colors.length; i++)
				colors[i] = EnumDyeColor.values()[RANDOM.nextInt(EnumDyeColor.values().length)].func_196060_f(); // getColorCode
			for (int i = 0; i < fadeColors.length; i++)
				fadeColors[i] = EnumDyeColor.values()[RANDOM.nextInt(EnumDyeColor.values().length)].func_196060_f(); // getColorCode

		}

		/**
		 * Create an {@link ExplosionInformation}
		 * 
		 * @since 2.0
		 */
		public ExplosionInformation(int type, boolean trail, boolean flicker, int[] colors, int[] fadeColors) {
			this(Shape.func_196070_a(type), trail, flicker, colors, fadeColors);
		}

		/**
		 * Create an {@link ExplosionInformation}
		 * 
		 * @since 2.1
		 */
		public ExplosionInformation(Shape type, boolean trail, boolean flicker, int[] colors, int[] fadeColors) {
			this.type = type;
			this.trail = trail;
			this.flicker = flicker;
			this.colors = colors;
			this.fadeColors = fadeColors;
		}

		/**
		 * Create an {@link ExplosionInformation} with the tag of the explosion
		 * 
		 * @param explosion
		 *            the non null explosion tag
		 */
		public ExplosionInformation(NBTTagCompound explosion) {
			this(explosion.getByte("Type"), explosion.getBoolean("Trail"), explosion.getBoolean("Flicker"),
					explosion.getIntArray("Colors"), explosion.getIntArray("FadeColors"));
		}

		@Override
		public ExplosionInformation clone() {
			return new ExplosionInformation(type, trail, flicker, colors, fadeColors);
		}

		public ExplosionInformation colors(int[] colors) {
			this.colors = colors;
			return this;
		}

		public ExplosionInformation fadeColors(int[] fadeColors) {
			this.fadeColors = fadeColors;
			return this;
		}

		public ExplosionInformation flicker(boolean flicker) {
			this.flicker = flicker;
			return this;
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
			tag.setByte("Type", (byte) type.func_196071_a());
			if (trail)
				tag.setBoolean("Trail", true);
			if (flicker)
				tag.setBoolean("Flicker", true);
			if (colors.length != 0)
				tag.setIntArray("Colors", colors);
			if (fadeColors.length != 0)
				tag.setIntArray("FadeColors", fadeColors);
			return tag;
		}

		public Shape getType() {
			return type;
		}

		public boolean isFlicker() {
			return flicker;
		}

		public boolean isTrail() {
			return trail;
		}

		public ExplosionInformation trail(boolean trail) {
			this.trail = trail;
			return this;
		}

		public ExplosionInformation type(Shape type) {
			this.type = type;
			return this;
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

		public PotionInformation customColor(int customColor) {
			this.customColor = customColor;
			return this;
		}

		public PotionInformation customEffects(List<PotionEffect> customEffects) {
			this.customEffects = customEffects;
			return this;
		}

		public PotionInformation main(PotionType main) {
			this.main = main;
			return this;
		}

	}

	public static final String NBT_CHILD_DISPLAY = "display";
	public static final String NBT_CHILD_ENCHANTMENTS = "Enchantments";
	public static final String NBT_CHILD_BOOK_ENCHANTMENTS = "StoredEnchantments";
	public static final String NBT_CHILD_EXPLOSIONS = "Explosions";
	public static final String NBT_CHILD_FIREWORKS = "Fireworks";
	public static final String NBT_CHILD_ATTRIBUTE_MODIFIER = "AttributeModifiers";

	private static final ItemReader ITEM_READER = new ItemReader();
	private static final Random RANDOM = ACTMod.RANDOM;
	/*
	 * Caches are made to avoid the waiting time between requests to Mojang API
	 */
	private static final Map<String, Tuple<Long, NBTTagCompound>> SKIN_CACHE = new HashMap<>();
	private static final Map<String, Tuple<Long, String>> UUID_CACHE = new HashMap<>();

	private static final Character[] RANDOM_CHAR = { 'X', 'Y', 'M', 'Z' };

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
	 * @param name
	 * @param lore
	 * @param enchantments
	 *            Array of {@link Tuple} of {@link Enchantment} with there level
	 * @return your stack
	 * @see #buildStack(Item, int, int, String, String[], Tuple...)
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack buildStack(Block block, int count, @Nullable String name, @Nullable String[] lore,
			@Nullable Tuple<Enchantment, Integer>[] enchantments) {
		return buildStack(Item.getItemFromBlock(block), count, name, lore, enchantments);
	}

	/**
	 * Create a custom stack
	 * 
	 * @param item
	 * @param count
	 * @param name
	 * @param lore
	 * @param enchantments
	 *            Array of {@link Tuple} of {@link Enchantment} with there level
	 * @return your stack
	 * @see #buildStack(Block, int, int, String, String[], Tuple...)
	 * @since 2.0
	 */
	public static ItemStack buildStack(Item item, int count, @Nullable String name, @Nullable String[] lore,
			@Nullable Tuple<Enchantment, Integer>[] enchantments) {
		ItemStack is = new ItemStack(item, count);
		if (name != null)
			is.setDisplayName(new TextComponentString(name));
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
			if (mc.player.inventory.getStackInSlot(i) == null
					|| mc.player.inventory.getStackInSlot(i).getItem().equals(Items.AIR))
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
		NBTTagCompound compound = stack.getTag();
		compound = compound == null ? new NBTTagCompound() : compound;
		NBTTagList list = compound.getList(NBT_CHILD_ATTRIBUTE_MODIFIER, 10);
		List<Tuple<EntityEquipmentSlot, AttributeModifier>> result = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			NBTTagCompound tag = list.getCompound(i);
			EntityEquipmentSlot slot = null;
			if (tag.hasKey("Slot"))
				try {
					slot = EntityEquipmentSlot.fromString(tag.getString("Slot"));
				} catch (Exception e) {
				}
			result.add(new Tuple<EntityEquipmentSlot, AttributeModifier>(slot,
					SharedMonsterAttributes.readAttributeModifier(tag)));
		}
		return result;
	}

	/**
	 * Get Leather armor integer color (10511680 if no color has been found)
	 */
	public static int getColor(ItemStack stack) {
		NBTTagCompound tag = stack.getTag();
		return (tag == null || !tag.hasKey(NBT_CHILD_DISPLAY) || !tag.getCompound(NBT_CHILD_DISPLAY).hasKey("color"))
				? 10511680
				: tag.getCompound(NBT_CHILD_DISPLAY).getInt("color");

	}

	/**
	 * Get a custom String tag at the nbt root of a stack or a default value
	 * 
	 * @since 2.0
	 * @see #setCustomTag(ItemStack, String, String)
	 */
	public static String getCustomTag(ItemStack stack, String key, String defaultValue) {
		NBTTagCompound tag = stack.getTag();
		if (tag == null)
			return defaultValue;
		return tag.hasKey(key) ? tag.getString(key) : defaultValue;
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
	@SuppressWarnings("deprecation")
	public static List<Tuple<Enchantment, Integer>> getEnchantments(ItemStack stack, boolean book) {
		LinkedTreeMap<Enchantment, Integer> map = new LinkedTreeMap<>(
				(e1, e2) -> e2.getName().compareToIgnoreCase(e1.getName()));
		IRegistry.field_212628_q.forEach(e -> map.put(e, 0));
		String key = book ? NBT_CHILD_BOOK_ENCHANTMENTS : NBT_CHILD_ENCHANTMENTS;
		NBTTagList list = stack.getTag() != null && stack.getTag().hasKey(key) ? stack.getTag().getList(key, 10)
				: new NBTTagList();
		for (int i = 0; i < list.size(); i++) {
			NBTTagCompound tag = list.getCompound(i);
			if (tag.hasKey("id")) {
				Enchantment ench = IRegistry.field_212628_q.func_212608_b(ResourceLocation.makeResourceLocation(tag.getString("id")));
				if (ench != null)
					map.put(ench, tag.hasKey("lvl") ? tag.getInt("lvl") : 0);
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
		return explosion == null ? new ExplosionInformation() : new ExplosionInformation(explosion);
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
		return ITEM_READER.readItem(code);
	}

	/**
	 * Get a give code from a {@link ItemStack} (like after a /give player (code))
	 * 
	 * @since 2.0
	 * @see #getFromGiveCode(String)
	 */
	public static String getGiveCode(ItemStack itemStack) {
		return getGiveCode(itemStack, true);
	}

	/**
	 * Get a give code from a {@link ItemStack} (like after a /give player (code))
	 * 
	 * @since 2.0
	 * @see #getFromGiveCode(String)
	 */
	@SuppressWarnings("deprecation")
	public static String getGiveCode(ItemStack itemStack, boolean showCount) {
		boolean noTag = itemStack.getTag() != null && !itemStack.getTag().isEmpty();
		return itemStack == null ? ""
				: IRegistry.field_212630_s.getKey(itemStack.getItem()).toString() // Item.REGISTRY.getNameForObject
						+ (noTag ? itemStack.getTag().toString() : "")
						+ (itemStack.getCount() == 1 && showCount ? "" : " " + String.valueOf(itemStack.getCount()));
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
			throws IOException, CommandSyntaxException, NoSuchElementException {
		NBTTagCompound skullOwner = is.getOrCreateChildTag("SkullOwner");
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
		NBTTagCompound skullOwner = is.getOrCreateChildTag("SkullOwner");
		skullOwner.setString("Id", addHyphen(uuid.replaceAll("-", "")));
		NBTTagList textures = new NBTTagList();
		NBTTagCompound texture = new NBTTagCompound();
		texture.setString("Value",
				Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes()));
		textures.add(texture);
		getOrCreateSubCompound(skullOwner, "Properties").setTag("textures", textures);
		if (name != null)
			skullOwner.setString("Name", name);
		else if (skullOwner.hasKey("Name"))
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
	public static ItemStack getHead(String name) throws IOException, CommandSyntaxException, NoSuchElementException {
		return getHead(new ItemStack(Items.PLAYER_HEAD, 1), name);
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
		return getHead(new ItemStack(Items.PLAYER_HEAD, 1), uuid, url, name);
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
	public static List<ItemStack> getHeads(Collection<EntityPlayerMP> players)
			throws IOException, CommandSyntaxException, NoSuchElementException {
		return getHeads(players.stream().map(EntityPlayerMP::getScoreboardName).toArray(String[]::new));
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
	public static List<ItemStack> getHeads(String... names)
			throws IOException, CommandSyntaxException, NoSuchElementException {
		List<ItemStack> stacks = new ArrayList<>();
		getUUIDByNames(names).stream().forEach(tuple -> {
			try {
				ItemStack stack = new ItemStack(Items.PLAYER_HEAD, 1);
				NBTTagCompound skullOwner = stack.getOrCreateChildTag("SkullOwner");
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
		NBTTagCompound display = stack.getOrCreateChildTag(NBT_CHILD_DISPLAY);
		NBTTagList nbtTagList = display.getList("Lore", 8);
		String[] array = new String[nbtTagList.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = nbtTagList.getString(i);
		display.setTag("Lore", nbtTagList);
		return array;
	}

	private static NBTTagCompound getOrCreateSubCompound(NBTTagCompound compound, String key) {
		if (compound.contains(key, 10))
			return compound.getCompound(key);
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
		NBTTagCompound tag = stack.getTag();
		PotionUtils.addCustomPotionEffectToList(tag, customEffects);
		return new PotionInformation(
				tag != null && tag.hasKey("CustomPotionColor") ? tag.getInt("CustomPotionColor") : -1,
				PotionUtils.getPotionTypeFromNBT(tag), customEffects);
	}

	/**
	 * Get a random legitimate fireworks
	 * 
	 * @return your fireworks
	 * @since 2.1
	 */
	public static ItemStack getRandomFireworks() {
		NBTTagCompound fwt = new NBTTagCompound();
		int flight = RANDOM.nextInt(3); // a number between 0 and 2 (number of additional gunpowder)
		fwt.setInt("Flight", flight + 1);
		NBTTagList explosions = new NBTTagList();
		int exp = RANDOM.nextInt(7 - flight) + 1; // a number between 1 and 7 - flight (number of additional
													// gunpowder) -> (number of explosion)
		for (int i = 0; i < exp; i++)
			explosions.add(new ExplosionInformation().getTag());
		fwt.setTag(NBT_CHILD_EXPLOSIONS, explosions);
		ItemStack fw = new ItemStack(Items.FIREWORK_ROCKET);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(NBT_CHILD_FIREWORKS, fwt);
		fw.setTag(tag);
		// create a random name and description because "why not ?"
		List<EntityType<?>> list = ForgeRegistries.ENTITIES.getValues().stream().filter(EntityType::isSummonable)
				.collect(Collectors.toList());
		fw.setDisplayName(new TextComponentString(I18n.format(list.get(RANDOM.nextInt(list.size())).getTranslationKey())
				+ " " + CommandUtils.getRandomElement(RANDOM_CHAR) + RANDOM.nextInt(1000))
						.setStyle(new Style().setColor(TextFormatting.values()[RANDOM.nextInt(16)])));
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
	 * @throws CommandSyntaxException
	 * @since 2.0
	 */
	public static NBTTagCompound getSkinInformationFromUUID(String uuid) throws IOException, CommandSyntaxException {
		// check if a skin is find in the cache (1min : the minimum time between
		// request)
		if (SKIN_CACHE.containsKey(uuid) && SKIN_CACHE.get(uuid).a + 60000 > System.currentTimeMillis())
			return SKIN_CACHE.get(uuid).b.copy();
		NBTTagCompound requestCompound = JsonToNBT.getTagFromJson(
				sendRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replaceAll("-", ""),
						null, null, null));
		NBTTagCompound newTag = new NBTTagCompound();
		if (requestCompound.contains("properties", 9)) {
			NBTTagList properties = requestCompound.getList("properties", 10);
			NBTTagList textures = new NBTTagList();
			properties.forEach(base -> {
				NBTTagCompound tex = (NBTTagCompound) base;
				NBTTagCompound newTex = new NBTTagCompound();
				if (tex.contains("value", 8))
					newTex.setString("Value", tex.getString("value"));
				textures.add(newTex);
			});
			newTag.setTag("Properties", new NBTTagCompound());
			newTag.setString("Id", addHyphen(uuid.replaceAll("-", "")));
			newTag.getCompound("Properties").setTag("textures", textures);
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
	 * @throws CommandSyntaxException
	 * @since 2.0
	 */
	public static List<Tuple<String, String>> getUUIDByNames(String... names)
			throws IOException, CommandSyntaxException {
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
			NBTTagCompound tag = JsonToNBT
					.getTagFromJson("{data:" + sendRequest("https://api.mojang.com/profiles/minecraft", "POST",
							"application/json", "[" + query + "]") + "}");
			if (tag.contains("data", 9))
				tag.getList("data", 10).forEach(base -> {
					NBTTagCompound data = (NBTTagCompound) base;
					if (data.contains("id", 8) && data.contains("name", 8)) {
						String name = data.getString("name");
						String id = data.getString("id");
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
		give(Minecraft.getInstance(), stack);
	}

	/**
	 * Try to give an item in a slot
	 * 
	 * @since 2.1
	 * @see #give(ItemStack)
	 */
	public static void give(ItemStack stack, int slot) {
		give(Minecraft.getInstance(), stack, slot);
	}

	/**
	 * Try to give an item in the hotbar
	 * 
	 * @param stack
	 * @since 2.0
	 * @see #give(ItemStack, int)
	 */
	public static void give(List<ItemStack> stack) {
		give(Minecraft.getInstance(), stack);
	}

	/**
	 * Try to give an item in the hotbar
	 * 
	 * @since 2.0
	 * @see #give(ItemStack)
	 */
	@Deprecated
	public static void give(Minecraft mc, ItemStack stack) {
		if (mc.player != null && mc.player.isCreative()) {
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

	/**
	 * Try to give an item in a slot
	 * 
	 * @since 2.0
	 * @see #give(ItemStack, int)
	 */
	@Deprecated
	public static void give(Minecraft mc, ItemStack stack, int slot) {
		if (mc.player.isCreative()) {
			mc.playerController.sendSlotPacket(stack, slot);
		} else
			ChatUtils.error(I18n.format("gui.act.nocreative"));
	}

	/**
	 * Try to give an item in the hotbar
	 * 
	 * @since 2.0
	 * @see #give(ItemStack)
	 */
	@Deprecated
	public static void give(Minecraft mc, List<ItemStack> stack) {
		if (mc.player != null && mc.player.isCreative()) {
			int i = 0, j = 0;
			ItemStack is;
			stacks: for (; j < stack.size(); j++) {
				is = stack.get(j);
				for (; i < 9; i++) {
					if (mc.player.inventory.mainInventory.get(i).isEmpty()) {
						give(mc, is, 36 + i);
						ChatUtils.itemStack(is);
						i++;
						continue stacks;
					}
				}
				ChatUtils.error(I18n.format("gui.act.give.fail"));
				return;
			}
		} else
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
		NBTTagCompound tag = stack.getTag();
		return tag != null && tag.getBoolean("Unbreakable");
	}

	private static String sendRequest(String url, String method, String contentType, String content)
			throws IOException {
		Proxy proxy = Minecraft.getInstance() == null ? null : Minecraft.getInstance().getProxy();
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
			nbttagcompound.setInt("Operation", tuple.b.getOperation());
			nbttagcompound.setUniqueId("UUID", tuple.b.getID());
			if (tuple.a != null)
				nbttagcompound.setString("Slot", tuple.a.getName());
			nbttaglist.add(nbttagcompound);
		});
		NBTTagCompound tag = stack.getTag();
		if (tag == null)
			stack.setTag(tag = new NBTTagCompound());
		tag.setTag(NBT_CHILD_ATTRIBUTE_MODIFIER, nbttaglist);
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
		if (stack.getTag() == null)
			stack.setTag(new NBTTagCompound());
		NBTTagCompound display = stack.getOrCreateChildTag(NBT_CHILD_DISPLAY);
		if (color == 10511680 && display.hasKey("color"))
			display.removeTag("color");
		else
			display.setInt("color", color);
		return stack;
	}

	/**
	 * Set or create a custom String tag at the nbt root of a stack
	 * 
	 * @since 2.0
	 * @see #getCustomTag(ItemStack, String, String)
	 */
	public static ItemStack setCustomTag(ItemStack stack, String key, String value) {
		NBTTagCompound tag = stack.getTag();
		if (tag == null)
			stack.setTag(tag = new NBTTagCompound());
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
			nbttagcompound.setString("id", tuple.a.getRegistryName().toString());
			nbttagcompound.setInt("lvl", tuple.b);
			nbttaglist.add(nbttagcompound);
		});
		stack.getOrCreateTag().setTag(book ? NBT_CHILD_BOOK_ENCHANTMENTS : NBT_CHILD_ENCHANTMENTS, nbttaglist);
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
		ItemStack is = new ItemStack(item, stack.getCount());
		is.setTag(stack.getTag());
		return is;
	}

	/**
	 * Set lore(description) of a {@link ItemStack}
	 * 
	 * @since 2.0
	 * @see #getLore(ItemStack)
	 */
	public static ItemStack setLore(ItemStack stack, String[] lore) {
		NBTTagList nbtTagList = new NBTTagList();
		for (String l : lore)
			nbtTagList.add(new NBTTagString(l));
		stack.getOrCreateChildTag(NBT_CHILD_DISPLAY).setTag("Lore", nbtTagList);
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
			compound.setInt("Id", Potion.getIdFromPotion(effect.getPotion()));
			compound.setInt("Amplifier", effect.getAmplifier());
			compound.setInt("Duration", effect.getDuration());
			compound.setInt("Ambient", effect.isAmbient() ? 1 : 0);
			compound.setInt("ShowParticles", effect.doesShowParticles() ? 1 : 0);
			nbttaglist.add(compound);
		});
		NBTTagCompound tag = stack.getOrCreateTag();
		tag.setString("Potion", info.getMain().getRegistryName().toString());
		if (info.customColor != -1)
			tag.setInt("CustomPotionColor", info.customColor);
		else if (tag.hasKey("CustomPotionColor"))
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
		if (value) {
			stack.getOrCreateTag().setBoolean("Unbreakable", true);
		} else {
			NBTTagCompound tag = stack.getTag();
			if (tag != null)
				tag.removeTag("Unbreakable");
		}
		return stack;
	}
}
