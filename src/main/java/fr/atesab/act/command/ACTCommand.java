package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.ACTMod;
import fr.atesab.act.command.node.MainCommand;
import fr.atesab.act.command.node.SubCommand;
import fr.atesab.act.gui.GuiMenu;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class ACTCommand extends MainCommand {
	public final SCOpenGiver SC_OPEN_GIVER;
	public final SCOpenMenu SC_OPEN_MENU;
	public final SCEdit SC_EDIT;
	public final SCGive SC_GIVE;
	public final SCHelp SC_HELP;
	public final SCRename SC_RENAME;
	public final SCHead SC_HEAD;
	public final SCRandomFireWorks SC_RANDOM_FIREWORKS;

	public ACTCommand() {
		super(new ArrayList<SubCommand>(), "help");
		subCommands.add(SC_HELP = new SCHelp(ACTMod.MOD_LITTLE_NAME + " v" + ACTMod.MOD_VERSION, 10));
		subCommands.add(SC_GIVE = new SCGive());
		subCommands.add(SC_OPEN_GIVER = new SCOpenGiver());
		subCommands.add(SC_EDIT = new SCEdit());
		subCommands.add(SC_OPEN_MENU = new SCOpenMenu("menu", "cmd.act.menu", args -> new GuiMenu(null)));
		subCommands.add(SC_RENAME = new SCRename());
		subCommands.add(SC_HEAD = new SCHead());
		subCommands.add(SC_RANDOM_FIREWORKS = new SCRandomFireWorks());
		sort();
	}

	@Override
	public List<String> getCommandAliases() {
		return new ArrayList<String>();
	}

	@Override
	public String getCommandName() {
		return "act";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return getCommandName();
	}

}
