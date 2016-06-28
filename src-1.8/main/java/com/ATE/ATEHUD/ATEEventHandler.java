package com.ATE.ATEHUD;

import com.ATE.ATEHUD.gui.ATEModGuiColorSelector;
import com.ATE.ATEHUD.gui.ATEModGuiMenu;
import com.ATE.ATEHUD.gui.ATEModGuiNbtCode;
import com.ATE.ATEHUD.utils.ChatEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class ATEEventHandler {
	public static boolean tryChangingGameMode=false;
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(ModMain.guifactory.isPressed())FMLClientHandler.instance().showGuiScreen(new  ATEModGuiMenu());
        if(ModMain.nbtitem.isPressed())FMLClientHandler.instance().showGuiScreen(new  ATEModGuiNbtCode());
    }
}