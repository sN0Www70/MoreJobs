package com.snow.morejobs.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.snow.morejobs.MoreJobsMod;
import com.snow.morejobs.container.BankChestContainer;
import com.snow.morejobs.network.ModNetworking;
import com.snow.morejobs.network.packets.InterestRatePacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

public class BankChestScreen extends ContainerScreen<BankChestContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(MoreJobsMod.MODID, "textures/gui/bank_chest.png");

    private TextFieldWidget interestField;
    private Button validateButton;
    private double lastKnownRate = -1;

    public BankChestScreen(BankChestContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        int fieldX = this.leftPos + 8;
        int fieldY = this.topPos + 48;

        interestField = new TextFieldWidget(this.font, fieldX, fieldY, 60, 20, new StringTextComponent("Interest Rate"));
        interestField.setMaxLength(5);
        double currentRate = menu.getTileEntity().getInterestRate();
        interestField.setValue(String.format("%.2f", currentRate));
        lastKnownRate = currentRate;
        this.children.add(interestField);
        this.setInitialFocus(interestField);

        // Bouton parfaitement aligné avec bord droit (170)
        int buttonX = this.leftPos + 120; // 170 - 50
        int buttonY = this.topPos + 39;
        validateButton = new Button(buttonX, buttonY, 50, 20, new StringTextComponent("Valider"),
                button -> validateInterestRate());
        this.addButton(validateButton);
    }

    private void validateInterestRate() {
        try {
            double rate = Double.parseDouble(interestField.getValue().replace(',', '.'));
            if (rate >= 0 && rate <= 100) {
                ModNetworking.INSTANCE.sendToServer(new InterestRatePacket(menu.getTileEntity().getBlockPos(), rate));
            } else {
                interestField.setValue(String.format("%.2f", menu.getTileEntity().getInterestRate()));
            }
        } catch (NumberFormatException e) {
            interestField.setValue(String.format("%.2f", menu.getTileEntity().getInterestRate()));
        }
    }

    @Override
    public void tick() {
        super.tick();
        interestField.tick();

        double currentRate = menu.getTileEntity().getInterestRate();
        if (Math.abs(currentRate - lastKnownRate) > 0.001) {
            if (!interestField.isFocused()) {
                interestField.setValue(String.format("%.2f", currentRate));
            }
            lastKnownRate = currentRate;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (interestField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                validateInterestRate();
                return true;
            }
            if (interestField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (interestField.isFocused() && interestField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (interestField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(TEXTURE);

        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight, 176, 166);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
        super.renderLabels(matrixStack, x, y);

        this.font.draw(matrixStack, this.title.getString(), 8.0f, 6.0f, 4210752);

        // Ligne supérieure (titre gauche)
        this.font.draw(matrixStack, "Taux d'intérêt (%)", 8, 39, 4210752);

        // Ligne inférieure (champ actuel à droite sous le bouton)
        double currentRate = menu.getTileEntity().getInterestRate();
        String rateText = String.format("Actuel: %.2f%%", currentRate);
        int rateTextWidth = this.font.width(rateText);
        int actualTextX = 170 - rateTextWidth;
        int actualTextY = 60; // juste sous le bouton Valider
        this.font.draw(matrixStack, rateText, actualTextX, actualTextY, 4210752);

        int totalItems = menu.getTileEntity().getTotalItemCount();
        if (totalItems > 0) {
            String itemCountText = "Items: " + totalItems;
            int itemTextWidth = this.font.width(itemCountText);
            this.font.draw(matrixStack, itemCountText, 170 - itemTextWidth, 72, 4210752);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        interestField.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
