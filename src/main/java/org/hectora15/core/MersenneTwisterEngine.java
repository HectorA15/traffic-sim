package org.hectora15.core;

import org.apache.commons.math3.random.MersenneTwister;

/**
 * 1. MERSENNE TWISTER METHOD
 * Core random number generator engine.
 */
public class MersenneTwisterEngine {

    private MersenneTwister rng;

    public MersenneTwisterEngine() {
        this.rng = new MersenneTwister();
    }

    public MersenneTwisterEngine(long seed) {
        this.rng = new MersenneTwister(seed);
    }

    public double nextDouble() {
        return this.rng.nextDouble();
    }

    public int nextInt(int maxValue) {
        return this.rng.nextInt(maxValue);
    }

    public long nextLong() {
        return this.rng.nextLong();
    }
}