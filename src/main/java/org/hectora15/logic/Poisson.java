package org.hectora15.logic;


public class Poisson {
    private double lambda;
    private MersenneTwisterEngine rng;

    public Poisson(double lambda, MersenneTwisterEngine rng) {
        if (lambda <= 0) {
            throw new IllegalArgumentException("Lambda must be > 0");
        }
        this.lambda = lambda;
        this.rng = rng;
    }

    public int nextArrivalCount() {
        double limit = Math.exp(-lambda);
        double probability = 1.0;
        int k = 0;

        do {
            k++;
            probability *= rng.nextDouble();
        } while (probability > limit);

        return k - 1;
    }

    public double getLambda() {
        return lambda;
    }
}