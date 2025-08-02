package com.snow.morejobs.gui.architect.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.snow.morejobs.data.ArchitectShopData;
import com.snow.morejobs.gui.architect.ArchitectShopContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

public class ArchitectShopScreen extends ContainerScreen<ArchitectShopContainer> {

    private static final int SLOT_SIZE = 20;
    private static final int SLOTS_PER_ROW = 8;
    private static final int PADDING = 12;
    private static final int HEADER_HEIGHT = 30;

    public ArchitectShopScreen(ArchitectShopContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        // Calcul dynamique de la taille basé sur le nombre d'items
        int itemCount = Math.max(24, ArchitectShopData.SHOP_ITEMS.size()); // Minimum 24 slots
        int rows = (itemCount + SLOTS_PER_ROW - 1) / SLOTS_PER_ROW;

        this.imageWidth = PADDING * 2 + SLOTS_PER_ROW * SLOT_SIZE + (SLOTS_PER_ROW - 1) * 2;
        this.imageHeight = HEADER_HEIGHT + PADDING * 2 + rows * SLOT_SIZE + (rows - 1) * 2;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        // Fond sombre semi-transparent
        this.renderBackground(matrixStack);

        int x = this.leftPos;
        int y = this.topPos;

        // Fond principal avec dégradé moderne
        this.renderModernBackground(matrixStack, x, y, this.imageWidth, this.imageHeight);

        // Header avec titre
        this.renderHeader(matrixStack, x, y);

        // Rendu des slots et items
        this.renderShopSlots(matrixStack);
    }

    private void renderModernBackground(MatrixStack matrixStack, int x, int y, int width, int height) {
        // Fond principal (gris moderne)
        fill(matrixStack, x, y, x + width, y + height, 0xE0282828);

        // Bordure externe (plus claire)
        fill(matrixStack, x, y, x + width, y + 1, 0xFF404040); // Haut
        fill(matrixStack, x, y, x + 1, y + height, 0xFF404040); // Gauche
        fill(matrixStack, x + width - 1, y, x + width, y + height, 0xFF404040); // Droite
        fill(matrixStack, x, y + height - 1, x + width, y + height, 0xFF404040); // Bas

        // Bordure interne (accent)
        fill(matrixStack, x + 1, y + 1, x + width - 1, y + 2, 0xFF505050); // Haut interne
        fill(matrixStack, x + 1, y + 1, x + 2, y + height - 1, 0xFF505050); // Gauche interne
    }

    private void renderHeader(MatrixStack matrixStack, int x, int y) {
        // Fond du header (plus foncé)
        fill(matrixStack, x + 1, y + 1, x + this.imageWidth - 1, y + HEADER_HEIGHT, 0xFF1A1A1A);

        // Ligne de séparation
        fill(matrixStack, x + 1, y + HEADER_HEIGHT, x + this.imageWidth - 1, y + HEADER_HEIGHT + 1, 0xFF404040);
    }

    private void renderShopSlots(MatrixStack matrixStack) {
        ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
        Map<Integer, ArchitectShopData.ShopItem> items = ArchitectShopData.SHOP_ITEMS;

        int startX = this.leftPos + PADDING;
        int startY = this.topPos + HEADER_HEIGHT + PADDING;

        for (Map.Entry<Integer, ArchitectShopData.ShopItem> entry : items.entrySet()) {
            int slot = entry.getKey();
            int row = slot / SLOTS_PER_ROW;
            int col = slot % SLOTS_PER_ROW;

            int slotX = startX + col * (SLOT_SIZE + 2);
            int slotY = startY + row * (SLOT_SIZE + 2);

            // Rendu du slot moderne
            this.renderModernSlot(matrixStack, slotX, slotY, false);

            // Rendu de l'item
            ItemStack stack = entry.getValue().stack.copy();
            itemRenderer.renderAndDecorateItem(stack, slotX + 2, slotY + 2);
            itemRenderer.renderGuiItemDecorations(this.font, stack, slotX + 2, slotY + 2);
        }
    }

    private void renderModernSlot(MatrixStack matrixStack, int x, int y, boolean hovered) {
        // Couleur du slot basée sur l'état
        int slotColor = hovered ? 0xFF3A3A3A : 0xFF2A2A2A;
        int borderColor = hovered ? 0xFF5A5A5A : 0xFF4A4A4A;

        // Fond du slot
        fill(matrixStack, x, y, x + SLOT_SIZE, y + SLOT_SIZE, slotColor);

        // Bordure du slot
        fill(matrixStack, x, y, x + SLOT_SIZE, y + 1, borderColor); // Haut
        fill(matrixStack, x, y, x + 1, y + SLOT_SIZE, borderColor); // Gauche
        fill(matrixStack, x + SLOT_SIZE - 1, y, x + SLOT_SIZE, y + SLOT_SIZE, borderColor); // Droite
        fill(matrixStack, x, y + SLOT_SIZE - 1, x + SLOT_SIZE, y + SLOT_SIZE, borderColor); // Bas
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // Highlight des slots au survol
        this.renderSlotHighlights(matrixStack, mouseX, mouseY);

        // Tooltips des items - CORRECTION: Déplacé ici pour utiliser les bonnes coordonnées
        this.renderItemTooltips(matrixStack, mouseX, mouseY);
    }

    private void renderSlotHighlights(MatrixStack matrixStack, int mouseX, int mouseY) {
        int startX = this.leftPos + PADDING;
        int startY = this.topPos + HEADER_HEIGHT + PADDING;

        for (Map.Entry<Integer, ArchitectShopData.ShopItem> entry : ArchitectShopData.SHOP_ITEMS.entrySet()) {
            int slot = entry.getKey();
            int row = slot / SLOTS_PER_ROW;
            int col = slot % SLOTS_PER_ROW;

            int slotX = startX + col * (SLOT_SIZE + 2);
            int slotY = startY + row * (SLOT_SIZE + 2);

            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                    mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {

                // Highlight semi-transparent
                fill(matrixStack, slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0x80FFFFFF);
            }
        }
    }

    private void renderItemTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        int startX = this.leftPos + PADDING;
        int startY = this.topPos + HEADER_HEIGHT + PADDING;

        for (Map.Entry<Integer, ArchitectShopData.ShopItem> entry : ArchitectShopData.SHOP_ITEMS.entrySet()) {
            int slot = entry.getKey();
            int row = slot / SLOTS_PER_ROW;
            int col = slot % SLOTS_PER_ROW;

            int slotX = startX + col * (SLOT_SIZE + 2);
            int slotY = startY + row * (SLOT_SIZE + 2);

            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                    mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {

                String itemName = entry.getValue().stack.getHoverName().getString();
                String price = "§6Prix: §f" + entry.getValue().price + "§7/u";
                String action = "§8Clic gauche: Acheter 1 | Clic droit: Acheter 16";

                String tooltipText = itemName + "\n" + price + "\n" + action;

                this.renderTooltip(matrixStack, new StringTextComponent(tooltipText), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = this.leftPos + PADDING;
        int startY = this.topPos + HEADER_HEIGHT + PADDING;

        for (Map.Entry<Integer, ArchitectShopData.ShopItem> entry : ArchitectShopData.SHOP_ITEMS.entrySet()) {
            int slot = entry.getKey();
            int row = slot / SLOTS_PER_ROW;
            int col = slot % SLOTS_PER_ROW;

            int slotX = startX + col * (SLOT_SIZE + 2);
            int slotY = startY + row * (SLOT_SIZE + 2);

            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                    mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {

                boolean rightClick = button == 1;
                this.minecraft.player.closeContainer();
                this.minecraft.player.chat("/archibuy " + slot + (rightClick ? " right" : ""));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Titre moderne centré dans le header
        String titleText = this.title.getString();
        int titleWidth = this.font.width(titleText);
        int titleX = (this.imageWidth - titleWidth) / 2;
        int titleY = (HEADER_HEIGHT - 8) / 2;

        this.font.draw(matrixStack, titleText, titleX, titleY, 0xFFFFFF);

    }
}
