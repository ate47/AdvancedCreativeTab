package com.ATE.ATEHUD.gui;

import java.io.IOException;

import com.ATE.ATEHUD.ModMain;
import com.ATE.ATEHUD.superclass.Colors;
import com.ATE.ATEHUD.superclass.Effect;
import com.ATE.ATEHUD.superclass.EffectType;
import com.ATE.ATEHUD.utils.Chat;
import com.ATE.ATEHUD.utils.GiveItem;
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

public class GuiPotionFactory extends GuiScreen {
	public String name,skin;
	public Item item;
	public GuiScreen Last;
	public EffectType[] list;
	public GuiTextField[] tfs_ampl,tfs_dura;
	private int ix=0,iy=0;
	private GuiButton bdone,bmax,bmaxlv,bmaxapl,bremove,bskin,bgive,badd;
	private String[] amplList,duraList;
	public GuiPotionFactory(GuiScreen last){
		Last=last;
		list=Effect.eff_effect;
		duraList=new String[list.length];
		amplList=new String[list.length];
	}
	public void updateScreen(){
		for (int i = 0; i < tfs_ampl.length; i++) {
			tfs_ampl[i].updateCursorCounter();
			tfs_dura[i].updateCursorCounter();
		}
		super.updateScreen();
	}
	protected void keyTyped(char par1, int par2) throws IOException{
		for (int i = 0; i < tfs_ampl.length; i++) {
			tfs_ampl[i].textboxKeyTyped(par1, par2);
			tfs_dura[i].textboxKeyTyped(par1, par2);
		}
		super.keyTyped(par1, par2);
	}
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		for (int i = 0; i < tfs_ampl.length; i++) {
			tfs_ampl[i].mouseClicked(x, y, btn);
			tfs_dura[i].mouseClicked(x, y, btn);
		}
		super.mouseClicked(x, y, btn);
	}
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button==bmax){
			for (int i = 0; i < tfs_ampl.length; i++) {
				tfs_ampl[i].setText("127");
				tfs_dura[i].setText(String.valueOf(Integer.MAX_VALUE));
			}	
		}
		if(button==bmaxlv){
			for (int i = 0; i < tfs_ampl.length; i++) {
				tfs_ampl[i].setText("127");
			}
		}
		if(button==bmaxapl){
			for (int i = 0; i < tfs_ampl.length; i++) {
				tfs_dura[i].setText(String.valueOf(Integer.MAX_VALUE));
			}			
		}
		if(button==bremove){
			for (int i = 0; i < tfs_ampl.length; i++) {
				tfs_ampl[i].setText("");
				tfs_dura[i].setText("");
			}
		}
		if(button==badd){
			ModMain.addConfig("AdvancedItem", item.getUnlocalizedName().substring(5)+" 1 0 "+getPotion().getTagCompound().toString());
			Chat.show(I18n.format("gui.act.add.msg"));
		}
		if(button==bgive)GiveItem.give(mc, getPotion());
		if(button==bskin)mc.displayGuiScreen(new GuiPotionTypeSelector(this));
		if(button==bdone)mc.displayGuiScreen(Last);
		super.actionPerformed(button);
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
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		for (int i = 0; i < amplList.length; i++) {
			duraList[i]=tfs_dura[i].getText();
			amplList[i]=tfs_ampl[i].getText();
		}		
		drawDefaultBackground();
		short[] posList=new short[tfs_ampl.length];
		for (int j = 0; j < posList.length; j++) {
			posList[j]=0;
		}
		for (int i = 0; i < posList.length; i++) {
			try {
				if(!tfs_ampl[i].getText().isEmpty()){
					Integer.valueOf(tfs_ampl[i].getText());
					posList[i]=1;
				}
			} catch (Exception e) {posList[i]=2;}
		}
		for (int i = 0; i < posList.length; i++) {
			if(posList[i]!=2)
			try {
				if(!tfs_dura[i].getText().isEmpty()){
					Integer.valueOf(tfs_dura[i].getText());
					posList[i]=1;
				}
			} catch (Exception e) {posList[i]=2;}
		}
		for (int i = 0; i < tfs_ampl.length; i++) {
			int finalColor=Colors.GRAY;
			switch (posList[i]) {
			case 1:
				finalColor=Colors.WHITE;
				break;
			case 2:
				finalColor=Colors.RED;
				break;
			}
			GuiUtils.drawRightString(fontRendererObj, I18n.format("effect."+list[i].name)+" : ", tfs_ampl[i].xPosition, tfs_ampl[i].yPosition, tfs_ampl[i].height, finalColor);
			tfs_ampl[i].drawTextBox();
			tfs_dura[i].drawTextBox();
		}
		drawItemStack(getPotion(), width/2+205,5);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(GuiUtils.isHover(width/2+205,5, 21, 21, mouseX, mouseY))
			renderToolTip(getPotion(), mouseX, mouseY);
		if(GuiUtils.isHover(width/2-100, 50, 40, iy*21+21, mouseX, mouseY) || GuiUtils.isHover(width/2+100, 50, 40, iy*21+21, mouseX, mouseY))
			GuiUtils.drawTextBox(this, mc, fontRendererObj, new String[]{I18n.format("gui.act.potionfactory.amplifier")}, mouseX, mouseY, Colors.WHITE);
		if(GuiUtils.isHover(width/2-56, 50, 40, iy*21+21, mouseX, mouseY) || GuiUtils.isHover(width/2-56+200, 50, 40, iy*21+21, mouseX, mouseY))
			GuiUtils.drawTextBox(this, mc, fontRendererObj, new String[]{I18n.format("gui.act.potionfactory.duration")}, mouseX, mouseY, Colors.WHITE);
		if(!mc.thePlayer.capabilities.isCreativeMode)
			GuiUtils.buttonHoverMessage(this, mc, bgive, mouseX, mouseY, fontRendererObj, new String[]{I18n.format("gui.act.nocreative")}, Colors.RED);
	}
	public ItemStack getPotion(){
		String[] strs1=new String[tfs_ampl.length];int j=0;
		for (int i = 0; i < strs1.length; i++) {
			try {
				if(!(tfs_ampl[i].getText().isEmpty() && tfs_dura[i].getText().isEmpty())){
					int duration;int amplifier;
					if(!tfs_dura[i].getText().isEmpty()){
						duration=Integer.valueOf(tfs_dura[i].getText());
					}else{
						duration=600;
					}
					if(!tfs_ampl[i].getText().isEmpty()){
						amplifier=Integer.valueOf(tfs_ampl[i].getText());
					}else{
						amplifier=0;
					}
					strs1[j]=new Effect(list[i].id, duration, amplifier).getNBT();
					j++;
				}
			} catch (Exception e) {}
		}
		String[] strs=new String[j];
		for (int i = 0; i < strs.length; i++) {
			strs[i]=strs1[i];
		}
		if(item==null)
			if(Effect.itm_type.length>0 && Effect.itm_type[0]!=null){
				item=Effect.itm_type[0];
			} else {
				item=Items.POTIONITEM;
			}
		if(skin==null)
			if(Effect.psk_skin.length>0 && Effect.psk_skin[0]!=null){
				skin=Effect.psk_skin[0].Name;
			} else {
				skin="water";
			}
		String a=skin;
		String b="";if(name!=null)b="display:{Name:\""+name.replaceAll("&&", "\u00a7")+"\"},";
		String c=Effect.getAllNBT(strs);
		String str="{Potion:"+a+","+b+"CustomPotionEffects:"+c+"}";
		// System.out.println(ItemStackGenHelper.getNBT(new ItemStack(item),str).getTagCompound().toString());
		return ItemStackGenHelper.getNBT(new ItemStack(item),str);
	}
	public void initGui() {
		ix=0;
		iy=0;
		tfs_ampl=new GuiTextField[Effect.eff_effect.length];
		tfs_dura=new GuiTextField[Effect.eff_effect.length];
		for (int i = 0; i < tfs_ampl.length; i++) {
			tfs_ampl[i]=new GuiTextField(i, fontRendererObj, width/2-100+ix*200, 50+iy*21, 40, 20);
			tfs_dura[i]=new GuiTextField(i+Effect.eff_effect.length, fontRendererObj, width/2-56+ix*200, 50+iy*21, 40, 20);
			if(ix==1){
				ix=0;
				iy++;
			}else{ix++;}
		}
		for (int i = 0; i < amplList.length; i++) {
			if(amplList[i]!=null)
				tfs_ampl[i].setText(amplList[i]);
			if(duraList[i]!=null)
				tfs_dura[i].setText(duraList[i]);
		}
		buttonList.add(bmax=new GuiButton(1,width/2-100,26,99,20,I18n.format("gui.act.itemfactory.max")));
		buttonList.add(bremove=new GuiButton(2,width/2-200,26,99,20,I18n.format("gui.act.itemfactory.set0")));
		
		buttonList.add(bmaxlv=new GuiButton(3,width/2-100,5,99,20,I18n.format("gui.act.itemfactory.set100")+" "+I18n.format("gui.act.potionfactory.amplifier")+" 127"));
		buttonList.add(bmaxapl=new GuiButton(4,width/2-200,5,99,20,I18n.format("gui.act.itemfactory.set100")+" "+I18n.format("gui.act.potionfactory.duration")));
		
		buttonList.add(bskin=new GuiButton(5,width/2,5,99,20,I18n.format("gui.act.potionfactory.skin")));
		buttonList.add(badd=new GuiButton(5,width/2+100,5,99,20,I18n.format("gui.act.add")));
		
		buttonList.add(bgive=new GuiButton(5,width/2,26,99,20,I18n.format("gui.act.give")));
		buttonList.add(bdone =new GuiButton(6,width/2+100,26,99,20,I18n.format("gui.done")));

		super.initGui();
	}
}
