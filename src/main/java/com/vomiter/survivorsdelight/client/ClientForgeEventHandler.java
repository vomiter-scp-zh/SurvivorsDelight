package com.vomiter.survivorsdelight.client;

import com.vomiter.survivorsdelight.core.device.stove.IStoveBlockEntity;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.data.Fuel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

public class ClientForgeEventHandler {

    public static void init(){
        final IEventBus bus = NeoForge.EVENT_BUS;
        bus.addListener(ClientForgeEventHandler::onRenderGameOverlayPost);

    }

    private static void drawCenteredText(Minecraft minecraft, GuiGraphics graphics, Component text, int x, int y)
    {
        final int textWidth = minecraft.font.width(text) / 2;
        graphics.drawString(minecraft.font, text, x - textWidth, y, 0xCCCCCC, false);
    }

    public static void onRenderGameOverlayPost(RenderGuiLayerEvent.Post event){
        final GuiGraphics stack = event.getGuiGraphics();
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;
        if (player != null)
        {
            boolean isHoldingFuel =
                    Fuel.get(player.getMainHandItem()) != null||
                            Fuel.get(player.getOffhandItem()) != null;
            if (
                    event.getName() == VanillaGuiLayers.CROSSHAIR
                    && minecraft.screen == null
                    && isHoldingFuel
                    && (! player.isShiftKeyDown())
            ) {
                final BlockPos targetedPos = ClientHelpers.getTargetedPos();
                assert minecraft.level != null;
                assert targetedPos != null;
                final BlockEntity targetedBlockEntity = minecraft.level.getBlockEntity(targetedPos);
                if(targetedBlockEntity instanceof IStoveBlockEntity iStove){
                    int x = stack.guiWidth() / 2 + 3;
                    int y = stack.guiHeight() / 2 + 8;
                    Component text = Component.translatable("overlay.survivorsdelight.stove_fuel_amount")
                            .append(Component.literal(": "))
                            .append(Component.literal(Integer.toString(iStove.sdtfc$getLeftBurnTick())));
                    drawCenteredText(minecraft, stack, text, x, y);
                }
            }
        }
    }

}
