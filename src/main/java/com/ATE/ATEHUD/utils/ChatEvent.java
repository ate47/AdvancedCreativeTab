package com.ATE.ATEHUD.utils;
import java.util.List;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChatEvent extends Event
{
	private IChatComponent component;
	private List<ChatLine> chatLines;
	
	public ChatEvent(IChatComponent component, List<ChatLine> chatLines)
	{
		this.component = component;
		this.chatLines = chatLines;
	}
	
	public IChatComponent getComponent()
	{
		return component;
	}
	
	public void setComponent(IChatComponent component)
	{
		this.component = component;
	}
	
	public List<ChatLine> getChatLines()
	{
		return chatLines;
	}
}