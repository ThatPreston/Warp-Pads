package thatpreston.warppads.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeRenderTypes;
import thatpreston.warppads.WarpPads;
import thatpreston.warppads.WarpPadUtils;
import thatpreston.warppads.block.WarpPadBlockEntity;

public class WarpPadRenderer extends TileEntityRenderer<WarpPadBlockEntity> {
    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("warppads", "textures/warp_beam.png");
    private static final float[] DEFAULT_COLOR = {0.5F, 1, 1};
    private float[] color;
    public WarpPadRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    @Override
    public void render(WarpPadBlockEntity entity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        if(entity.shouldRender()) {
            float animation = entity.getAnimation() + partialTicks;
            float y1 = 0;
            float y2 = 6;
            float v = 0.75F;
            if(animation <= 10) {
                y2 = MathHelper.lerp(animation / 10, 0, y2);
            } else if(animation >= 30 && animation <= 40) {
                animation -= 30;
                y1 = MathHelper.lerp(animation / 10, 0, y2);
                v = (float)MathHelper.clampedLerp(0.75F, 1, animation / 5);
            } else if(animation > 40) {
                return;
            }
            float[] cachedColor = entity.getCachedColor();
            color = cachedColor != null ? cachedColor : DEFAULT_COLOR;
            if(y1 == 0) {
                addParticles(entity.getLevel(), entity.getBlockPos(), 1.5F, 2);
            }
            stack.pushPose();
            stack.translate(0.5F, 1, 0.5F);
            IVertexBuilder builder = buffer.getBuffer(ForgeRenderTypes.getUnlitTranslucent(BEAM_TEXTURE));
            renderBeam(stack, builder, 1.5F, y1, y2, v);
            stack.popPose();
        }
    }
    private void addParticles(World world, BlockPos pos, float apothem, int count) {
        Vector3d top = WarpPadUtils.getTopCenter(pos);
        for(int i = 0; i < count; i++) {
            Vector3d newPos = WarpPadUtils.getPositionOnSquare(top, apothem);
            world.addParticle(WarpPads.WARP_PARTICLE.get(), newPos.x, newPos.y + 0.1F, newPos.z, color[0], color[1], color[2]);
        }
    }
    private void renderBeam(MatrixStack stack, IVertexBuilder builder, float apothem, float y1, float y2, float v) {
        MatrixStack.Entry entry = stack.last();
        renderQuad(entry, builder, -apothem, apothem, y1, y2, -apothem, -apothem, 0, 1, v, 0, 0, 0, -1);
        renderQuad(entry, builder, apothem, apothem, y1, y2, -apothem, apothem, 0, 1, v, 0, 1, 0, 0);
        renderQuad(entry, builder, apothem, -apothem, y1, y2, apothem, apothem, 0, 1, v, 0, 0, 0, 1);
        renderQuad(entry, builder, -apothem, -apothem, y1, y2, apothem, -apothem, 0, 1, v, 0, -1, 0, 0);
    }
    private void renderQuad(MatrixStack.Entry entry, IVertexBuilder builder, float x1, float x2, float y1, float y2, float z1, float z2, float u1, float u2, float v1, float v2, float nx, float ny, float nz) {
        renderVertex(builder, entry, x1, y1, z1, u1, v1, nx, ny, nz);
        renderVertex(builder, entry, x1, y2, z1, u1, v2, nx, ny, nz);
        renderVertex(builder, entry, x2, y2, z2, u2, v2, nx, ny, nz);
        renderVertex(builder, entry, x2, y1, z2, u2, v1, nx, ny, nz);
    }
    private void renderVertex(IVertexBuilder builder, MatrixStack.Entry entry, float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        builder.vertex(entry.pose(), x, y, z).color(color[0], color[1], color[2], 0.5F).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 15)).normal(entry.normal(), nx, ny, nz).endVertex();
    }
}