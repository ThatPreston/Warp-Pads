package thatpreston.warppads.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import thatpreston.warppads.menu.WarpConfigMenu;

public class WarpConfigScreen extends ContainerScreen<WarpConfigMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("warppads", "textures/gui/warp_config.png");
    private TextFieldWidget name;
    public WarpConfigScreen(WarpConfigMenu menu, PlayerInventory inventory, ITextComponent title) {
        super(menu, inventory, title);
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 6;
        TextFieldWidget name = new TextFieldWidget(this.font, this.leftPos + 24, this.topPos + 22, 104, 12, new TranslationTextComponent("container.warppads.warp_config.hint"));
        name.setCanLoseFocus(true);
        name.setTextColor(-1);
        name.setTextColorUneditable(-1);
        name.setBordered(false);
        name.setMaxLength(50);
        String currentName = menu.getInfo().getName();
        name.setValue(currentName);
        if(currentName.length() <= 0) {
            this.setInitialFocus(name);
        }
        this.addWidget(name);
        this.name = name;
    }
    @Override
    public void onClose() {
        menu.saveName(name.getValue());
        super.onClose();
    }
    @Override
    protected void renderLabels(MatrixStack stack, int mouseX, int mouseY) {
        this.font.draw(stack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        this.font.draw(stack, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        minecraft.getTextureManager().bind(BACKGROUND);
        int x = this.leftPos;
        int y = this.topPos;
        blit(stack, x, y, 0, 0, this.imageWidth, this.imageHeight);
        if(!menu.hasDye()) {
            blit(stack, x + 138, y + 18, 176, 0, 20, 20);
        }
    }
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.name.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 256) {
            onClose();
        }
        return this.name.keyPressed(keyCode, scanCode, modifiers) || this.name.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }
}