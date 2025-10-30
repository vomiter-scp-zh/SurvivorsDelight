package com.vomiter.survivorsdelight.data.food;

import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.dries007.tfc.common.items.Food;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.List;
import java.util.function.Supplier;

public class FDFoodData {
    SDFoodDataProvider provider;
    public FDFoodData(SDFoodDataProvider provider){
        this.provider = provider;
    }

    public void save(){
        fishRoll();
        cutFood();
        drink();
        pie();
        soup();
    }

    public void soup(){
        provider
                .newBuilder("soups/soup")
                .tag(SDTags.ItemTags.SOUPS.location().toString())
                .setHunger(4)
                .setDecay(4.5)
                .type("dynamic")
                .save();
    }

    public void pie(){
        var bread = provider.readTfcFoodJson(Food.BARLEY_BREAD);
        var apple = provider.readTfcFoodJson(Food.RED_APPLE);

        provider.newBuilder("pie/pie_crust")
                .item(ModItems.PIE_CRUST.get())
                .from(bread)
                .setDecay(bread.decayModifier() / 2f)
                .setDairy(0.5)
                .save();

        provider.newBuilder("pie/apple_pie")
                .tag(SDTags.ItemTags.PIES_APPLE_PIE.location().toString())
                .multipliedFrom(Food.RED_APPLE, 3)
                .setDairy(1)
                .setDecay(bread.decayModifier() / 2f)
                .setGrain(bread.grain())
                .setHunger(apple.hunger() * 3 + bread.hunger())
                .setSaturation(apple.saturation() * 3 + bread.saturation())
                .save();

        provider.newBuilder("pie/sweet_berry_cheesecake")
                .tag(SDTags.ItemTags.PIES_SWEET_BERRY_CHEESECAKE.location().toString())
                .multipliedFrom(Food.RASPBERRY, 3)
                .setDairy(1)
                .setDecay(bread.decayModifier() / 2f)
                .setGrain(bread.grain())
                .setHunger(apple.hunger() * 2 + bread.hunger())
                .setSaturation(apple.saturation() * 2 + bread.saturation())
                .save();

        provider.newBuilder("pie/chocolate_pie")
                .tag(SDTags.ItemTags.PIES_CHOCOLATE_PIE.location().toString())
                .setHunger(bread.hunger() + 4)
                .setGrain(bread.grain() + 1)
                .setSaturation(bread.saturation() + 1)
                .setDairy(2)
                .save();

    }

    public void fishRoll(){
        var cod = provider.readTfcFoodJson(Food.COD);
        var salmon = provider.readTfcFoodJson(Food.SALMON);
        var cooked_rice = provider.readTfcFoodJson(Food.COOKED_RICE);

        provider.newBuilder("food/cod_roll").item(ModItems.COD_ROLL.get())
                .slicedFrom(Food.COD, 2)
                .setGrain(cooked_rice.grain())
                .setHunger(cooked_rice.hunger() + Math.round(cod.hunger()/2f))
                .setSaturation(Math.max(cooked_rice.saturation(), cod.saturation()))
                .save();

        provider.newBuilder("food/salmon_roll").item(ModItems.SALMON_ROLL.get())
                .slicedFrom(Food.SALMON, 2)
                .setGrain(cooked_rice.grain())
                .setHunger(cooked_rice.hunger() + Math.round(salmon.hunger()/2f))
                .setSaturation(Math.max(cooked_rice.saturation(), salmon.saturation()))
                .save();
    }

    public static List<CutSpec> cutSpecs = List.of(
            cut2(ModItems.CABBAGE_LEAF, Food.CABBAGE),
            cut2(ModItems.MINCED_BEEF, Food.BEEF),
            cut2(ModItems.BEEF_PATTY, Food.COOKED_BEEF),
            cut2(ModItems.CHICKEN_CUTS, Food.CHICKEN),
            cut2(ModItems.COOKED_CHICKEN_CUTS, Food.COOKED_CHICKEN),
            cut2(ModItems.BACON, Food.PORK),
            cut2(ModItems.COOKED_BACON, Food.COOKED_PORK),
            cut2(ModItems.COD_SLICE, Food.COD),
            cut2(ModItems.COOKED_COD_SLICE, Food.COOKED_COD),
            cut2(ModItems.SALMON_SLICE, Food.SALMON),
            cut2(ModItems.COOKED_SALMON_SLICE, Food.COOKED_SALMON),
            cut2(ModItems.MUTTON_CHOPS, Food.MUTTON),
            cut2(ModItems.COOKED_MUTTON_CHOPS, Food.COOKED_MUTTON)
    );

    public void cutFood() {
        provider
                .newBuilder("cut/raw_pasta")
                .item(ModItems.RAW_PASTA.get())
                .from(provider.readTfcFoodJson(Food.BARLEY_DOUGH))
                .save();

        registerCuts(cutSpecs, provider);

        provider.newBuilder("ham").item(ModItems.HAM.get()).multipliedFrom(Food.PORK, 2).save();
        provider.newBuilder("smoked_ham").item(ModItems.SMOKED_HAM.get()).multipliedFrom(Food.COOKED_PORK, 2).save();
    }

    public record CutSpec(Supplier<? extends Item> item, Food from, int slices) {}

    public static CutSpec cut2(Supplier<? extends Item> item, Food from) {
        return new CutSpec(item, from, 2);
    }

    private void registerCuts(List<CutSpec> defs, SDFoodDataProvider provider) {
        for (var spec : defs) {
            final Item item = spec.item().get();
            final ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
            if (key == null) {
                throw new IllegalStateException("Unregistered item: " + item);
            }
            final String builderId = "cut/" + key.getPath(); // 規則：cut/ + item path

            provider
                    .newBuilder(builderId)
                    .item(item)
                    .slicedFrom(provider.readTfcFoodJson(spec.from()), spec.slices())
                    .save();
        }
    }

    public void drink(){
        var apple = provider.readTfcFoodJson(Food.RED_APPLE);
        var melon = provider.readTfcFoodJson(Food.MELON_SLICE);

        provider.newBuilder("drink/apple_cider").item(ModItems.APPLE_CIDER.get())
                .multipliedFrom(apple, 5)
                .setHunger(1)
                .setSaturation(0)
                .setWater(10)
                .save();

        provider.newBuilder("drink/melon_juice").item(ModItems.MELON_JUICE.get())
                .multipliedFrom(melon, 3)
                .setHunger(1)
                .setSaturation(0)
                .setWater(20)
                .save();

        provider.newBuilder("drink/hot_cocoa").item(ModItems.HOT_COCOA.get())
                .setDairy(1.5)
                .setHunger(0)
                .setSaturation(0)
                .setWater(10)
                .save();
    }

}
