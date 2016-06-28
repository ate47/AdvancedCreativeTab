package com.ATE.ATEHUD.gui;

import java.io.IOException;

import org.fusesource.jansi.Ansi.Color;

import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.superclass.Effect;
import com.ATE.ATEHUD.superclass.EffectType;
import com.ATE.ATEHUD.utils.GuiUtils;
import com.ATE.ATEHUD.utils.ItemStackGenHelper;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GuiPotionTypeSelector extends GuiScreen {
	public GuiPotionFactory Last;
	private GuiButton bdone,bcancel;
	private GuiTextField name;
	private String skin;
	private Item item;
	public GuiPotionTypeSelector(GuiPotionFactory last){
		Last=last;
		if(Effect.itm_type.length>0 && Effect.itm_type[0]!=null){
			item=Effect.itm_type[0];
		} else {
			item=Items.POTIONITEM;
		}
		if(Effect.psk_skin.length>0 && Effect.psk_skin[0]!=null){
			skin=Effect.psk_skin[0].Name;
		} else {
			skin="water";
		}
	}
	public void updateScreen(){
		name.updateCursorCounter();
		super.updateScreen();
	}
	protected void keyTyped(char par1, int par2) throws IOException{
		name.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}
	protected void mouseClicked(int mouseX, int mouseY, int btn) throws IOException {
		name.mouseClicked(mouseX, mouseY, btn);
		super.mouseClicked(mouseX, mouseY, btn);

		for (int i = 0; i < Effect.itm_type.length; i++)
			if(GuiUtils.isHover((int)(width/2-(float)(22*Effect.itm_type.length/2)+22*i),26, 20, 20, mouseX, mouseY))
				item=Effect.itm_type[i];
		int yo=0,xo=0;
		for (int i = 0; i < Effect.psk_skin.length; i++) {
			if(GuiUtils.isHover((int)(width/2-(float)(22*4.5)+22*xo),69+22*yo, 21, 21, mouseX, mouseY))
				skin=Effect.psk_skin[i].Name;
			if(xo>7){
				yo++;
				xo=0;
			} else {xo++;}
			if(Effect.psk_skin[i].isLongable){
				if(GuiUtils.isHover((int)(width/2-(float)(22*4.5)+22*xo),69+22*yo, 21, 21, mouseX, mouseY))
					skin="long_"+Effect.psk_skin[i].Name;
				if(xo>7){
					yo++;
					xo=0;
				} else {xo++;}
			}
			if(Effect.psk_skin[i].isStrongable){
				if(GuiUtils.isHover((int)(width/2-(float)(22*4.5)+22*xo),69+22*yo, 21, 21, mouseX, mouseY))
					skin="strong_"+Effect.psk_skin[i].Name;
				if(xo>7){
					yo++;
					xo=0;
				} else {xo++;}
			}
		}
		
	}
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
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button==bdone){
			if(!name.getText().isEmpty())Last.name=name.getText();
			Last.item=item;
			Last.skin=skin;
			mc.displayGuiScreen(Last);
		}
		if(button==bcancel)mc.displayGuiScreen(Last);
		super.actionPerformed(button);
	}
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();

		GuiUtils.drawRightString(fontRendererObj, I18n.format("gui.act.potionfactory.potion.type")+" : ",(int)(width/2-(float)(22*Effect.itm_type.length/2)),26, 20, Colors.GOLD);
		GuiUtils.drawRightString(fontRendererObj, I18n.format("gui.act.fwf.name")+" : ", width/2-98, 48, 20, Colors.GOLD);
		GuiUtils.drawRightString(fontRendererObj, I18n.format("gui.act.potionfactory.potion.model")+" : ", (int)(width/2-(float)(22*4.5)),69, 20, Colors.GOLD);
		name.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (int i = 0; i < Effect.itm_type.length; i++) {
			if(item==Effect.itm_type[i])
				drawItemStack(new ItemStack(ModMain.iconItem), (int)(width/2-(float)(22*Effect.itm_type.length/2)+22*i),26);
			if(GuiUtils.isHover((int)(width/2-(float)(22*Effect.itm_type.length/2)+22*i),26,20,20, mouseX, mouseY))
				renderToolTip(new ItemStack(Effect.itm_type[i]), mouseX, mouseY);
			drawItemStack(new ItemStack(Effect.itm_type[i]), (int)(width/2-(float)(22*Effect.itm_type.length/2)+22*i),26);
		}
		int xo=0,yo=0;boolean rend=false;ItemStack info=new ItemStack(Blocks.BARRIER);
		for (int i = 0; i < Effect.psk_skin.length; i++) {
			if(Effect.psk_skin[i].Name==skin)
				drawItemStack(new ItemStack(ModMain.iconItem), (int)(width/2-(float)(22*4.5)+22*xo),69+22*yo);
			drawItemStack(ItemStackGenHelper.getNBT(new ItemStack(item),"{Potion:"+Effect.psk_skin[i].Name+"}"),(int)(width/2-(float)(22*4.5)+22*xo),69+22*yo);
			if(GuiUtils.isHover((int)(width/2-(float)(22*4.5)+22*xo),69+22*yo, 21, 21, mouseX, mouseY)){
				rend=true;
				info=ItemStackGenHelper.getNBT(new ItemStack(item),"{Potion:"+Effect.psk_skin[i].Name+"}");
			}
			if(xo>7){
				yo++;
				xo=0;
			} else {xo++;}
			if(Effect.psk_skin[i].isLongable){
				if(Effect.psk_skin[i].Name==skin)
					drawItemStack(new ItemStack(ModMain.iconItem), (int)(width/2-(float)(22*4.5)+22*xo),69+22*yo);
				drawItemStack(ItemStackGenHelper.getNBT(new ItemStack(item),"{Potion:long_"+Effect.psk_skin[i].Name+"}"),(int)(width/2-(float)(22*4.5)+22*xo),69+22*yo);
				if(GuiUtils.isHover((int)(width/2-(float)(22*4.5)+22*xo),69+22*yo, 21, 21, mouseX, mouseY)){
					rend=true;
					info=ItemStackGenHelper.getNBT(new ItemStack(item),"{Potion:long_"+Effect.psk_skin[i].Name+"}");
				}
				if(xo>7){
					yo++;
					xo=0;
				} else {xo++;}
			}
			if(Effect.psk_skin[i].isStrongable){
				if(Effect.psk_skin[i].Name==skin)
					drawItemStack(new ItemStack(ModMain.iconItem), (int)(width/2-(float)(22*4.5)+22*xo),69+22*yo);
				drawItemStack(ItemStackGenHelper.getNBT(new ItemStack(item),"{Potion:strong_"+Effect.psk_skin[i].Name+"}"),(int)(width/2-(float)(22*4.5)+22*xo),69+22*yo);
				if(GuiUtils.isHover((int)(width/2-(float)(22*4.5)+22*xo),69+22*yo, 21, 21, mouseX, mouseY)){
					rend=true;
					info=ItemStackGenHelper.getNBT(new ItemStack(item),"{Potion:strong_"+Effect.psk_skin[i].Name+"}");
				}
				if(xo>7){
					yo++;
					xo=0;
				} else {xo++;}
			}
		}
		if(rend){
			this.renderToolTip(info, mouseX, mouseY);
		}
	}
	public void initGui() {
		name=new GuiTextField(0, fontRendererObj, width/2-98, 48, 196, 16);
		if(Last.skin!=null)skin=Last.skin;
		if(Last.item!=null)item=Last.item;
		if(Last.name!=null && !Last.name.isEmpty())name.setText(Last.name);
		buttonList.add(bcancel=new GuiButton(0,width/2-100,5, 99, 20, I18n.format("gui.act.cancel")));
		buttonList.add(bdone=new GuiButton(1, width/2, 5, 100, 20, I18n.format("gui.done")));
		super.initGui();
	}

}
