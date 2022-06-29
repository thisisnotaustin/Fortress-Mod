package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    // Overwrite renderStatusBars method with custom code.
    private void renderStatusBars( MatrixStack matrices ) {
        // Check if the player camera exists.
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity == null) {
            return;
        }

        // Grab important screen positions.
        int screenLeft = this.scaledWidth / 2 - 91;
        int screenRight = this.scaledWidth / 2 + 91;
        int screenBottom = this.scaledHeight - 39;

        // Set random seed.
        this.random.setSeed(this.ticks * 312871L);

        /* LEFT-HAND BARS */
        // Health bar.
        // Grab important values.
        this.client.getProfiler().swap("health");
        int health = MathHelper.ceil(playerEntity.getHealth());
        float maxHealth = Math.max((float) playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float) Math.max(this.renderHealthValue, health));
        int absorption = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int numberOfLines = MathHelper.ceil((maxHealth + (float) absorption) / 2.0f / 10.0f);
        int offsetPerRow = Math.max(10 - (numberOfLines - 2), 3);

        // Regeneration animation.
        long currentTime = Util.getMeasuringTimeMs();
        if (health < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = currentTime;
            this.heartJumpEndTick = this.ticks + 20;
        } else if (health > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = currentTime;
            this.heartJumpEndTick = this.ticks + 10;
        }
        if (currentTime - this.lastHealthCheckTime > 1000L) {
            this.renderHealthValue = health;
            this.lastHealthCheckTime = currentTime;
        }
        this.lastHealthValue = health;
        boolean isBlinking = this.heartJumpEndTick > (long) this.ticks && (this.heartJumpEndTick - (long) this.ticks) / 3L % 2L == 1L;
        int regeneratingHeartIndex = -1;
        if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
            regeneratingHeartIndex = this.ticks % MathHelper.ceil(maxHealth + 5.0f);
        }

        // Run health bar-drawing method.
        this.renderHealthBar(matrices, playerEntity, screenLeft, screenBottom, offsetPerRow, regeneratingHeartIndex, maxHealth, health, this.renderHealthValue, absorption, isBlinking);

        // Draw food-healing hearts.
        int foodHealing = playerEntity.getHungerManager().getFoodLevel();
        if (foodHealing > 0) {
            final int healthHearts = MathHelper.ceil((double) maxHealth / 2.0);
            for (int i = healthHearts - 1; i >= 0; i--) {
                final int thisRow = i / 10;
                final int thisColumn = i % 10;

                // The default X/Y coordinate to draw the heart.
                int drawX = screenLeft + thisColumn * 8;
                int drawY = screenBottom - thisRow * offsetPerRow;

                // Near-death shimmy.
                if (health + absorption <= 4) {
                    drawY += this.random.nextInt(1);
                }

                // Regeneration shimmy.
                if (i == regeneratingHeartIndex) {
                    drawY -= 2;
                }

                // Draw food healing heart.
                int uOffset = playerEntity.hasStatusEffect(StatusEffects.HUNGER) ? 27 : 0;
                int comparison = i * 2 + 1;
                if (comparison >= health) {
                    if (comparison < health + foodHealing) {
                        // Full heart.
                        if (comparison > health) {
                            this.drawTexture(matrices, drawX, drawY, 79 + uOffset, 18, 9, 9);
                        }
                        // Completing half heart.
                        else {
                            this.drawTexture(matrices, drawX, drawY, 97 + uOffset, 18, 9, 9);
                        }
                    }
                    // New half heart.
                    if (comparison == health + foodHealing) {
                        this.drawTexture(matrices, drawX, drawY, 88 + uOffset, 18, 9, 9);
                    }
                }
            }
        }

        // Air bar.
        this.client.getProfiler().swap("air");
        int maxAir = playerEntity.getMaxAir();
        int airLevel = Math.min(playerEntity.getAir(), maxAir);
        if (playerEntity.isSubmergedIn(FluidTags.WATER) || airLevel < maxAir) {
            int ab = MathHelper.ceil((double)(airLevel - 2) * 10.0 / (double) maxAir);
            int ac = MathHelper.ceil((double)airLevel * 10.0 / (double) maxAir) - ab;
            int drawY = screenBottom - (numberOfLines - 1) * offsetPerRow - 10; // Above health + absorption.
            for (int i = 0; i < ab + ac; ++i) {
                if (i < ab) {
                    this.drawTexture(matrices, screenLeft + i * 8, drawY, 16, 18, 9, 9);
                    continue;
                }
                this.drawTexture(matrices, screenLeft + i * 8, drawY, 25, 18, 9, 9);
            }
        }

        /* RIGHT-HAND BARS */
        // Check the current steed's health. If the player is riding a steed,
        // all right-hand bars need to be offset vertically to make room.
        int steedHealth = this.getHeartCount(this.getRiddenEntity());

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
        }

        // Food bar (unused).
        /*this.client.getProfiler().swap("food");
        int foodLevel = playerEntity.getHungerManager().getFoodLevel();
        for (int i = 0; i < 10; ++i) {
            int uOffsetFiller = 16;
            int uOffsetBg = 0;
            if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                uOffsetFiller += 36;
                uOffsetBg = 13;
            }
            int drawX = screenRight - i * 8 - 9;
            int drawY = screenBottom - ((steedHealth == 0) ? 0 : 10) - ((armorLevel <= 0) ? 0 : 10);   // Above steed health and armor bar.
            // Icon stuttering when saturation is empty.
            if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0f && this.ticks % (foodLevel * 3 + 1) == 0) {
                drawY += this.random.nextInt(3) - 1;
            }
            this.drawTexture(matrices, drawX, drawY, 16 + uOffsetBg * 9, 27, 9, 9);
            if (i * 2 + 1 < foodLevel) {
                this.drawTexture(matrices, drawX, drawY, uOffsetFiller + 36, 27, 9, 9);
            }
            if (i * 2 + 1 != foodLevel) continue;
            this.drawTexture(matrices, drawX, drawY, uOffsetFiller + 45, 27, 9, 9);
        }*/

        // Pop.
        this.client.getProfiler().pop();
    }
}
