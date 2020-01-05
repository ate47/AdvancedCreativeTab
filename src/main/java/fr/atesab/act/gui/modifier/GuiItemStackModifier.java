package fr.atesab.act.gui.modifier;

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
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;

public class GuiItemStackModifier extends GuiModifier<ItemStack> {
	private ItemStack currentItemStack;

	public GuiItemStackModifier(GuiScreen parent, ItemStack currentItemStack, Consumer<ItemStack> setter) {
		super(parent, setter);
		this.currentItemStack = currentItemStack != null ? currentItemStack : new ItemStack(Blocks.STONE);
		if (this.currentItemStack.getTag() == null)
			this.currentItemStack.setTag(new NBTTagCompound());
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.render(mouseX, mouseY, partialTicks);
		if (currentItemStack != null) {
			GuiUtils.drawItemStack(itemRender, partialTicks, this, currentItemStack, width / 2 - 10, height / 2 - 63);
			if (GuiUtils.isHover(width / 2 - 10, height / 2 - 63, 20, 20, mouseX, mouseY))
				renderToolTip(currentItemStack, mouseX, mouseY);
			GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initGui() {

		addButton(new GuiButton(2, width / 2 - 100, height / 2 - 42, 100, 20, I18n.format("gui.act.modifier.name")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new GuiStringModifier(GuiItemStackModifier.this,
						currentItemStack.getDisplayName().getFormattedText().replaceAll("" + ChatUtils.MODIFIER, "&"),
						name -> {
							currentItemStack.setDisplayName(name.isEmpty() ? null
									: new TextComponentString(
											name.replaceAll("&", String.valueOf(ChatUtils.MODIFIER))));
						}));
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(3, width / 2 + 1, height / 2 - 42, 99, 20, I18n.format("gui.act.modifier.lore")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new GuiStringArrayModifier(GuiItemStackModifier.this,
						ItemUtils.getLore(currentItemStack), value -> {
							for (int i = 0; i < value.length; i++)
								value[i] = value[i].replaceAll("&", String.valueOf(ChatUtils.MODIFIER));
							ItemUtils.setLore(currentItemStack, value);
						}));
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(4, width / 2 - 100, height / 2 - 21, 100, 20, I18n.format("gui.act.modifier.ench")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(
						new GuiEnchModifier(GuiItemStackModifier.this, ItemUtils.getEnchantments(currentItemStack),
								list -> ItemUtils.setEnchantments(list, currentItemStack)));
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(5, width / 2 + 1, height / 2 - 21, 99, 20, I18n.format("gui.act.modifier.attr")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(
						new GuiAttributeModifier(GuiItemStackModifier.this, ItemUtils.getAttributes(currentItemStack),
								list -> ItemUtils.setAttributes(list, currentItemStack)));
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(6, width / 2 - 100, height / 2, 100, 20, I18n.format("gui.act.modifier.type")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new GuiTypeListSelector(GuiItemStackModifier.this, is -> {
					NBTTagCompound tag = (currentItemStack.getTag() == null ? new NBTTagCompound()
							: currentItemStack.getTag());
					tag.merge(is.getTag() == null ? new NBTTagCompound() : is.getTag());
					is.setTag(tag);
					currentItemStack = is;
					return null;
				}));
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(7, width / 2 + 1, height / 2, 99, 20, I18n.format("gui.act.modifier.meta")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(
						new GuiMetaModifier(GuiItemStackModifier.this, is -> currentItemStack = is, currentItemStack));
				super.onClick(mouseX, mouseY);
			}
		});
		int i = 1;
		if (currentItemStack.getItem() instanceof ItemArmor
				&& ((ItemArmor) currentItemStack.getItem()).getArmorMaterial() == ArmorMaterial.LEATHER)
			addButton(
					new GuiButton(8, width / 2 - 100, height / 2 + 21, I18n.format("gui.act.modifier.meta.setColor")) {
						@Override
						public void onClick(double mouseX, double mouseY) {
							mc.displayGuiScreen(new GuiColorModifier(GuiItemStackModifier.this,
									i -> ItemUtils.setColor(currentItemStack, i),
									ItemUtils.getColor(currentItemStack)));
							super.onClick(mouseX, mouseY);
						}
					});
		else if (currentItemStack.getItem().equals(Items.ENCHANTED_BOOK))
			addButton(new GuiButton(9, width / 2 - 100, height / 2 + 21, I18n.format("gui.act.modifier.ench") + " ("
					+ I18n.format(Items.ENCHANTED_BOOK.getTranslationKey()) + ")") {
				@Override
				public void onClick(double mouseX, double mouseY) {
					mc.displayGuiScreen(new GuiEnchModifier(GuiItemStackModifier.this,
							ItemUtils.getEnchantments(currentItemStack, true),
							list -> ItemUtils.setEnchantments(list, currentItemStack, true)));
					super.onClick(mouseX, mouseY);
				}
			});
		else if (currentItemStack.getItem().equals(Items.POTION)
				|| currentItemStack.getItem().equals(Items.SPLASH_POTION)
				|| currentItemStack.getItem().equals(Items.LINGERING_POTION)
				|| currentItemStack.getItem().equals(Items.TIPPED_ARROW)) {
			addButton(new GuiButton(10, width / 2 - 100, height / 2 + 21, 100, 20,
					I18n.format("gui.act.modifier.meta.potion")) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					mc.displayGuiScreen(new GuiPotionModifier(GuiItemStackModifier.this,
							pi -> ItemUtils.setPotionInformation(currentItemStack, pi),
							ItemUtils.getPotionInformation(currentItemStack)));
					super.onClick(mouseX, mouseY);
				}
			});
			addButton(new GuiButton(11, width / 2 + 1, height / 2 + 21, 99, 20,
					I18n.format("gui.act.modifier.meta.potionType")) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					NonNullList<ItemStack> potionType = NonNullList.create();
					potionType.add(new ItemStack(Items.POTION));
					potionType.add(new ItemStack(Items.SPLASH_POTION));
					potionType.add(new ItemStack(Items.LINGERING_POTION));
					potionType.add(new ItemStack(Items.TIPPED_ARROW));
					mc.displayGuiScreen(new GuiTypeListSelector(GuiItemStackModifier.this, is -> {
						currentItemStack = ItemUtils.setItem(is.getItem(), currentItemStack);
						return null;
					}, potionType));
					super.onClick(mouseX, mouseY);
				}
			});
		} else if (currentItemStack.getItem().equals(Items.PLAYER_HEAD))
			addButton(new GuiButton(12, width / 2 - 100, height / 2 + 21,
					I18n.format(Items.PLAYER_HEAD.getTranslationKey())) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					mc.displayGuiScreen(new GuiHeadModifier(GuiItemStackModifier.this, is -> currentItemStack = is,
							currentItemStack));
					super.onClick(mouseX, mouseY);
				}
			});
		else if (currentItemStack.getItem().equals(Items.COMMAND_BLOCK_MINECART)
				|| currentItemStack.getItem().equals(Item.getItemFromBlock(Blocks.COMMAND_BLOCK))
				|| currentItemStack.getItem().equals(Item.getItemFromBlock(Blocks.CHAIN_COMMAND_BLOCK))
				|| currentItemStack.getItem().equals(Item.getItemFromBlock(Blocks.REPEATING_COMMAND_BLOCK))) {
			addButton(
					new GuiButton(13, width / 2 - 100, height / 2 + 21, I18n.format("gui.act.modifier.meta.command")) {
						@Override
						public void onClick(double mouseX, double mouseY) {
							mc.displayGuiScreen(new GuiCommandBlockModifier(GuiItemStackModifier.this,
									is -> currentItemStack = is, currentItemStack));
							super.onClick(mouseX, mouseY);
						}
					});
		} else if (currentItemStack.getItem() instanceof ItemSpawnEgg)
			addButton(new GuiButton(14, width / 2 - 100, height / 2 + 21,
					I18n.format("gui.act.modifier.meta.setEntity")) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					List<Tuple<String, ItemSpawnEgg>> eggs = new ArrayList<>();
					ItemSpawnEgg.getEggs().forEach(
							egg -> eggs.add(new Tuple<String, ItemSpawnEgg>(egg.getName().getFormattedText(), egg)));
					mc.displayGuiScreen(
							new GuiButtonListSelector<ItemSpawnEgg>(GuiItemStackModifier.this, eggs, egg -> {
								currentItemStack = ItemUtils.setItem(egg, currentItemStack);
								return null;
							}));
					super.onClick(mouseX, mouseY);
				}
			});
		else if (currentItemStack.getItem().equals(Items.FIREWORK_ROCKET))
			addButton(new GuiButton(15, width / 2 - 100, height / 2 + 21,
					I18n.format("gui.act.modifier.meta.fireworks")) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					mc.displayGuiScreen(new GuiFireworksModifer(GuiItemStackModifier.this, tag -> {
						NBTTagCompound compound = currentItemStack.getTag();
						if (compound == null)
							currentItemStack.setTag(compound = new NBTTagCompound());
						compound.setTag("Fireworks", tag);
					}, currentItemStack.getOrCreateChildTag("Fireworks")));
					super.onClick(mouseX, mouseY);
				}
			});
		else if (currentItemStack.getItem().equals(Items.FIREWORK_STAR))
			addButton(new GuiButton(16, width / 2 - 100, height / 2 + 21,
					I18n.format("gui.act.modifier.meta.explosion")) {
				@Override
				public void onClick(double mouseX, double mouseY) {
					mc.displayGuiScreen(new GuiFireworksModifer.GuiExplosionModifier(GuiItemStackModifier.this, exp -> {
						NBTTagCompound compound = currentItemStack.getTag();
						if (compound == null)
							currentItemStack.setTag(compound = new NBTTagCompound());
						compound.setTag("Explosion", exp.getTag());
					}, ItemUtils.getExplosionInformation(currentItemStack.getOrCreateChildTag("Explosion"))));
					super.onClick(mouseX, mouseY);
				}
			});
		else
			i = 0;
		addButton(new GuiButton(0, width / 2 + 1, height / 2 + 25 + 21 * i, 99, 20, I18n.format("gui.act.cancel")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(1, width / 2 - 100, height / 2 + 25 + 21 * i, 100, 20, I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				set(currentItemStack);
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		super.initGui();
	}
}
