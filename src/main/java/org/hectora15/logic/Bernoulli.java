package org.hectora15.logic;


public class Bernoulli {
    private double successProbability;
    private MersenneTwisterEngine rng;

    public Bernoulli(double successProbability, MersenneTwisterEngine rng) {
        if (successProbability < 0 || successProbability > 1) {
            throw new IllegalArgumentException("p must be in [0, 1]");
        }
        this.successProbability = successProbability;
        this.rng = rng;
    }

    public boolean trial() {
        return rng.nextDouble() < successProbability;
    }

    public int nTrials(int n) {
        int successes = 0;
        for (int i = 0; i < n; i++) {
            if (trial()) {
                successes++;
            }
        }
        return successes;
    }

    public double getSuccessProbability() {
        return successProbability;
    }
}