package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Predicate;

@Mixin( ZombieEntity.class )
public class ZombieEntityMixin {
    // Shadow fields.
    @Shadow public void setCanBreakDoors(boolean b ) {}

    // Set doors to be able to be broken at normal or hard difficulty.
    private static final Predicate<Difficulty> DOOR_BREAK_DIFFICULTY_CHECKER = difficulty -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD;

    // Set all zombies to be able to break doors.
    @Inject( method = "initialize", at = @At("TAIL"))
    private void injectInitialize( ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> info ) {
        this.setCanBreakDoors(true);
    }
}
