package xyz.bluspring.tokeneaterorigin.events

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerListener
import net.minecraft.world.item.ItemStack
import xyz.bluspring.tokeneaterorigin.events.ContainerEvents.SlotChanged

interface ContainerEvents {
    fun interface SlotChanged {
        fun onSlotChange(container: AbstractContainerMenu, slot: Int, stack: ItemStack)
    }

    companion object {
        @JvmField
        val LISTENER = object : ContainerListener {
            override fun slotChanged(containerToSend: AbstractContainerMenu, dataSlotIndex: Int, stack: ItemStack) {
                SLOT_CHANGE.invoker().onSlotChange(containerToSend, dataSlotIndex, stack)
            }

            override fun dataChanged(containerMenu: AbstractContainerMenu, dataSlotIndex: Int, value: Int) {
            }
        }

        @JvmField
        val SLOT_CHANGE = EventFactory.createArrayBacked(
            SlotChanged::class.java
        ) {
            SlotChanged { container, slot, stack ->
                for (event in it) {
                    event.onSlotChange(container, slot, stack)
                }
            }
        }
    }
}