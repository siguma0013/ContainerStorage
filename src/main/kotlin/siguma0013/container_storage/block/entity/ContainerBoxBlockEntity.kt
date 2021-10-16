package siguma0013.container_storage.block.entity

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import siguma0013.container_storage.ContainerStorage

class ContainerBoxBlockEntity(blockPos: BlockPos?, blockState: BlockState?) :
    BlockEntity(ContainerStorage.CONTAINER_BOX_BLOCK_ENTITY, blockPos, blockState),
    Inventory,
    BlockEntityClientSerializable
{
    companion object {
        private const val KEY_FILTER = "filter_id"
    }

    // インベントリ
    private var inventory = DefaultedList.ofSize(3, ItemStack.EMPTY)
    val count: Int
        get() {
            var tmpCount = 0
            for (index in 0 until inventory.size) {
                if (ItemStack.EMPTY != inventory[index]) {
                    tmpCount += inventory[index].count
                }
            }
            return tmpCount
        }

    // フィルター
    private val filterInitId = Item.getRawId(ItemStack.EMPTY.item)
    private var filterRawId = filterInitId
    val itemFiltered: Item get() = Item.byRawId(filterRawId)

    /**
     * アイテムの格納
     *
     * @return アイテムを1つ以上格納できた場合 true、それ以外 false
     */
    fun addStack(itemStack: ItemStack): Boolean {
        // 空の場合のみフィルターIDを設定
        if (filterInitId == filterRawId) filterRawId = Item.getRawId(itemStack.item)

        // フィルタリング
        if (!equalItemFilter(itemStack.item)) return false

        // 満タンチェック
        var tmpCount = count
        val maxCount = itemStack.maxCount * size()
        if (maxCount == tmpCount) return false

        tmpCount += itemStack.count
        if (maxCount < tmpCount) {
            itemStack.count = tmpCount - maxCount
            tmpCount = maxCount
        } else {
            itemStack.count = 0
        }

        refillInventory(tmpCount, itemStack.maxCount)

        return true
    }

    // アイテムの取出
    fun takeStock(reqCount: Int): ItemStack {
        val invCount = count

        val takeCount = if (reqCount < invCount) reqCount else invCount

        refillInventory(invCount - takeCount, Item.byRawId(filterRawId).maxCount)

        return ItemStack(Item.byRawId(filterRawId), takeCount)
    }

    // フィルター関数
    fun equalItemFilter(item: Item): Boolean {
        val itemFiltered = Item.byRawId(filterRawId)

        return itemFiltered.equals(item)
    }

    // インベントリの詰め直し関数
    private fun refillInventory(count: Int, itemMaxCount: Int) {
        val countStack = count / itemMaxCount
        val countModulo = count % itemMaxCount
        val tmpInventory = DefaultedList.ofSize(size(), ItemStack.EMPTY)
        val itemFiltered = Item.byRawId(filterRawId)

        // フルスタックアイテムの格納
        for (index in 0 until countStack) {
            tmpInventory[index] = ItemStack(itemFiltered, itemMaxCount)
        }

        // 非フルスタックアイテムの格納
        if (size() > countStack) {
            tmpInventory[countStack] = ItemStack(itemFiltered, countModulo)
        }

        inventory = tmpInventory
    }

    override fun readNbt(nbt: NbtCompound?) {
        super.readNbt(nbt)
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY)

        if (null != nbt) {
            filterRawId = nbt.getInt(KEY_FILTER)
        }

        Inventories.readNbt(nbt, inventory)
    }

    override fun writeNbt(nbt: NbtCompound?): NbtCompound? {
        super.writeNbt(nbt)

        Inventories.writeNbt(nbt, inventory, false)

        nbt?.putInt(KEY_FILTER, filterRawId)

        return nbt
    }

    override fun fromClientTag(tag: NbtCompound?) {
        readNbt(tag)
    }

    override fun toClientTag(tag: NbtCompound?): NbtCompound? {
        return writeNbt(tag)
    }

    override fun markDirty() {
        super.markDirty()
        this.sync()
    }

    override fun size(): Int = inventory.size

    override fun canPlayerUse(player: PlayerEntity?): Boolean = true

    override fun clear() {
        inventory.clear()
    }

    override fun isEmpty(): Boolean = count == 0

    override fun getStack(slot: Int): ItemStack = inventory[slot]

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        val itemStack = Inventories.splitStack(inventory, slot, amount)

        if (!itemStack.isEmpty) {
            this.markDirty()
        }

        return itemStack
    }

    override fun removeStack(slot: Int): ItemStack {
        return Inventories.removeStack(inventory, slot)
    }

    override fun setStack(slot: Int, stack: ItemStack?) {
        inventory[slot] = stack
        if (stack!!.count > this.maxCountPerStack) {
            stack.count = this.maxCountPerStack
        }

        this.markDirty()
    }
}
