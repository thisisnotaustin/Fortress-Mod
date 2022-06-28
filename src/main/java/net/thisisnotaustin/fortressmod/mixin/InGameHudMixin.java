package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings( "unused" )
@Mixin( InGameHud.class )
public abstract class InGameHudMixin extends DrawableHelper {
    // Shadow fields and methods.
    @Shadow private int ticks;
    @Shadow private int lastHealthValue;
    @Shadow private int renderHealthValue;
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Shadow private long heartJumpEndTick;
    @Shadow private long lastHealthCheckTime;
    @Shadow private final Random random;
    @Shadow private final MinecraftClient client;

    protected InGameHudMixin(Random random, MinecraftClient client) {
        this.random = random;
        this.client = client;
    }

    @Shadow private PlayerEntity getCameraPlayer() {
        return null;
    }
    @Shadow private LivingEntity getRiddenEntity() {
        return null;
    }
    @Shadow private int getHeartCount( LivingEntity a ) {
        return 0;
    }
    @Shadow private void renderHealthBar( MatrixStack a, PlayerEntity b, int c, int d, int e, int f, float g, int h, int i, int j, boolean k ) {}
    @Shadow private int getHeartRows( int a ) {
        return 0;
    }

    private void renderStatusBars( MatrixStack matrices ) {
        // Check if the player camera exists.
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity == null) {
            return;
        }

        // Health regen animation (as near as i can tell?)
        int healthRoundedUp = MathHelper.ceil(playerEntity.getHealth());
        boolean bl = this.heartJumpEndTick > (long) this.ticks && (this.heartJumpEndTick - (long) this.ticks) / 3L % 2L == 1L;
        long currentTime = Util.getMeasuringTimeMs();
        if (healthRoundedUp < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = currentTime;
            this.heartJumpEndTick = this.ticks + 20;
        } else if (healthRoundedUp > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = currentTime;
            this.heartJumpEndTick = this.ticks + 10;
        }
        if (currentTime - this.lastHealthCheckTime > 1000L) {
            this.renderHealthValue = healthRoundedUp;
            this.lastHealthCheckTime = currentTime;
        }
        this.lastHealthValue = healthRoundedUp;

        // Set random seed.
        this.random.setSeed(this.ticks * 312871L);

        // Grab important screen positions.
        int screenLeft = this.scaledWidth / 2 - 91;
        int screenRight = this.scaledWidth / 2 + 91;
        int screenBottom = this.scaledHeight - 39;

        int foodLevel = playerEntity.getHungerManager().getFoodLevel();
        float maxHealth = Math.max((float) playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float) Math.max(this.renderHealthValue, healthRoundedUp));
        int absorptionRoundedUp = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int healthAndAbsorptionRows = MathHelper.ceil((maxHealth + (float) absorptionRoundedUp) / 2.0f / 10.0f);
        int healthBarLines = Math.max(10 - (healthAndAbsorptionRows - 2), 3);
        int t = screenBottom - 10;
        int regeneratingHeartIndex = -1;
        if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
            regeneratingHeartIndex = this.ticks % MathHelper.ceil(maxHealth + 5.0f);
        }

        // Health bar.
        this.client.getProfiler().swap("health");
        this.renderHealthBar(matrices, playerEntity, screenLeft, screenBottom, healthBarLines, regeneratingHeartIndex, maxHealth, healthRoundedUp, this.renderHealthValue, absorptionRoundedUp, bl);
        LivingEntity livingEntity = this.getRiddenEntity();
        int steedHealth = this.getHeartCount(livingEntity);

        // Armor bar.
        this.client.getProfiler().push("armor");
        int armorLevel = playerEntity.getArmor();
        int toughnessLevel = (int) playerEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        for (int i = 0; i < 10; ++i) {
            if (armorLevel <= 0) continue;
            int drawX = screenRight - i * 8 - 9;    // Right side.
            int drawY = screenBottom - ((steedHealth == 0) ? 0 : 10);   // Above steed health, if mounted.
            int comparison = i * 2 + 1;

            // Full toughness icon.
            if (comparison < toughnessLevel) {
                this.drawTexture(matrices, drawX, drawY, 70, 18, 9, 9);
                continue;
            }
            if (comparison == toughnessLevel) {
                // Half toughness full armor icon.
                if (comparison < armorLevel) {
                    this.drawTexture(matrices, drawX, drawY, 61, 18, 9, 9);
                    continue;
                }
                // Half toughness icon.
                this.drawTexture(matrices, drawX, drawY, 52, 18, 9, 9);
                continue;
            }
            // No toughness full armor icon.
            if (comparison < armorLevel) {
                this.drawTexture(matrices, drawX, drawY, 34, 9, 9, 9);
                continue;
            }
            // No toughness Half armor icon.
            if (comparison == armorLevel) {
                this.drawTexture(matrices, drawX, drawY, 25, 9, 9, 9);
                continue;
            }
            // Empty armor icon.
            this.drawTexture(matrices, drawX, drawY, 16, 9, 9, 9);
            continue;
        }

        // Food bar.
        /*if (steedHealth == 0) {
            this.client.getProfiler().swap("food");
            for (int i = 0; i < 10; ++i) {
                int aa = 16;
                int ab = 0;
                if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                    aa += 36;
                    ab = 13;
                }
                int drawY = screenBottom;
                if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0f && this.ticks % (foodLevel * 3 + 1) == 0) {
                    drawY += this.random.nextInt(3) - 1;
                }
                int ac = screenRight - i * 8 - 9;
                this.drawTexture(matrices, ac, drawY, 16 + ab * 9, 27, 9, 9);
                if (i * 2 + 1 < foodLevel) {
                    this.drawTexture(matrices, ac, drawY, aa + 36, 27, 9, 9);
                }
                if (i * 2 + 1 != foodLevel) continue;
                this.drawTexture(matrices, ac, drawY, aa + 45, 27, 9, 9);
            }
            t -= 10;
        }*/

        // Air bar.
        this.client.getProfiler().swap("air");
        int maxAir = playerEntity.getMaxAir();
        int airLevel = Math.min(playerEntity.getAir(), maxAir);
        if (playerEntity.isSubmergedIn(FluidTags.WATER) || airLevel < maxAir) {
            int ab = MathHelper.ceil((double)(airLevel - 2) * 10.0 / (double) maxAir);
            int ac = MathHelper.ceil((double)airLevel * 10.0 / (double) maxAir) - ab;
            int drawY = screenBottom - (healthAndAbsorptionRows - 1) * healthBarLines - 10; // Above health + absorption.
            for (int i = 0; i < ab + ac; ++i) {
                if (i < ab) {
                    this.drawTexture(matrices, screenLeft + i * 8, drawY, 16, 18, 9, 9);
                    continue;
                }
                this.drawTexture(matrices, screenLeft + i * 8, drawY, 25, 18, 9, 9);
            }
        }

        this.client.getProfiler().pop();
    }
}
