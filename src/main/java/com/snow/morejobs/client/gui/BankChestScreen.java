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

    public BankChestScreen(BankChestContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        int slotY = this.topPos + 32;
        int fieldX = this.leftPos + 10;
        int buttonX = this.leftPos + 110;

        // Champ de texte
        interestField = new TextFieldWidget(this.font, fieldX, slotY, 60, 20, new StringTextComponent("Interest Rate"));
        interestField.setMaxLength(5);
        interestField.setValue(String.format("%.2f", menu.getTileEntity().getInterestRate()));
        this.children.add(interestField);
        this.setInitialFocus(interestField);

        // Bouton
        validateButton = new Button(buttonX, slotY, 50, 20, new StringTextComponent("Valider"),
                button -> validateInterestRate());
        this.addButton(validateButton);
    }

    private void validateInterestRate() {
        try {
            double rate = Double.parseDouble(interestField.getValue().replace(',', '.'));
            if (rate >= 0 && rate <= 100) {
                ModNetworking.INSTANCE.sendToServer(new InterestRatePacket(menu.getTileEntity().getBlockPos(), rate));
                menu.getTileEntity().setInterestRate(rate);
            }
        } catch (NumberFormatException e) {
            interestField.setValue(String.format("%.2f", menu.getTileEntity().getInterestRate()));
        }
    }

    @Override
    public void tick() {
        super.tick();
        interestField.tick();
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

        // Position des textes en dessous des éléments
        int textY = 32 + 22; // ligne sous le slot
        this.font.draw(matrixStack, "Taux d'intérêt (%)", 10, textY, 4210752);

        double currentRate = menu.getTileEntity().getInterestRate();
        String rateText = String.format("%.2f%%", currentRate);
        this.font.draw(matrixStack, "Actuel:", 110, textY, 4210752);
        this.font.draw(matrixStack, rateText, 110, textY + 10, 4210752); // une ligne en dessous
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        interestField.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
