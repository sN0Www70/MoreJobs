package com.snow.morejobs.gui.checkup;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CheckupScreen extends ContainerScreen<CheckupContainer> {

    // Texture GUI (à créer dans resources/assets/morejobs/textures/gui/checkup.png)
    private static final ResourceLocation BG = new ResourceLocation("morejobs", "textures/gui/checkup.png");

    public CheckupScreen(CheckupContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth  = 176; // Largeur standard Minecraft
        this.imageHeight = 240; // Hauteur pour deux inventaires l'un au-dessus de l'autre
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos  = (this.height - this.imageHeight) / 2;
    }

    @Override
    protected void renderBg(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(BG);
        blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack ms, int mouseX, int mouseY) {
        // Titre du GUI
        this.font.draw(ms, this.title, 8, 6, 0x404040);
        // Titre de l’inventaire du policier (celui qui regarde)
        this.font.draw(ms, this.inventory.getDisplayName(), 8, this.imageHeight - 94, 0x404040);
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
    }
}
