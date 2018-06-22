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
	public final SCOpenGiver openGiver;
	public final SCEdit edit;

	public ACTCommand() {
		super(new ArrayList<SubCommand>(), "help");
		subCommands.add(new SCHelp(ACTMod.MOD_LITTLE_NAME + " v" + ACTMod.MOD_VERSION, 10));
		subCommands.add(new SCGive());
		subCommands.add(openGiver = new SCOpenGiver());
		subCommands.add(edit = new SCEdit());
		subCommands.add(new SCOpenMenu("menu", "cmd.act.menu", args -> new GuiMenu(null)));
		subCommands.add(new SCRename());
		subCommands.add(new SCHead());
		sort();
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getAliases() {
		return new ArrayList<String>();
	}

	@Override
	public String getName() {
		return "act";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName();
	}

}
