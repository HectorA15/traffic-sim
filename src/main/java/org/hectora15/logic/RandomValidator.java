package org.hectora15.logic;


public class RandomValidator {
        private Kolmogorov kolmogorov;
        private ChiSquare chiSquare;

        public RandomValidator(MersenneTwisterEngine random, double[] data) {
                this.kolmogorov = new Kolmogorov(data.length, random);
                this.chiSquare = new ChiSquare(100, data);
        }

        public boolean isValid() {
                return passesKolmogorov() && passesChiSquare();
        }

        public boolean passesKolmogorov() {
                return kolmogorov.executeTest().passesTest();
        }

        public boolean passesChiSquare() {
                return chiSquare.isUniform();
        }

}