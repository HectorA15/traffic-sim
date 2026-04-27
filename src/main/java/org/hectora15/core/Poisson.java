package org.hectora15.core;

import java.util.ArrayList;
import java.util.List;


public class Poisson {

    private int k; // maximo numero de eventos
    private double lambda; // media de eventos esperados

    public Poisson(int k, double lambda) {
        this.k = k;
        this.lambda = lambda;
    }

    // para calcular la probabilidad de la distribución de Poisson para k eventos
    public static List<Double> calcularProbabilidadPoisson(double lambda, int maxK) {
        ArrayList<Double> probabilidades = new ArrayList<>();

        for (int k = 0; k <= maxK; k++) {
            double probabilidad = (Math.pow(lambda, k)) * (Math.exp(-lambda)) / factorial(k);
            probabilidades.add(probabilidad);
        }

        return probabilidades;
    }

    // Función para calcular el factorial de un número n
    public static double factorial(int n) {
        if (n == 0) return 1;
        double resultado = 1;
        for (int i = 1; i <= n; i++) {
            resultado *= i;
        }
        return resultado;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

}
