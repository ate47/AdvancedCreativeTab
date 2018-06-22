package fr.atesab.act.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;

public class CommandUtils {

	public static List<String> getItemList() {
		ArrayList<String> items = new ArrayList<String>();
		for (ResourceLocation name : Item.REGISTRY.getKeys())
			items.add(name.toString());
		return items;
	}

	public static List<String> getPlayerList() {
		List<NetworkPlayerInfo> networkPlayerInfos = new ArrayList<NetworkPlayerInfo>(
				Minecraft.getMinecraft().player.connection.getPlayerInfoMap());
		networkPlayerInfos.sort(new Comparator<NetworkPlayerInfo>() {
			public int compare(NetworkPlayerInfo o1, NetworkPlayerInfo o2) {
				return o1.getGameProfile().getName().compareToIgnoreCase(o2.getGameProfile().getName());
			}
		});
		List<String> players = new ArrayList<String>();
		for (NetworkPlayerInfo info : networkPlayerInfos)
			players.add(info.getGameProfile().getName());
		return players;
	}

	public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
		return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText()
				: ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(),
						networkPlayerInfoIn.getGameProfile().getName());
	}

	public static List<String> getTabCompletion(List<String> options, String[] args) {
		List<String> options_End = new ArrayList<String>();
		if (args.length == 0)
			return options_End;
		String start = args[args.length - 1].toLowerCase();
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).toLowerCase().startsWith(start.toLowerCase()))
				options_End.add(options.get(i));
		}
		options_End.sort(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		}); // sort by name
		return options_End;
	}

	public static void sendMessage(String message) {
		EntityPlayerSP p;
		if ((p = Minecraft.getMinecraft().player) != null) {
			if (ClientCommandHandler.instance.executeCommand(p, message) != 0)
				return;
			p.sendChatMessage(message);
		}
	}
}
