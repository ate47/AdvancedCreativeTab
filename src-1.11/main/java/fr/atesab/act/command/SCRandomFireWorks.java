package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.command.node.MainCommand;
import fr.atesab.act.command.node.SubCommand;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;

public class SCRandomFireWorks extends SubCommand {
	private List<String> aliases;

	public SCRandomFireWorks() {
		super("randomfireworks", "cmd.act.rfw", CommandClickOption.doCommand);
		aliases = new ArrayList<>();
		aliases.add("rfw");
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return new ArrayList<>();
	}

	@Override
	public String getDescription() {
		return I18n.format(super.getDescription());
	}

	@Override
	public List<String> getAlias() {
		return aliases;
	}

	@Override
	public String getSubCommandUsage(ICommandSender sender) {
		return getName();
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException {
		ItemUtils.give(ItemUtils.getRandomFireworks());
	}

}
