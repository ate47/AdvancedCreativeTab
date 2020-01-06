package fr.atesab.act.command;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.GuiMenu;
import net.minecraft.util.text.TextFormatting;

public class ModdedCommandACT extends ModdedCommand {
	public final ModdedCommandOpenGiver SC_OPEN_GIVER;
	public final ModdedCommandOpenMenu SC_OPEN_MENU;
	public final ModdedCommandEdit SC_EDIT;
	public final ModdedCommandEnchant SC_ENCHANT;
	public final ModdedCommandFormat SC_FORMAT;
	public final ModdedCommandGive SC_GIVE;
	public final ModdedCommandHelp SC_HELP;
	public final ModdedCommandRename SC_RENAME;
	public final ModdedCommandHead SC_HEAD;
	public final ModdedCommandRandomFireWorks SC_RANDOM_FIREWORKS;

	public ModdedCommandACT() {
		super(ACTMod.MOD_ID);
		registerDefaultSubCommand(
				SC_HELP = new ModdedCommandHelp(this, ACTMod.MOD_LITTLE_NAME + " v" + ACTMod.MOD_VERSION,
						TextFormatting.YELLOW, TextFormatting.GOLD, TextFormatting.WHITE));
		registerSubCommand(SC_EDIT = new ModdedCommandEdit());
		registerSubCommand(SC_ENCHANT = new ModdedCommandEnchant());
		registerSubCommand(SC_FORMAT = new ModdedCommandFormat());
		registerSubCommand(SC_GIVE = new ModdedCommandGive());
		registerSubCommand(SC_HEAD = new ModdedCommandHead());
		registerSubCommand(SC_OPEN_GIVER = new ModdedCommandOpenGiver());
		registerSubCommand((SC_OPEN_MENU = new ModdedCommandOpenMenu("menu", "cmd.act.menu", () -> new GuiMenu(null)))
				.addAlias("om"));
		registerSubCommand(SC_RANDOM_FIREWORKS = new ModdedCommandRandomFireWorks());
		registerSubCommand(SC_RENAME = new ModdedCommandRename());
	}
}
