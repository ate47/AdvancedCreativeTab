package fr.atesab.act.command.arguments;

import java.util.Arrays;
import java.util.Collections;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.client.Minecraft;

public class PlayerListArgumentType extends StringListArgumentType {

	public PlayerListArgumentType() {
		super(Collections.emptyList(),
				Arrays.asList(Minecraft.getInstance().getSession().getUsername(), "Notch", "ATE47"), true);
	}

	public static PlayerListArgumentType playerList() {
		return new PlayerListArgumentType();
	}

	public static String[] getPlayerList(final CommandContext<?> context, final String name) {
		return getStringList(context, name);
	}
}
