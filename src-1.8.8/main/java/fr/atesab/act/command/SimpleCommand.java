package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class SimpleCommand implements ICommand {
	private String name;
	private String usage;
	private List<String> aliases;
	private Consumer<String[]> function;

	public SimpleCommand(String name, String usage, Consumer<String[]> function, String... aliases) {
		this.name = name;
		this.usage = usage;
		this.aliases = Arrays.asList(aliases);
		this.function = function;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public int compareTo(ICommand o) {
		return o.getCommandName().compareToIgnoreCase(getCommandName());
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		function.accept(args);
	}

	@Override
	public List<String> getCommandAliases() {
		return aliases;
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos targetPos) {
		return new ArrayList<>();
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return usage;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

}
