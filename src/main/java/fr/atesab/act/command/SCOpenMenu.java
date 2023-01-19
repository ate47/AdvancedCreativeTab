package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import fr.atesab.act.command.node.MainCommand;
import fr.atesab.act.command.node.SubCommand;
import fr.atesab.act.utils.GuiUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class SCOpenMenu extends SubCommand {
	private Function<String[], GuiScreen> menu;

	public SCOpenMenu(String name, String description, Function<String[], GuiScreen> menu) {
		super(name, description, CommandClickOption.doCommand);
		this.menu = menu;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return new ArrayList<>();
	}

	@Override
	public List<String> getAlias() {
		return new ArrayList<>();
	}

	@Override
	public String getDescription() {
		return I18n.format(super.getDescription());
	}

	@Override
	public String getSubCommandUsage(ICommandSender sender) {
		return getName();
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException {
		GuiUtils.displayScreen(menu.apply(args));
	}

}
