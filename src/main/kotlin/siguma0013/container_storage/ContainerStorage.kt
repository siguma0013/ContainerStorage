package siguma0013.container_storage
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import siguma0013.container_storage.block.ContainerBoxBlock
import siguma0013.container_storage.block.entity.ContainerBoxBlockEntity

object ContainerStorage : ModInitializer {
    private const val MOD_ID = "container_storage"
    private val ITEM_GROUP = FabricItemGroupBuilder
        .create(getId("general"))
        .icon { ItemStack(Blocks.COBBLESTONE) }
        .build()
    private val GENERIC_ITEM_SETTINGS = FabricItemSettings().group(ITEM_GROUP)

    // Container Box
    private val CONTAINER_BOX_ID = getId("container_box")
    private val CONTAINER_BOX_BLOCK = ContainerBoxBlock(FabricBlockSettings.of(Material.METAL).strength(1f))
    lateinit var CONTAINER_BOX_BLOCK_ENTITY: BlockEntityType<ContainerBoxBlockEntity>

    override fun onInitialize() {
        Registry.register(Registry.BLOCK, CONTAINER_BOX_ID, CONTAINER_BOX_BLOCK)
        Registry.register(Registry.ITEM, CONTAINER_BOX_ID, BlockItem(CONTAINER_BOX_BLOCK, GENERIC_ITEM_SETTINGS))
        CONTAINER_BOX_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            CONTAINER_BOX_ID,
            FabricBlockEntityTypeBuilder.create(::ContainerBoxBlockEntity, CONTAINER_BOX_BLOCK).build())
    }

    private fun getId(path: String): Identifier = Identifier(MOD_ID, path)
}