package xyz.bluspring.tokeneaterorigin

import io.github.apace100.apoli.power.Power
import io.github.apace100.apoli.power.PowerTypeReference
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import xyz.bluspring.tokeneaterorigin.effect.GotToGoMobEffect
import xyz.bluspring.tokeneaterorigin.item.TrackingCompassItem


class TokenEaterOrigin : ModInitializer {
    override fun onInitialize() {
        /*ContainerEvents.SLOT_CHANGE.register { container, slot, stack ->
            if (stack.`is`(SOULBOUND_COMPASS) && container !is InventoryMenu) {
                container.suppressRemoteUpdates()
                container.setRemoteCarried(ItemStack.EMPTY)
                container.getSlot(slot).set(stack)
                container.resumeRemoteUpdates()
                container.broadcastFullState()
            }
        }*/

        ServerPlayConnectionEvents.JOIN.register { listener, sender, server ->
            if (SOULBOUND_COMPASS_POWER.isActive(listener.player)) {
                listener.player.addItem(ItemStack(SOULBOUND_COMPASS, 1).apply {
                    this.enchant(Enchantments.VANISHING_CURSE, 1)
                })
            }
        }
    }

    companion object {
        @JvmField val ID = "crimecraft"

        @JvmField val GOT_TO_GO_EFFECT: GotToGoMobEffect = Registry.register(BuiltInRegistries.MOB_EFFECT, ResourceLocation(ID, "got_to_go"), GotToGoMobEffect())
        @JvmField val GOT_TO_GO_POWER = PowerTypeReference<Power>(ResourceLocation(ID, "got_to_go"))

        @JvmField val RANDOM_POTION_FUNCTION = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, ResourceLocation(ID, "get_random_potion"), LootItemFunctionType(RandomPotionLootFunction.Serializer()))

        @JvmField val SOULBOUND_COMPASS = Registry.register(BuiltInRegistries.ITEM, ResourceLocation(ID, "soulbound_compass"), TrackingCompassItem())
        @JvmField val SOULBOUND_COMPASS_POWER = PowerTypeReference<Power>(ResourceLocation(ID, "soulbound_compass"))
    }
}