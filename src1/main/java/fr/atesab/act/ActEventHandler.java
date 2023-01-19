package fr.atesab.act;

import fr.atesab.act.gui.GuiColorSelector;
import fr.atesab.act.gui.GuiMenu;
import fr.atesab.act.gui.GuiNbtCode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.RegistryEvent;

public class ActEventHandler {
	public static boolean tryChangingGameMode = false;

	public ActEventHandler() {
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (ModMain.guifactory.isPressed())
			FMLClientHandler.instance().showGuiScreen(new GuiMenu(null));
		if (ModMain.nbtitem.isPressed())
			FMLClientHandler.instance().showGuiScreen(new GuiNbtCode(null));
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(ModMain.fakeItem = (Item) new FakeItems().setRegistryName("fakeItem"));
		event.getRegistry().register(ModMain.fakeItem2 = (Item) new FakeItems2().setRegistryName("fakeItem2"));
		event.getRegistry().register(ModMain.fakeItem3 = (Item) new FakeItems3().setRegistryName("fakeItem3"));
	}
}
