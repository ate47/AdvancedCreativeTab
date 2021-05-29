package fr.atesab.act.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;

public class ModdedCommandGive extends ModdedCommand {
	public ModdedCommandGive() {
		super("give", "cmd.act.give", CommandClickOption.suggestCommand, true);
		addAlias("g");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		return command.then(Commands.argument("giveoption", ItemArgument.item())
				.then(Commands.argument("givecount", IntegerArgumentType.integer()).executes(c -> {
					ItemUtils.give(ItemArgument.getItem(c, "giveoption")
							.createItemStack(IntegerArgumentType.getInteger(c, "givecount"), false));
					return 0;
				})).executes(c -> {
					ItemUtils.give(ItemArgument.getItem(c, "giveoption").createItemStack(1, false));
					return 0;
				}));
	}

}
