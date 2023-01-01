package fr.atesab.act.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.atesab.act.ACTMod;
import fr.atesab.act.command.ModdedCommandHelp.CommandClickOption;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.Map;

public class ModdedCommandEnchant extends ModdedCommand {

    public ModdedCommandEnchant() {
        super("enchant", "cmd.act.enchant", CommandClickOption.suggestCommand);
        addAlias("en");
    }

    @Override
    protected LiteralArgumentBuilder<CommandSourceStack> onArgument(
            LiteralArgumentBuilder<CommandSourceStack> command, CommandBuildContext context) {
        return command.then(Commands.argument("enchantname", enchantmentArgument(context)).executes(c -> {
            Enchantment e = ResourceArgument.getEnchantment(c, "enchantname").get();
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return 0;
            }
            ItemStack is = mc.player.getMainHandItem().copy();
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(is);
            map.put(e, e.getMaxLevel());
            EnchantmentHelper.setEnchantments(map, is);
            ItemUtils.give(is, 36 + mc.player.getInventory().selected);
            return 0;
        }).then(Commands.argument("enchantlevel", IntegerArgumentType.integer()).executes(c -> {
            Enchantment e = ResourceArgument.getEnchantment(c, "enchantname").get();
            int lvl = IntegerArgumentType.getInteger(c, "enchantlevel");
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return 0;
            }
            ItemStack is = mc.player.getMainHandItem().copy();
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(is);
            map.put(e, lvl);
            EnchantmentHelper.setEnchantments(map, is);
            ItemUtils.give(is, 36 + mc.player.getInventory().selected);
            return 0;
        })));
    }

    @Override
    protected Command<CommandSourceStack> onNoArgument() {
        return c -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return 0;
            }
            ItemStack is = mc.player.getMainHandItem();
            is = is.copy();
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(is);
            for (EnchantmentInstance data : EnchantmentHelper.selectEnchantment(ACTMod.RANDOM_SOURCE, is, 30, true)) {
                map.put(data.enchantment, data.level);
            }
            EnchantmentHelper.setEnchantments(map, is);
            ItemUtils.give(is, 36 + mc.player.getInventory().selected);
            return 0;
        };
    }
}
