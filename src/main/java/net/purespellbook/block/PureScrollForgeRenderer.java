package net.purespellbook.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

/**
 * Renders the Pure Scroll Forge with an enchanted glint overlay and any placed ink item on top.
 */
public class PureScrollForgeRenderer implements BlockEntityRenderer<PureScrollForgeBlockEntity> {
    public PureScrollForgeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PureScrollForgeBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BakedModel model = blockRenderer.getBlockModel(state);

        // Render enchanted glint overlay on top of the block model
        poseStack.pushPose();
        VertexConsumer glintConsumer = bufferSource.getBuffer(RenderType.glint());
        blockRenderer.getModelRenderer().renderModel(
                poseStack.last(),
                glintConsumer,
                state,
                model,
                1.0F, 1.0F, 1.0F,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.glint()
        );
        poseStack.popPose();

        // Render ink bottle on the table if present
        ItemStack inkStack = blockEntity.getStackInSlot(0);
        if (!inkStack.isEmpty()) {
            poseStack.pushPose();
            Direction facing = state.getValue(PureScrollForgeBlock.FACING);
            poseStack.translate(0.5D, 0.88D, 0.5D);
            poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            poseStack.translate(-0.25D, 0.0D, 0.0D);
            poseStack.scale(0.45F, 0.45F, 0.45F);
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderStatic(inkStack, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                    poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }
}
