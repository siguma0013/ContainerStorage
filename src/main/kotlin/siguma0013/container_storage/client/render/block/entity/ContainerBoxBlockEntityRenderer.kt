package siguma0013.container_storage.client.render.block.entity

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction.*
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f
import siguma0013.container_storage.block.entity.ContainerBoxBlockEntity


@Environment(EnvType.CLIENT)
class ContainerBoxBlockEntityRenderer : BlockEntityRenderer<ContainerBoxBlockEntity> {

    override fun render(
        entity: ContainerBoxBlockEntity?,
        tickDelta: Float,
        matrices: MatrixStack?,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int
    ) {
        matrices!!.push()

        // 向き調整
        when (entity?.cachedState?.get(Properties.FACING)) {
            NORTH -> {
                matrices.translate(0.5, 0.5, -0.001)
                matrices.multiply(Quaternion(Vec3f.POSITIVE_Y, 180F, true))
            }
            SOUTH -> {
                matrices.translate(0.5, 0.5, 1.001)
            }
            WEST -> {
                matrices.translate(-0.001, 0.5, 0.5)
                matrices.multiply(Quaternion(Vec3f.POSITIVE_Y, 270F, true))
            }
            EAST -> {
                matrices.translate(1.001, 0.5, 0.5)
                matrices.multiply(Quaternion(Vec3f.POSITIVE_Y, 90F, true))
            }
            else -> {}
        }

        // サイズ調整
        matrices.scale(0.4f, 0.4f ,0f)

        matrices.peek().normal.load(Matrix3f.scale(1f, 1f, 1f));
        val lightAbove = WorldRenderer.getLightmapCoordinates(entity?.world, entity?.pos?.up())

        MinecraftClient.getInstance().itemRenderer.renderItem(
            ItemStack(entity?.itemFiltered , 1),
            ModelTransformation.Mode.GUI,
            lightAbove,
            overlay,
            matrices,
            vertexConsumers,
            0
        )

        matrices.pop()


        matrices.push()

        // 向き調整
        when (entity?.cachedState?.get(Properties.FACING)) {
            NORTH -> {
                matrices.translate(0.5, 0.25, -0.001)
                matrices.multiply(Quaternion(Vec3f.POSITIVE_Y, 180F, true))
            }
            SOUTH -> {
                matrices.translate(0.5, 0.25, 1.001)
            }
            WEST -> {
                matrices.translate(-0.001, 0.25, 0.5)
                matrices.multiply(Quaternion(Vec3f.POSITIVE_Y, 270F, true))
            }
            EAST -> {
                matrices.translate(1.001, 0.25, 0.5)
                matrices.multiply(Quaternion(Vec3f.POSITIVE_Y, 90F, true))
            }
            else -> {}
        }

        matrices.multiply(Quaternion(Vec3f.POSITIVE_X, 180F, true))

        // サイズ調整
        matrices.scale(0.01f, 0.01f,1f)

        val count = entity?.count.toString()
        val width = MinecraftClient.getInstance().textRenderer.getWidth(count)
        val x_p = (width / 2).toFloat()

        MinecraftClient.getInstance().textRenderer.draw(matrices, count, -x_p, 0F, 0)

        matrices.pop()
    }

}