package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
@Mixin( ZombieVillagerEntity.class )
public abstract class ZombieVillagerEntityMixin extends ZombieEntity {
    public ZombieVillagerEntityMixin(World world) {
        super(world);
    }

    // Make it so that Zombie Villagers don't burn in daylight.
    @Override protected boolean burnsInDaylight() {
        return false;
    }
}
