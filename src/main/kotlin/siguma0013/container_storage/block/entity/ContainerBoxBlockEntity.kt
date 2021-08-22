package siguma0013.container_storage.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import siguma0013.container_storage.ContainerStorage

class ContainerBoxBlockEntity(blockPos: BlockPos?, blockState: BlockState?) :
    LootableContainerBlockEntity(ContainerStorage.CONTAINER_BOX_BLOCK_ENTITY, blockPos, blockState)
{
    private var inventory = DefaultedList.ofSize(3, ItemStack.EMPTY)

    var filterId: String = ""
    var filterRawId: Int? = null

    // アイテムの格納
    fun addStack(itemStack: ItemStack): Boolean {
        if (null == filterRawId) filterRawId = Item.getRawId(itemStack.item)

        for (index in 0 until inventory.size) {
            if (ItemStack.EMPTY == inventory[index]) {
                inventory[index] = itemStack.copy()
                itemStack.count = 0
                return true
            }
        }

        return false
    }

    //
    fun takeStock(): ItemStack? {
        val index = inventory.indexOfFirst { it != ItemStack.EMPTY }

        return if (0 <= index) {
            val itemStack = inventory[index].copy()
            inventory[index] = ItemStack.EMPTY
            itemStack
        } else {
            null
        }
    }

    override fun readNbt(nbt: NbtCompound?) {
        super.readNbt(nbt)
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY)

        if (null != nbt) {
            filterId = nbt.getString("filter_id")
        }

        Inventories.readNbt(nbt, inventory)
    }

    override fun writeNbt(nbt: NbtCompound?): NbtCompound? {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inventory, false)

        if (null != nbt) {
            nbt.putString("filter_id", filterId)
        }

        return nbt
    }

    override fun size(): Int = inventory.size

    override fun getContainerName(): Text {
        TODO("Not yet implemented")
    }

    override fun createScreenHandler(syncId: Int, playerInventory: PlayerInventory?): ScreenHandler {
        TODO("Not yet implemented")
    }

    override fun getInvStackList(): DefaultedList<ItemStack> = inventory

    override fun setInvStackList(list: DefaultedList<ItemStack>?) {
        inventory = list
    }
}