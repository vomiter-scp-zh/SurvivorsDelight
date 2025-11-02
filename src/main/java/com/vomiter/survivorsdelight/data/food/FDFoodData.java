package com.vomiter.survivorsdelight.data.food;

import com.vomiter.survivorsdelight.data.tags.SDTags;
import net.dries007.tfc.common.items.Food;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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
        meal();
        mushrooms();
        feast();
    }

    public void feast(){
        provider.newBuilder("feast/shepherds_pie")
                .ingredient(Ingredient.of(ModItems.SHEPHERDS_PIE.get(), ModItems.SHEPHERDS_PIE_BLOCK.get()).toJson())
                .type("dynamic")
                .save();

        provider.newBuilder("feast/honey_glazed_ham")
                .ingredient(Ingredient.of(ModItems.HONEY_GLAZED_HAM_BLOCK.get(), ModItems.HONEY_GLAZED_HAM.get()).toJson())
                .type("dynamic")
                .save();

        provider.newBuilder("feast/stuffed_pumpkin")
                .ingredient(Ingredient.of(ModItems.STUFFED_PUMPKIN_BLOCK.get(), ModItems.STUFFED_PUMPKIN.get()).toJson())
                .type("dynamic")
                .save();

        provider.newBuilder("feast/roasted_chicken")
                .ingredient(Ingredient.of(ModItems.ROAST_CHICKEN_BLOCK.get(), ModItems.ROAST_CHICKEN.get()).toJson())
                .type("dynamic")
                .save();

        provider.newBuilder("feast/rice_roll_medley")
                .item(ModItems.RICE_ROLL_MEDLEY_BLOCK.get())
                .setDecay(3.0f)
                .save();
    }

    public void mushrooms(){
        provider.newBuilder("mushrooms/general")
                .ingredient(Ingredient.of(Items.RED_MUSHROOM, Items.BROWN_MUSHROOM, ModItems.RED_MUSHROOM_COLONY.get(), ModItems.BROWN_MUSHROOM_COLONY.get()).toJson())
                .setDecay(5)
                .save();

        provider.newBuilder("mushrooms/brown_mushroom")
                .item(Items.BROWN_MUSHROOM)
                .setSaturation(1)
                .setDairy(0.5)
                .setVegetables(0.5)
                .save();
    }

    public void meal(){
        // 1) 動態餐點
        provider.newBuilder("meal/dynamic_meal")
                .tag(SDTags.ItemTags.DYNAMIC_MEALS.location().toString())
                .type("dynamic")
                .save();

        // 2) 先讀會被拿來當「基準」的 TFC 食物 -----------------
        var cooked_rice = provider.readTfcFoodJson(Food.COOKED_RICE);
        var onion = provider.readTfcFoodJson(Food.ONION);
        var cooked_egg = provider.readTfcFoodJson(Food.COOKED_EGG);
        var cooked_pork = provider.readTfcFoodJson(Food.PORK);
        var tomato = provider.readTfcFoodJson(Food.TOMATO);
        var cooked_beef = provider.readTfcFoodJson(Food.COOKED_BEEF);
        var cooked_mutton = provider.readTfcFoodJson(Food.COOKED_MUTTON);
        var baked_potato = provider.readTfcFoodJson(Food.BAKED_POTATO);
        var cooked_calamari = provider.readTfcFoodJson(Food.COOKED_CALAMARI);
        var garlic = provider.readTfcFoodJson(Food.GARLIC);

        float tagGrain = cooked_rice.grain();
        float tagGrainSat = cooked_rice.saturation();


        provider.newBuilder("meal/fried_rice")
                .item(ModItems.FRIED_RICE.get())
                .setDairy(cooked_egg.dairy())
                .setProtein(cooked_egg.protein())
                .setGrain(cooked_rice.grain())
                .setVegetables(onion.vegetables())
                .setHunger(cooked_rice.hunger())
                .setSaturation(cooked_rice.saturation() + cooked_egg.saturation() + onion.saturation())
                .save();

        provider.newBuilder("meal/bacon_and_eggs")
                .item(ModItems.BACON_AND_EGGS.get())
                .setDairy(cooked_egg.dairy() * 2)
                .setProtein(cooked_egg.protein() * 2 + cooked_pork.protein())
                .setHunger(cooked_pork.hunger())
                .setSaturation(cooked_egg.saturation() * 2 + cooked_pork.saturation())
                .save();


        // - meal/vegetable_noodles     // uses tag: tfc:foods/vegetables (x3)
        // - meal/ratatouille           // uses tag: tfc:foods/vegetables (x3)
        // - meal/grilled_salmon        // uses tag: survivorsdelight:fruit_for_cheesecake

        // 1) pasta_with_meatballs
        // tomato_sauce → tomato
        // raw_pasta → 用 cooked_rice 當一份穀類的代理
        // beef_patty → 用 cooked_beef
        provider.newBuilder("meal/pasta_with_meatballs")
                .item(ModItems.PASTA_WITH_MEATBALLS.get())
                .setDairy(0)
                .setProtein(cooked_beef.protein())
                .setGrain(cooked_rice.grain())
                .setVegetables(tomato.vegetables())
                .setHunger(cooked_rice.hunger())
                .setSaturation(
                        cooked_rice.saturation()
                                + cooked_beef.saturation()
                                + tomato.saturation()
                )
                .save();

        // 2) pasta_with_mutton_chop
        provider.newBuilder("meal/pasta_with_mutton_chop")
                .item(ModItems.PASTA_WITH_MUTTON_CHOP.get())
                .setDairy(0)
                .setProtein(cooked_mutton.protein())
                .setGrain(cooked_rice.grain())
                .setVegetables(tomato.vegetables() * 1.5)
                .setHunger(cooked_rice.hunger())
                .setSaturation(
                        cooked_rice.saturation()
                                + cooked_mutton.saturation()
                                + tomato.saturation()
                )
                .save();

        // 3) roasted_mutton_chops   // uses tag: tfc:foods/grains
        // mutton → 用 cooked_pork 當代理
        // tomato → tomato
        // tfc:foods/grains → 用 cooked_rice 當一份穀類
        provider.newBuilder("meal/roasted_mutton_chops")
                .item(ModItems.ROASTED_MUTTON_CHOPS.get())
                .setDairy(0)
                .setProtein(cooked_mutton.protein())
                .setGrain(tagGrain) // 這是標籤材料
                .setVegetables(tomato.vegetables())
                .setHunger(cooked_mutton.hunger()) // 以「肉」當主食量
                .setSaturation(
                        cooked_mutton.saturation()
                                + tomato.saturation()
                                + tagGrainSat
                )
                .save();

        // 4) vegetable_noodles      // uses tag: tfc:foods/vegetables (x3) // -> dynamic

        // 5) steak_and_potatoes
        // cooked_beef + baked_potato + onion
        provider.newBuilder("meal/steak_and_potatoes")
                .item(ModItems.STEAK_AND_POTATOES.get())
                .setProtein(cooked_beef.protein())
                .setVegetables(onion.vegetables() + baked_potato.vegetables())
                .setHunger(cooked_beef.hunger()) // 以馬鈴薯當主食
                .setSaturation(
                        cooked_beef.saturation()
                                + baked_potato.saturation()
                                + onion.saturation()
                )
                .save();

        // 6) ratatouille            // uses tag: tfc:foods/vegetables (x3) //-> dynamic

        // 7) squid_ink_pasta
        // raw_pasta → cooked_rice
        // ink_sac → none
        // cooked_calamari → cooked_calamari
        provider.newBuilder("meal/squid_ink_pasta")
                .item(ModItems.SQUID_INK_PASTA.get())
                .setProtein(cooked_calamari.protein())
                .setGrain(cooked_rice.grain())
                .setVegetables(garlic.vegetables())
                .setHunger(cooked_rice.hunger())
                .setSaturation(
                        cooked_rice.saturation()
                        + cooked_calamari.saturation()
                        + garlic.saturation()
                )
                .save();

        // 8) grilled_salmon         // uses tag: survivorsdelight:fruit_for_cheesecake // -> dynamic
        // cooked_salmon + onion + 「一份水果 tag」

        // 9) mushroom_rice
        // cooked_rice + red_mushroom + brown_mushroom + bone_broth

        provider.newBuilder("meal/mushroom_rice")
                .item(ModItems.MUSHROOM_RICE.get())
                .setDairy(2)
                .setGrain(cooked_rice.grain())
                .setVegetables(0.5)
                .setHunger(cooked_rice.hunger())
                .setSaturation(cooked_rice.saturation() + 2)
                .save();
    }

    public void soup(){
        provider.newBuilder("soups/soup")
                .tag(SDTags.ItemTags.SOUPS.location().toString())
                .setHunger(4)
                .setDecay(4.5)
                .type("dynamic")
                .save();

        provider.newBuilder("soup/bone_broth")
                .item(ModItems.BONE_BROTH.get())
                .setDecay(0.5)
                .setDairy(1)
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

        provider.newBuilder("food/tomato_sauce").item(ModItems.TOMATO_SAUCE.get())
                .multipliedFrom(Food.TOMATO, 2)
                .setDecay(4)
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
