package xyz.bluspring.tokeneaterorigin.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.renderer.item.CompassItemPropertyFunction
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.GlobalPos
import net.minecraft.resources.ResourceLocation
import xyz.bluspring.tokeneaterorigin.TokenEaterOrigin
import xyz.bluspring.tokeneaterorigin.item.TrackingCompassItem
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class TokenEaterOriginClient : ClientModInitializer {
    override fun onInitializeClient() {
        ItemProperties.register(TokenEaterOrigin.SOULBOUND_COMPASS, ResourceLocation("angle"), CompassItemPropertyFunction { level, stack, entity ->
            TrackingCompassItem.getTrackedPlayerPos(level, stack, entity)
        })

        ClientPlayNetworking.registerGlobalReceiver(ResourceLocation(TokenEaterOrigin.ID, "update_soulbound_compass")) { mc, listener, buf, sender ->
            val uuid = buf.readUUID()
            val pos = buf.readGlobalPos()

            currentCompassPos[uuid] = pos
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            currentCompassPos.clear()
        }

        ClientPlayNetworking.registerGlobalReceiver(ResourceLocation(TokenEaterOrigin.ID, "clear_soulbound_compass")) { mc, listener, buf, sender ->
            val uuid = buf.readUUID()
            currentCompassPos.remove(uuid)
        }
    }

    companion object {
        val currentCompassPos = ConcurrentHashMap<UUID, GlobalPos>()
    }
}