package com.vomiter.survivorsdelight.util;

import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.Nutrient;

import java.util.Arrays;

public class FoodDataBuilder {

    private int hunger = 0;
    private float water = 0;
    private float saturation = 0;
    private final float[] nutrients = new float[Nutrient.values().length];
    private float decayModifier = 1;

    public static FoodDataBuilder create() {
        return new FoodDataBuilder();
    }

    public static FoodDataBuilder from(FoodData data) {
        FoodDataBuilder builder = new FoodDataBuilder();

        builder.hunger = data.hunger();
        builder.water = data.water();
        builder.saturation = data.saturation();
        builder.decayModifier = data.decayModifier();

        float[] sourceNutrients = data.nutrients();
        System.arraycopy(
                sourceNutrients,
                0,
                builder.nutrients,
                0,
                Math.min(sourceNutrients.length, builder.nutrients.length)
        );

        return builder;
    }

    public FoodDataBuilder hunger(int hunger) {
        this.hunger = hunger;
        return this;
    }

    public FoodDataBuilder addHunger(int hunger) {
        this.hunger += hunger;
        return this;
    }

    public FoodDataBuilder water(float water) {
        this.water = water;
        return this;
    }

    public FoodDataBuilder addWater(float water) {
        this.water += water;
        return this;
    }

    public FoodDataBuilder saturation(float saturation) {
        this.saturation = saturation;
        return this;
    }

    public FoodDataBuilder addSaturation(float saturation) {
        this.saturation += saturation;
        return this;
    }

    public FoodDataBuilder nutrient(Nutrient nutrient, float value) {
        nutrients[nutrient.ordinal()] = value;
        return this;
    }

    public FoodDataBuilder addNutrient(Nutrient nutrient, float value) {
        nutrients[nutrient.ordinal()] += value;
        return this;
    }

    public FoodDataBuilder nutrients(float... nutrients) {
        Arrays.fill(this.nutrients, 0);

        System.arraycopy(
                nutrients,
                0,
                this.nutrients,
                0,
                Math.min(nutrients.length, this.nutrients.length)
        );

        return this;
    }

    public FoodDataBuilder decayModifier(float decayModifier) {
        this.decayModifier = decayModifier;
        return this;
    }

    public int hunger() {
        return hunger;
    }

    public float water() {
        return water;
    }

    public float saturation() {
        return saturation;
    }

    public float nutrient(Nutrient nutrient) {
        return nutrients[nutrient.ordinal()];
    }

    public float[] nutrients() {
        return nutrients.clone();
    }

    public float decayModifier() {
        return decayModifier;
    }

    public FoodDataBuilder mul(float factor) {
        hunger = Math.round(hunger * factor);
        water *= factor;
        saturation *= factor;

        for (int i = 0; i < nutrients.length; i++) {
            nutrients[i] *= factor;
        }

        return this;
    }

    public FoodDataBuilder mulNutrient(float factor, Nutrient... nutrient) {
        for (int i = 0; i < nutrients.length; i++) {
            int finalI = i;
            if (Arrays.stream(nutrient).anyMatch(n->n.ordinal() == finalI)) nutrients[i] *= factor;
        }
        return this;
    }


    public FoodDataBuilder addBuilder(FoodDataBuilder other) {
        hunger += other.hunger;
        water += other.water;
        saturation += other.saturation;

        for (int i = 0; i < nutrients.length; i++) {
            nutrients[i] += other.nutrients[i];
        }

        return this;
    }

    public FoodDataBuilder addData(FoodData data) {
        hunger += data.hunger();
        water += data.water();
        saturation += data.saturation();

        float[] otherNutrients = data.nutrients();
        int length = Math.min(nutrients.length, otherNutrients.length);

        for (int i = 0; i < length; i++) {
            nutrients[i] += otherNutrients[i];
        }

        return this;
    }

    public FoodData build() {
        return new FoodData(
                hunger,
                water,
                saturation,
                nutrients[0],
                nutrients[1],
                nutrients[2],
                nutrients[3],
                nutrients[4],
                decayModifier
        );
    }

    public boolean isEmpty() {
        if (hunger != 0) return false;
        if (Float.compare(water, 0f) != 0) return false;
        if (Float.compare(saturation, 0f) != 0) return false;

        for (float nutrient : nutrients) {
            if (Float.compare(nutrient, 0f) != 0) {
                return false;
            }
        }

        return true;
    }
}