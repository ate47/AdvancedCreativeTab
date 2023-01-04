package fr.atesab.act.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftMixin {
    @Accessor("rightClickDelay")
    void setRightClickDelay(int rightClickDelay);
}
