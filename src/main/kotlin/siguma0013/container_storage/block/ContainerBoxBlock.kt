package siguma0013.container_storage.block

import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
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
        val playerInventory = player?.inventory

        var reqCount = 64
        for (index in 0 until playerInventory?.size()!!) {
            val playerItemStack = playerInventory.getStack(index)

            if (blockEntity.equalItemFilter(playerItemStack.item) && playerItemStack.count < playerItemStack.maxCount) {
                // 手持ちアイテムの空き容量算出
                var tmpReqCount = playerItemStack.maxCount - playerItemStack.count

                // 空き容量と要求量との比較
                if (tmpReqCount > reqCount) tmpReqCount = reqCount

                val tookItemStack = blockEntity.takeStock(tmpReqCount)

                playerItemStack.count = playerItemStack.count + tookItemStack.count
                reqCount -= tookItemStack.count
            } else if (playerItemStack.isEmpty) {
                playerInventory.setStack(index, blockEntity.takeStock(reqCount))
                reqCount = 0
            }

            // 終了条件
            if (reqCount <= 0 || blockEntity.count <= 0) break
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
            if (blockEntity.addStack(itemStack)) blockEntity.markDirty()
        }

        // 0 air(非ItemStack.EMPTY) 対策
        if (true == itemStack?.isEmpty) player.setStackInHand(hand, ItemStack.EMPTY)

        return ActionResult.SUCCESS
    }
}
