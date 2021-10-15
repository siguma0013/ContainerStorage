package siguma0013.container_storage

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.resource.ResourceManager
import siguma0013.container_storage.client.render.block.entity.ContainerBoxBlockEntityRenderer
import java.util.function.Function

class ContainerStorageClient : ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    override fun onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(ContainerStorage.CONTAINER_BOX_BLOCK_ENTITY
        ) { ContainerBoxBlockEntityRenderer() }
    }
}