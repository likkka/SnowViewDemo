package com.example.rio.snowviewdemo.snowfallview;

import java.util.Random;

/**
 * Created by rio on 17-1-23.
 */
public class Randomizer {
    private Random random;

    private Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }

    public double randomDouble(int max) {
        return getRandom().nextDouble() * (max + 1);
    }

    public int randomInt(int min, int max, boolean gaussian) {
        return randomInt(max - min, gaussian) + min;
    }

    public int randomInt(int max, boolean gaussian) {
        return gaussian ? (int) (Math.abs(randomGaussian()) * (max + 1)) : getRandom().nextInt(max + 1);
    }


    public double randomGaussian() {
        double gaussian = getRandom().nextGaussian() / 3;
        return (gaussian > -1 && gaussian < 1) ? gaussian : randomGaussian();
    }

    public int randomSignum() {
        return getRandom().nextBoolean() ? 1 : -1;
    }
}
