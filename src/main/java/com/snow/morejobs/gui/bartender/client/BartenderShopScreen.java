package com.snow.morejobs.gui.bartender.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.snow.morejobs.data.BartenderShopData;
import com.snow.morejobs.gui.bartender.BartenderShopContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

public class BartenderShopScreen extends ContainerScreen<BartenderShopContainer> {

    private static final int SLOT_SIZE = 20;
    private static final int SLOTS_PER_ROW = 8;
    private static final int PADDING = 12;
    private static final int HEADER_HEIGHT = 30;

    public BartenderShopScreen(BartenderShopContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        // Calcul dynamique de la taille basé sur le nombre d'items
        int itemCount = Math.max(24, BartenderShopData.SHOP_ITEMS.size()); // Minimum 24 slots
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

        // Fond principal avec dégradé moderne (thème bar/taverne)
        this.renderTavernBackground(matrixStack, x, y, this.imageWidth, this.imageHeight);

        // Header avec titre
        this.renderHeader(matrixStack, x, y);

        // Rendu des slots et items
        this.renderShopSlots(matrixStack);
    }

    private void renderTavernBackground(MatrixStack matrixStack, int x, int y, int width, int height) {
        // Fond principal (brun chaleureux pour une taverne)
        fill(matrixStack, x, y, x + width, y + height, 0xE03A2A1A);

        // Bordure externe (dorée/bronze)
        fill(matrixStack, x, y, x + width, y + 1, 0xFFB8860B); // Haut
        fill(matrixStack, x, y, x + 1, y + height, 0xFFB8860B); // Gauche
        fill(matrixStack, x + width - 1, y, x + width, y + height, 0xFFB8860B); // Droite
        fill(matrixStack, x, y + height - 1, x + width, y + height, 0xFFB8860B); // Bas

        // Bordure interne (accent doré plus clair)
        fill(matrixStack, x + 1, y + 1, x + width - 1, y + 2, 0xFFDAA520); // Haut interne
        fill(matrixStack, x + 1, y + 1, x + 2, y + height - 1, 0xFFDAA520); // Gauche interne
    }

    private void renderHeader(MatrixStack matrixStack, int x, int y) {
        // Fond du header (plus foncé avec teinte rougeâtre)
        fill(matrixStack, x + 1, y + 1, x + this.imageWidth - 1, y + HEADER_HEIGHT, 0xFF2A1A0A);

        // Ligne de séparation (dorée)
        fill(matrixStack, x + 1, y + HEADER_HEIGHT, x + this.imageWidth - 1, y + HEADER_HEIGHT + 1, 0xFFB8860B);
    }

    private void renderShopSlots(MatrixStack matrixStack) {
        ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
        Map<Integer, BartenderShopData.ShopItem> items = BartenderShopData.SHOP_ITEMS;

        int startX = this.leftPos + PADDING;
        int startY = this.topPos + HEADER_HEIGHT + PADDING;

        for (Map.Entry<Integer, BartenderShopData.ShopItem> entry : items.entrySet()) {
            int slot = entry.getKey();
            int row = slot / SLOTS_PER_ROW;
            int col = slot % SLOTS_PER_ROW;

            int slotX = startX + col * (SLOT_SIZE + 2);
            int slotY = startY + row * (SLOT_SIZE + 2);

            // Rendu du slot moderne avec thème taverne
            this.renderTavernSlot(matrixStack, slotX, slotY, false);

            // Rendu de l'item
            ItemStack stack = entry.getValue().stack.copy();
            itemRenderer.renderAndDecorateItem(stack, slotX + 2, slotY + 2);
            itemRenderer.renderGuiItemDecorations(this.font, stack, slotX + 2, slotY + 2);
        }
    }

    private void renderTavernSlot(MatrixStack matrixStack, int x, int y, boolean hovered) {
        // Couleur du slot basée sur l'état (tons bruns/dorés)
        int slotColor = hovered ? 0xFF4A3A2A : 0xFF3A2A1A;
        int borderColor = hovered ? 0xFFDAA520 : 0xFFB8860B;

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

        // Tooltips des items
        this.renderItemTooltips(matrixStack, mouseX, mouseY);
    }

    private void renderSlotHighlights(MatrixStack matrixStack, int mouseX, int mouseY) {
        int startX = this.leftPos + PADDING;
        int startY = this.topPos + HEADER_HEIGHT + PADDING;

        for (Map.Entry<Integer, BartenderShopData.ShopItem> entry : BartenderShopData.SHOP_ITEMS.entrySet()) {
            int slot = entry.getKey();
            int row = slot / SLOTS_PER_ROW;
            int col = slot % SLOTS_PER_ROW;

            int slotX = startX + col * (SLOT_SIZE + 2);
            int slotY = startY + row * (SLOT_SIZE + 2);

            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                    mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {

                // Highlight doré semi-transparent pour thème taverne
                fill(matrixStack, slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0x80DAA520);
            }
        }
    }

    private void renderItemTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        int startX = this.leftPos + PADDING;
        int startY = this.topPos + HEADER_HEIGHT + PADDING;

        for (Map.Entry<Integer, BartenderShopData.ShopItem> entry : BartenderShopData.SHOP_ITEMS.entrySet()) {
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

        for (Map.Entry<Integer, BartenderShopData.ShopItem> entry : BartenderShopData.SHOP_ITEMS.entrySet()) {
            int slot = entry.getKey();
            int row = slot / SLOTS_PER_ROW;
            int col = slot % SLOTS_PER_ROW;

            int slotX = startX + col * (SLOT_SIZE + 2);
            int slotY = startY + row * (SLOT_SIZE + 2);

            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                    mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {

                boolean rightClick = button == 1;
                this.minecraft.player.closeContainer();
                this.minecraft.player.chat("/barbuy " + slot + (rightClick ? " right" : ""));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Titre moderne centré dans le header avec couleur dorée
        String titleText = this.title.getString();
        int titleWidth = this.font.width(titleText);
        int titleX = (this.imageWidth - titleWidth) / 2;
        int titleY = (HEADER_HEIGHT - 8) / 2;

        // Couleur dorée pour le titre du barman
        this.font.draw(matrixStack, titleText, titleX, titleY, 0xFFDAA520);
    }
}