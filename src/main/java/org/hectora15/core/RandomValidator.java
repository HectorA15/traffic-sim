package org.hectora15.core;

/**
 * Validates the simulation data using both Chi-Square and Kolmogorov methods.
 */
public class RandomValidator {
        private Kolmogorov kolmogorov;
        private ChiSquare chiSquare;

        public RandomValidator(MersenneTwisterEngine random, double[] data) {
                this.kolmogorov = new Kolmogorov(data.length, random);
                this.chiSquare = new ChiSquare(100, data);
        }

        public boolean isValid() {
                boolean ksTest = kolmogorov.executeTest().passesTest();
                boolean chiTest = chiSquare.isUniform();
                return ksTest && chiTest;
        }
}