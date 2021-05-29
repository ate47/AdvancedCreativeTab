package fr.atesab.act.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import fr.atesab.act.ACTMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * A set of tools to help to create commands
 * 
 * @author ATE47
 * @since 2.0
 */
public class CommandUtils {
	private static final Random RANDOM = ACTMod.RANDOM;

	public static <T> T getRandomElement(T[] array) {
		return array[RANDOM.nextInt(array.length)];
	}

	/**
	 * Get registry name of items
	 * 
	 * @return items' names
	 * @since 2.0
	 */
	@SuppressWarnings("deprecation")
	public static List<String> getItemList() {
		List<String> items = new ArrayList<>();
		Registry.ITEM.keySet().forEach(rl -> items.add(rl.toString()));
		return items;
	}

	/**
	 * Get all visible players in tab
	 * 
	 * @return players' names
	 * @since 2.0
	 */
	public static List<String> getPlayerList() {
		List<NetworkPlayerInfo> networkPlayerInfos = new ArrayList<>(
				Minecraft.getInstance().player.connection.getOnlinePlayers());
		networkPlayerInfos.sort((o1, o2) -> o1.getGameMode().getName().compareToIgnoreCase(o2.getGameMode().getName()));
		List<String> players = new ArrayList<String>();
		for (NetworkPlayerInfo info : networkPlayerInfos)
			players.add(info.getGameMode().getName());
		return players;
	}

	/**
	 * Get all sub completion argument
	 * 
	 * @param options Possible sub arguments
	 * @param args    command arguments
	 * @return List of string who match sorted by name
	 * @since 2.0
	 * @deprecated 
	 */
	@Deprecated
	public static List<String> getTabCompletion(List<String> options, String[] args) {
		List<String> optionsEnd = new ArrayList<>();
		if (args.length == 0)
			return optionsEnd;
		String start = args[args.length - 1].toLowerCase();
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).toLowerCase().startsWith(start.toLowerCase()))
				optionsEnd.add(options.get(i));
		}
		optionsEnd.sort(String::compareToIgnoreCase); // sort by name
		return optionsEnd;
	}

	/**
	 * Send a message in chat like with the client (Client command will be execute)
	 * 
	 * @param message chat message to send
	 * @since 2.0
	 */
	public static void sendMessage(String message) {
		ClientPlayerEntity p;
		if ((p = Minecraft.getInstance().player) != null) {
			if (ForgeEventFactory.onClientSendMessage(message).isEmpty())
				return;
			p.chat(message);
		}
	}

}
