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
import net.thisisnotaustin.fortressmod.util.ModTags;
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

    // Make it so that you can only eat if you aren't currently healing from food.
    public boolean isNotFull() {
        return this.foodLevel == 0;
    }

    // Custom eating code.
    public void eat( Item item, ItemStack stack ) {
        if (item.isFood()) {
            float newFoodLevel = 0;
            this.healingSpeed = 0;

            // Decide on healing amount.
            if (stack.isIn(ModTags.Items.EATING_HEALS_1)) this.setFoodLevel(1);
            if (stack.isIn(ModTags.Items.EATING_HEALS_2)) this.setFoodLevel(2);
            if (stack.isIn(ModTags.Items.EATING_HEALS_3)) this.setFoodLevel(3);
            if (stack.isIn(ModTags.Items.EATING_HEALS_4)) this.setFoodLevel(4);
            if (stack.isIn(ModTags.Items.EATING_HEALS_5)) this.setFoodLevel(5);
            if (stack.isIn(ModTags.Items.EATING_HEALS_6)) this.setFoodLevel(6);
            if (stack.isIn(ModTags.Items.EATING_HEALS_7)) this.setFoodLevel(7);
            if (stack.isIn(ModTags.Items.EATING_HEALS_8)) this.setFoodLevel(8);
            if (stack.isIn(ModTags.Items.EATING_HEALS_9)) this.setFoodLevel(9);
            if (stack.isIn(ModTags.Items.EATING_HEALS_10)) this.setFoodLevel(10);
            if (stack.isIn(ModTags.Items.EATING_HEALS_11)) this.setFoodLevel(11);
            if (stack.isIn(ModTags.Items.EATING_HEALS_12)) this.setFoodLevel(12);
            if (stack.isIn(ModTags.Items.EATING_HEALS_13)) this.setFoodLevel(13);
            if (stack.isIn(ModTags.Items.EATING_HEALS_14)) this.setFoodLevel(14);
            if (stack.isIn(ModTags.Items.EATING_HEALS_15)) this.setFoodLevel(15);
            if (stack.isIn(ModTags.Items.EATING_HEALS_16)) this.setFoodLevel(16);
            if (stack.isIn(ModTags.Items.EATING_HEALS_17)) this.setFoodLevel(17);
            if (stack.isIn(ModTags.Items.EATING_HEALS_18)) this.setFoodLevel(18);
            if (stack.isIn(ModTags.Items.EATING_HEALS_19)) this.setFoodLevel(19);
            if (stack.isIn(ModTags.Items.EATING_HEALS_20)) this.setFoodLevel(20);

            // Decide on healing speed.
            this.healingSpeed = stack.isIn(ModTags.Items.EATING_HEALS_VERY_FAST) ? 0 : (stack.isIn(ModTags.Items.EATING_HEALS_FAST) ? 5 : (stack.isIn(ModTags.Items.EATING_HEALS_SLOW) ? 20 : 10));

            // Make the first heal immediate.
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
