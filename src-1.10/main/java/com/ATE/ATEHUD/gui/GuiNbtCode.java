package com.ATE.ATEHUD.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

import com.ATE.ATEHUD.FakeItems3;
import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.utils.Chat;
import com.ATE.ATEHUD.utils.GiveItem;
import com.ATE.ATEHUD.utils.GuiUtils;
import com.ATE.ATEHUD.utils.ItemStackGenHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiNbtCode extends GuiScreen{
	private String text="";
	private GuiScreen Last=null;
	private ItemStack nameIS=null;
	private GuiButton useParButton,give,add,modifier;
	public GuiTextField name;
	public void updateScreen()
	{
		name.updateCursorCounter();
	}
	protected void keyTyped(char par1, int par2) throws IOException{
		name.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		name.mouseClicked(x, y, btn);
	}
	private void drawItemStack(ItemStack stack, int x, int y)
	{
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
		FontRenderer font = null;
		if (stack != null) font = stack.getItem().getFontRenderer(stack);
		if (font == null) font = fontRendererObj;
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
	}
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		String str=I18n.format("gui.act.inhanditem");
		fontRendererObj.drawString(str, width/2-fontRendererObj.getStringWidth(str)/2, height/2-23-fontRendererObj.FONT_HEIGHT, Colors.WHITE);
		drawItemStack(nameIS, width/2+152, height/2-22);
		// GuiContainer
		name.drawTextBox();
		if(nameIS!=null && GuiUtils.isHover(width/2+152, height/2-22, 32, 32, mouseX, mouseY))
			this.renderToolTip(nameIS, mouseX, mouseY);
		if(!mc.thePlayer.capabilities.isCreativeMode && GuiUtils.isHover(width/2-50, height/2+21,99,20, mouseX, mouseY))
			GuiUtils.drawTextBox(this, mc, fontRendererObj, new String[]{I18n.format("gui.act.nocreative")}, mouseX, mouseY, Colors.RED);;
		give.enabled=!name.getText().isEmpty();
		add.enabled=!name.getText().isEmpty();
		modifier.enabled=!name.getText().isEmpty();
		
	}
	public void initGui() {
		Minecraft mc=Minecraft.getMinecraft();
		if(mc.thePlayer.inventory!=null)
			if(mc.thePlayer.inventory.getCurrentItem()!=null){
			nameIS=mc.thePlayer.inventory.getCurrentItem();
			ItemStack is=nameIS;
			String nbt="";if(is.getTagCompound()!=null)nbt=is.getTagCompound().toString();
			text=((ResourceLocation)Item.REGISTRY.getNameForObject(is.getItem())).toString()+" "+is.stackSize+
			" "+is.getMetadata()+
			" "+nbt;
		}
		buttonList.add(new GuiButton(1, width/2, height/2+21,150,20, I18n.format("gui.done")));
		buttonList.add(modifier=new GuiButton(5, width/2-150, height/2+21,149,20, I18n.format("gui.act.modifier")));
		buttonList.add(add=new GuiButton(2, width/2-150, height/2,99,20, I18n.format("gui.act.add")));
		buttonList.add(give=new GuiButton(4, width/2-50, height/2,99,20, I18n.format("gui.act.give")));
		buttonList.add(new GuiButton(3, width/2+50, height/2,100,20, I18n.format("gui.act.clipload")));
		name=new GuiTextField(1, fontRendererObj, width/2-148, height/2-22, 296, 20);
		name.setMaxStringLength(Integer.MAX_VALUE);
		text=text.replaceAll("§", "&&");
		name.setText(text);
		super.initGui();
	}
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 1: Minecraft.getMinecraft().displayGuiScreen(Last);break;
		case 2: 
			ModMain.addConfig("AdvancedItem",name.getText());
			Chat.show(I18n.format("gui.act.add.msg"));
			break;
		case 3: 
			StringSelection select=new StringSelection(name.getText());
			Clipboard cb=Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(select, select);
		break;
		case 4:
			try {
				GiveItem.give(mc, ItemStackGenHelper.getGive(name.getText()));
			} catch (NumberInvalidException e) {
				Chat.error(I18n.format("gui.act.cantgive"));
			}
			 break;
		case 5: try {
				ItemStack is=ItemStackGenHelper.getGive(name.getText());
				if(is!=null)
				Minecraft.getMinecraft().displayGuiScreen(new GuiModifier(this,is));
			} catch (NumberInvalidException e) {} break;
		default:
			break;
		}
		super.actionPerformed(button);
	}
	public GuiNbtCode(){
		
	}
	public GuiNbtCode(GuiScreen last){
		Last=last;
		
	}
}