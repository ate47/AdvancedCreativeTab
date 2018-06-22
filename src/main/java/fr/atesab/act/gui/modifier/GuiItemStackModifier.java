package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.gui.selector.GuiTypeListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class GuiItemStackModifier extends GuiModifier<ItemStack> {
	static class GuiMetaModifier extends GuiModifier<ItemStack> {
		private ItemStack stack;
		private GuiButton unbreak;

		public GuiMetaModifier(GuiScreen parent, Consumer<ItemStack> setter, ItemStack stack) {
			super(parent, setter);
			this.stack = stack;
		}

		@Override
		protected void actionPerformed(GuiButton button) throws IOException {
			switch (button.id) {
			case 0: {
				set(stack);
				mc.displayGuiScreen(parent);
			}
				break;
			case 1: {
				ItemUtils.setUnbreakable(stack, !ItemUtils.isUnbreakable(stack));
			}
				break;
			}
			super.actionPerformed(button);
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			drawDefaultBackground();
			GuiUtils.drawItemStack(itemRender, partialTicks, this, stack, width / 2 - 10, height / 2 - 21);
			super.drawScreen(mouseX, mouseY, partialTicks);
			GlStateManager.color(1, 1, 1);
			if (GuiUtils.isHover(width / 2 - 10, height / 2 - 21, 20, 20, mouseX, mouseY))
				renderToolTip(stack, mouseX, mouseY);
		}

		@Override
		public void initGui() {
			int i = 0;
			buttonList.add(unbreak = new GuiButton(1, width / 2 - 100, height / 2 - 21 + 21 * (i += 1),
					I18n.format("item.unbreakable").replaceAll(ChatUtils.MODIFIER + "[a-fA-F0-9rRk-oK-O]", "")));
			buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 - 17 + 21 * (i += 1), I18n.format("gui.done")));
			super.initGui();
		}

		@Override
		public void updateScreen() {
			unbreak.packedFGColour = ItemUtils.isUnbreakable(stack) ? Color.GREEN.getRGB() : Color.RED.getRGB();
			super.updateScreen();
		}
	}

	private ItemStack currentItemStack;

	public GuiItemStackModifier(GuiScreen parent, ItemStack currentItemStack, Consumer<ItemStack> setter) {
		super(parent, setter);
		this.currentItemStack = currentItemStack != null ? currentItemStack : new ItemStack(Blocks.STONE);
		if (this.currentItemStack.getTagCompound() == null)
			this.currentItemStack.setTagCompound(new NBTTagCompound());
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) // cancel
			mc.displayGuiScreen(parent);
		else if (button.id == 1) { // done
			set(currentItemStack);
			mc.displayGuiScreen(parent);
		} else if (button.id == 2) { // name
			NBTTagCompound display = currentItemStack.getOrCreateSubCompound("display");
			mc.displayGuiScreen(new GuiStringModifier(this,
					display.hasKey("Name") ? display.getString("Name").replaceAll("" + ChatUtils.MODIFIER, "&") : "",
					name -> {
						if (name.isEmpty()) {
							if (display.hasKey("Name"))
								display.removeTag("Name");
						} else
							currentItemStack
									.setStackDisplayName(name.replaceAll("&", String.valueOf(ChatUtils.MODIFIER)));
					}));
		} else if (button.id == 3) // lore
			mc.displayGuiScreen(new GuiStringArrayModifier(this, ItemUtils.getLore(currentItemStack), value -> {
				for (int i = 0; i < value.length; i++)
					value[i] = value[i].replaceAll("&", String.valueOf(ChatUtils.MODIFIER));
				ItemUtils.setLore(currentItemStack, value);
			}));
		else if (button.id == 4) // ench
			mc.displayGuiScreen(new GuiEnchModifier(this, ItemUtils.getEnchantments(currentItemStack),
					list -> ItemUtils.setEnchantments(list, currentItemStack)));
		else if (button.id == 5) // attribute
			mc.displayGuiScreen(new GuiAttributeModifier(this, ItemUtils.getAttributes(currentItemStack),
					list -> ItemUtils.setAttributes(list, currentItemStack)));
		else if (button.id == 6) // type
			mc.displayGuiScreen(new GuiTypeListSelector(this, is -> {
				NBTTagCompound tag = (currentItemStack.getTagCompound() == null ? new NBTTagCompound()
						: currentItemStack.getTagCompound());
				tag.merge(is.getTagCompound() == null ? new NBTTagCompound() : is.getTagCompound());
				is.setTagCompound(tag);
				currentItemStack = is;
			}));
		else if (button.id == 7) // meta
			mc.displayGuiScreen(new GuiMetaModifier(this, is -> currentItemStack = is, currentItemStack));
		else if (button.id == 8) // dye
			mc.displayGuiScreen(new GuiColorModifier(this, i -> ItemUtils.setColor(currentItemStack, i),
					ItemUtils.getColor(currentItemStack)));
		else if (button.id == 9) // book
			mc.displayGuiScreen(new GuiEnchModifier(this, ItemUtils.getEnchantments(currentItemStack, true),
					list -> ItemUtils.setEnchantments(list, currentItemStack, true)));
		else if (button.id == 10) // potion
			mc.displayGuiScreen(new GuiPotionModifier(this, pi -> ItemUtils.setPotionInformation(currentItemStack, pi),
					ItemUtils.getPotionInformation(currentItemStack)));
		else if (button.id == 11) { // potion type
			NonNullList<ItemStack> potionType = NonNullList.create();
			potionType.add(new ItemStack(Items.POTIONITEM));
			potionType.add(new ItemStack(Items.SPLASH_POTION));
			potionType.add(new ItemStack(Items.LINGERING_POTION));
			potionType.add(new ItemStack(Items.TIPPED_ARROW));
			mc.displayGuiScreen(new GuiTypeListSelector(this, is -> {
				currentItemStack = ItemUtils.setItem(is.getItem(), currentItemStack);
			}, potionType));
		} else if (button.id == 12) // head
			mc.displayGuiScreen(new GuiHeadModifier(this, is -> currentItemStack = is, currentItemStack));
		else if (button.id == 13) // cb editor
			mc.displayGuiScreen(new GuiCommandBlockModifier(this, is -> currentItemStack = is, currentItemStack));
		else if (button.id == 14) { // egg editor
			List<Tuple<String, String>> entities = new ArrayList<>();
			entities.add(new Tuple<>(I18n.format("gui.act.none"), null));
			ForgeRegistries.ENTITIES.forEach(entityEntry -> {
				if (entityEntry.getEgg() != null)
					entities.add(new Tuple<String, String>(I18n.format("entity." + entityEntry.getName() + ".name"),
							entityEntry.getRegistryName().toString()));
			});
			mc.displayGuiScreen(new GuiButtonListSelector<String>(this, entities, s -> {
				if (s != null)
					currentItemStack.getOrCreateSubCompound("EntityTag").setString("id", s);
				else {
					NBTTagCompound tag = currentItemStack.getOrCreateSubCompound("EntityTag");
					if (tag.hasKey("id"))
						tag.removeTag("id");
				}
			}));
		} else if (button.id == 15) // firework
			mc.displayGuiScreen(new GuiFireworksModifer(this, tag -> {
				NBTTagCompound compound = currentItemStack.getTagCompound();
				if (compound == null)
					currentItemStack.setTagCompound(compound = new NBTTagCompound());
				compound.setTag("Fireworks", tag);
			}, currentItemStack.getOrCreateSubCompound("Fireworks")));
		else if (button.id == 16) // explosion
			mc.displayGuiScreen(new GuiFireworksModifer.GuiExplosionModifier(this, exp -> {
				NBTTagCompound compound = currentItemStack.getTagCompound();
				if (compound == null)
					currentItemStack.setTagCompound(compound = new NBTTagCompound());
				compound.setTag("Explosion", exp.getTag());
			}, ItemUtils.getExplosionInformation(currentItemStack.getOrCreateSubCompound("Explosion"))));
		; // TODO
		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (currentItemStack != null) {
			GuiUtils.drawItemStack(itemRender, partialTicks, this, currentItemStack, width / 2 - 10, height / 2 - 63);
			if (GuiUtils.isHover(width / 2 - 10, height / 2 - 63, 20, 20, mouseX, mouseY))
				renderToolTip(currentItemStack, mouseX, mouseY);
			GlStateManager.color(1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public void initGui() {

		buttonList
				.add(new GuiButton(2, width / 2 - 100, height / 2 - 42, 100, 20, I18n.format("gui.act.modifier.name")));
		buttonList.add(new GuiButton(3, width / 2 + 1, height / 2 - 42, 99, 20, I18n.format("gui.act.modifier.lore")));
		buttonList
				.add(new GuiButton(4, width / 2 - 100, height / 2 - 21, 100, 20, I18n.format("gui.act.modifier.ench")));
		buttonList.add(new GuiButton(5, width / 2 + 1, height / 2 - 21, 99, 20, I18n.format("gui.act.modifier.attr")));
		buttonList.add(new GuiButton(6, width / 2 - 100, height / 2, 100, 20, I18n.format("gui.act.modifier.type")));
		buttonList.add(new GuiButton(7, width / 2 + 1, height / 2, 99, 20, I18n.format("gui.act.modifier.meta")));
		int i = 1;
		if (currentItemStack.getItem() instanceof ItemArmor
				&& ((ItemArmor) currentItemStack.getItem()).getArmorMaterial() == ArmorMaterial.LEATHER)
			buttonList.add(
					new GuiButton(8, width / 2 - 100, height / 2 + 21, I18n.format("gui.act.modifier.meta.setColor")));
		else if (currentItemStack.getItem().equals(Items.ENCHANTED_BOOK))
			buttonList.add(new GuiButton(9, width / 2 - 100, height / 2 + 21,
					I18n.format("gui.act.modifier.ench") + " (" + I18n.format("item.book.name") + ")"));
		else if (currentItemStack.getItem().equals(Items.POTIONITEM)
				|| currentItemStack.getItem().equals(Items.SPLASH_POTION)
				|| currentItemStack.getItem().equals(Items.LINGERING_POTION)
				|| currentItemStack.getItem().equals(Items.TIPPED_ARROW)) {
			buttonList.add(new GuiButton(10, width / 2 - 100, height / 2 + 21, 100, 20,
					I18n.format("gui.act.modifier.meta.potion")));
			buttonList.add(new GuiButton(11, width / 2 + 1, height / 2 + 21, 99, 20,
					I18n.format("gui.act.modifier.meta.potionType")));
		} else if (currentItemStack.getItem().equals(Items.SKULL) && currentItemStack.getItemDamage() == 3)
			buttonList.add(new GuiButton(12, width / 2 - 100, height / 2 + 21, I18n.format("item.skull.char.name")));
		else if (currentItemStack.getItem().equals(Items.COMMAND_BLOCK_MINECART)
				|| currentItemStack.getItem().equals(Item.getItemFromBlock(Blocks.COMMAND_BLOCK))
				|| currentItemStack.getItem().equals(Item.getItemFromBlock(Blocks.CHAIN_COMMAND_BLOCK))
				|| currentItemStack.getItem().equals(Item.getItemFromBlock(Blocks.REPEATING_COMMAND_BLOCK))) {
			buttonList.add(
					new GuiButton(13, width / 2 - 100, height / 2 + 21, I18n.format("gui.act.modifier.meta.command")));
		} else if (currentItemStack.getItem().equals(Items.SPAWN_EGG))
			buttonList.add(new GuiButton(14, width / 2 - 100, height / 2 + 21,
					I18n.format("gui.act.modifier.meta.setEntity")));
		else if (currentItemStack.getItem().equals(Items.FIREWORKS))
			buttonList.add(new GuiButton(15, width / 2 - 100, height / 2 + 21,
					I18n.format("gui.act.modifier.meta.fireworks")));
		else if (currentItemStack.getItem().equals(Items.FIREWORK_CHARGE))
			buttonList.add(new GuiButton(16, width / 2 - 100, height / 2 + 21,
					I18n.format("gui.act.modifier.meta.explosion")));
		else
			i = 0;
		buttonList
				.add(new GuiButton(0, width / 2 + 1, height / 2 + 25 + 21 * i, 99, 20, I18n.format("gui.act.cancel")));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 + 25 + 21 * i, 100, 20, I18n.format("gui.done")));
		super.initGui();
	}
}
