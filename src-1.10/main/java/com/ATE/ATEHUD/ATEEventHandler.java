package com.ATE.ATEHUD;

import com.ATE.ATEHUD.gui.GuiColorSelector;
import com.ATE.ATEHUD.gui.GuiMenu;
import com.ATE.ATEHUD.gui.GuiNbtCode;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ATEEventHandler {
	public static boolean tryChangingGameMode=false;
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(ModMain.guifactory.isPressed())FMLClientHandler.instance().showGuiScreen(new  GuiMenu());
        if(ModMain.nbtitem.isPressed())FMLClientHandler.instance().showGuiScreen(new  GuiNbtCode());
    }
}