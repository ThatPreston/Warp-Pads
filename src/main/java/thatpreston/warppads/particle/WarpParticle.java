package thatpreston.warppads.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class WarpParticle extends SpriteTexturedParticle {
    public IAnimatedSprite sprites;
    protected WarpParticle(ClientWorld world, double x, double y, double z, double r, double g, double b, IAnimatedSprite sprites) {
        super(world, x, y, z);
        this.sprites = sprites;
        setColor((float)r, (float)g, (float)b);
        setSpriteFromAge(sprites);
        quadSize = 0.3F;
        lifetime = 2;
    }
    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;
        public Factory(IAnimatedSprite sprites) {
            this.sprites = sprites;
        }
        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new WarpParticle(world, x, y, z, r, g, b, sprites);
        }
    }
}