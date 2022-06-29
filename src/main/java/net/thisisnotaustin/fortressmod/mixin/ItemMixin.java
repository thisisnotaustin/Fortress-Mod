package net.thisisnotaustin.fortressmod.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.thisisnotaustin.fortressmod.util.ModTags;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
@Mixin( Item.class )
public abstract class ItemMixin {

    public int getMaxUseTime(ItemStack stack) {
        // Decide food-eating speed based on item tags.
        if (stack.getItem().isFood()) {
            return stack.isIn(ModTags.Items.EATING_SPEED_SLOW) ? 48 : (stack.isIn(ModTags.Items.EATING_SPEED_FAST) ? 16 : 32);
        }

        // Default use time.
        return 0;
    }
}