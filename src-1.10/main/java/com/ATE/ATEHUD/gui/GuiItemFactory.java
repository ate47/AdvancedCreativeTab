package com.ATE.ATEHUD.gui;

import java.io.IOException;

import com.ATE.ATEHUD.FakeItems3;
import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Attribute;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.superclass.EnchantmentInfo;
import com.ATE.ATEHUD.superclass.Enchantments;
import com.ATE.ATEHUD.utils.Chat;
import com.ATE.ATEHUD.utils.GiveItem;
import com.ATE.ATEHUD.utils.GuiUtils;
import com.ATE.ATEHUD.utils.ItemStackGenHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.ItemStack;

public class GuiItemFactory extends GuiScreen{
	public GuiButton give,add;
	public GuiScreen Last=null;
	public String StockName="",StockMeta="",StockItem="";
	public GuiTextField name,meta,item;
	public GuiButton unbreak;
	public EnchantmentInfo[] enchantments=new EnchantmentInfo[]{};
	public String[] lores=new String[]{};
	public Attribute[] attributes=new Attribute[]{};
	public GuiItemFactory(){
		
	}
	public GuiItemFactory(GuiScreen last){
		Last=last;
		
	}
	public String getEnchNbt(){
		String str="[";
		for (int i = 0; i < enchantments.length; i++) {
			if(enchantments[i]!=null)str+=","+enchantments[i].getEnchNbt();
		}
		return str.replaceFirst(",", "")+"]";
	}
	public String getAttributeNbt(){
		String str="[";
		for (int i = 0; i < attributes.length; i++) {
			if(attributes[i]!=null)str+=","+attributes[i].getAttriNbt();
		}
		return str.replaceFirst(",", "")+"]";
	}
	public String getLoreNbt(){
		String[] lores2=lores;int j=0;
		for (int i = 0; i < lores2.length; i++) {
			if(!lores2[i].isEmpty()){
				lores2[j]=lores[i].replaceAll("&&", "\u00a7");
				j++;
			}
		}for (int i = j; i < lores2.length; i++) {
			lores2[i]=null;
		}
		String str="[";
		for (int i = 0; i < lores2.length; i++) {
			if(lores2[i]!=null)str+=",\""+lores2[i]+"\"";
		}
		return str.replaceFirst(",", "")+"]";
	}
	public String getNbt(){
		String nameout=name.getText().replaceAll("&&", "\u00a7");
		String unb="";
		if(unbreak.packedFGColour==GuiAttributeSelector.buttonYESColor)unb="Unbreakable:1,";
		
		nameout="";if(name.getText().isEmpty())nameout="Name:\""+name.getText()+"\",";
		return "{"+unb+"display:{"+nameout+"Lore:"+getLoreNbt()+"},AttributeModifiers:"+getAttributeNbt()+
				",ench:"+getEnchNbt()+"}";
	}
	public void updateScreen()
	{
		name.updateCursorCounter();
		meta.updateCursorCounter();
		item.updateCursorCounter();
	}
	protected void keyTyped(char par1, int par2) throws IOException{
		name.textboxKeyTyped(par1, par2);
		meta.textboxKeyTyped(par1, par2);
		item.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		name.mouseClicked(x, y, btn);
		meta.mouseClicked(x, y, btn);
		item.mouseClicked(x, y, btn);
		super.mouseClicked(x, y, btn);
	}
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		StockItem=item.getText();
		StockMeta=meta.getText();
		StockName=name.getText();
		drawDefaultBackground();
		meta.drawTextBox();
		name.drawTextBox();
		item.drawTextBox();
		GuiUtils.drawRightString(fontRendererObj, " : ", width/2+122, height/2-19, 20, Colors.WHITE);
		GuiUtils.drawRightString(fontRendererObj, I18n.format("gui.act.fwf.name")+" = ", width/2-148, height/2-40, 20, Colors.GOLD);
		GuiUtils.drawRightString(fontRendererObj, I18n.format("gui.act.itemfactory.itemid")+" = ", width/2-148, height/2-20, 20, Colors.GOLD);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(!mc.thePlayer.capabilities.isCreativeMode && GuiUtils.isHover(width/2-50, height/2+42, 99, 20, mouseX, mouseY))
			GuiUtils.drawTextBox(this, mc, fontRendererObj, new String[]{I18n.format("gui.act.nocreative")}, mouseX, mouseY, Colors.RED);
		give.enabled=!item.getText().isEmpty();
		add.enabled=!item.getText().isEmpty();
	}
	public void initGui() {
		name=new GuiTextField(6, fontRendererObj, width/2-148, height/2-40, 296, 16);
		item=new GuiTextField(6, fontRendererObj, width/2-148, height/2-19, 296-30-fontRendererObj.getStringWidth(" : "), 16);
		meta=new GuiTextField(6, fontRendererObj, width/2+122, height/2-19, 26, 16);
		name.setText(StockName);
		meta.setText(StockMeta);
		item.setText(StockItem);
		buttonList.add(new GuiButton(5, width/2-150, height/2, 149, 20, I18n.format("gui.act.itemfactory.lores")));
		unbreak=new GuiButton(6, width/2, height/2, 150, 20, I18n.format("item.unbreakable"));
		unbreak.packedFGColour=GuiAttributeSelector.buttonNOColor;
		buttonList.add(unbreak);
		buttonList.add(new GuiButton(4, width/2-150, height/2+21, 149, 20, I18n.format("gui.act.itemfactory.attributes")));
		buttonList.add(new GuiButton(3, width/2, height/2+21, 150, 20, I18n.format("gui.act.itemfactory.enchants")));
		buttonList.add(new GuiButton(2, width/2-150, height/2+42,99,20, I18n.format("gui.done")));
		give=(new GuiButton(1, width/2-50, height/2+42,99,20, I18n.format("gui.act.give")));
		add=new GuiButton(0, width/2+50, height/2+42,100,20, I18n.format("gui.act.add"));
		buttonList.add(give);
		buttonList.add(add);
		super.initGui();
	}
	protected void actionPerformed(GuiButton button) throws IOException {
		ItemStack is=null;
		int metav=0;
		switch (button.id) {
		case 0://add
			try {
				metav=Integer.valueOf(meta.getText());
			} catch (Exception e) {}
			ModMain.addConfig("AdvancedItem", item.getText()+" 1 "+metav+" "+getNbt());
			Chat.show(I18n.format("gui.act.add.msg"));
			break;
		case 1://Give
			try {
				try {
					metav=Integer.valueOf(meta.getText());
				} catch (Exception e) {}
				is=ItemStackGenHelper.getGive(item.getText()+" 1 "+metav+" "+getNbt());
				GiveItem.give(mc, is);
			} catch (NumberInvalidException e) {
				Chat.error(I18n.format("gui.act.give.fail2"));
			}
			break;
		case 2://Cancel/Done
			Minecraft.getMinecraft().displayGuiScreen(Last);break;
		case 3://Enchants
			Minecraft.getMinecraft().displayGuiScreen(new GuiEnchantSelector(this));break;
		case 4://Attribute
			Minecraft.getMinecraft().displayGuiScreen(new GuiAttributeSelector(this));break;
		case 5://Lore
			Minecraft.getMinecraft().displayGuiScreen(new GuiLoreSelector(this));break;
		case 6:
			if(unbreak.packedFGColour==GuiAttributeSelector.buttonNOColor){
				unbreak.packedFGColour=GuiAttributeSelector.buttonYESColor;
			}else{
				unbreak.packedFGColour=GuiAttributeSelector.buttonNOColor;
			}
			break;
		}
		super.actionPerformed(button);
	}
}
