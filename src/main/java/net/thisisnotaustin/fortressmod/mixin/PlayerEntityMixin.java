package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@SuppressWarnings("unused")
@Mixin( PlayerEntity.class )
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // Shadow fields and methods.
    @Shadow protected HungerManager hungerManager;

    // Always return a player's sprinting state as false.
    public void setSprinting( boolean sprinting ) {}

    // Remove the ability to change a player's sprinting state.
    public boolean isSprinting() {
        return false;
    }

    // Code when eating food.
    @Inject( method = "eatFood", at = @At("TAIL"), cancellable = true )
    private void injectEatFood( World world, ItemStack stack, CallbackInfoReturnable<ItemStack> info ) {
        if (this.hasStatusEffect(StatusEffects.HUNGER)) {
            this.hungerManager.setFoodLevel((int) Math.ceil(this.hungerManager.getFoodLevel() / 2f));
        }
    }
}