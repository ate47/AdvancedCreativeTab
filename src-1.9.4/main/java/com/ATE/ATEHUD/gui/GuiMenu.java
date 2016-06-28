package com.ATE.ATEHUD.gui;

import com.ATE.ATEHUD.ATEEventHandler;
import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.utils.Chat;
import com.ATE.ATEHUD.utils.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GuiMenu extends GuiScreen{
	private GuiScreen Last=null;
    private GuiButton buttonDone,buttonClipLoad,buttonSkull,buttonFactory,buttonFireWorksFactory,buttonPotionFactory,buttonItemFactory,buttonInHand,buttongm1,buttongm0;
    
    private int nextGuiId=0;
    private int getNextGuiId() {
    	nextGuiId++;
    	return nextGuiId;
    }
    private Minecraft mc=Minecraft.getMinecraft();
	public void drawItemStack(ItemStack stack, int x, int y)
    {
        GlStateManager.translate(0.0F, 0.0F, 21.0F);
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
		this.drawDefaultBackground();
		fontRendererObj.drawString(ModMain.Name+" - "+ModMain.Version, width/2-fontRendererObj.getStringWidth(ModMain.Name+" - "+ModMain.Version)/2, height/2- 63-fontRendererObj.FONT_HEIGHT-1,Colors.RED);
		fontRendererObj.drawString(I18n.format("gui.act.menu"), width/2-fontRendererObj.getStringWidth(I18n.format("gui.act.menu"))/2, height/2- 63,Colors.GOLD);
		drawItemStack(new ItemStack(Items.FIREWORKS), width/2+116, height/2- 42);
		drawItemStack(new ItemStack(Items.ENCHANTED_BOOK), width/2+116, height/2- 21);
		String inHandExist=I18n.format("gui.act.inhanditem");
		if(mc.thePlayer.inventory!=null)
		if(mc.thePlayer.inventory.getCurrentItem()!=null){
			drawItemStack(mc.thePlayer.inventory.getCurrentItem(), width/2+116, height/2);
		}
		GuiInventory.drawEntityOnScreen(width/2-150, height/2+21, 30, (float)(width/2-150)-mouseX, (float)(height/2+21)-mouseY, this.mc.thePlayer);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(GuiUtils.isHover(width/2 -115, height/2+21, 231, 20, mouseX, mouseY)){
			String[] strList=I18n.format("gui.act.setGm.hover").split("::");
			GuiUtils.drawTextBox(this, mc, fontRendererObj, strList, mouseX+5, mouseY+5, Colors.RED);
		}
	}
	public void initGui() {
		buttongm0 = new GuiButton(getNextGuiId(), width/2 -115, height/2+21, 115, 20, "/gamemode 0");
		buttongm1 = new GuiButton(getNextGuiId(), width/2+1, height/2+21, 115, 20, "/gamemode 1");
		buttonList.add(buttongm0);
		buttonList.add(buttongm1);
		
		buttonFireWorksFactory = new GuiButton(getNextGuiId(), width/2+1, height/2- 42, 115, 20, I18n.format("gui.act.fireworksfactory", new Object[0]));
		buttonPotionFactory = new GuiButton(getNextGuiId(), width/2-115, height/2- 42, 115, 20, I18n.format("gui.act.potionfactory", new Object[0]));
		
		buttonItemFactory = new GuiButton(getNextGuiId(), width/2-115, height/2-21, 115, 20, I18n.format("gui.act.itemfactory", new Object[0]));
		buttonSkull = new GuiButton(getNextGuiId(), width/2+1, height/2-21, 115, 20, I18n.format("gui.act.skullfactory", new Object[0]));
		String inHandExist=I18n.format("gui.act.inhanditem");
		buttonInHand = new GuiButton(getNextGuiId(), width/2 -115, height/2, 231, 20, inHandExist);
		buttonFactory = new GuiButton(getNextGuiId(), width/2 -115, height/2+42, 115, 20, I18n.format("gui.act.factory", new Object[0]));
		buttonDone = new GuiButton(getNextGuiId(), width/2+1, height/2+42, 115, 20, I18n.format("gui.done", new Object[0]));
		
		buttonList.add(buttonFireWorksFactory);
		buttonList.add(buttonPotionFactory);
		buttonList.add(buttonDone);
		buttonList.add(buttonFactory);
		buttonList.add(buttonInHand);
		buttonList.add(buttonItemFactory);
		buttonList.add(buttonSkull);
		super.initGui();
	}
	public void actionPerformed(GuiButton button){
		if(button==buttonDone)FMLClientHandler.instance().showGuiScreen(Last);
		if(button==buttonFactory)FMLClientHandler.instance().showGuiScreen(new ConfigGUI(this));
		if(button==buttonItemFactory)FMLClientHandler.instance().showGuiScreen(new GuiItemFactory(this));
		if(button==buttonPotionFactory)FMLClientHandler.instance().showGuiScreen(new GuiPotionFactory(this));
		if(button==buttonFireWorksFactory)FMLClientHandler.instance().showGuiScreen(new GuiFireWorkFactory(this));
		if(button==buttonSkull)FMLClientHandler.instance().showGuiScreen(new GuiSkullGiver(this));;
		if(button==buttonInHand)FMLClientHandler.instance().showGuiScreen(new GuiNbtCode(this));
		if(button==buttongm1){
			Chat.show(I18n.format("gui.act.setGm"));
			ATEEventHandler.tryChangingGameMode=true;
			sendChatMessage("/gamemode 1",false);
		}
		if(button==buttongm0){
			Chat.show(I18n.format("gui.act.setGm"));
			ATEEventHandler.tryChangingGameMode=true;
			sendChatMessage("/gamemode 0",false);
		}
	}
	public GuiMenu() {
		mc=Minecraft.getMinecraft();
	}
	public GuiMenu(GuiScreen last) {
		Last=last;
		mc=Minecraft.getMinecraft();
	}
}