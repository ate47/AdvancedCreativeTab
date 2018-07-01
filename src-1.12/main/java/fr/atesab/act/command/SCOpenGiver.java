package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.command.node.MainCommand;
import fr.atesab.act.command.node.SubCommand;
import fr.atesab.act.gui.GuiGiver;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class SCOpenGiver extends SubCommand {

	public SCOpenGiver() {
		super("opengiver", "", CommandClickOption.doCommand, true);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return new ArrayList<String>();
	}

	@Override
	public List<String> getAlias() {
		return new ArrayList<String>();
	}

	@Override
	public String getDescription() {
		return I18n.format("cmd.act.opengiver");
	}

	@Override
	public String getSubCommandUsage(ICommandSender sender) {
		return getName() + " [" + I18n.format("cmd.act.ui.data") + "]";
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException {
		GuiUtils.displayScreen(new GuiGiver(null, args.length > 0 ? CommandBase.buildString(args, 0)
				: ItemUtils.getGiveCode(Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND))));
	}

}
