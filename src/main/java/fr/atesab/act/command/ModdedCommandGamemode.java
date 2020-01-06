package fr.atesab.act.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.world.GameType;

public class ModdedCommandGamemode extends ModdedCommand {

	public ModdedCommandGamemode(String name) {
		super(name);
	}

	public ModdedCommandGamemode(String name, String description, CommandClickOption clickOption) {
		super(name, description, clickOption);
	}

	public ModdedCommandGamemode(String name, String description, CommandClickOption clickOption,
			boolean displayInHelp) {
		super(name, description, clickOption, displayInHelp);
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		// /gm <gamemode>
		for (GameType gametype : GameType.values())
			if (gametype != GameType.NOT_SET) {
				command.then(Commands.literal(gametype.getName()).executes(c -> {
					Minecraft.getInstance().player.sendChatMessage("/gamemode " + gametype.getName().toLowerCase());
					return 0;
				}));
			}
		// /gm 0,1,2,3
		return command.then(Commands
				.argument("gamemodeid", IntegerArgumentType.integer(0, GameType.values().length - 2)).executes(c -> {
					Minecraft.getInstance().player.sendChatMessage(
							"/gamemode " + GameType.values()[1 + IntegerArgumentType.getInteger(c, "gamemodeid")]);
					return 0;
				}));
	}

}
