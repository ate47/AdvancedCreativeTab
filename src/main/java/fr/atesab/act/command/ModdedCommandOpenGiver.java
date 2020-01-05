package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.gui.GuiGiver;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.EnumHand;

public class ModdedCommandOpenGiver extends ModdedCommand {

	public ModdedCommandOpenGiver() {
		super("opengiver", "cmd.act.opengiver", CommandClickOption.doCommand, true);
		addAlias("og");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> onArgument(LiteralArgumentBuilder<CommandSource> command) {
		return command.then(Commands.argument("giveroptions", StringArgumentType.greedyString()).executes(c -> {
			GuiUtils.displayScreen(new GuiGiver(null, StringArgumentType.getString(c, "giveroptions")));
			return 0;
		}));
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return c -> {
			GuiUtils.displayScreen(new GuiGiver(null,
					ItemUtils.getGiveCode(Minecraft.getInstance().player.getHeldItem(EnumHand.MAIN_HAND))));
			return 0;
		};
	}

}
