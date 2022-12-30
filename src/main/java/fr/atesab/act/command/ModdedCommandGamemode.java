package fr.atesab.act.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.GameType;

public class ModdedCommandGamemode extends ModdedCommand {

    public ModdedCommandGamemode(String name) {
        super(name);
    }

    public ModdedCommandGamemode(String name, String description, CommandClickOption clickOption) {
        super(name, description, clickOption);
    }

    public ModdedCommandGamemode(String name, String description, CommandClickOption clickOption,
                                 boolean displayInHelp) {
        super(name, description, clickOption, displayInHelp);
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> onArgument(
            LiteralArgumentBuilder<CommandSourceStack> command) {
        // /gm <gamemode>
        for (GameType gametype : GameType.values())
            command.then(Commands.literal(gametype.getName()).executes(c -> {
                sendSigned("/gamemode " + gametype.getName().toLowerCase());
                return 0;
            }));
        // /gm 0,1,2,3
        return command.then(Commands
                .argument("gamemodeid", IntegerArgumentType.integer(0, GameType.values().length - 2)).executes(c -> {
                    sendSigned("/gamemode " + GameType.values()[1 + IntegerArgumentType.getInteger(c, "gamemodeid")]);
                    return 0;
                }));
    }

}
