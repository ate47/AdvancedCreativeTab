package fr.atesab.act.command;

import com.mojang.brigadier.Command;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.world.GameType;

public class ModdedCommandGamemodeQuick extends ModdedCommand {
	private GameType gamemode;

	public ModdedCommandGamemodeQuick(String name, GameType gamemode) {
		super(name);
		this.gamemode = gamemode;
	}

	public ModdedCommandGamemodeQuick(String name, String description, boolean displayInHelp, GameType gamemode) {
		super(name, description, CommandClickOption.doCommand, displayInHelp);
		this.gamemode = gamemode;
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return c -> {
			Minecraft.getInstance().player.sendChatMessage("/gamemode " + gamemode.getName());
			return 0;
		};
	}

}
