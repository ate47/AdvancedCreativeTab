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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.google.gson.internal.LinkedTreeMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fr.atesab.act.ACTMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.FireworkRocketItem.Shape;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A set of tools to help to create and modify {@link ItemStack}
 * 
 * @author ATE47
 * @since 2.0
 */
public class ItemUtils {
	/**
	 * Attribute data
	 */
	public static class AttributeData {
		private EquipmentSlot slot;
		private AttributeModifier modifier;
		private Attribute attribute;

		public AttributeData(EquipmentSlot slot, AttributeModifier modifier, Attribute attribute) {
			this.slot = slot;
			this.modifier = modifier;
			this.attribute = attribute;
		}

		/**
		 * @param slot the slot to set
		 */
		public void setSlot(EquipmentSlot slot) {
			this.slot = slot;
		}

		/**
		 * @param modifier the modifier to set
		 */
		public void setModifier(AttributeModifier modifier) {
			this.modifier = modifier;
		}

		/**
		 * @param attribute the attribute to set
		 */
		public void setAttribute(Attribute attribute) {
			this.attribute = attribute;
		}

		/**
		 * @return the attribute
		 */
		public Attribute getAttribute() {
			return attribute;
		}

		/**
		 * @return the modifier
		 */
		public AttributeModifier getModifier() {
			return modifier;
		}

		/**
		 * @return the slot
		 */
		public EquipmentSlot getSlot() {
			return slot;
		}
	}

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
				colors[i] = DyeColor.values()[RANDOM.nextInt(DyeColor.values().length)].getFireworkColor();
			for (int i = 0; i < fadeColors.length; i++)
				fadeColors[i] = DyeColor.values()[RANDOM.nextInt(DyeColor.values().length)].getFireworkColor();

		}

		/**
		 * Create an {@link ExplosionInformation}
		 * 
		 * @param type       the type of the explosion
		 * @param trail      if the explosion has a trail
		 * @param flicker    if the explosion has a flicker
		 * @param colors     the explosion colors
		 * @param fadeColors the explosion fadeColors
		 * 
		 * @since 2.0
		 */
		public ExplosionInformation(int type, boolean trail, boolean flicker, int[] colors, int[] fadeColors) {
			this(Shape.byId(type), trail, flicker, colors, fadeColors);
		}

		/**
		 * Create an {@link ExplosionInformation}
		 * 
		 * @param type       the type of the explosion
		 * @param trail      if the explosion has a trail
		 * @param flicker    if the explosion has a flicker
		 * @param colors     the explosion colors
		 * @param fadeColors the explosion fadeColors
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
		 * @param explosion the non null explosion tag
		 */
		public ExplosionInformation(CompoundTag explosion) {
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
		public CompoundTag getTag() {
			CompoundTag tag = new CompoundTag();
			tag.putByte("Type", (byte) type.getId());
			if (trail)
				tag.putBoolean("Trail", true);
			if (flicker)
				tag.putBoolean("Flicker", true);
			if (colors.length != 0)
				tag.putIntArray("Colors", colors);
			if (fadeColors.length != 0)
				tag.putIntArray("FadeColors", fadeColors);
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
		private OptionalInt customColor;
		private List<MobEffectInstance> customEffects;
		private Potion main;

		public PotionInformation(OptionalInt customColor, Potion main, List<MobEffectInstance> customEffects) {
			this.customColor = customColor;
			this.main = main;
			this.customEffects = customEffects;
		}

		public OptionalInt getCustomColor() {
			return customColor;
		}

		public List<MobEffectInstance> getCustomEffects() {
			return customEffects;
		}

		public Potion getMain() {
			return main;
		}

		public PotionInformation customColor(OptionalInt customColor) {
			this.customColor = customColor;
			return this;
		}

		public PotionInformation customEffects(List<MobEffectInstance> customEffects) {
			this.customEffects = customEffects;
			return this;
		}

		public PotionInformation main(Potion main) {
			this.main = main;
			return this;
		}

	}

	public static class AttributeModifierBuilder {
		private static final List<AttributeModifierBuilder> BUILDERS_INTERNAL = new ArrayList<>();
		public static final List<AttributeModifierBuilder> BUILDERS = Collections.unmodifiableList(BUILDERS_INTERNAL);
		public static final AttributeModifierBuilder ARMOR;
		public static final AttributeModifierBuilder ARMOR_TOUGHNESS;
		public static final AttributeModifierBuilder KNOCKBACK_RESISTANCE;
		public static final AttributeModifierBuilder TOOL_ATTACK_DAMAGE;
		public static final AttributeModifierBuilder TOOL_ATTACK_SPEED;
		public static final AttributeModifierBuilder SWORD_ATTACK_DAMAGE;
		public static final AttributeModifierBuilder SWORD_ATTACK_SPEED;
		static {
			ArmorItem armor = (ArmorItem) Items.NETHERITE_CHESTPLATE;
			DiggerItem tool = (DiggerItem) Items.NETHERITE_PICKAXE;
			SwordItem sword = (SwordItem) Items.NETHERITE_SWORD;

			final Multimap<Attribute, AttributeModifier> poolArmor = armor
					.getDefaultAttributeModifiers(armor.getSlot());
			final Multimap<Attribute, AttributeModifier> poolTool = tool
					.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
			final Multimap<Attribute, AttributeModifier> poolSword = sword
					.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);

			ARMOR = new AttributeModifierBuilder(Attributes.ARMOR,
					poolArmor.get(Attributes.ARMOR).stream().findAny().get());
			ARMOR_TOUGHNESS = new AttributeModifierBuilder(Attributes.ARMOR_TOUGHNESS,
					poolArmor.get(Attributes.ARMOR_TOUGHNESS).stream().findAny().get());
			KNOCKBACK_RESISTANCE = new AttributeModifierBuilder(Attributes.KNOCKBACK_RESISTANCE,
					poolArmor.get(Attributes.KNOCKBACK_RESISTANCE).stream().findAny().get());
			TOOL_ATTACK_DAMAGE = new AttributeModifierBuilder(Attributes.ATTACK_DAMAGE,
					poolTool.get(Attributes.ATTACK_DAMAGE).stream().findAny().get());
			TOOL_ATTACK_SPEED = new AttributeModifierBuilder(Attributes.ATTACK_SPEED,
					poolTool.get(Attributes.ATTACK_SPEED).stream().findAny().get());
			SWORD_ATTACK_DAMAGE = new AttributeModifierBuilder(Attributes.ATTACK_DAMAGE,
					poolSword.get(Attributes.ATTACK_DAMAGE).stream().findAny().get());
			SWORD_ATTACK_SPEED = new AttributeModifierBuilder(Attributes.ATTACK_SPEED,
					poolSword.get(Attributes.ATTACK_SPEED).stream().findAny().get());
		}
		private AttributeModifier clone;
		private Attribute attribute;

		private AttributeModifierBuilder(Attribute attribute, AttributeModifier modifier) {
			this.clone = modifier;
			this.attribute = attribute;
			BUILDERS_INTERNAL.add(this);
		}

		public UUID getId() {
			return clone.getId();
		}

		public String getName() {
			return clone.getName();
		}

		public AttributeModifier build(double val, AttributeModifier.Operation op) {
			return new AttributeModifier(clone.getId(), clone.getName(), val, op);
		}

		public String getDescriptionId() {
			return attribute.getDescriptionId();
		}

		public AttributeData buildData(EquipmentSlot slot, double val, AttributeModifier.Operation op) {
			return new AttributeData(slot, build(val, op), attribute);
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
	// Caches are made to avoid the waiting time between requests to Mojang API
	private static final Map<String, Tuple<Long, CompoundTag>> SKIN_CACHE = new HashMap<>();
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
	 * @param block        the stack block
	 * @param count        the stack count
	 * @param name         the stack display name
	 * @param lore         the lore of the stack
	 * @param enchantments Array of {@link Tuple} of {@link Enchantment} with there
	 *                     level
	 * @return your stack
	 * @see #buildStack(Item, int, String, String[], Tuple[])
	 */
	public static ItemStack buildStack(Block block, int count, @Nullable String name, @Nullable String[] lore,
			@Nullable Tuple<Enchantment, Integer>[] enchantments) {
		return buildStack(block.asItem(), count, name, lore, enchantments);
	}

	/**
	 * Create a custom stack
	 * 
	 * @param item         the stack item
	 * @param count        the stack count
	 * @param name         the stack display name
	 * @param lore         the lore of the stack
	 * @param enchantments Array of {@link Tuple} of {@link Enchantment} with there
	 *                     level
	 * @return your stack
	 * @see #buildStack(Block, int, String, String[], Tuple[])
	 * @since 2.0
	 */
	public static ItemStack buildStack(Item item, int count, @Nullable String name, @Nullable String[] lore,
			@Nullable Tuple<Enchantment, Integer>[] enchantments) {
		ItemStack is = new ItemStack(item, count);
		if (name != null)
			is.setHoverName(new TextComponent(name));
		if (lore != null)
			setLore(is, lore);
		if (enchantments != null)
			setEnchantments(Arrays.asList(enchantments), is, is.getItem().equals(Items.ENCHANTED_BOOK));
		return is;
	}

	/**
	 * Check if the player have a clear slot in his hotbar
	 * 
	 * @param mc the game
	 * @return true if a slot is clear
	 * @since 2.0
	 */
	public static boolean canGive(Minecraft mc) {
		for (int i = 0; i < 9; i++) {
			ItemStack it = mc.player.getInventory().getItem(i);
			if (it == null || it.equals(Items.AIR))
				return true;
		}
		return false;
	}

	/**
	 * Get a list of tuples of Slot / Attribute of a stack
	 * 
	 * @param stack the stack to read
	 * @return the attributes
	 * @since 2.0
	 * @see #setAttributes(List, ItemStack)
	 */
	public static List<AttributeData> getAttributes(ItemStack stack) {
		List<AttributeData> l = new ArrayList<>();
		for (EquipmentSlot slot : EquipmentSlot.values())
			stack.getAttributeModifiers(slot)
					.forEach((attribute, modifier) -> l.add(new AttributeData(slot, modifier, attribute)));

		return l;
	}

	/**
	 * Get Leather armor integer color (10511680 if no color has been found)
	 * 
	 * @param stack the stack to read
	 * @return the color of the stack
	 */
	public static int getColor(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return (tag == null || !tag.contains(NBT_CHILD_DISPLAY)
				|| !tag.getCompound(NBT_CHILD_DISPLAY).contains("color")) ? 10511680
						: tag.getCompound(NBT_CHILD_DISPLAY).getInt("color");

	}

	/**
	 * Get a custom String tag at the nbt root of a stack or a default value
	 * 
	 * @param stack        the custom tag
	 * @param key          the key
	 * @param defaultValue the default value
	 * @return the value
	 * @since 2.0
	 * @see #setCustomTag(ItemStack, String, String)
	 */
	public static String getCustomTag(ItemStack stack, String key, String defaultValue) {
		CompoundTag tag = stack.getTag();
		if (tag == null)
			return defaultValue;
		return tag.contains(key) ? tag.getString(key) : defaultValue;
	}

	/**
	 * Get {@link Enchantment}s of an {@link ItemStack}
	 * 
	 * @param stack the stack to read
	 * @return the enchantments
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
	 * @param stack the stack to read
	 * @param book  if this stack is a book or not
	 * @return the enchantments
	 * @see #setEnchantments(List, ItemStack, boolean)
	 * @see #getEnchantments(ItemStack)
	 * @since 2.0
	 */
	@SuppressWarnings("deprecation")
	public static List<Tuple<Enchantment, Integer>> getEnchantments(ItemStack stack, boolean book) {
		LinkedTreeMap<Enchantment, Integer> map = new LinkedTreeMap<>(
				(e1, e2) -> e2.getDescriptionId().compareToIgnoreCase(e1.getDescriptionId()));
		Registry.ENCHANTMENT.forEach(e -> map.put(e, 0));
		String key = book ? NBT_CHILD_BOOK_ENCHANTMENTS : NBT_CHILD_ENCHANTMENTS;
		ListTag list = stack.getTag() != null && stack.getTag().contains(key) ? stack.getTag().getList(key, 10)
				: new ListTag();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag tag = list.getCompound(i);
			if (tag.contains("id")) {
				Enchantment ench = Registry.ENCHANTMENT.get(ResourceLocation.tryParse(tag.getString("id")));
				if (ench != null)
					map.put(ench, tag.contains("lvl") ? tag.getInt("lvl") : 0);
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
	 * @param explosion get an explosion from a tag, null for a random explosion
	 * @return the explosion
	 * 
	 * @since 2.1
	 */
	public static ExplosionInformation getExplosionInformation(@Nullable CompoundTag explosion) {
		return explosion == null ? new ExplosionInformation() : new ExplosionInformation(explosion);
	}

	/**
	 * Get an {@link ItemStack} from a give code (like after a /give player (code))
	 * 
	 * @param code the item code
	 * @return the item stack
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
	 * @param itemStack the stack
	 * @return the give code
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
	 * @param itemStack the stack
	 * @param showCount the count
	 * @return the give code
	 * 
	 * @since 2.0
	 * @see #getFromGiveCode(String)
	 */
	@SuppressWarnings("deprecation")
	public static String getGiveCode(ItemStack itemStack, boolean showCount) {
		boolean noTag = itemStack.getTag() != null && !itemStack.getTag().isEmpty();
		return itemStack == null ? ""
				: Registry.ITEM.getKey(itemStack.getItem()).toString() + (noTag ? itemStack.getTag().toString() : "")
						+ (itemStack.getCount() == 1 && showCount ? "" : " " + itemStack.getCount());
	}

	/**
	 * Change a head with new skin information given by name
	 * 
	 * @param is   the stack to fill
	 * @param name the head name
	 * @return the item stack
	 * 
	 * @since 2.0
	 * @see #getHead(String)
	 * @see #getHead(String, String, String)
	 * @see #getHead(ItemStack, String, String, String)
	 * @see #getHeads(String...)
	 * @throws IOException            if an error occured while asking the profile
	 * @throws CommandSyntaxException if an error occured while parsing the profile
	 * @throws NoSuchElementException if the profile can't be read with the answer
	 */
	public static ItemStack getHead(ItemStack is, String name)
			throws IOException, CommandSyntaxException, NoSuchElementException {
		CompoundTag skullOwner = is.getOrCreateTagElement("SkullOwner");
		String uuid = getUUIDByNames(name).stream().findFirst().get().b;
		skullOwner.merge(getSkinInformationFromUUID(uuid));
		skullOwner.putString("Name", name);
		skullOwner.putString("Id", addHyphen(uuid));
		return is;
	}

	/**
	 * Change a head with new skin information given by uuid, url and name
	 * 
	 * @param is   the stack to fill
	 * @param uuid the head uuid
	 * @param url  the head url
	 * @param name the head name
	 * @return the item stack
	 * 
	 * @since 2.0
	 * @see #getHead(String)
	 * @see #getHead(ItemStack, String)
	 * @see #getHead(ItemStack, String, String, String)
	 * @see #getHeads(String...)
	 */
	public static ItemStack getHead(ItemStack is, String uuid, String url, String name) {
		CompoundTag skullOwner = is.getOrCreateTagElement("SkullOwner");
		skullOwner.putString("Id", addHyphen(uuid.replaceAll("-", "")));
		ListTag textures = new ListTag();
		CompoundTag texture = new CompoundTag();
		texture.putString("Value",
				Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes()));
		textures.add(texture);
		getOrCreateSubCompound(skullOwner, "Properties").put("textures", textures);
		if (name != null)
			skullOwner.putString("Name", name);
		else if (skullOwner.contains("Name"))
			skullOwner.remove("Name");
		return is;
	}

	/**
	 * Create a head with skin information given by name
	 * 
	 * @param name the head name
	 * @return the item stack
	 * 
	 * @since 2.0
	 * @see #getHead(ItemStack, String)
	 * @see #getHead(String, String, String)
	 * @see #getHead(ItemStack, String, String, String)
	 * @see #getHeads(String...)
	 * @throws IOException            if an error occured while asking the profile
	 * @throws CommandSyntaxException if an error occured while parsing the profile
	 * @throws NoSuchElementException if the profile can't be read with the answer
	 */
	public static ItemStack getHead(String name) throws IOException, CommandSyntaxException, NoSuchElementException {
		return getHead(new ItemStack(Items.PLAYER_HEAD, 1), name);
	}

	/**
	 * Create a head with skin information given by uuid, url and name
	 * 
	 * @param uuid the head uuid
	 * @param url  the head url
	 * @param name the head name
	 * @return the item stack
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
	 * @param players the player to get the head
	 * @return the item heads
	 * 
	 * @since 2.0
	 * @see #getHead(String)
	 * @see #getHead(ItemStack, String)
	 * @see #getHead(String, String, String)
	 * @see #getHead(ItemStack, String, String, String)
	 * @throws IOException            if an error occured while asking the profile
	 * @throws CommandSyntaxException if an error occured while parsing the profile
	 * @throws NoSuchElementException if the profile can't be read with the answer
	 */
	public static List<ItemStack> getHeads(Collection<Player> players)
			throws IOException, CommandSyntaxException, NoSuchElementException {
		return getHeads(players.stream().map(Player::getScoreboardName).toArray(String[]::new));
	}

	/**
	 * Create a heads with skin information given by names
	 * 
	 * @param names the name of the players
	 * @return the item heads
	 * 
	 * @since 2.0
	 * @see #getHead(String)
	 * @see #getHead(ItemStack, String)
	 * @see #getHead(String, String, String)
	 * @see #getHead(ItemStack, String, String, String)
	 * @throws IOException            if an error occured while asking the profile
	 * @throws CommandSyntaxException if an error occured while parsing the profile
	 * @throws NoSuchElementException if the profile can't be read with the answer
	 */
	public static List<ItemStack> getHeads(String... names)
			throws IOException, CommandSyntaxException, NoSuchElementException {
		List<ItemStack> stacks = new ArrayList<>();
		getUUIDByNames(names).stream().forEach(tuple -> {
			try {
				ItemStack stack = new ItemStack(Items.PLAYER_HEAD, 1);
				CompoundTag skullOwner = stack.getOrCreateTagElement("SkullOwner");
				String uuid = tuple.b;
				skullOwner.merge(getSkinInformationFromUUID(uuid));
				skullOwner.putString("Name", tuple.a);
				skullOwner.putString("Id", addHyphen(uuid));
				stacks.add(stack);
			} catch (Exception e) {
			}
		});
		return stacks;
	}

	/**
	 * Get lore(description) of an {@link ItemStack}
	 * 
	 * @param stack the stack to read
	 * @return the lore
	 * 
	 * @since 2.0
	 * @see #setLore(ItemStack, String[])
	 */
	public static String[] getLore(ItemStack stack) {
		CompoundTag display = stack.getOrCreateTagElement(NBT_CHILD_DISPLAY);
		ListTag nbtTagList = display.getList("Lore", 8);
		String[] array = new String[nbtTagList.size()];
		for (int j = 0; j < nbtTagList.size(); ++j) {
			String s = nbtTagList.getString(j);
			array[j] = "";
			try {
				MutableComponent MutableComponent1 = Component.Serializer.fromJson(s);
				if (MutableComponent1 != null) {
					array[j] = MutableComponent1.getString();
				}
			} catch (JsonParseException jsonparseexception) {
			}
		}
		return array;
	}

	private static CompoundTag getOrCreateSubCompound(CompoundTag compound, String key) {
		if (compound.contains(key, 10))
			return compound.getCompound(key);
		CompoundTag nbttagcompound = new CompoundTag();
		compound.put(key, nbttagcompound);
		return nbttagcompound;
	}

	/**
	 * Get {@link PotionInformation} of stack
	 * 
	 * @param stack your potion
	 * @return potion information
	 * @see #setPotionInformation(ItemStack, PotionInformation)
	 * @since 2.0
	 */
	public static PotionInformation getPotionInformation(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		List<MobEffectInstance> customEffects = PotionUtils.getCustomEffects(tag);
		return new PotionInformation(
				(tag != null && tag.contains("CustomPotionColor") ? OptionalInt.of(tag.getInt("CustomPotionColor"))
						: OptionalInt.empty()),
				PotionUtils.getPotion(tag), customEffects);
	}

	/**
	 * Get a random legitimate fireworks
	 * 
	 * @return your fireworks
	 * @since 2.1
	 */
	public static ItemStack getRandomFireworks() {
		CompoundTag fwt = new CompoundTag();
		int flight = RANDOM.nextInt(3); // a number between 0 and 2 (number of additional gunpowder)
		fwt.putInt("Flight", flight + 1);
		ListTag explosions = new ListTag();
		int exp = RANDOM.nextInt(7 - flight) + 1; // a number between 1 and 7 - flight (number of additional
													// gunpowder) -> (number of explosion)
		for (int i = 0; i < exp; i++)
			explosions.add(getExplosionInformation(null).getTag());
		fwt.put(NBT_CHILD_EXPLOSIONS, explosions);
		ItemStack fw = new ItemStack(Items.FIREWORK_ROCKET);
		CompoundTag tag = new CompoundTag();
		tag.put(NBT_CHILD_FIREWORKS, fwt);
		fw.setTag(tag);
		// create a random name and description because "why not ?"
		List<EntityType<?>> list = ForgeRegistries.ENTITIES.getValues().stream().filter(EntityType::canSummon)
				.collect(Collectors.toList());
		fw.setHoverName(new TranslatableComponent(list.get(RANDOM.nextInt(list.size())).getDescriptionId())
				.withStyle(ChatFormatting.values()[RANDOM.nextInt(16)])
				.append(" " + CommandUtils.getRandomElement(RANDOM_CHAR) + RANDOM.nextInt(1000)));
		setLore(fw, new String[] { ChatFormatting.YELLOW + "" + ChatFormatting.ITALIC + I18n.get("cmd.act.rfw") });
		return fw;
	}

	/**
	 * Request {@link CompoundTag} of Skin Information with the Mojang API
	 * 
	 * @param uuid Player's UUID
	 * @return skin information
	 * @throws IOException            if an error occured while asking the profile
	 * @throws CommandSyntaxException if an error occured while parsing the profile
	 * @since 2.0
	 */
	public static CompoundTag getSkinInformationFromUUID(String uuid) throws IOException, CommandSyntaxException {
		// check if a skin is find in the cache (1min : the minimum time between
		// request)
		if (SKIN_CACHE.containsKey(uuid) && SKIN_CACHE.get(uuid).a + 60000 > System.currentTimeMillis())
			return SKIN_CACHE.get(uuid).b.copy();
		CompoundTag requestCompound = TagParser.parseTag(
				sendRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replaceAll("-", ""),
						null, null, null));
		CompoundTag newTag = new CompoundTag();
		if (requestCompound.contains("properties", 9)) {
			ListTag properties = requestCompound.getList("properties", 10);
			ListTag textures = new ListTag();
			properties.forEach(base -> {
				CompoundTag tex = (CompoundTag) base;
				CompoundTag newTex = new CompoundTag();
				if (tex.contains("value", 8))
					newTex.putString("Value", tex.getString("value"));
				textures.add(newTex);
			});
			newTag.put("Properties", new CompoundTag());
			newTag.putString("Id", addHyphen(uuid.replaceAll("-", "")));
			newTag.getCompound("Properties").put("textures", textures);
		}
		SKIN_CACHE.put(uuid, new Tuple<Long, CompoundTag>(System.currentTimeMillis(), newTag.copy()));
		return newTag;
	}

	/**
	 * Request {@link Tuple} of name / UUID of users by there name with the Mojang
	 * API
	 * 
	 * @param names Players' names
	 * @return List of name find
	 * @throws IOException            if an error occured while downloading the uuid
	 * @throws CommandSyntaxException if an error occured while parsing the uuid
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
			CompoundTag tag = TagParser.parseTag("{data:" + sendRequest("https://api.mojang.com/profiles/minecraft",
					"POST", "application/json", "[" + query + "]") + "}");
			if (tag.contains("data", 9))
				tag.getList("data", 10).forEach(base -> {
					CompoundTag data = (CompoundTag) base;
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
	 * @param stack the stack
	 * @since 2.0
	 * @see #give(ItemStack, int)
	 */
	public static void give(ItemStack stack) {
		give(Minecraft.getInstance(), stack);
	}

	/**
	 * Try to give an item in a slot
	 * 
	 * @param stack the stack
	 * @param slot  the slot to fill
	 * @since 2.1
	 * @see #give(ItemStack)
	 */
	public static void give(ItemStack stack, int slot) {
		give(Minecraft.getInstance(), stack, slot);
	}

	/**
	 * Try to give an item in the hotbar
	 * 
	 * @param stacks the stacks to give
	 * @since 2.0
	 * @see #give(ItemStack, int)
	 */
	public static void give(List<ItemStack> stacks) {
		give(Minecraft.getInstance(), stacks);
	}

	/**
	 * Try to give an item in the hotbar
	 * 
	 * @param mc    the game
	 * @param stack the stack to give
	 * 
	 * @since 2.0
	 * @see #give(ItemStack)
	 * @deprecated use {@link #give(ItemStack)} instead
	 */
	@Deprecated
	public static void give(Minecraft mc, ItemStack stack) {
		if (mc.player != null && mc.player.isCreative()) {
			if (stack != null) {
				for (int i = 0; i < 9; i++) {
					if (mc.player.getInventory().items.get(i).isEmpty()) {
						give(mc, stack, 36 + i);
						ChatUtils.itemStack(stack);
						return;
					}
				}
			}
			ChatUtils.error(I18n.get("gui.act.give.fail"));
		} else
			ChatUtils.error(I18n.get("gui.act.nocreative"));
	}

	/**
	 * Try to give an item in a slot
	 * 
	 * @param mc    the game
	 * @param stack the stack to give
	 * @param slot  the slot to fill
	 * @since 2.0
	 * @see #give(ItemStack, int)
	 * @deprecated use {@link #give(ItemStack, int)} instead
	 */
	@Deprecated
	public static void give(Minecraft mc, ItemStack stack, int slot) {
		if (mc.player.isCreative()) {
			mc.gameMode.handleCreativeModeItemAdd(stack, slot);
		} else
			ChatUtils.error(I18n.get("gui.act.nocreative"));
	}

	/**
	 * Try to give an item in the hotbar
	 * 
	 * @param mc     the game
	 * @param stacks the stacks to give
	 * @since 2.0
	 * @see #give(ItemStack)
	 * @deprecated use {@link #give(List)} instead
	 */
	@Deprecated
	public static void give(Minecraft mc, List<ItemStack> stacks) {
		if (mc.player != null && mc.player.isCreative()) {
			int i = 0, j = 0;
			ItemStack is;
			stacks: for (; j < stacks.size(); j++) {
				is = stacks.get(j);
				for (; i < 9; i++) {
					if (mc.player.getInventory().items.get(i).isEmpty()) {
						give(mc, is, 36 + i);
						ChatUtils.itemStack(is);
						i++;
						continue stacks;
					}
				}
				ChatUtils.error(I18n.get("gui.act.give.fail"));
				return;
			}
		} else
			ChatUtils.error(I18n.get("gui.act.nocreative"));
	}

	/**
	 * Check if a {@link ItemStack} is unbreakable
	 * 
	 * @param stack your stack
	 * @return if the stack is unbreakable
	 * @see #setUnbreakable(ItemStack, boolean)
	 * @since 2.0
	 */
	public static boolean isUnbreakable(ItemStack stack) {
		CompoundTag tag = stack.getTag();
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
	 * @param attributes the attributes to set
	 * @param stack      the stack to update
	 * @return the stack
	 * 
	 * @since 2.0
	 * @see #getAttributes(ItemStack)
	 */
	public static ItemStack setAttributes(List<AttributeData> attributes, ItemStack stack) {
		attributes.forEach(data -> stack.addAttributeModifier(data.attribute, data.modifier, data.slot));
		return stack;
	}

	/**
	 * base color of the leather armor
	 */
	public static final int LEATHER_ARMOR_BASE_COLOR = 10511680;

	/**
	 * Change the color of a leather armor
	 * 
	 * @param stack leather armor
	 * @param color integer color
	 * @return your stack
	 * @since 2.0
	 */
	public static ItemStack setColor(ItemStack stack, int color) {
		if (stack.getTag() == null)
			stack.setTag(new CompoundTag());
		CompoundTag display = stack.getOrCreateTagElement(NBT_CHILD_DISPLAY);
		if (color != LEATHER_ARMOR_BASE_COLOR)
			display.putInt("color", color);
		else if (display.contains("color"))
			display.remove("color");
		return stack;
	}

	/**
	 * @param stack the stack
	 * @return if the stack is a potion item
	 */
	public static boolean isPotionItem(ItemStack stack) {
		return stack.getItem().equals(Items.POTION) || stack.getItem().equals(Items.SPLASH_POTION)
				|| stack.getItem().equals(Items.LINGERING_POTION) || stack.getItem().equals(Items.TIPPED_ARROW);
	}

	/**
	 * @param stack the stack
	 * @return if the stack is a leather armor
	 */
	public static boolean isLeatherArmor(ItemStack stack) {
		return stack.getItem()instanceof ArmorItem ai && ai.getMaterial() == ArmorMaterials.LEATHER;
	}

	/**
	 * @param stack the stack
	 * @return if you can change the color of the stack
	 */
	public static boolean canGlobalColorIt(ItemStack stack) {
		return isPotionItem(stack) || isLeatherArmor(stack);
	}

	/**
	 * remove the color (if any) of the stack
	 * 
	 * @param stack the stack
	 * @return the stack
	 */
	public static ItemStack removeColor(ItemStack stack) {
		if (isPotionItem(stack)) {
			var tag = stack.getTag();
			if (tag != null && tag.contains("CustomPotionColor")) {
				tag.remove("CustomPotionColor");
				stack.setTag(tag);
			}
		} else if (isLeatherArmor(stack)) {
			setColor(stack, LEATHER_ARMOR_BASE_COLOR);
		}
		return stack;
	}

	/**
	 * set the color (if possible) of the stack
	 * 
	 * @param stack the stack
	 * @param color the color to set
	 * @return the stack
	 */
	public static ItemStack setGlobalColor(ItemStack stack, int color) {
		if (isPotionItem(stack)) {
			var tag = stack.getOrCreateTag();
			tag.putInt("CustomPotionColor", color);
		} else if (isLeatherArmor(stack)) {
			setColor(stack, color);
		}

		return stack;
	}

	/**
	 * @param stack the stack
	 * @return the default color of the stack
	 */
	public static OptionalInt getDefaultGlobalColor(ItemStack stack) {
		if (isLeatherArmor(stack))
			return OptionalInt.of(LEATHER_ARMOR_BASE_COLOR);
		return OptionalInt.empty();
	}

	/**
	 * @param stack the stack
	 * @return the color of the stack
	 */
	public static OptionalInt getGlobalColor(ItemStack stack) {
		if (isPotionItem(stack)) {
			var tag = stack.getTag();
			if (tag != null && tag.contains("CustomPotionColor"))
				return OptionalInt.of(tag.getInt("CustomPotionColor"));
		} else if (isLeatherArmor(stack)) {
			var tag = stack.getTag();
			if (tag != null && tag.contains(NBT_CHILD_DISPLAY)) {
				var display = tag.getCompound(NBT_CHILD_DISPLAY);
				if (display.contains("color"))
					return OptionalInt.of(display.getInt("color"));
			}
		}

		return OptionalInt.empty();
	}

	/**
	 * Set or create a custom String tag at the nbt root of a stack
	 * 
	 * @param stack the stack to set
	 * @param key   the key
	 * @param value the value to set
	 * @return the stack
	 * 
	 * @since 2.0
	 * @see #getCustomTag(ItemStack, String, String)
	 */
	public static ItemStack setCustomTag(ItemStack stack, String key, String value) {
		CompoundTag tag = stack.getTag();
		if (tag == null)
			stack.setTag(tag = new CompoundTag());
		tag.putString(key, value);
		return stack;
	}

	/**
	 * Set {@link Enchantment}s of an {@link ItemStack}
	 * 
	 * @param enchantments the enchantments to set
	 * @param stack        the stack to fill
	 * @return the stack
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
	 * @param enchantments the enchantments to set
	 * @param stack        the stack to fill
	 * @param book         if this stack is a book or not
	 * @return the stack
	 * @see #setEnchantments(List, ItemStack)
	 * @see #getEnchantments(ItemStack, boolean)
	 * @since 2.0
	 */
	public static ItemStack setEnchantments(List<Tuple<Enchantment, Integer>> enchantments, ItemStack stack,
			boolean book) {
		ListTag nbttaglist = new ListTag();
		enchantments.forEach(tuple -> {
			if (tuple.b == 0)
				return;
			CompoundTag nbttagcompound = new CompoundTag();
			nbttagcompound.putString("id", tuple.a.getRegistryName().toString());
			nbttagcompound.putInt("lvl", tuple.b);
			nbttaglist.add(nbttagcompound);
		});
		stack.getOrCreateTag().put(book ? NBT_CHILD_BOOK_ENCHANTMENTS : NBT_CHILD_ENCHANTMENTS, nbttaglist);
		return stack;
	}

	/**
	 * Create a new {@link ItemStack} with the same count, metadata and
	 * {@link CompoundTag} than the given stack but with a new {@link Item} type
	 * 
	 * @param item  New {@link Item} type
	 * @param stack Your stack to copy
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
	 * @param stack Set stack to set
	 * @param lore  the lore to set
	 * @return Your new stack
	 * @since 2.0
	 * @see #getLore(ItemStack)
	 */
	public static ItemStack setLore(ItemStack stack, String[] lore) {
		ListTag nbtTagList = new ListTag();
		for (String l : lore)
			nbtTagList.add(StringTag.valueOf(Component.Serializer.toJson(new TextComponent(l))));
		CompoundTag display = stack.getOrCreateTagElement(NBT_CHILD_DISPLAY);
		display.put("Lore", nbtTagList);
		return stack;
	}

	/**
	 * Set {@link PotionInformation} to a {@link ItemStack}
	 * 
	 * @param stack your stack to change
	 * @param info  {@link PotionInformation} to set
	 * @return your stack
	 * @see #getPotionInformation(ItemStack)
	 * @since 2.0
	 */
	public static ItemStack setPotionInformation(ItemStack stack, PotionInformation info) {
		ListTag nbttaglist = new ListTag();
		info.getCustomEffects().forEach(effect -> {
			nbttaglist.add(effect.save(new CompoundTag()));
		});
		CompoundTag tag = stack.getOrCreateTag();
		tag.putString("Potion", info.getMain().getRegistryName().toString());
		if (info.customColor.isPresent())
			tag.putInt("CustomPotionColor", info.customColor.getAsInt());
		else if (tag.contains("CustomPotionColor"))
			tag.remove("CustomPotionColor");
		tag.put("CustomPotionEffects", nbttaglist);
		return stack;
	}

	/**
	 * Set a {@link ItemStack} Unbreakable
	 * 
	 * @param stack Your stack
	 * @param value Unbreakable or not
	 * @return Your stack
	 * @see #isUnbreakable(ItemStack)
	 * @since 2.0
	 */
	public static ItemStack setUnbreakable(ItemStack stack, boolean value) {
		if (value) {
			stack.getOrCreateTag().putBoolean("Unbreakable", true);
		} else {
			CompoundTag tag = stack.getTag();
			if (tag != null)
				tag.remove("Unbreakable");
		}
		return stack;
	}
}
