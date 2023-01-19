package fr.atesab.act.command.node;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;

public abstract class SubCommand {
	public static enum CommandClickOption {
		doCommand, suggestCommand;
	}

	private String name;
	private String description;
	private CommandClickOption clickOption;

	private boolean displayInHelp;

	public SubCommand(String name, String description, CommandClickOption clickOption) {
		this(name, description, clickOption, true);
	}

	public SubCommand(String name, String description, CommandClickOption clickOption, boolean displayInHelp) {
		this.name = name;
		this.description = description;
		this.clickOption = clickOption;
		this.displayInHelp = displayInHelp;
	}

	public abstract List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos);

	public int compareTo(SubCommand sub) {
		return name.compareToIgnoreCase(sub.getName());
	}

	public abstract List<String> getAlias();

	public CommandClickOption getClickOption() {
		return clickOption;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public abstract String getSubCommandUsage(ICommandSender sender);

	public boolean isDisplayInHelp() {
		return displayInHelp;
	}

	public abstract void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException;
}
