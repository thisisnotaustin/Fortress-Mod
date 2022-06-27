package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( BlastFurnaceBlockEntity.class )
public abstract class BlastFurnaceBlockEntityMixin {

    @Inject( method = "getFuelTime", at = @At("RETURN"), cancellable = true)
    protected void injectGetFuelTime( ItemStack fuel, CallbackInfoReturnable<Integer> info ) {
        // Get the fuel time for a Furnace (the vanilla return value x2).
        int furnaceTime = info.getReturnValue() * 2;

        // Make the fuel burn 3 times as fast as a Furnace.
        info.setReturnValue(furnaceTime / 3);
    }
}