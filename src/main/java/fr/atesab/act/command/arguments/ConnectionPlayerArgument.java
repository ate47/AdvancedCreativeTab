package fr.atesab.act.command.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import fr.atesab.act.ACTMod;
import fr.atesab.act.utils.ACTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

public class ConnectionPlayerArgument implements ArgumentType<PlayerInfo[]> {
    public static final DynamicCommandExceptionType ERROR_UNKNOW_PLAYER = new DynamicCommandExceptionType(
            name -> new TranslatableComponent("cmd.act.argument.player.unknown", name));

    public static ConnectionPlayerArgument player() {
        return new ConnectionPlayerArgument(false);
    }

    public static ConnectionPlayerArgument players() {
        return new ConnectionPlayerArgument(true);
    }

    public static PlayerInfo getPlayer(CommandContext<CommandSourceStack> context, String name)
            throws CommandSyntaxException {
        return context.getArgument(name, PlayerInfo[].class)[0];
    }

    public static PlayerInfo[] getPlayers(CommandContext<CommandSourceStack> context, String name)
            throws CommandSyntaxException {
        return context.getArgument(name, PlayerInfo[].class);
    }

    private boolean multiple;

    private ConnectionPlayerArgument(boolean multiple) {
        this.multiple = multiple;
    }

    @Override
    public PlayerInfo[] parse(StringReader reader) throws CommandSyntaxException {
        if (multiple) {
            String end = reader.getRemaining();
            reader.setCursor(reader.getTotalLength());
            var playerNames = end.split(" ");
            var players = new PlayerInfo[playerNames.length];
            for (int i = 0; i < playerNames.length; i++) {
                var name = playerNames[i];
                players[i] = Minecraft.getInstance().getConnection().getPlayerInfo(name);

                if (players[i] == null)
                    throw ERROR_UNKNOW_PLAYER.createWithContext(reader, name);
            }
            return players;

        } else {
            var playerName = reader.readString();
            var info = Minecraft.getInstance().getConnection().getPlayerInfo(playerName);

            if (info == null)
                throw ERROR_UNKNOW_PLAYER.createWithContext(reader, playerName);

            return new PlayerInfo[] { info };
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (var info : Minecraft.getInstance().getConnection().getOnlinePlayers())
            builder.suggest(info.getProfile().getName());
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return ACTUtils.applyAndGet(new ArrayList<>(), e -> {
			e.add(Minecraft.getInstance().getUser().getName());
			e.add("Notch");
			e.addAll(Arrays.asList(ACTMod.MOD_AUTHORS_ARRAY));
		};
    }
}
