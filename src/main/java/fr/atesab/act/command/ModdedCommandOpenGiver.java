package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.gui.GuiGiver;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.InteractionHand;

public class ModdedCommandOpenGiver extends ModdedCommand {

	public ModdedCommandOpenGiver() {
		super("opengiver", "cmd.act.opengiver", CommandClickOption.doCommand, true);
		addAlias("og");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSourceStack> onArgument(
			LiteralArgumentBuilder<CommandSourceStack> command) {
		return command.then(Commands.argument("giveroptions", StringArgumentType.greedyString()).executes(c -> {
			GuiUtils.displayScreen(new GuiGiver(null, StringArgumentType.getString(c, "giveroptions")));
			return 0;
		}));
	}

	@Override
	protected Command<CommandSourceStack> onNoArgument() {
		return c -> {
			GuiUtils.displayScreen(new GuiGiver(null,
					ItemUtils.getGiveCode(Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND))));
			return 0;
		};
	}

}
