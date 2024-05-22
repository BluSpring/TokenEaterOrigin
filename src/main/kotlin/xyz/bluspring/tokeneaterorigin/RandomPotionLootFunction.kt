package xyz.bluspring.tokeneaterorigin

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition

class RandomPotionLootFunction internal constructor(conditions: Array<LootItemCondition>) : LootItemConditionalFunction(conditions) {
    override fun getType(): LootItemFunctionType {
        return TokenEaterOrigin.RANDOM_POTION_FUNCTION
    }

    public override fun run(stack: ItemStack, context: LootContext): ItemStack {
        val potion = BuiltInRegistries.POTION.getRandom(RandomSource.create()).orElseThrow().value()
        PotionUtils.setPotion(stack, potion)

        return stack
    }

    class Serializer : LootItemConditionalFunction.Serializer<RandomPotionLootFunction>() {
        override fun serialize(
            json: JsonObject,
            setPotionFunction: RandomPotionLootFunction,
            serializationContext: JsonSerializationContext
        ) {
            super.serialize(json, setPotionFunction, serializationContext)
        }

        override fun deserialize(
            `object`: JsonObject,
            deserializationContext: JsonDeserializationContext,
            conditions: Array<LootItemCondition>
        ): RandomPotionLootFunction {
            return RandomPotionLootFunction(conditions)
        }
    }
}
