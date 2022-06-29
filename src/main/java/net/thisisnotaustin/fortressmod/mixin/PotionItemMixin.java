package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin( PotionItem.class )
public abstract class PotionItemMixin extends Item {
    public PotionItemMixin(Settings settings) {
        super(settings);
    }

    // Overwrite use time length for potions.
    @Inject( method = "getMaxUseTime", at = @At("RETURN"), cancellable = true )
    private void injectGetMaxUseTime(ItemStack stack, CallbackInfoReturnable<Integer> info ) {
        info.setReturnValue(16);
    }
}
