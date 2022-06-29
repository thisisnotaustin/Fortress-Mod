package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Pair;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Mixin( HungerManager.class )
public abstract class HungerManagerMixin {
    // Shadow fields.
    @Shadow private int foodLevel;
    @Shadow private float saturationLevel;
    @Shadow private float exhaustion;
    @Shadow private int foodTickTimer;
    @Shadow public void setFoodLevel( int foodLevel ) {}
    @Shadow public int getFoodLevel() {
        return this.foodLevel;
    }

    // Custom field.
    private final List<Pair<Integer, Integer>> healBeats = new ArrayList<>();
    private int healingSpeed = 0;
    private static final int INTERVAL_INSTANT = 0;
    private static final int INTERVAL_FAST = 5;
    private static final int INTERVAL_MEDIUM = 10;
    private static final int INTERVAL_SLOW = 20;

    // Make it so that you can only eat if you aren't currently healing from food.
    public boolean isNotFull() {
        return this.foodLevel == 0;
    }

    // Custom eating code.
    public void eat( Item item, ItemStack stack ) {
        if (item.isFood()) {
            float newFoodLevel = 0;
            this.healingSpeed = 0;

            if (item == Items.ROTTEN_FLESH || item == Items.SPIDER_EYE || item == Items.POISONOUS_POTATO) {
                newFoodLevel = 1;
                //this.healingSpeed = INTERVAL_INSTANT;
            }
            if (item == Items.COOKIE || item == Items.SWEET_BERRIES || item == Items.GLOW_BERRIES || item == Items.MELON_SLICE || item == Items.DRIED_KELP) {
                newFoodLevel = 1;
                //this.healingSpeed = INTERVAL_INSTANT;
            }
            if (item == Items.APPLE || item == Items.HONEY_BOTTLE) {
                newFoodLevel = 2;
                this.healingSpeed = INTERVAL_FAST;
            }
            if (item == Items.PUMPKIN_PIE) {
                newFoodLevel = 3;
                this.healingSpeed = INTERVAL_FAST;
            }
            if (item == Items.POTATO || item == Items.BEETROOT || item == Items.COD || item == Items.SALMON || item == Items.TROPICAL_FISH || item == Items.PUFFERFISH) {
                newFoodLevel = 2;
                this.healingSpeed = INTERVAL_MEDIUM;
            }
            if (item == Items.CARROT) {
                newFoodLevel = 3;
                this.healingSpeed = INTERVAL_MEDIUM;
            }
            if (item == Items.BREAD || item == Items.BAKED_POTATO || item == Items.COOKED_COD || item == Items.COOKED_SALMON) {
                newFoodLevel = 4;
                this.healingSpeed = INTERVAL_MEDIUM;
            }
            if (item == Items.COOKED_PORKCHOP || item == Items.COOKED_BEEF || item == Items.COOKED_MUTTON || item == Items.COOKED_RABBIT) {
                newFoodLevel = 6;
                this.healingSpeed = INTERVAL_SLOW;
            }
            if (item == Items.COOKED_CHICKEN) {
                newFoodLevel = 4;
                this.healingSpeed = INTERVAL_SLOW;
            }
            if (item == Items.PORKCHOP || item == Items.BEEF || item == Items.MUTTON || item == Items.CHICKEN || item == Items.RABBIT) {
                newFoodLevel = 2;
                this.healingSpeed = INTERVAL_SLOW;
            }
            if (item == Items.MUSHROOM_STEW || item == Items.RABBIT_STEW || item == Items.BEETROOT_SOUP || item == Items.SUSPICIOUS_STEW) {
                newFoodLevel = 10;
                this.healingSpeed = INTERVAL_FAST;
            }
            if (item == Items.GOLDEN_CARROT) {
                newFoodLevel = 2;
                this.healingSpeed = INTERVAL_INSTANT;
            }
            if (item == Items.GOLDEN_APPLE) {
                newFoodLevel = 5;
                this.healingSpeed = INTERVAL_INSTANT;
            }
            if (item == Items.ENCHANTED_GOLDEN_APPLE) {
                newFoodLevel = 20;
                this.healingSpeed = INTERVAL_INSTANT;
            }

            // Halve food value if the player is suffering from Hunger.
            if (stack.getHolder() instanceof PlayerEntity player) {
                if (player.hasStatusEffect(StatusEffects.HUNGER)) {
                    newFoodLevel = (int) Math.ceil(newFoodLevel / 2f);
                }
            }

            this.setFoodLevel((int) newFoodLevel);
            this.foodTickTimer = this.healingSpeed;
        }
    }

    // Custom healing code.
    public void update( PlayerEntity player ) {
        // If healing is in progress...
        if (this.getFoodLevel() > 0) {
            if (++this.foodTickTimer >= this.healingSpeed) {
                this.foodTickTimer = 0;
                this.setFoodLevel(this.getFoodLevel() - 1);

                // Heal the player by 1 (if natural regeneration is enabled).
                if (player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION) && player.canFoodHeal()){
                    player.heal(1);
                }
            }
        }
    }

    // Load healing speed from NBT data.
    @Inject( method = "readNbt", at = @At("TAIL"))
    private void injectReadNbt( NbtCompound nbt, CallbackInfo info ) {
        if (nbt.contains("foodLevel", NbtElement.NUMBER_TYPE)) {
            this.healingSpeed = nbt.getInt("foodHealingSpeed");
        }
    }

    // Write healing speed to NBT data.
    @Inject( method = "writeNbt", at = @At("TAIL"))
    private void injectWriteNbt( NbtCompound nbt, CallbackInfo info ) {
        nbt.putInt("foodHealingSpeed", healingSpeed);
    }
}
