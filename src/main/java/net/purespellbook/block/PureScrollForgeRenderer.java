package net.purespellbook.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.item.InkItem;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.purespellbook.client.PureRenderTypes;

/**
 * Renders the Pure Scroll Forge with a steady enchanted glint overlay (via polygon offset)
 * and any placed ink bottle lying flat on the table matching ISS ScrollForgeRenderer.
 */
public class PureScrollForgeRenderer implements BlockEntityRenderer<PureScrollForgeBlockEntity> {
    private static final Vec3 INK_POS = new Vec3(0.175D, 0.876D, 0.25D);
    private final ItemRenderer itemRenderer;

    public PureScrollForgeRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PureScrollForgeBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BakedModel model = blockRenderer.getBlockModel(state);

        // Render enchanted glint using LEQUAL + polygon offset to prevent Z-fighting while staying visible
        poseStack.pushPose();
        RenderType glintType = PureRenderTypes.blockGlint();
        VertexConsumer glintConsumer = bufferSource.getBuffer(glintType);
        blockRenderer.getModelRenderer().renderModel(
                poseStack.last(),
                glintConsumer,
                state,
                model,
                1.0F, 1.0F, 1.0F,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                glintType
        );
        poseStack.popPose();

        // Render ink bottle flat on the table surface (matching ISS ScrollForgeRenderer)
        ItemStack inkStack = blockEntity.getStackInSlot(0);
        if (!inkStack.isEmpty() && inkStack.getItem() instanceof InkItem) {
            poseStack.pushPose();
            rotatePoseWithBlock(poseStack, state);
            poseStack.translate(INK_POS.x, INK_POS.y, INK_POS.z);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-15.0F));
            poseStack.scale(0.45F, 0.45F, 0.45F);
            this.itemRenderer.renderStatic(inkStack, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                    poseStack, bufferSource, blockEntity.getLevel(), (int) blockEntity.getBlockPos().asLong());
            poseStack.popPose();
        }
    }

    private static void rotatePoseWithBlock(PoseStack poseStack, BlockState state) {
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(getBlockFacingDegrees(state)));
        poseStack.translate(-0.5D, -0.5D, -0.5D);
    }

    private static int getBlockFacingDegrees(BlockState state) {
        Direction facing = state.getValue(PureScrollForgeBlock.FACING);
        return switch (facing) {
            case NORTH -> 180;
            case EAST -> 90;
            case WEST -> -90;
            default -> 0;
        };
    }
}
