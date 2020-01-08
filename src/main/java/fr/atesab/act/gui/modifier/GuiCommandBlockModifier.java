package fr.atesab.act.gui.modifier;

import java.awt.Color;
import java.util.function.Consumer;

import com.mojang.blaze3d.platform.GlStateManager;

import fr.atesab.act.gui.selector.GuiTypeListSelector;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class GuiCommandBlockModifier extends GuiModifier<ItemStack> {
	private ItemStack stack;
	private TextFieldWidget command, name;
	private Button auto;
	private boolean autoValue;

	public GuiCommandBlockModifier(Screen parent, Consumer<ItemStack> setter, ItemStack stack) {
		super(parent, "gui.act.modifier.meta.command", setter);
		this.stack = stack.copy();
	}

	private void setData() {
		CompoundNBT tag = stack.getOrCreateChildTag("BlockEntityTag");
		stack.setDisplayName(new StringTextComponent(name.getText().isEmpty() ? "@"
				: name.getText().replaceAll("&", "" + ChatUtils.MODIFIER) + TextFormatting.RESET));
		tag.putString("Command", command.getText().replaceAll("&", "" + ChatUtils.MODIFIER));
		tag.putByte("auto", (byte) (autoValue ? 1 : 0));
	}

	private void loadData() {
		CompoundNBT tag = stack.getOrCreateChildTag("BlockEntityTag");
		name.setText((stack.hasDisplayName() ? stack.getDisplayName().getFormattedText() : "@")
				.replaceAll("" + ChatUtils.MODIFIER, "&"));
		command.setText(
				(tag.contains("Command", 8) ? tag.getString("Command") : "").replaceAll("" + ChatUtils.MODIFIER, "&"));
		autoValue = tag.contains("auto", 99) && tag.getByte("auto") == (byte) 1;
	}

	@Override
	public void init() {
		int l = Math.max(font.getStringWidth(I18n.format("gui.act.modifier.meta.command.cmd") + " : "),
				font.getStringWidth(I18n.format("gui.act.modifier.meta.command.name") + " : ")) + 5;
		name = new TextFieldWidget(font, width / 2 - 148 + l, height / 2 - 19, 296 - l, 16, "");
		command = new TextFieldWidget(font, width / 2 - 148 + l, height / 2 + 2, 296 - l, 16, "");
		name.setMaxStringLength(Integer.MAX_VALUE);
		command.setMaxStringLength(Integer.MAX_VALUE);
		addButton(auto = new Button(width / 2 + 1, height / 2 + 21, 149, 20,
				I18n.format("advMode.mode.redstoneTriggered"), b -> {
					autoValue = !autoValue;
				}));
		addButton(new Button(width / 2 - 150, height / 2 + 21, 150, 20, I18n.format("gui.act.modifier.type"), b -> {
			setData();
			NonNullList<ItemStack> potionType = NonNullList.create();
			potionType.add(new ItemStack(Blocks.COMMAND_BLOCK));
			potionType.add(new ItemStack(Blocks.REPEATING_COMMAND_BLOCK));
			potionType.add(new ItemStack(Blocks.CHAIN_COMMAND_BLOCK));
			potionType.add(new ItemStack(Items.COMMAND_BLOCK_MINECART));
			getMinecraft().displayGuiScreen(
					new GuiTypeListSelector(GuiCommandBlockModifier.this, "gui.act.modifier.type", is -> {
						stack = ItemUtils.setItem(is.getItem(), stack);
						return null;
					}, potionType));
		}));
		addButton(new Button(width / 2 + 1, height / 2 + 42, 149, 20, I18n.format("gui.act.cancel"),
				b -> getMinecraft().displayGuiScreen(parent)));
		addButton(new Button(width / 2 - 150, height / 2 + 42, 150, 20, I18n.format("gui.done"), b -> {
			setData();
			set(stack);
			getMinecraft().displayGuiScreen(parent);
		}));
		loadData();
		super.init();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		GuiUtils.drawString(font, I18n.format("gui.act.modifier.meta.command.cmd") + " : ", width / 2 - 150, command.y,
				Color.WHITE.getRGB(), command.getHeight());
		GuiUtils.drawString(font, I18n.format("gui.act.modifier.meta.command.name") + " : ", width / 2 - 150, name.y,
				Color.WHITE.getRGB(), name.getHeight());
		command.render(mouseX, mouseY, partialTicks);
		name.render(mouseX, mouseY, partialTicks);
		super.render(mouseX, mouseY, partialTicks);
		GuiUtils.drawItemStack(itemRenderer, this, stack, width / 2 - 10, name.y - 20);
		if (GuiUtils.isHover(width / 2 - 10, name.y - 20, 20, 20, mouseX, mouseY))
			renderTooltip(stack, mouseX, mouseY);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
	}

	@Override
	public void tick() {
		command.tick();
		name.tick();
		auto.setFGColor(GuiUtils.getRedGreen(!autoValue));
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
