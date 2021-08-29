package siguma0013.container_storage.block

import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import siguma0013.container_storage.block.entity.ContainerBoxBlockEntity

class ContainerBoxBlock(settings: Settings?) : BlockWithEntity(settings) {
    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity {
        return ContainerBoxBlockEntity(pos, state)
    }

    override fun getRenderType(state: BlockState?): BlockRenderType = BlockRenderType.MODEL

    // 左クリック
    override fun onBlockBreakStart(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?) {
        if (true == world?.isClient) return

        val blockEntity = world?.getBlockEntity(pos) as ContainerBoxBlockEntity
        val itemStack = blockEntity.takeStock() ?: return

        val playerInventory = player?.inventory
        for (index in 0 until playerInventory?.size()!!) {
            if (ItemStack.EMPTY == playerInventory.getStack(index)) {
                playerInventory.setStack(index, itemStack)
                break
            }
        }
        blockEntity.markDirty()
    }

    // 右クリック
    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        if (true == world?.isClient) return ActionResult.SUCCESS

        val blockEntity = world?.getBlockEntity(pos) as ContainerBoxBlockEntity
        val itemStack = player?.getStackInHand(hand)

        if (false == itemStack?.isEmpty) {
            if (blockEntity.addStack(itemStack)) {
                player.setStackInHand(hand, ItemStack.EMPTY)
                blockEntity.markDirty()
            }
        }

        return ActionResult.SUCCESS
    }
}