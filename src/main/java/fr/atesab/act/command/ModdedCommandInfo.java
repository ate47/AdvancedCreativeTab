package fr.atesab.act.command;

import com.mojang.brigadier.Command;

import fr.atesab.act.ACTMod;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class ModdedCommandInfo extends ModdedCommand {

	public ModdedCommandInfo() {
		super("info", "cmd.act.info", CommandClickOption.doCommand);
		addAlias("information");
	}

	@Override
	protected Command<CommandSource> onNoArgument() {
		return c -> {
			CommandSource src = c.getSource();
			src.sendSuccess(new TranslationTextComponent("cmd.act.info.title").withStyle(TextFormatting.GOLD)
					.append(new StringTextComponent(": ").withStyle(TextFormatting.DARK_GRAY))
					.append(new StringTextComponent(ACTMod.MOD_NAME).withStyle(TextFormatting.WHITE)), false);
			src.sendSuccess(
					new TranslationTextComponent("cmd.act.info.version").withStyle(TextFormatting.GOLD)
							.append(new StringTextComponent(": ").withStyle(TextFormatting.DARK_GRAY))
							.append(new StringTextComponent(ACTMod.MOD_VERSION).withStyle(TextFormatting.WHITE)),
					false);
			src.sendSuccess(
					new TranslationTextComponent("cmd.act.info.authors").withStyle(TextFormatting.GOLD)
							.append(new StringTextComponent(": ").withStyle(TextFormatting.DARK_GRAY))
							.append(new StringTextComponent(ACTMod.MOD_AUTHORS).withStyle(TextFormatting.WHITE)),
					false);
			src.sendSuccess(
					new TranslationTextComponent("cmd.act.info.licence").withStyle(TextFormatting.GOLD)
							.append(new StringTextComponent(": ")
									.withStyle(TextFormatting.DARK_GRAY))
							.append(new StringTextComponent(ACTMod.MOD_LICENCE).withStyle(s -> s
									.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new TranslationTextComponent("cmd.act.info.link.open")
													.withStyle(TextFormatting.YELLOW)))
									.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ACTMod.MOD_LICENCE_LINK))
									.withColor(TextFormatting.WHITE))),
					false);
			src.sendSuccess(
					new TranslationTextComponent("cmd.act.info.link").withStyle(TextFormatting.GOLD)
							.append(new StringTextComponent(": ")
									.withStyle(TextFormatting.DARK_GRAY))
							.append(new StringTextComponent("curseforge.com").withStyle(s -> s
									.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new TranslationTextComponent("cmd.act.info.link.open")
													.withStyle(TextFormatting.YELLOW)))
									.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ACTMod.MOD_LINK))
									.withColor(TextFormatting.BLUE))),
					false);
			return 5;
		};
	}
}
