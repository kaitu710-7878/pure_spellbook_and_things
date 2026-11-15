package net.purespellbook.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;

/**
 * Custom RenderTypes for client-side block entity overlays.
 */
public final class PureRenderTypes extends RenderType {
    private static final RenderType BLOCK_GLINT = create(
            "pure_block_glint",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER)
                    .setTextureState(new TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ENTITY, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(ENTITY_GLINT_TEXTURING)
                    .setLayeringState(POLYGON_OFFSET_LAYERING)
                    .createCompositeState(false)
    );

    private PureRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                            boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    /**
     * Enchantment glint RenderType using LEQUAL depth test and polygon offset layering
     * so placed blocks show a steady glint without Z-fighting.
     */
    public static RenderType blockGlint() {
        return BLOCK_GLINT;
    }
}
