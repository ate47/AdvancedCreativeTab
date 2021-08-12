package fr.atesab.act.command;

import com.mojang.brigadier.Command;

import fr.atesab.act.ACTMod;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;

public class ModdedCommandInfo extends ModdedCommand {

	public ModdedCommandInfo() {
		super("info", "cmd.act.info", CommandClickOption.doCommand);
		addAlias("information");
	}

	@Override
	protected Command<CommandSourceStack> onNoArgument() {
		return c -> {
			CommandSourceStack src = c.getSource();
			src.sendSuccess(new TranslatableComponent("cmd.act.info.title").withStyle(ChatFormatting.GOLD)
					.append(new TextComponent(": ").withStyle(ChatFormatting.DARK_GRAY))
					.append(new TextComponent(ACTMod.MOD_NAME).withStyle(ChatFormatting.WHITE)), false);
			src.sendSuccess(new TranslatableComponent("cmd.act.info.version").withStyle(ChatFormatting.GOLD)
					.append(new TextComponent(": ").withStyle(ChatFormatting.DARK_GRAY))
					.append(new TextComponent(ACTMod.MOD_VERSION).withStyle(ChatFormatting.WHITE)), false);
			src.sendSuccess(new TranslatableComponent("cmd.act.info.authors").withStyle(ChatFormatting.GOLD)
					.append(new TextComponent(": ").withStyle(ChatFormatting.DARK_GRAY))
					.append(new TextComponent(ACTMod.MOD_AUTHORS).withStyle(ChatFormatting.WHITE)), false);
			src.sendSuccess(new TranslatableComponent("cmd.act.info.licence").withStyle(ChatFormatting.GOLD)
					.append(new TextComponent(": ").withStyle(ChatFormatting.DARK_GRAY))
					.append(new TextComponent(ACTMod.MOD_LICENCE).withStyle(s -> s
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new TranslatableComponent("cmd.act.info.link.open")
											.withStyle(ChatFormatting.YELLOW)))
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ACTMod.MOD_LICENCE_LINK))
							.withColor(ChatFormatting.WHITE))),
					false);
			src.sendSuccess(new TranslatableComponent("cmd.act.info.link").withStyle(ChatFormatting.GOLD)
					.append(new TextComponent(": ").withStyle(ChatFormatting.DARK_GRAY))
					.append(new TextComponent("curseforge.com").withStyle(s -> s
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new TranslatableComponent("cmd.act.info.link.open")
											.withStyle(ChatFormatting.YELLOW)))
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ACTMod.MOD_LINK))
							.withColor(ChatFormatting.BLUE))),
					false);
			return 5;
		};
	}
}
