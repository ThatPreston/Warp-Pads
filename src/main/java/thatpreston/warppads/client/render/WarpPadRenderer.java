package thatpreston.warppads.client.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeRenderTypes;
import thatpreston.warppads.WarpPads;
import thatpreston.warppads.WarpPadUtils;
import thatpreston.warppads.block.WarpPadBlockEntity;

public class WarpPadRenderer implements BlockEntityRenderer<WarpPadBlockEntity> {
    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("warppads", "textures/warp_beam.png");
    private static final float[] DEFAULT_COLOR = {0.5F, 1, 1};
    private float[] color;
    public WarpPadRenderer(BlockEntityRendererProvider.Context context) {}
    @Override
    public void render(WarpPadBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        if(entity.shouldRender()) {
            float animation = entity.getAnimation() + partialTicks;
            float y1 = 0;
            float y2 = 6;
            float v = 0.75F;
            if(animation <= 10) {
                y2 = Mth.lerp(animation / 10, 0, y2);
            } else if(animation >= 30 && animation <= 40) {
                animation -= 30;
                y1 = Mth.lerp(animation / 10, 0, y2);
                v = Mth.clampedLerp(0.75F, 1, animation / 5);
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
            VertexConsumer consumer = source.getBuffer(ForgeRenderTypes.getUnlitTranslucent(BEAM_TEXTURE));
            renderBeam(stack, consumer, 1.5F, y1, y2, v);
            stack.popPose();
        }
    }
    private void addParticles(Level level, BlockPos pos, float apothem, int count) {
        Vec3 top = WarpPadUtils.getTopCenter(pos);
        for(int i = 0; i < count; i++) {
            Vec3 newPos = WarpPadUtils.getPositionOnSquare(top, apothem);
            level.addParticle(WarpPads.WARP_PARTICLE.get(), newPos.x, newPos.y + 0.1F, newPos.z, color[0], color[1], color[2]);
        }
    }
    private void renderBeam(PoseStack stack, VertexConsumer consumer, float apothem, float y1, float y2, float v) {
        PoseStack.Pose pose = stack.last();
        renderQuad(pose, consumer, -apothem, apothem, y1, y2, -apothem, -apothem, 0, 1, v, 0, 0, 0, -1);
        renderQuad(pose, consumer, apothem, apothem, y1, y2, -apothem, apothem, 0, 1, v, 0, 1, 0, 0);
        renderQuad(pose, consumer, apothem, -apothem, y1, y2, apothem, apothem, 0, 1, v, 0, 0, 0, 1);
        renderQuad(pose, consumer, -apothem, -apothem, y1, y2, apothem, -apothem, 0, 1, v, 0, -1, 0, 0);
    }
    private void renderQuad(PoseStack.Pose pose, VertexConsumer consumer, float x1, float x2, float y1, float y2, float z1, float z2, float u1, float u2, float v1, float v2, float nx, float ny, float nz) {
        renderVertex(consumer, pose, x1, y1, z1, u1, v1, nx, ny, nz);
        renderVertex(consumer, pose, x1, y2, z1, u1, v2, nx, ny, nz);
        renderVertex(consumer, pose, x2, y2, z2, u2, v2, nx, ny, nz);
        renderVertex(consumer, pose, x2, y1, z2, u2, v1, nx, ny, nz);
    }
    private void renderVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        consumer.vertex(pose.pose(), x, y, z).color(color[0], color[1], color[2], 0.5F).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), nx, ny, nz).endVertex();
    }
}