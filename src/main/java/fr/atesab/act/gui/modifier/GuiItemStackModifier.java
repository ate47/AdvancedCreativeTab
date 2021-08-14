package fr.atesab.act.gui.modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.gui.selector.GuiTypeListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Blocks;

public class GuiItemStackModifier extends GuiModifier<ItemStack> {
	private ItemStack currentItemStack;

	public GuiItemStackModifier(Screen parent, ItemStack currentItemStack, Consumer<ItemStack> setter) {
		super(parent, new TranslatableComponent("gui.act.give.editor"), setter);
		this.currentItemStack = currentItemStack != null ? currentItemStack : new ItemStack(Blocks.STONE);
		if (this.currentItemStack.getTag() == null)
			this.currentItemStack.setTag(new CompoundTag());
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		if (currentItemStack != null) {
			GuiUtils.drawItemStack(itemRenderer, this, currentItemStack, width / 2 - 10, height / 2 - 63);
			if (GuiUtils.isHover(width / 2 - 10, height / 2 - 63, 20, 20, mouseX, mouseY))
				renderTooltip(matrixStack, currentItemStack, mouseX, mouseY);
		}
	}

	@Override
	public void init() {

		addRenderableWidget(new Button(width / 2 - 100, height / 2 - 42, 100, 20,
				new TranslatableComponent("gui.act.modifier.name"), b -> {
					getMinecraft().setScreen(new GuiStringModifier(GuiItemStackModifier.this,
							new TranslatableComponent("gui.act.modifier.name"),
							currentItemStack.getHoverName().getString().replaceAll("" + ChatUtils.MODIFIER, "&"),
							name -> {
								currentItemStack.setHoverName(name.isEmpty() ? null
										: new TextComponent(name.replaceAll("&", String.valueOf(ChatUtils.MODIFIER))));
							}));
				}));

		addRenderableWidget(new Button(width / 2 + 1, height / 2 - 42, 99, 20,
				new TranslatableComponent("gui.act.modifier.lore"), b -> {
					getMinecraft().setScreen(new GuiStringArrayModifier(GuiItemStackModifier.this,
							new TranslatableComponent("gui.act.modifier.lore"), ItemUtils.getLore(currentItemStack),
							value -> {
								for (int i = 0; i < value.length; i++)
									value[i] = value[i].replaceAll("&", String.valueOf(ChatUtils.MODIFIER));
								ItemUtils.setLore(currentItemStack, value);
							}));
				}));
		addRenderableWidget(new Button(width / 2 - 100, height / 2 - 21, 100, 20,
				new TranslatableComponent("gui.act.modifier.ench"), b -> {
					getMinecraft().setScreen(
							new GuiEnchModifier(GuiItemStackModifier.this, ItemUtils.getEnchantments(currentItemStack),
									list -> ItemUtils.setEnchantments(list, currentItemStack)));
				}));
		addRenderableWidget(new Button(width / 2 + 1, height / 2 - 21, 99, 20,
				new TranslatableComponent("gui.act.modifier.attr"), b -> {
					getMinecraft().setScreen(new GuiAttributeModifier(GuiItemStackModifier.this,
							ItemUtils.getAttributes(currentItemStack),
							list -> ItemUtils.setAttributes(list, currentItemStack)));
				}));
		addRenderableWidget(new Button(width / 2 - 100, height / 2, 100, 20,
				new TranslatableComponent("gui.act.modifier.type"), b -> {
					getMinecraft().setScreen(new GuiTypeListSelector(GuiItemStackModifier.this,
							new TranslatableComponent("gui.act.modifier.type"), is -> {
								CompoundTag tag = (currentItemStack.getTag() == null ? new CompoundTag()
										: currentItemStack.getTag());
								tag.merge(is.getTag() == null ? new CompoundTag() : is.getTag());
								is.setTag(tag);
								currentItemStack = is;
								return null;
							}));
				}));
		addRenderableWidget(
				new Button(width / 2 + 1, height / 2, 99, 20, new TranslatableComponent("gui.act.modifier.meta"), b -> {
					getMinecraft().setScreen(new GuiMetaModifier(GuiItemStackModifier.this, is -> currentItemStack = is,
							currentItemStack));
				}));
		int i = 1;
		if (currentItemStack.getItem() instanceof ArmorItem
				&& ((ArmorItem) currentItemStack.getItem()).getMaterial() == ArmorMaterials.LEATHER)
			addRenderableWidget(new Button(width / 2 - 100, height / 2 + 21, 200, 20,
					new TranslatableComponent("gui.act.modifier.meta.setColor"), b -> {
						getMinecraft().setScreen(new GuiColorModifier(GuiItemStackModifier.this,
								color -> ItemUtils.setColor(currentItemStack, color),
								ItemUtils.getColor(currentItemStack)));
					}));
		else if (currentItemStack.getItem().equals(Items.ENCHANTED_BOOK))
			addRenderableWidget(new Button(width / 2 - 100, height / 2 + 21, 200, 20,
					new TranslatableComponent("gui.act.modifier.ench").append(" (")
							.append(Items.ENCHANTED_BOOK.getDescription()).append(")"),
					b -> getMinecraft().setScreen(new GuiEnchModifier(GuiItemStackModifier.this,
							ItemUtils.getEnchantments(currentItemStack, true),
							list -> ItemUtils.setEnchantments(list, currentItemStack, true)))));
		else if (currentItemStack.getItem().equals(Items.POTION)
				|| currentItemStack.getItem().equals(Items.SPLASH_POTION)
				|| currentItemStack.getItem().equals(Items.LINGERING_POTION)
				|| currentItemStack.getItem().equals(Items.TIPPED_ARROW)) {
			addRenderableWidget(new Button(width / 2 - 100, height / 2 + 21, 100, 20,
					new TranslatableComponent("gui.act.modifier.meta.potion"), b -> {
						getMinecraft().setScreen(new GuiPotionModifier(GuiItemStackModifier.this,
								pi -> ItemUtils.setPotionInformation(currentItemStack, pi),
								ItemUtils.getPotionInformation(currentItemStack)));
					}));
			addRenderableWidget(new Button(width / 2 + 1, height / 2 + 21, 99, 20,
					new TranslatableComponent("gui.act.modifier.meta.potionType"), b -> {
						NonNullList<ItemStack> potionType = NonNullList.create();
						potionType.add(new ItemStack(Items.POTION));
						potionType.add(new ItemStack(Items.SPLASH_POTION));
						potionType.add(new ItemStack(Items.LINGERING_POTION));
						potionType.add(new ItemStack(Items.TIPPED_ARROW));
						getMinecraft().setScreen(new GuiTypeListSelector(GuiItemStackModifier.this,
								new TranslatableComponent("gui.act.modifier.meta.potionType"), is -> {
									currentItemStack = ItemUtils.setItem(is.getItem(), currentItemStack);
									return null;
								}, potionType));
					}));
		} else if (currentItemStack.getItem().equals(Items.PLAYER_HEAD))
			addRenderableWidget(
					new Button(width / 2 - 100, height / 2 + 21, 200, 20, Items.PLAYER_HEAD.getDescription(), b -> {
						getMinecraft().setScreen(new GuiHeadModifier(GuiItemStackModifier.this,
								is -> currentItemStack = is, currentItemStack));
					}));
		else if (currentItemStack.getItem().equals(Items.COMMAND_BLOCK_MINECART)
				|| currentItemStack.getItem().equals(Blocks.COMMAND_BLOCK.asItem())
				|| currentItemStack.getItem().equals(Blocks.CHAIN_COMMAND_BLOCK.asItem())
				|| currentItemStack.getItem().equals(Blocks.REPEATING_COMMAND_BLOCK.asItem())) {
			addRenderableWidget(new Button(width / 2 - 100, height / 2 + 21, 200, 20,
					new TranslatableComponent("gui.act.modifier.meta.command"), b -> {
						getMinecraft().setScreen(new GuiCommandBlockModifier(GuiItemStackModifier.this,
								is -> currentItemStack = is, currentItemStack));
					}));
		} else if (currentItemStack.getItem() instanceof SpawnEggItem)
			addRenderableWidget(new Button(width / 2 - 100, height / 2 + 21, 200, 20,
					new TranslatableComponent("gui.act.modifier.meta.setEntity"), b -> {
						List<Tuple<String, SpawnEggItem>> eggs = new ArrayList<>();
						SpawnEggItem.eggs()
								.forEach(egg -> eggs.add(new Tuple<>(egg.getDescription().getString(), egg)));
						getMinecraft().setScreen(new GuiButtonListSelector<SpawnEggItem>(GuiItemStackModifier.this,
								new TranslatableComponent("gui.act.modifier.meta.setEntity"), eggs, egg -> {
									currentItemStack = ItemUtils.setItem(egg, currentItemStack);
									return null;
								}));
					}));
		else if (currentItemStack.getItem().equals(Items.FIREWORK_ROCKET))
			addRenderableWidget(new Button(width / 2 - 100, height / 2 + 21, 200, 20,
					new TranslatableComponent("gui.act.modifier.meta.fireworks"), b -> {
						getMinecraft().setScreen(new GuiFireworksModifer(GuiItemStackModifier.this, tag -> {
							CompoundTag compound = currentItemStack.getTag();
							if (compound == null)
								currentItemStack.setTag(compound = new CompoundTag());
							compound.put("Fireworks", tag);
						}, currentItemStack.getOrCreateTagElement("Fireworks")));
					}));
		else if (currentItemStack.getItem().equals(Items.FIREWORK_STAR))
			addRenderableWidget(new Button(width / 2 - 100, height / 2 + 21, 200, 20,
					new TranslatableComponent("gui.act.modifier.meta.explosion"), b -> {
						getMinecraft().setScreen(new GuiFireworksModifer.GuiExplosionModifier(this, exp -> {
							CompoundTag compound = currentItemStack.getTag();
							if (compound == null)
								currentItemStack.setTag(compound = new CompoundTag());
							compound.put("Explosion", exp.getTag());
						}, ItemUtils.getExplosionInformation(currentItemStack.getOrCreateTagElement("Explosion"))));
					}));
		else
			i = 0;
		addRenderableWidget(new Button(width / 2 + 1, height / 2 + 25 + 21 * i, 99, 20,
				new TranslatableComponent("gui.act.cancel"), b -> getMinecraft().setScreen(parent)));
		addRenderableWidget(new Button(width / 2 - 100, height / 2 + 25 + 21 * i, 100, 20,
				new TranslatableComponent("gui.done"), b -> {
					set(currentItemStack);
					getMinecraft().setScreen(parent);
				}));
		super.init();
	}
}
