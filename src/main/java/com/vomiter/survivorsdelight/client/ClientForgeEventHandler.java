package com.vomiter.survivorsdelight.client;

import com.mojang.blaze3d.platform.Window;
import com.vomiter.survivorsdelight.core.device.stove.IStoveBlockEntity;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.Fuel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientForgeEventHandler {

    public static void init(){
        final IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(ClientForgeEventHandler::onRenderGameOverlayPost);

        bus.addListener(SkilletClientHooks::onLeftClickBlock);
        bus.addListener(SkilletClientHooks::onLeftClickEmpty);
    }

    private static void drawCenteredText(Minecraft minecraft, GuiGraphics graphics, Component text, int x, int y)
    {
        final int textWidth = minecraft.font.width(text) / 2;
        graphics.drawString(minecraft.font, text, x - textWidth, y, 0xCCCCCC, false);
    }

    public static void onRenderGameOverlayPost(RenderGuiOverlayEvent.Post event){
        final GuiGraphics stack = event.getGuiGraphics();
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;
        if (player != null)
        {
            boolean isHoldingFuel =
                    Fuel.get(player.getMainHandItem()) != null||
                            Fuel.get(player.getOffhandItem()) != null;
            if (
                    event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()
                    && minecraft.screen == null
                    && isHoldingFuel
                    && (! player.isShiftKeyDown())
            ) {
                final BlockPos targetedPos = ClientHelpers.getTargetedPos();
                Window window = event.getWindow();
                assert minecraft.level != null;
                assert targetedPos != null;
                final BlockEntity targetedBlockEntity = minecraft.level.getBlockEntity(targetedPos);
                if(targetedBlockEntity instanceof IStoveBlockEntity iStove){
                    int x = window.getGuiScaledWidth() / 2 + 3;
                    int y = window.getGuiScaledHeight() / 2 + 8;
                    Component text = Component.translatable("overlay.survivorsdelight.stove_fuel_amount")
                            .append(Component.literal(": "))
                            .append(Component.literal(String.format(
                                    "%.1f",
                                    Math.min(100, 100f * (float)iStove.sdtfc$getLeftBurnTick() / (float)IStoveBlockEntity.sdtfc$getMaxDuration()))
                            ))
                            .append(Component.literal(" %"));
                    drawCenteredText(minecraft, stack, text, x, y);
                }
            }
        }
    }

}
