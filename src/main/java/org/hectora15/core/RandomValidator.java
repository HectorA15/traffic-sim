package org.hectora15.core;

public class RandomValidator {

        Kolmogorov kolmogorov;
        ChiCuadrada chiCuadrada;

        double[] data;

        public RandomValidator(MersenneTwisterEngine random, double[] data) {
                this.kolmogorov = new Kolmogorov(data.length, random);
                this.chiCuadrada = new ChiCuadrada(100, data);
        }

        public boolean isValid(double[] data){
                return (kolmogorov.ejecutarPrueba() && chiCuadrada.isUniforme());
        }
}
