package thatpreston.warppads.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class WarpParticle extends TextureSheetParticle {
    public SpriteSet sprites;
    private float rotSpeed;
    protected WarpParticle(ClientLevel level, double x, double y, double z, double r, double g, double b, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        setColor((float)r, (float)g, (float)b);
        setSpriteFromAge(sprites);
        quadSize = 0.3F;
        lifetime = 2;
    }
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double r, double g, double b) {
            return new WarpParticle(level, x, y, z, r, g, b, sprites);
        }
    }
}