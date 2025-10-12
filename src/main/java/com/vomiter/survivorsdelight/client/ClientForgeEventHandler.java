package com.vomiter.survivorsdelight.client;

import com.vomiter.survivorsdelight.core.device.stove.IStoveBlockEntity;
import com.vomiter.survivorsdelight.core.registry.SDSkilletItems;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.data.Fuel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.client.renderer.SkilletItemRenderer;
import vectorwing.farmersdelight.common.item.component.ItemStackWrapper;
import vectorwing.farmersdelight.common.registry.ModDataComponents;

public class ClientForgeEventHandler {

    public static void init(IEventBus modBus){
        final IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener(ClientForgeEventHandler::onRenderGameOverlayPost);
        modBus.addListener(ClientForgeEventHandler::registerClientExtensions);
        modBus.addListener(ClientForgeEventHandler::onClientSetup);
    }

    private static void registerSkilletPredicate(Item skillet){
        ItemProperties.register(skillet, ResourceLocation.withDefaultNamespace("cooking"),
                (stack, world, entity, s) -> stack.getOrDefault(ModDataComponents.SKILLET_INGREDIENT, ItemStackWrapper.EMPTY).getStack().isEmpty() ? 0 : 1);
    }

    public static void onClientSetup(FMLClientSetupEvent e){
        SDSkilletItems.SKILLETS.forEach((m, skillet) -> registerSkilletPredicate(skillet.get()));
        registerSkilletPredicate(SDSkilletItems.FARMER.get());
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


    private static IClientItemExtensions buildSkilletItemRenderer(){
        return new IClientItemExtensions() {
            final BlockEntityWithoutLevelRenderer renderer = new SkilletItemRenderer();
            @Override public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        };
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        var skilletItemRenderer = buildSkilletItemRenderer();
        SDSkilletItems.SKILLETS.forEach((m, skillet) -> event.registerItem(skilletItemRenderer, skillet.get()));
        event.registerItem(skilletItemRenderer, SDSkilletItems.FARMER.get());
    }

}
