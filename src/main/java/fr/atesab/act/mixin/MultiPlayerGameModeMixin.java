package fr.atesab.act.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeMixin {
    @Accessor("destroyDelay")
    void setDestroyDelay(int destroyDelay);
}
