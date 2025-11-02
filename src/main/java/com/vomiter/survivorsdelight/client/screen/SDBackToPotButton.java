package com.vomiter.survivorsdelight.client.screen;

import com.vomiter.survivorsdelight.network.SDNetwork;
import com.vomiter.survivorsdelight.network.cooking_pot.OpenBackToFDPotC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.ArrayList;
import java.util.List;

public class SDBackToPotButton extends AbstractButton {
    private final Component basicTooltip;

    public SDBackToPotButton(int x, int y,
                             Component tooltip
    ) {
        super(x, y, 16, 16, Component.empty());
        basicTooltip = tooltip;
    }

    @Override
    public void render(@NotNull GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);
        List<Component> tooltips = new ArrayList<>();
        tooltips.add(basicTooltip);
        if(this.isHoveredOrFocused()){
            gg.renderComponentTooltip(Minecraft.getInstance().font, tooltips, mouseX, mouseY);
        }
    }

    @Override
    public void onPress() {
        SDNetwork.CHANNEL.sendToServer(new OpenBackToFDPotC2SPacket());
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        if (this.isHoveredOrFocused()) {
            gg.fill(getX()-1, getY()-1, getX()+width+1, getY()+height+1, 0x80FFFFFF);
        }
        int x = getX();
        int y = getY() + (height - 16) / 2;
        Item cookingPot = ModItems.COOKING_POT.get();
        gg.renderItem(cookingPot.getDefaultInstance(), x, y);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput p_259858_) {

    }
}
