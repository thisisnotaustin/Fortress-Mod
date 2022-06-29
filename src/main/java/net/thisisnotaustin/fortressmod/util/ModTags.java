package net.thisisnotaustin.fortressmod.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.thisisnotaustin.fortressmod.FortressMod;

public class ModTags {
    public static class Blocks {
        // The method used to create a new block tag.
        private static TagKey<Block> createTag(String name ) {
            return TagKey.of(Registry.BLOCK_KEY, new Identifier(FortressMod.MOD_ID, name));
        }
    }

    public static class Items {
        // Determines how long it takes to eat food.
        public static final TagKey<Item> EATING_SPEED_SLOW = createTag("eating/eaten_slowly");
        public static final TagKey<Item> EATING_SPEED_FAST = createTag("eating/eaten_quickly");

        // Determines how quickly you heal after eating the food item.
        public static final TagKey<Item> EATING_HEALS_SLOW = createTag("eating/heals_slowly");
        public static final TagKey<Item> EATING_HEALS_FAST = createTag("eating/heals_quickly");
        public static final TagKey<Item> EATING_HEALS_VERY_FAST = createTag("eating/heals_immediately");

        // Determines how much health you get from eating the food item.
        public static final TagKey<Item> EATING_HEALS_1 = createTag("eating/restores_health/1_health");
        public static final TagKey<Item> EATING_HEALS_2 = createTag("eating/restores_health/2_health");
        public static final TagKey<Item> EATING_HEALS_3 = createTag("eating/restores_health/3_health");
        public static final TagKey<Item> EATING_HEALS_4 = createTag("eating/restores_health/4_health");
        public static final TagKey<Item> EATING_HEALS_5 = createTag("eating/restores_health/5_health");
        public static final TagKey<Item> EATING_HEALS_6 = createTag("eating/restores_health/6_health");
        public static final TagKey<Item> EATING_HEALS_7 = createTag("eating/restores_health/7_health");
        public static final TagKey<Item> EATING_HEALS_8 = createTag("eating/restores_health/8_health");
        public static final TagKey<Item> EATING_HEALS_9 = createTag("eating/restores_health/9_health");
        public static final TagKey<Item> EATING_HEALS_10 = createTag("eating/restores_health/10_health");
        public static final TagKey<Item> EATING_HEALS_11 = createTag("eating/restores_health/11_health");
        public static final TagKey<Item> EATING_HEALS_12 = createTag("eating/restores_health/12_health");
        public static final TagKey<Item> EATING_HEALS_13 = createTag("eating/restores_health/13_health");
        public static final TagKey<Item> EATING_HEALS_14 = createTag("eating/restores_health/14_health");
        public static final TagKey<Item> EATING_HEALS_15 = createTag("eating/restores_health/15_health");
        public static final TagKey<Item> EATING_HEALS_16 = createTag("eating/restores_health/16_health");
        public static final TagKey<Item> EATING_HEALS_17 = createTag("eating/restores_health/17_health");
        public static final TagKey<Item> EATING_HEALS_18 = createTag("eating/restores_health/18_health");
        public static final TagKey<Item> EATING_HEALS_19 = createTag("eating/restores_health/19_health");
        public static final TagKey<Item> EATING_HEALS_20 = createTag("eating/restores_health/20_health");

        // The method used to create a new item tag.
        private static TagKey<Item> createTag(String name ) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier(FortressMod.MOD_ID, name));
        }
    }
}
