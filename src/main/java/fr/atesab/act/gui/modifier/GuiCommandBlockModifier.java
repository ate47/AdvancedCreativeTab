package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.function.Consumer;

import fr.atesab.act.gui.selector.GuiTypeListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class GuiCommandBlockModifier extends GuiModifier<ItemStack> {
	private ItemStack stack;
	private GuiTextField command, name;
	private GuiButton auto;
	private boolean autoValue;

	public GuiCommandBlockModifier(GuiScreen parent, Consumer<ItemStack> setter, ItemStack stack) {
		super(parent, setter);
		this.stack = stack.copy();
	}

	private void setData() {
		NBTTagCompound tag = stack.getOrCreateChildTag("BlockEntityTag");
		stack.setDisplayName(new TextComponentString(name.getText().isEmpty() ? "@"
				: name.getText().replaceAll("&", "" + ChatUtils.MODIFIER) + TextFormatting.RESET));
		tag.setString("Command", command.getText().replaceAll("&", "" + ChatUtils.MODIFIER));
		tag.setByte("auto", (byte) (autoValue ? 1 : 0));
	}

	private void loadData() {
		NBTTagCompound tag = stack.getOrCreateChildTag("BlockEntityTag");
		name.setText((stack.hasDisplayName() ? stack.getDisplayName().getFormattedText() : "@")
				.replaceAll("" + ChatUtils.MODIFIER, "&"));
		command.setText(
				(tag.contains("Command", 8) ? tag.getString("Command") : "").replaceAll("" + ChatUtils.MODIFIER, "&"));
		autoValue = tag.contains("auto", 99) && tag.getByte("auto") == (byte) 1;
	}

	@Override
	public void initGui() {
		int l = Math.max(fontRenderer.getStringWidth(I18n.format("gui.act.modifier.meta.command.cmd") + " : "),
				fontRenderer.getStringWidth(I18n.format("gui.act.modifier.meta.command.name") + " : ")) + 5;
		name = new GuiTextField(0, fontRenderer, width / 2 - 148 + l, height / 2 - 19, 296 - l, 16);
		command = new GuiTextField(0, fontRenderer, width / 2 - 148 + l, height / 2 + 2, 296 - l, 16);
		name.setMaxStringLength(Integer.MAX_VALUE);
		command.setMaxStringLength(Integer.MAX_VALUE);
		addButton(auto = new GuiButton(2, width / 2 + 1, height / 2 + 21, 149, 20,
				I18n.format("advMode.mode.redstoneTriggered")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				autoValue = !autoValue;
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(3, width / 2 - 150, height / 2 + 21, 150, 20, I18n.format("gui.act.modifier.type")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				setData();
				NonNullList<ItemStack> potionType = NonNullList.create();
				potionType.add(new ItemStack(Blocks.COMMAND_BLOCK));
				potionType.add(new ItemStack(Blocks.REPEATING_COMMAND_BLOCK));
				potionType.add(new ItemStack(Blocks.CHAIN_COMMAND_BLOCK));
				potionType.add(new ItemStack(Items.COMMAND_BLOCK_MINECART));
				mc.displayGuiScreen(new GuiTypeListSelector(GuiCommandBlockModifier.this, is -> {
					stack = ItemUtils.setItem(is.getItem(), stack);
					return null;
				}, potionType));
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(1, width / 2 + 1, height / 2 + 42, 149, 20, I18n.format("gui.act.cancel")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		addButton(new GuiButton(0, width / 2 - 150, height / 2 + 42, 150, 20, I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				setData();
				set(stack);
				mc.displayGuiScreen(parent);
				super.onClick(mouseX, mouseY);
			}
		});
		loadData();
		super.initGui();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GuiUtils.drawString(fontRenderer, I18n.format("gui.act.modifier.meta.command.cmd") + " : ", width / 2 - 150,
				command.y, Color.WHITE.getRGB(), command.height);
		GuiUtils.drawString(fontRenderer, I18n.format("gui.act.modifier.meta.command.name") + " : ", width / 2 - 150,
				name.y, Color.WHITE.getRGB(), name.height);
		command.drawTextField(mouseX, mouseY, partialTicks);
		name.drawTextField(mouseX, mouseY, partialTicks);
		super.render(mouseX, mouseY, partialTicks);
		GuiUtils.drawItemStack(itemRender, zLevel, this, stack, width / 2 - 10, name.y - 20);
		if (GuiUtils.isHover(width / 2 - 10, name.y - 20, 20, 20, mouseX, mouseY))
			renderToolTip(stack, mouseX, mouseY);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
	}

	@Override
	public void tick() {
		command.tick();
		name.tick();
		auto.packedFGColor = GuiUtils.getRedGreen(!autoValue);
		super.tick();
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		return command.charTyped(key, modifiers) || name.charTyped(key, modifiers) || super.charTyped(key, modifiers);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		command.keyPressed(key, scanCode, modifiers);
		name.keyPressed(key, scanCode, modifiers);
		return super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		command.mouseClicked(mouseX, mouseY, mouseButton);
		name.mouseClicked(mouseX, mouseY, mouseButton);
		if (GuiUtils.isHover(command, (int) mouseX, (int) mouseY) && mouseButton == 1)
			command.setText("");
		if (GuiUtils.isHover(name, (int) mouseX, (int) mouseY) && mouseButton == 1)
			name.setText("");
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

}
