package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.command.arguments.PlayerListArgumentType;
import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ModdedCommandHead extends ModdedCommand {

	public ModdedCommandHead() {
		super("head", "cmd.act.head", CommandClickOption.suggestCommand);
		addAlias("h");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		return command.then(Commands.argument("players", PlayerListArgumentType.playerList()).executes(c -> {
			try {
				ItemUtils.give(ItemUtils.getHeads(PlayerListArgumentType.getPlayerList(c, "players")));
			} catch (Exception e) {
				ChatUtils.error(e.getClass().getName() + ": " + e.getMessage());
			}
			return 0;
		}));
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return c -> {
			try {
				ItemUtils.getHeads(Minecraft.getInstance().getUser().getName()).forEach(ItemUtils::give);
			} catch (Exception e) {
				ChatUtils.error(e.getClass().getName() + ": " + e.getMessage());
			}
			return 0;
		};
	}

}
