package fr.atesab.act.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import fr.atesab.act.ACTMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.registry.IRegistry;
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
		IRegistry.field_212630_s.getKeys().forEach(rl -> items.add(rl.toString())); // Item.REGISTRY
		return items;
	}

	/**
	 * Get all visible players in tab
	 * 
	 * @return players' names
	 * @since 2.0
	 */
	public static List<String> getPlayerList() {
		List<NetworkPlayerInfo> networkPlayerInfos = new ArrayList<NetworkPlayerInfo>(
				Minecraft.getInstance().player.connection.getPlayerInfoMap());
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

	/**
	 * Get all sub completion argument
	 * 
	 * @param options
	 *            Possible sub arguments
	 * @param args
	 *            command arguments
	 * @return List of string who match sorted by name
	 * @since 2.0
	 */
	@Deprecated
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

	/**
	 * Send a message in chat like with the client (Client command will be execute)
	 * 
	 * @param message
	 *            chat message to send
	 * @since 2.0
	 */
	public static void sendMessage(String message) {
		EntityPlayerSP p;
		if ((p = Minecraft.getInstance().player) != null) {
			if (ForgeEventFactory.onClientSendMessage(message).isEmpty())
				return;
			p.sendChatMessage(message);
		}
	}

}
