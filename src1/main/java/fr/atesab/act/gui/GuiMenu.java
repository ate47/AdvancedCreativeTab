package fr.atesab.act.gui;

import fr.atesab.act.superclass.Colors;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GuiUtils;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiMenu extends GuiScreen {
	private GuiScreen parent = null;
	private GuiButton buttonDone;
	private GuiButton buttonClipLoad;
	private GuiButton buttonSkull;
	private GuiButton buttonFactory;
	private GuiButton buttonFireWorksFactory;
	private GuiButton buttonPotionFactory;
	private GuiButton buttonItemFactory;
	private GuiButton buttonInHand;
	private GuiButton buttongm1;
	private GuiButton buttongm0;
	private int nextGuiId = 0;

	private Minecraft mc = Minecraft.getMinecraft();

	public GuiMenu(GuiScreen parent) {
		this.parent = parent;
	}

	public void actionPerformed(GuiButton button) {
		if (button == buttonDone)
			FMLClientHandler.instance().showGuiScreen(parent);
		if (button == buttonFactory)
			FMLClientHandler.instance().showGuiScreen(new ConfigGUI(this));
		if (button == buttonItemFactory)
			FMLClientHandler.instance().showGuiScreen(new GuiItemFactory(this));
		if (button == buttonPotionFactory)
			FMLClientHandler.instance().showGuiScreen(new GuiPotionFactory(this));
		if (button == buttonFireWorksFactory)
			FMLClientHandler.instance().showGuiScreen(new GuiFireWorkFactory(this));
		if (button == buttonSkull)
			FMLClientHandler.instance().showGuiScreen(new GuiSkullGiver(this));
		if (button == buttonInHand)
			FMLClientHandler.instance().showGuiScreen(new GuiNbtCode(this));
		if (button == buttongm1) {
			ChatUtils.show(I18n.format("gui.act.setGm", new Object[0]));
			fr.atesab.act.ActEventHandler.tryChangingGameMode = true;
			sendChatMessage("/gamemode 1", false);
		}
		if (button == buttongm0) {
			ChatUtils.show(I18n.format("gui.act.setGm", new Object[0]));
			fr.atesab.act.ActEventHandler.tryChangingGameMode = true;
			sendChatMessage("/gamemode 0", false);
		}
	}

	public void drawItemStack(ItemStack stack, int x, int y) {
		GlStateManager.translate(0.0F, 0.0F, 21.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
		FontRenderer font = null;
		if (stack != null)
			font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = fontRenderer;
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		fontRenderer.drawString("Advanced Creative Tab - 1.3",
				width / 2 - fontRenderer.getStringWidth("Advanced Creative Tab - 1.3") / 2,
				height / 2 - 63 - fontRenderer.FONT_HEIGHT - 1, Colors.RED);
		fontRenderer.drawString(I18n.format("gui.act.menu", new Object[0]),
				width / 2 - fontRenderer.getStringWidth(I18n.format("gui.act.menu", new Object[0])) / 2,
				height / 2 - 63, Colors.GOLD);
		drawItemStack(new ItemStack(Items.FIREWORKS), width / 2 + 116, height / 2 - 42);
		drawItemStack(new ItemStack(Items.ENCHANTED_BOOK), width / 2 + 116, height / 2 - 21);
		String inHandExist = I18n.format("gui.act.inhanditem", new Object[0]);
		if ((mc.player.inventory != null) && (mc.player.inventory.getCurrentItem() != null)) {
			drawItemStack(mc.player.inventory.getCurrentItem(), width / 2 + 116, height / 2);
		}
		GuiInventory.drawEntityOnScreen(width / 2 - 150, height / 2 + 21, 30, width / 2 - 150 - mouseX,
				height / 2 + 21 - mouseY, mc.player);

		super.drawScreen(mouseX, mouseY, partialTicks);
		if (GuiUtils.isHover(width / 2 - 115, height / 2 + 21, 231, 20, mouseX, mouseY)) {
			String[] strList = I18n.format("gui.act.setGm.hover", new Object[0]).split("::");
			GuiUtils.drawTextBox(this, mc, fontRenderer, strList, mouseX + 5, mouseY + 5, Colors.RED);
		}
	}

	private int getNextGuiId() {
		nextGuiId += 1;
		return nextGuiId;
	}

	public void initGui() {
		buttongm0 = new GuiButton(getNextGuiId(), width / 2 - 115, height / 2 + 21, 115, 20, "/gamemode 0");
		buttongm1 = new GuiButton(getNextGuiId(), width / 2 + 1, height / 2 + 21, 115, 20, "/gamemode 1");
		buttonList.add(buttongm0);
		buttonList.add(buttongm1);

		buttonFireWorksFactory = new GuiButton(getNextGuiId(), width / 2 + 1, height / 2 - 42, 115, 20,
				I18n.format("gui.act.fireworksfactory", new Object[0]));
		buttonPotionFactory = new GuiButton(getNextGuiId(), width / 2 - 115, height / 2 - 42, 115, 20,
				I18n.format("gui.act.potionfactory", new Object[0]));

		buttonItemFactory = new GuiButton(getNextGuiId(), width / 2 - 115, height / 2 - 21, 115, 20,
				I18n.format("gui.act.itemfactory", new Object[0]));
		buttonSkull = new GuiButton(getNextGuiId(), width / 2 + 1, height / 2 - 21, 115, 20,
				I18n.format("gui.act.skullfactory", new Object[0]));
		String inHandExist = I18n.format("gui.act.inhanditem", new Object[0]);
		buttonInHand = new GuiButton(getNextGuiId(), width / 2 - 115, height / 2, 231, 20, inHandExist);
		buttonFactory = new GuiButton(getNextGuiId(), width / 2 - 115, height / 2 + 42, 115, 20,
				I18n.format("gui.act.factory", new Object[0]));
		buttonDone = new GuiButton(getNextGuiId(), width / 2 + 1, height / 2 + 42, 115, 20,
				I18n.format("gui.done", new Object[0]));

		buttonList.add(buttonFireWorksFactory);
		buttonList.add(buttonPotionFactory);
		buttonList.add(buttonDone);
		buttonList.add(buttonFactory);
		buttonList.add(buttonInHand);
		buttonList.add(buttonItemFactory);
		buttonList.add(buttonSkull);
		super.initGui();
	}
}
