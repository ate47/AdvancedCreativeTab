package fr.atesab.act.command.arguments;

import java.util.ArrayList;
import java.util.Arrays;

import com.mojang.brigadier.context.CommandContext;

import fr.atesab.act.ACTMod;
import fr.atesab.act.utils.ACTUtils;
import net.minecraft.client.Minecraft;

public class PlayerListArgumentType extends StringListArgumentType {

	public PlayerListArgumentType() {
		super(() -> {
			var l = new ArrayList<String>();
			Minecraft.getInstance().getConnection().getOnlinePlayers().forEach(p -> l.add(p.getProfile().getName()));
			var n = Minecraft.getInstance().getUser().getName();
			if (!l.contains(n))
				l.add(n); // add our username if not here
			return l;
		}, ACTUtils.applyAndGet(new ArrayList<>(), e -> {
			e.add(Minecraft.getInstance().getUser().getName());
			e.add("Notch");
			e.addAll(Arrays.asList(ACTMod.MOD_AUTHORS_ARRAY));
		}), true);
	}

	public static PlayerListArgumentType playerList() {
		return new PlayerListArgumentType();
	}

	public static String[] getPlayerList(final CommandContext<?> context, final String name) {
		return getStringList(context, name);
	}
}
