package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin( PlayerEntity.class )
public abstract class PlayerEntityMixin {

    // Always return a player's sprinting state as false.
    public void setSprinting( boolean sprinting ) {}

    // Remove the ability to change a player's sprinting state.
    public boolean isSprinting() {
        return false;
    }
}