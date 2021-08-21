package fr.atesab.act.command;

import java.util.Arrays;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.gui.modifier.GuiColorModifier;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ModdedCommandColor extends ModdedCommand {

    public ModdedCommandColor() {
        super("color", "cmd.act.color", CommandClickOption.doCommand, true);
        registerDefaultSubCommand(new ModdedCommand("info", "cmd.act.color.info", CommandClickOption.doCommand) {
            @Override
            protected Command<CommandSourceStack> onNoArgument() {
                return c -> {
                    var mc = Minecraft.getInstance();
                    var is = mc.player.getMainHandItem();
                    // try if we can color it
                    if (!ItemUtils.canGlobalColorIt(is)) {
                        c.getSource().sendFailure(new TranslatableComponent("cmd.act.color.error.notcolorable")
                                .withStyle(ChatFormatting.RED));
                        return 1;
                    }

                    // we have a colorable item
                    var color = ItemUtils.getGlobalColor(is);
                    if (!color.isPresent()) {
                        c.getSource().sendSuccess(new TranslatableComponent("cmd.act.color.color")
                                .withStyle(ChatFormatting.YELLOW).append(":").withStyle(ChatFormatting.DARK_GRAY)
                                .append(new TranslatableComponent("cmd.act.color.error.nocolor")
                                        .withStyle(ChatFormatting.WHITE)),
                                false);
                    } else {
                        c.getSource().sendSuccess(new TranslatableComponent("cmd.act.color.color")
                                .withStyle(ChatFormatting.YELLOW).append(":").withStyle(ChatFormatting.DARK_GRAY)
                                .append(new TextComponent("\u2589\u2589\u2589\u2589")
                                        .withStyle(s -> s.withColor(TextColor.fromRgb(color.getAsInt()))))
                                .append(" ")
                                // remove color button
                                .append(new TextComponent("[").withStyle(ChatFormatting.RED)
                                        .withStyle(s -> s
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new TranslatableComponent("cmd.act.color.remove.hover")
                                                                .withStyle(ChatFormatting.YELLOW)))
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                        "/act " + getName() + " remove")))
                                        .append(new TranslatableComponent("cmd.act.color.remove")).append("]")),
                                false);
                    }

                    c.getSource()
                            .sendSuccess(
                                    new TextComponent(
                                            "[").withStyle(ChatFormatting.WHITE)
                                                    .withStyle(s -> s
                                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                    new TranslatableComponent(
                                                                            "cmd.act.color.picker.hover")
                                                                                    .withStyle(ChatFormatting.YELLOW)))
                                                            .withClickEvent(
                                                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                            "/act " + getName() + " picker")))
                                                    .append(new TranslatableComponent("cmd.act.color.picker"))
                                                    .append("]"),
                                    false);

                    return 2;
                };
            }
        });
        registerDefaultSubCommand(
                new ModdedCommandHelp(this, "color", ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.WHITE));
        registerSubCommand(new ModdedCommand("picker", "cmd.act.color.picker", CommandClickOption.doCommand) {
            @Override
            protected Command<CommandSourceStack> onNoArgument() {
                return c -> {
                    var mc = Minecraft.getInstance();
                    var is = mc.player.getMainHandItem();
                    // try if we can color it
                    if (!ItemUtils.canGlobalColorIt(is)) {
                        c.getSource().sendFailure(new TranslatableComponent("cmd.act.color.error.notcolorable")
                                .withStyle(ChatFormatting.RED));
                        return 1;
                    }

                    var color = ItemUtils.getGlobalColor(is);
                    var defaultColor = ItemUtils.getDefaultGlobalColor(is);

                    GuiUtils.displayScreen(new GuiColorModifier(null, newColor -> {
                        if (newColor.isPresent() || (newColor.getAsInt() & 0xFF000000) == 0) {
                            ItemUtils.give(ItemUtils.removeColor(is), 36 + mc.player.getInventory().selected);
                        } else {
                            ItemUtils.give(ItemUtils.setGlobalColor(is, newColor.getAsInt()),
                                    36 + mc.player.getInventory().selected);
                        }
                    }, color, defaultColor.orElse(0), !defaultColor.isPresent()));

                    return 1;
                };
            }
        });

        registerSubCommand(new ModdedCommand("set", "cmd.act.color.set", CommandClickOption.suggestCommand) {
            {
                registerDefaultSubCommand(new ModdedCommandHelp(this, "set", ChatFormatting.GOLD, ChatFormatting.YELLOW,
                        ChatFormatting.WHITE));
                registerSubCommand(
                        new ModdedCommand("rgb", "cmd.act.color.set.rgb", CommandClickOption.suggestCommand) {
                            @Override
                            protected LiteralArgumentBuilder<CommandSourceStack> onArgument(
                                    LiteralArgumentBuilder<CommandSourceStack> command) {
                                return command.then(Commands.argument("red", IntegerArgumentType.integer(0, 0xFF)).then(
                                        Commands.argument("green", IntegerArgumentType.integer(0, 0xFF)).then(Commands
                                                .argument("blue", IntegerArgumentType.integer(0, 0xFF)).executes(c -> {
                                                    var r = IntegerArgumentType.getInteger(c, "red");
                                                    var g = IntegerArgumentType.getInteger(c, "green");
                                                    var b = IntegerArgumentType.getInteger(c, "blue");
                                                    var rgb = GuiUtils.asRGBA(r, g, b, 0xFF);

                                                    var mc = Minecraft.getInstance();
                                                    var is = mc.player.getMainHandItem();
                                                    ItemUtils.give(ItemUtils.setGlobalColor(is, rgb),
                                                            36 + mc.player.getInventory().selected);
                                                    return 1;
                                                }))));
                            }
                        });
                registerSubCommand(
                        new ModdedCommand("hsl", "cmd.act.color.set.hsl", CommandClickOption.suggestCommand) {
                            @Override
                            protected LiteralArgumentBuilder<CommandSourceStack> onArgument(
                                    LiteralArgumentBuilder<CommandSourceStack> command) {
                                return command.then(Commands.argument("hue", IntegerArgumentType.integer(0, 359))
                                        .then(Commands.argument("saturation", IntegerArgumentType.integer(0, 100))
                                                .then(Commands
                                                        .argument("lightness", IntegerArgumentType.integer(0, 100))
                                                        .executes(c -> {
                                                            var h = IntegerArgumentType.getInteger(c, "hue");
                                                            var s = IntegerArgumentType.getInteger(c, "saturation");
                                                            var l = IntegerArgumentType.getInteger(c, "lightness");
                                                            var rgb = GuiUtils.fromHSL(h, s, l);

                                                            var mc = Minecraft.getInstance();
                                                            var is = mc.player.getMainHandItem();
                                                            ItemUtils.give(ItemUtils.setGlobalColor(is, rgb),
                                                                    36 + mc.player.getInventory().selected);
                                                            return 1;
                                                        }))));
                            }
                        });
                registerSubCommand(
                        new ModdedCommand("hex", "cmd.act.color.set.hex", CommandClickOption.suggestCommand) {
                            @Override
                            protected LiteralArgumentBuilder<CommandSourceStack> onArgument(
                                    LiteralArgumentBuilder<CommandSourceStack> command) {
                                return command
                                        .then(Commands.argument("hexcode", StringArgumentType.word()).executes(c -> {
                                            var h = StringArgumentType.getString(c, "hexcode");
                                            int rgb;
                                            try {
                                                rgb = Integer.valueOf(h, 16);
                                            } catch (NumberFormatException r) {
                                                c.getSource().sendFailure(
                                                        new TranslatableComponent("cmd.act.color.error.valid")
                                                                .withStyle(ChatFormatting.RED));
                                                return 1;
                                            }

                                            var mc = Minecraft.getInstance();
                                            var is = mc.player.getMainHandItem();
                                            ItemUtils.give(ItemUtils.setGlobalColor(is, rgb),
                                                    36 + mc.player.getInventory().selected);
                                            return 1;
                                        }));
                            }
                        });

                Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).forEach(t -> {
                    var name = t.getName().toLowerCase();
                    var rgb = t.getColor();
                    registerSubCommand(new ModdedCommand(name) {
                        @Override
                        protected Command<CommandSourceStack> onNoArgument() {
                            return c -> {
                                var mc = Minecraft.getInstance();
                                var is = mc.player.getMainHandItem();
                                ItemUtils.give(ItemUtils.setGlobalColor(is, rgb),
                                        36 + mc.player.getInventory().selected);
                                return 1;
                            };
                        }
                    });
                });
            }
        });
    }
}
