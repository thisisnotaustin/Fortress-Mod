package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("unused")
@Mixin( ZombieEntity.class )
public abstract class ZombieEntityMixin extends HostileEntity {
    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    // Shadow fields.
    @Shadow public void setCanBreakDoors( @SuppressWarnings("unused") boolean b ) {}

    // Make all zombies able to break doors, only on normal or hard difficulty.
    private static final Predicate<Difficulty> DOOR_BREAK_DIFFICULTY_CHECKER = difficulty -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD;

    @Inject( method = "initialize", at = @At("TAIL"))
    private void injectInitialize( ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> info ) {
        this.setCanBreakDoors(true);
    }

    // Increase knock-back resistance of zombies.
    @Inject( method = "applyAttributeModifiers", at = @At("TAIL"))
    protected void injectApplyAttributeModifiers( float chanceMultiplier, CallbackInfo info ) {
        // Create a random float between 0.0f and 0.15f.
        // This value will be added to the movement speed and subtracted from the knock-back
        // resistance, creating either slow, heavy zombies or fast, lightweight zombies.
        float balance = (this.random.nextFloat() * 0.06f) + (this.random.nextFloat() * 0.06f);
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).addPersistentModifier(new EntityAttributeModifier("Fortress Mod: Movement speed buff", balance, EntityAttributeModifier.Operation.ADDITION));
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).addPersistentModifier(new EntityAttributeModifier("Fortress Mod: Knock-back resistance buff", 0.7f - balance, EntityAttributeModifier.Operation.ADDITION));
    }

    // Changes to villager zombification.
    @Overwrite
    public boolean onKilledOther(ServerWorld world, LivingEntity other) {
        boolean bl = super.onKilledOther(world, other);
        // Removed difficulty check so that infection occurs on every difficulty.
        if (other instanceof VillagerEntity villagerEntity) {
            /*
            Removed block that doesn't guarantee infection on normal mode.
             */
            ZombieVillagerEntity zombieVillagerEntity = villagerEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
            assert zombieVillagerEntity != null;
            zombieVillagerEntity.initialize(world, world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), null);
            zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
            zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE).getValue());
            zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toNbt());
            zombieVillagerEntity.setXp(villagerEntity.getExperience());
            if (!this.isSilent()) {
                world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, this.getBlockPos(), 0);
            }
            bl = false;
        }
        return bl;
    }
}