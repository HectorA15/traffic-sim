package org.hectora15.core;

import java.util.Arrays;
import java.util.Random;

public class Kolmogorov {
    private static final double VALOR_CRITICO_ALPHA_05 = 1.36;
    private final MersenneTwisterEngine random;
    private int totalNumeros;


    public Kolmogorov(int totalNumeros) {
        this.totalNumeros = totalNumeros;
        this.random = new  MersenneTwisterEngine();
    }

    public Kolmogorov(int totalNumeros, MersenneTwisterEngine random) {
        if (totalNumeros <= 0) {
            throw new IllegalArgumentException("totalNumeros debe ser mayor que 0");
        }
        if (random == null) {
            throw new IllegalArgumentException("random no puede ser null");
        }

        this.totalNumeros = totalNumeros;
        this.random = random;
    }

    public int getNumerosTotales() {
        return this.totalNumeros;
    }

    public void setNumerosTotales(int num) {
        this.totalNumeros = num;
    }

    public ResultadoKolmogorov ejecutarPrueba() {
        double[] numerosOrdenados = generarNumerosOrdenados();
        double distanciaMaxima = calcularDistanciaMaxima(numerosOrdenados);
        double puntoCritico = calcularPuntoCritico();
        boolean pasaPrueba = distanciaMaxima <= puntoCritico;

        return new ResultadoKolmogorov(distanciaMaxima, puntoCritico, pasaPrueba, numerosOrdenados);

    }

    public double[] generarNumerosOrdenados() {
        double[] numeros = new double[totalNumeros];

        for (int i = 0; i < totalNumeros; i++) {
            numeros[i] = random.nextDouble();
        }

        Arrays.sort(numeros);
        return numeros;
    }


    public double calcularDistanciaMaxima(double[] numerosOrdenados) {
        if (numerosOrdenados == null || numerosOrdenados.length == 0) {
            throw new IllegalArgumentException("numerosOrdenados no puede ser null ni vacio");
        }

        double distanciaMaxima = 0.0;

        for (int i = 0; i < numerosOrdenados.length; i++) {
            double esquinaSuperior = (i + 1.0) / numerosOrdenados.length - numerosOrdenados[i]; // D+
            double esquinaInferior = numerosOrdenados[i] - (i * 1.0) / numerosOrdenados.length; // D-
            double mayorDistancia = Math.max(esquinaSuperior, esquinaInferior);

            distanciaMaxima = Math.max(distanciaMaxima, mayorDistancia);
        }

        return distanciaMaxima;
    }

    public double calcularPuntoCritico() {
        return VALOR_CRITICO_ALPHA_05 / Math.sqrt(totalNumeros);
    }

    public int getTotalNumeros() {
        return totalNumeros;
    }

    public record ResultadoKolmogorov(double distanciaMaxima, double puntoCritico, boolean pasaPrueba,
                                      double[] numerosOrdenados) {
    }
}
