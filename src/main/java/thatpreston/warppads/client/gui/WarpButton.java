package thatpreston.warppads.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class WarpButton extends ExtendedButton {
    private TextComponent text;
    public WarpButton(int x, int y, int width, int height, TextComponent text, IPressable handler) {
        super(x, y, width, height, text, handler);
        this.text = text;
    }
    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer font = minecraft.font;
        int v = this.isHovered() ? 189 : 166;
        blit(stack, x, y, 0, v, 144, 23);
        int x2 = x + (144 - font.width(text)) / 2;
        int y2 = y + (23 - font.lineHeight) / 2;
        font.draw(stack, text, x2, y2, 4210752);
    }
}