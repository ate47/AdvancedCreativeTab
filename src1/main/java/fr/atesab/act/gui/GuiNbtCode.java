package fr.atesab.act.gui;

import fr.atesab.act.ModMain;
import fr.atesab.act.superclass.Colors;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.GiveUtils;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemStackGenHelper;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.text.TextFormatting;

public class GuiNbtCode extends GuiScreen {
	private String text = "";
	private GuiScreen Last = null;
	private ItemStack nameIS = null;
	private GuiButton useParButton, give, add, modifier;
	public GuiTextField name;

	public GuiNbtCode(GuiScreen last) {
		Last = last;
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 1:
			Minecraft.getMinecraft().displayGuiScreen(Last);
			break;
		case 2:
			ModMain.addConfig("AdvancedItem", name.getText());
			ChatUtils.show(I18n.format("gui.act.add.msg", new Object[0]));
			break;
		case 3:
			StringSelection select = new StringSelection(name.getText());
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(select, select);
			break;
		case 4:
			try {
				GiveUtils.give(mc, ItemStackGenHelper.getGive(name.getText()));
			} catch (NumberInvalidException e) {
				ChatUtils.error(I18n.format("gui.act.cantgive", new Object[0]));
			}
		case 5:
			try {
				ItemStack is = ItemStackGenHelper.getGive(name.getText());
				if (is != null) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiModifier(this, is));
				}
			} catch (NumberInvalidException localNumberInvalidException1) {
			}
		}
		super.actionPerformed(button);
	}

	private void drawItemStack(ItemStack stack, int x, int y) {
		if(stack==null) return;
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
		FontRenderer font = null;
		font = stack.getItem().getFontRenderer(stack);
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		String str = I18n.format("gui.act.inhanditem");
		fontRenderer.drawString(str, width / 2 - fontRenderer.getStringWidth(str) / 2,
				height / 2 - 23 - fontRenderer.FONT_HEIGHT, Colors.WHITE);
		drawItemStack(nameIS, width / 2 + 152, height / 2 - 22);
		// GuiContainer
		name.drawTextBox();
		if (nameIS != null && GuiUtils.isHover(width / 2 + 152, height / 2 - 22, 32, 32, mouseX, mouseY))
			this.renderToolTip(nameIS, mouseX, mouseY);
		if (!mc.player.capabilities.isCreativeMode
				&& GuiUtils.isHover(width / 2 - 50, height / 2 + 21, 99, 20, mouseX, mouseY))
			GuiUtils.drawTextBox(this, mc, fontRenderer, new String[] { I18n.format("gui.act.nocreative") }, mouseX,
					mouseY, Colors.RED);
		;
		give.enabled = !name.getText().isEmpty();
		add.enabled = !name.getText().isEmpty();
		modifier.enabled = !name.getText().isEmpty();

	}

	public void initGui() {
		text = GiveUtils.getItemStack(nameIS = mc.player.inventory !=null?
				mc.player.inventory.getCurrentItem():null);
		buttonList.add(new GuiButton(1, width / 2, height / 2 + 21, 150, 20, I18n.format("gui.done", new Object[0])));
		buttonList.add(this.modifier = new GuiButton(5, width / 2 - 150, height / 2 + 21, 149, 20,
				I18n.format("gui.act.modifier", new Object[0])));
		buttonList.add(this.add = new GuiButton(2, width / 2 - 150, height / 2, 99, 20,
				I18n.format("gui.act.add", new Object[0])));
		buttonList.add(this.give = new GuiButton(4, width / 2 - 50, height / 2, 99, 20,
				I18n.format("gui.act.give", new Object[0])));
		buttonList.add(
				new GuiButton(3, width / 2 + 50, height / 2, 100, 20, I18n.format("gui.act.clipload", new Object[0])));
		name = new GuiTextField(1, fontRenderer, width / 2 - 148, height / 2 - 22, 296, 20);
		name.setMaxStringLength(Integer.MAX_VALUE);
		name.setText(text = text.replaceAll(String.valueOf(ChatUtils.MODIFIER), "&&"));
		super.initGui();
	}

	protected void keyTyped(char par1, int par2) throws IOException {
		name.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		name.mouseClicked(x, y, btn);
	}

	public void updateScreen() {
		name.updateCursorCounter();
		text = name.getText();
		try {
			nameIS = ItemStackGenHelper.getGive(name.getText());
		} catch (Exception e) {
			nameIS = null;
		}
	}
}
