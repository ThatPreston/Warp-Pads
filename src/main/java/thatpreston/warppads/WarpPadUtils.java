package thatpreston.warppads;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

public class WarpPadUtils {
    public static final Random RANDOM = new Random();
    public static float getRandom() {
        return RANDOM.nextFloat();
    }
    public static Vector3d getTopCenter(BlockPos pos) {
        return Vector3d.upFromBottomCenterOf(pos, 1);
    }
    public static float getDirectionX(float angle) {
        return (float)Math.sin(2 * Math.PI * angle);
    }
    public static float getDirectionZ(float angle) {
        return (float)Math.cos(2 * Math.PI * angle);
    }
    public static Vector3d getDirection(float angle) {
        float x = getDirectionX(angle);
        float z = getDirectionZ(angle);
        return new Vector3d(x, 0, z);
    }
    public static Vector3d getDirection() {
        return getDirection(getRandom());
    }
    public static Vector3d getPositionOnSquare(Vector3d pos, float apothem) {
        float radius = MathHelper.sqrt(2 * MathHelper.square(apothem));
        Vector3d dir = getDirection().scale(radius);
        double x = MathHelper.clamp(dir.x, -apothem, apothem);
        double z = MathHelper.clamp(dir.z, -apothem, apothem);
        return pos.add(x, 0, z);
    }
    public static AxisAlignedBB getBoxAbovePosition(Vector3d pos, float width, float height) {
        Vector3d bottom = pos.add(-width / 2, 0, -width / 2);
        Vector3d top = bottom.add(width, height, width);
        return new AxisAlignedBB(bottom, top);
    }
    public static float[] brightenColor(float[] color, float delta) {
        float r = Math.min(1, color[0] + delta);
        float g = Math.min(1, color[1] + delta);
        float b = Math.min(1, color[2] + delta);
        return new float[]{r, g, b};
    }
}