package xyz.bluspring.tokeneaterorigin.item

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.ChatFormatting
import net.minecraft.core.GlobalPos
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CompassItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import xyz.bluspring.tokeneaterorigin.TokenEaterOrigin
import xyz.bluspring.tokeneaterorigin.client.TokenEaterOriginClient
import java.util.*

class TrackingCompassItem : CompassItem(Properties().rarity(Rarity.EPIC)) {
    override fun useOn(context: UseOnContext): InteractionResult {
        return InteractionResult.PASS
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        components: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        if (level == null)
            return

        val tag = stack.orCreateTag

        val playerName = if (!tag.contains("TrackedPlayer", Tag.TAG_INT_ARRAY.toInt())) {
            "(none)"
        } else {
            val playerUuid = tag.getUUID("TrackedPlayer")
            val player = level.getPlayerByUUID(playerUuid)

            if (player == null)
                "(none)"
            else
                player.gameProfile.name
        }

        components.add(Component.literal("Tracking: $playerName"))
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)
        val tag = stack.orCreateTag

        if (!level.isClientSide()) {
            val players = level.players().filter { it.uuid != player.uuid }
            if (players.isEmpty()) {
                player.displayClientMessage(Component.literal("No players available in your world!").withStyle(ChatFormatting.RED), true)
                return InteractionResultHolder.fail(stack)
            }

            val currentIndex = if (tag.hasUUID("TrackedPlayer")) {
                val currentPlayerUuid = tag.getUUID("TrackedPlayer")

                players.indexOfFirst { it.uuid == currentPlayerUuid }
            } else {
                -1
            }

            val nextIndex = if (!player.isShiftKeyDown && currentIndex + 1 >= players.size) {
                0
            } else if (player.isShiftKeyDown && currentIndex - 1 < 0) {
                players.size - 1
            } else if (!player.isShiftKeyDown)
                currentIndex + 1
            else
                currentIndex - 1

            val selectedPlayer = players.getOrNull(nextIndex)

            if (selectedPlayer == null) {
                player.displayClientMessage(Component.literal("A fatal error occurred in searching.").withStyle(ChatFormatting.RED), true)
                return InteractionResultHolder.fail(stack)
            }

            tag.putUUID("TrackedPlayer", selectedPlayer.uuid)
            player.displayClientMessage(Component.literal("Tracking: ").append(selectedPlayer.displayName), true)
        }

        return InteractionResultHolder.success(stack)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (level.isClientSide())
            return

        if (entity !is ServerPlayer)
            return

        if (level.gameTime % 50L != 0L) // limit updates
            return

        val tag = stack.orCreateTag
        if (!tag.hasUUID("UUID")) {
            tag.putUUID("UUID", UUID.randomUUID())
        }

        if (!tag.hasUUID("TrackedPlayer"))
            return

        val uuid = tag.getUUID("UUID")
        val trackedPlayer = level.getPlayerByUUID(tag.getUUID("TrackedPlayer"))

        if (trackedPlayer == null) {
            val buf = PacketByteBufs.create()
            buf.writeUUID(uuid)
            ServerPlayNetworking.send(entity, ResourceLocation(TokenEaterOrigin.ID, "clear_soulbound_compass"), buf)

            return
        }

        val buf = PacketByteBufs.create()
        buf.writeUUID(uuid)
        buf.writeGlobalPos(GlobalPos.of(trackedPlayer.level().dimension(), trackedPlayer.blockPosition()))

        ServerPlayNetworking.send(entity, ResourceLocation(TokenEaterOrigin.ID, "update_soulbound_compass"), buf)
    }

    companion object {
        fun getTrackedPlayerPos(level: Level, stack: ItemStack, entity: Entity): GlobalPos {
            val tag = stack.tag ?: return GlobalPos.of(level.dimension(), entity.blockPosition())

            if (!tag.hasUUID("TrackedPlayer") || !tag.hasUUID("UUID"))
                return GlobalPos.of(level.dimension(), entity.blockPosition())

            //val trackedPlayer = level.getPlayerByUUID(tag.getUUID("TrackedPlayer")) ?: return GlobalPos.of(level.dimension(), entity.blockPosition())
            return TokenEaterOriginClient.currentCompassPos[tag.getUUID("UUID")] ?: GlobalPos.of(level.dimension(), entity.blockPosition())
        }
    }
}