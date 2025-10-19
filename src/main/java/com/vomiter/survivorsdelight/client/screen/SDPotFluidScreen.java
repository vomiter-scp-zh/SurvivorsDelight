package com.vomiter.survivorsdelight.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vomiter.survivorsdelight.core.device.cooking_pot.SDCookingPotFluidMenu;
import com.vomiter.survivorsdelight.network.SDNetwork;
import net.dries007.tfc.client.RenderHelpers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

public class SDPotFluidScreen extends AbstractContainerScreen<SDCookingPotFluidMenu> {
    private static final ResourceLocation BG = new ResourceLocation("survivorsdelight", "textures/gui/pot_fluid.png");
    public static final int X_DEVIATION = 22;
    public static final int Y_DEVIATION = -3;

    @Unique
    private int sdtfc$recipeBtnX() { return this.leftPos + 5; }
    @Unique private int sdtfc$recipeBtnY() { return this.height / 2 - 49; }

    @Unique private int sdtfc$bucketX() { return sdtfc$recipeBtnX() + 2; }
    @Unique private int sdtfc$bucketY() { return sdtfc$recipeBtnY() - 20; }

    public SDPotFluidScreen(SDCookingPotFluidMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override protected void init() {
        super.init();
        SDBackToPotButton potButton = new SDBackToPotButton(
                sdtfc$bucketX(), sdtfc$bucketY(), Component.translatable("gui.survivorsdelight.pot.pot_menu")
        );
        this.addRenderableWidget(potButton);
    }

    @Override public void render(@NotNull GuiGraphics gg, int mouseX, int mouseY, float partial) {
        renderBackground(gg);
        super.render(gg, mouseX, mouseY, partial);
        renderTooltip(gg, mouseX, mouseY);
    }

    @Override protected void renderBg(GuiGraphics gg, float partialTicks, int mouseX, int mouseY) {
        gg.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int cap = menu.getFluidCapacity();
        FluidStack stack = menu.getClientFluid();

        if (!stack.isEmpty() && cap > 0 && stack.getAmount() > 0) {
            final TextureAtlasSprite sprite = RenderHelpers.getAndBindFluidSprite(stack);
            final int fillHeight = (int) Math.ceil((float) 50 * stack.getAmount() / 4000f);
            RenderHelpers.fillAreaWithSprite(gg, sprite, leftPos + 8 + X_DEVIATION, topPos + 70 +Y_DEVIATION - fillHeight, 16, fillHeight, 16, 16);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
        }
    }

}
