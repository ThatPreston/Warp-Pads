package thatpreston.warppads.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class WarpButton extends ExtendedButton {
    private Component text;
    public WarpButton(int x, int y, int width, int height, Component text, OnPress handler) {
        super(x, y, width, height, text, handler);
        this.text = text;
    }
    @Override
    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int x = getX();
        int y = getY();
        int v = this.isHoveredOrFocused() ? 189 : 166;
        blit(stack, x, y, 0, v, 144, 23);
        int x2 = x + (144 - font.width(text)) / 2;
        int y2 = y + (23 - font.lineHeight) / 2;
        font.draw(stack, text, x2, y2, 4210752);
    }
}