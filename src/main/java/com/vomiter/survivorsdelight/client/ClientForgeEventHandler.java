package com.vomiter.survivorsdelight.client;

import com.mojang.blaze3d.platform.Window;
import com.vomiter.survivorsdelight.client.screen.SDCabinetScreen;
import com.vomiter.survivorsdelight.client.screen.SDPotFluidScreen;
import com.vomiter.survivorsdelight.compat.jei.JEIIntegration;
import com.vomiter.survivorsdelight.core.container.SDCabinetBlockEntity;
import com.vomiter.survivorsdelight.core.container.SDCabinetMenu;
import com.vomiter.survivorsdelight.core.device.cooking_pot.fluid_handle.SDCookingPotFluidMenu;
import com.vomiter.survivorsdelight.core.device.stove.IStoveBlockEntity;
import com.vomiter.survivorsdelight.core.registry.SDContainerTypes;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ClientForgeEventHandler {

    public static void init(){
        final IEventBus bus = NeoForge.EVENT_BUS;
        //bus.addListener(ClientForgeEventHandler::onRenderGameOverlayPost);
        bus.addListener(ClientForgeEventHandler::onItemTooltip);
        bus.addListener(SkilletClientHooks::onLeftClickBlock);
        bus.addListener(SkilletClientHooks::onLeftClickEmpty);

    }

    private static void drawCenteredText(Minecraft minecraft, GuiGraphics graphics, Component text, int x, int y)
    {
        final int textWidth = minecraft.font.width(text) / 2;
        graphics.drawString(minecraft.font, text, x - textWidth, y, 0xCCCCCC, false);
    }

    public static void registerMenuScreens(RegisterMenuScreensEvent event){
        event.register(SDContainerTypes.CABINET.get(), SDCabinetScreen::new);
        event.register(SDCookingPotFluidMenu.TYPE, SDPotFluidScreen::new);
    }


    /*
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
                if(minecraft.level == null) return;
                if(targetedPos == null) return;
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

     */

    private static void fallbackTooltip(ItemTooltipEvent event){
        /*
        final ItemStack stack = event.getItemStack();
        final IFood f = FoodCapability.get(stack);
        if (!(f instanceof FoodHandler.Dynamic dynamic)) return;
        boolean isInCreativeTab = f.getCreationDate() == -1L;
        var fallback = SDFallbackFoodData.get(stack.getItem());
        if(dynamic.getData().equals(fallback)
                && !fallback.equals(FoodData.EMPTY)
                && (isInCreativeTab || isInJEI())){
            event.getToolTip().add(Component.translatable("tooltip.survivorsdelight.foodfallback"));
        }

         */
    }

    private static void cabinetNotTreatedTooltip(ItemTooltipEvent event) {
        final Screen screen = Minecraft.getInstance().screen;
        if (!(screen instanceof SDCabinetScreen cabinetScreen)) return;
        if(!(cabinetScreen.getSlotUnderMouse() instanceof SDCabinetMenu.SDCabinetSlot cabinetSlot)) return;
        if(!(cabinetScreen.getMenu().getContainer() instanceof SDCabinetBlockEntity cabinet)) return;
        if(cabinet.isTreated()) return;
        if(FoodCapability.get(event.getItemStack()) == null) return;
        event.getToolTip().add(Component.translatable("tooltip.survivorsdelight.cabinet_not_treated").withStyle(ChatFormatting.RED));
    }


    public static void onItemTooltip(ItemTooltipEvent event) {
        fallbackTooltip(event);
        cabinetNotTreatedTooltip(event);
    }

    static boolean isInJEI(){
        if(ModList.get().isLoaded("jei")) return JEIIntegration.isJEIScreen();
        return false;
    }
}
