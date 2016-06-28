package com.ATE.ATEHUD.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import scala.tools.nsc.doc.model.Trait;

import com.ATE.ATEHUD.Chat;
import com.ATE.ATEHUD.FakeItems3;
import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.superclass.Explosion;
import com.ATE.ATEHUD.superclass.Firework;
import com.ATE.ATEHUD.utils.GiveItem;
import com.ATE.ATEHUD.utils.GuiUtils;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ATEModGuiFireWorkFactory extends GuiScreen{
	private GuiScreen Last=null;
    private GuiButton buttonDone,buttonAdd,buttonAddExp,buttonCopy,buttonGiveItem;
    private GuiTextField name=null,flightduration=null;
    public Explosion[] Explosions={null,null,null,null,null,null,null};
	private int maxExplosion=Explosions.length-1;
    private List nButtonList=buttonList;
	public String getChargeType(int type){
		switch (type) {
		case 0:
			return LanguageRegistry.instance().getStringLocalization("item.fireworksCharge.type.0");
		case 1:
			return LanguageRegistry.instance().getStringLocalization("item.fireworksCharge.type.1");
		case 2:
			return LanguageRegistry.instance().getStringLocalization("item.fireworksCharge.type.2");
		case 3:
			return LanguageRegistry.instance().getStringLocalization("item.fireworksCharge.type.3");
		case 4:
			return LanguageRegistry.instance().getStringLocalization("item.fireworksCharge.type.4");
		default:
			return LanguageRegistry.instance().getStringLocalization("item.fireworksCharge.type");
		}
	};
    private int nextGuiId=0;
    public int countExplosion(){
    	int r = 0;
    	for (int i = 0; i < Explosions.length; i++) {
    		if(Explosions[i]!=null)r++;
    	}
    	return r;
    }

    public void updateScreen()
    {
    	name.updateCursorCounter();
    	flightduration.updateCursorCounter();
    }
    public void addExplosion(Explosion exp){
    	if(countExplosion()<=maxExplosion)Explosions[countExplosion()]=exp;
    }
    public void removeExplosion(int index) {
    	Explosion[] exp2 = Explosions;
    	if(index>=0 && index<Explosions.length) {
    		Explosions[index]=null;
    		int j=0;
    		for (int i = 0; i < Explosions.length; i++) {
				if(Explosions[i]!=null){
					exp2[j]=Explosions[i];
					j++;
				}
			}
    		for (int i = j; i < Explosions.length; i++) {
    			exp2[i]=null;
    		}
    		Explosions=exp2;
    	}
    }
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		nButtonList=new ArrayList();
		int info=-1;
		for (int i = 0; i < countExplosion(); i++) {
			nButtonList.add(new GuiButton(5+i,width/2+20,height/2-74+(21*(i)),75,20,LanguageRegistry.instance().getStringLocalization("gui.act.explosion")+(Integer)(i+1)));
			nButtonList.add(new GuiButton(5+i+5+maxExplosion,width/2+20+76,height/2-74+(21*(i)),20,20,"\u00a7c-"));
			if(mouseX>=width/2+20 && mouseX<=width/2+20+95 && mouseY>=height/2-74+(21*(i)) && mouseY<=height/2-74+(21*(i))+20){
				info=i;
			}
		}
		buttonList=nButtonList;
		if(countExplosion()!=maxExplosion+1){
			buttonAddExp=new GuiButton(1,width/2+20,height/2-74+(21*(countExplosion())),96,20,"\u00a7a+");
			buttonList.add(buttonAddExp);
		}
		
		GuiUtils.drawCenterString(fontRendererObj, LanguageRegistry.instance().getStringLocalization("gui.act.fwf.exp"), width/2+20,height/2-74+(21*(-1)),20,Colors.GOLD);
		GuiUtils.drawRightString(fontRendererObj, LanguageRegistry.instance().getStringLocalization("gui.act.fwf.name")+" : ", width/2-100, height/2-74,18,Colors.WHITE);
		GuiUtils.drawRightString(fontRendererObj, LanguageRegistry.instance().getStringLocalization("gui.act.fwf.flightduration")+" : ", width/2-100, height/2-22,18,Colors.WHITE);

		buttonCopy = new GuiButton(3, width/2 -150, height-35, 75, 20, I18n.format("gui.act.clipload", new Object[0]));
		buttonAdd  = new GuiButton(4, width/2 -75, height-35, 75, 20, I18n.format("gui.act.add", new Object[0]));
		buttonGiveItem  = new GuiButton(999, width/2 -0, height-35, 75, 20, I18n.format("gui.act.give", new Object[0]));
		buttonDone = new GuiButton(2, width/2+75, height-35, 75, 20, I18n.format("gui.done", new Object[0]));

		buttonList.add(buttonDone);
		buttonList.add(buttonAdd);
		buttonList.add(buttonCopy);
		buttonList.add(buttonGiveItem);
		name.drawTextBox();
		flightduration.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(info>=0) {
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/backgrounds.png"));
			GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(mouseX+5, mouseY+5, 0, 0, 160, (fontRendererObj.FONT_HEIGHT+1)*(6+Explosions[info].getColors().length+Explosions[info].getFadeColors().length)+10);
            CursorY=-1;
			fontRendererObj.drawString(LanguageRegistry.instance().getStringLocalization("gui.act.explosion")+(Integer)(info+1), mouseX+10, mouseY+10+getCursorY(), Colors.AQUA);
			int colortrail=Colors.DARK_GREEN;String stringtrail=LanguageRegistry.instance().getStringLocalization("gui.act.yes");  
			if(Explosions[info].getTrail()==0){colortrail=Colors.DARK_RED;stringtrail=LanguageRegistry.instance().getStringLocalization("gui.act.no");}
			int colorflicker=Colors.DARK_GREEN;String stringflicker=LanguageRegistry.instance().getStringLocalization("gui.act.yes");
			if(Explosions[info].getFlicker()==0){colorflicker=Colors.DARK_RED;stringflicker=LanguageRegistry.instance().getStringLocalization("gui.act.no");}
			int a = getCursorY();
			fontRendererObj.drawString(I18n.format("item.fireworksCharge.trail", new Object[0])+" : ", mouseX+10, mouseY+10+a, Colors.DARK_AQUA);
			fontRendererObj.drawString(stringtrail, mouseX+10+fontRendererObj.getStringWidth(I18n.format("item.fireworksCharge.trail", new Object[0])+" : "), mouseY+10+a, colortrail);
			a = getCursorY();
			fontRendererObj.drawString(I18n.format("item.fireworksCharge.flicker", new Object[0])+" : ", mouseX+10, mouseY+10+a, Colors.DARK_AQUA);
			fontRendererObj.drawString(stringflicker, mouseX+10+fontRendererObj.getStringWidth(I18n.format("item.fireworksCharge.flicker", new Object[0])+" : "), mouseY+10+a, colorflicker);
			a = getCursorY();
			fontRendererObj.drawString(I18n.format("gui.act.type", new Object[0])+" : "+I18n.format("item.fireworksCharge.type."+Explosions[info].getType(), new Object[0]), mouseX+10, mouseY+10+a, Colors.DARK_AQUA);
			fontRendererObj.drawString(getChargeType(Explosions[info].getType()), mouseX+10+fontRendererObj.getStringWidth(I18n.format("gui.act.type", new Object[0])+" : "), mouseY+10+a, Colors.DARK_AQUA);
			fontRendererObj.drawString(I18n.format("gui.act.colors", new Object[0])+" : ", mouseX+10, mouseY+10+getCursorY(), Colors.DARK_AQUA);
			for (int i = 0; i < Explosions[info].getColors().length; i++) {
				fontRendererObj.drawString("=========", mouseX+10, mouseY+10+getCursorY(), Explosions[info].getColors()[i]);
				
			}
			fontRendererObj.drawString(I18n.format("gui.act.fadecolor", new Object[0])+" : ", mouseX+10, mouseY+10+getCursorY(), Colors.DARK_AQUA);
			for (int i = 0; i < Explosions[info].getFadeColors().length; i++) {
				fontRendererObj.drawString("=========", mouseX+10, mouseY+10+getCursorY(), Explosions[info].getFadeColors()[i]);
			}
		}
		if(!mc.thePlayer.capabilities.isCreativeMode)
		GuiUtils.buttonHoverMessage(this, mc, buttonGiveItem, mouseX, mouseY, fontRendererObj, I18n.format("gui.act.nocreative").split("::"),Colors.RED);
	}
	private int CursorY=-1;
	private int getCursorY(){
		CursorY++;
		return CursorY*(fontRendererObj.FONT_HEIGHT+1);
	}
	public void initGui() {
		if(name==null)name=new GuiTextField(0, fontRendererObj, width/2-99, height/2-74, 90, 20);
		fontRendererObj.drawString(LanguageRegistry.instance().getStringLocalization("gui.act.fwf.colorinfo"), width/2-99, height-53-fontRendererObj.FONT_HEIGHT/2,Colors.GRAY);
		if(flightduration==null)flightduration=new GuiTextField(1, fontRendererObj, width/2-99, height/2-22, 90, 20);

		super.initGui();

	}
	protected void keyTyped(char par1, int par2) throws IOException{
		name.textboxKeyTyped(par1, par2);
		flightduration.textboxKeyTyped(par1, par2);
        super.keyTyped(par1, par2);

    }
	protected void mouseClicked(int x, int y, int btn) throws IOException {
        super.mouseClicked(x, y, btn);
        name.mouseClicked(x, y, btn);
        flightduration.mouseClicked(x, y, btn);
	}
	public int getFlight(){
		int flight=1;
		try {
			flight=Integer.valueOf(flightduration.getText());
		} catch (Exception e) {
		}
		return flight;
	}
	public String getConfigData(){
		Firework fw =new Firework(Explosions, getFlight());
		String str=this.name.getText();
		if(str!="")	fw.setName(str);
		return "minecraft:fireworks 1 0 "+fw.getNBTFirework();
	}
	public void actionPerformed(GuiButton button){
		if(button==buttonDone)mc.displayGuiScreen(Last);
		if(button==buttonAdd){
			ModMain.addConfig("AdvancedItem", getConfigData());
			Chat.show(I18n.format("gui.act.add.msg"));
			mc.displayGuiScreen(Last);
		}
		if(button==buttonAddExp) addExplosion(new Explosion());
		if(button==buttonCopy){
			ATEModGuiNbtCode.setClipboardString(getConfigData());
			Chat.show(I18n.format("gui.act.clipload.msg"));
		}
		if(button==buttonGiveItem){
			if(mc.thePlayer.capabilities.isCreativeMode){
				int flight=1;
				try {
					flight=Integer.valueOf(flightduration.getText());
				} catch (Exception e) {
				}
				int amount = 1;
				ItemStack stack;
				try {
					stack = FakeItems3.getGive(getConfigData());
					GiveItem.give(mc, stack);
				} catch (NumberInvalidException e) {
					Chat.error(e.getMessage());
				}

				
			}else{
				Chat.error(I18n.format("gui.act.nocreative"));
			}
		}
		if(button.id>=5+maxExplosion+4 && button.id<5+maxExplosion+2+maxExplosion+4)removeExplosion(button.id-10-maxExplosion);
		if(button.id>=5 && button.id<5+maxExplosion+2)mc.displayGuiScreen(new ATEModGuiExplosionSelector(this, button.id-5,Explosions[button.id-5]));
		
	}
	public ATEModGuiFireWorkFactory(GuiScreen last) {
		super();
		addExplosion(new Explosion(0,1,1,new int[]{Colors.RANDOM(),Colors.RANDOM()},new int[]{Colors.RANDOM(),Colors.RANDOM()}));
		addExplosion(new Explosion());
		Last=last;
	}
}