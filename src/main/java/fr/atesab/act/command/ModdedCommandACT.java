package fr.atesab.act.command;

import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.GuiMenu;
import net.minecraft.ChatFormatting;

public class ModdedCommandACT extends ModdedCommand {
    public final ModdedCommandOpenGiver SC_OPEN_GIVER;
    public final ModdedCommandOpenMenu SC_OPEN_MENU;
    public final ModdedCommandColor SC_COLOR;
    public final ModdedCommandEdit SC_EDIT;
    public final ModdedCommandEnchant SC_ENCHANT;
    public final ModdedCommandFormat SC_FORMAT;
    public final ModdedCommandGive SC_GIVE;
    public final ModdedCommandHelp SC_HELP;
    public final ModdedCommandRename SC_RENAME;
    public final ModdedCommandHead SC_HEAD;
    public final ModdedCommandRandomFireWorks SC_RANDOM_FIREWORKS;
    public final ModdedCommandInfo SC_INFO;
    public final ModdedCommandSpTp SC_SPTP;
    public final ModdedCommandUnbreakable SC_UNBREAKABLE;
    public final ModdedCommandPalette SC_PALETTE;
    public final ModdedCommandInstantClick SC_INSTANT_CLICK;
    public final ModdedCommandInstantPlace SC_INSTANT_PLACE;

    public ModdedCommandACT() {
        super(ACTMod.MOD_ID);
        registerDefaultSubCommand(
                SC_HELP = new ModdedCommandHelp(this, ACTMod.getModName() + " v" + ACTMod.getModVersion(),
                        ChatFormatting.YELLOW, ChatFormatting.GOLD, ChatFormatting.WHITE));
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
        registerSubCommand(SC_INFO = new ModdedCommandInfo());
        registerSubCommand(SC_SPTP = new ModdedCommandSpTp());
        registerSubCommand(SC_COLOR = new ModdedCommandColor());
        registerSubCommand(SC_UNBREAKABLE = new ModdedCommandUnbreakable());
        registerSubCommand(SC_PALETTE = new ModdedCommandPalette());
        registerSubCommand(SC_INSTANT_CLICK = new ModdedCommandInstantClick());
        registerSubCommand(SC_INSTANT_PLACE = new ModdedCommandInstantPlace());
    }
}
