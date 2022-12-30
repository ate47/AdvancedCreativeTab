package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.GameType;

public class ModdedCommandGamemodeQuick extends ModdedCommand {
    private final GameType gamemode;

    public ModdedCommandGamemodeQuick(String name, GameType gamemode) {
        super(name);
        this.gamemode = gamemode;
    }

    public ModdedCommandGamemodeQuick(String name, String description, boolean displayInHelp, GameType gamemode) {
        super(name, description, CommandClickOption.doCommand, displayInHelp);
        this.gamemode = gamemode;
    }

    @Override
    protected Command<CommandSourceStack> onNoArgument() {
        return c -> {
            sendSigned("/gamemode " + gamemode.getName());
            return 0;
        };
    }

}
