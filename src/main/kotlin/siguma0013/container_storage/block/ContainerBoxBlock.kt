package siguma0013.container_storage.block

import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContext.Dropper
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import siguma0013.container_storage.block.entity.ContainerBoxBlockEntity
import java.util.function.Consumer

class ContainerBoxBlock(settings: Settings?) : BlockWithEntity(settings) {
    companion object {
        val facing: DirectionProperty = Properties.FACING
    }

    init {
        defaultState = getStateManager().defaultState.with(facing, Direction.NORTH)
    }

    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity {
        return ContainerBoxBlockEntity(pos, state)
    }

    override fun getRenderType(state: BlockState?): BlockRenderType = BlockRenderType.MODEL

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
            builder?.add(facing)
    }

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

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return defaultState.with(facing, ctx?.playerFacing?.opposite)
    }

    override fun rotate(state: BlockState?, rotation: BlockRotation?): BlockState? {
        return state?.with(facing, rotation?.rotate(state.get(facing)))
    }

    override fun mirror(state: BlockState?, mirror: BlockMirror?): BlockState? {
        return state?.rotate(mirror?.getRotation(state.get(facing)))
    }

    override fun getDroppedStacks(state: BlockState?, builder: LootContext.Builder?): MutableList<ItemStack> {
        val blockEntity = builder?.getNullable(LootContextParameters.BLOCK_ENTITY)
        var rebuilder: LootContext.Builder? = null

        if (blockEntity is ContainerBoxBlockEntity) {
            val entity = blockEntity as ContainerBoxBlockEntity
            rebuilder = builder.putDrop(
                Identifier("contents")
            ) { lootContext: LootContext?, consumer: Consumer<ItemStack?> ->
                for (i in 0 until entity.size()) {
                    consumer.accept(entity.getStack(i))
                }
            }
        }

        return super.getDroppedStacks(state, rebuilder)
    }
}
