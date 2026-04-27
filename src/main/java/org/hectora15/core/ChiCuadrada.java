package org.hectora15.core;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;


/**
 * Clase que calcula el chi cuadrada para determinar si un conjunto de datos es uniforme o no,
 * comparando el valor de chi cuadrada calculada con el punto critico obtenido de la distribucion chi cuadrada con
 * los grados de libertad correspondientes y la tolerancia establecida.
 * <p>
 * La clase tiene un constructor que recibe la cantidad de intervalos, el array de datos y la tolerancia (opcional, de otra forma es 0.05 por defecto),
 * y metodos para calcular la frecuencia esperada, el chi cuadrada, los grados de libertad, el punto critico y para determinar si es uniforme o no.
 */
public class ChiCuadrada {
    private final int cantidadDatos;
    private double[] datos;
    private int[] frecuenciaObservada;
    private double frecuenciaEsperada;
    private double tolerancia = 0.05;
    private int cantidadIntervalos;

    public ChiCuadrada(int cantidadIntervalos, double[] datos) {
        this.datos = datos;
        this.cantidadDatos = datos.length;
        this.cantidadIntervalos = cantidadIntervalos;
    }

    public ChiCuadrada(int cantidadIntervalos, double[] datos, double tolerancia) {
        this.datos = datos;
        this.cantidadDatos = datos.length;
        this.cantidadIntervalos = cantidadIntervalos;
        this.tolerancia = tolerancia;
    }

    public double getTolerancia() {
        return tolerancia;
    }

    public void setTolerancia(double tolerancia) {
        this.tolerancia = tolerancia;
    }

    public int getFrecuenciaEsperada() {
        return (int) frecuenciaEsperada;
    }

    public int getCantidadIntervalos() {
        return cantidadIntervalos;
    }

    public void setCantidadIntervalos(int cantidad) {
        this.cantidadIntervalos = cantidad;
    }

    public int getCantidadDatos() {
        return cantidadDatos;
    }

    public int[] getFrecuenciaObservada() {
        return frecuenciaObservada;
    }

    public double[] getDatos() {
        return datos;
    }

    public void setDatos(double[] datos) {
        this.datos = datos;
    }

    public double calFrecuenciaEsperada() {
        return (double) this.cantidadDatos / this.cantidadIntervalos;
    }

    // calcula el chi cuadrada
    public double calChiCuadrada() {
        frecuenciaObservada = new int[cantidadIntervalos];
        frecuenciaEsperada = calFrecuenciaEsperada();

        // calculamos la frecuencia de cada intervalo, recorriendo cada numero guardado
        for (int i = 0; i < cantidadDatos; i++) {
            int valor = (int) (datos[i] * cantidadIntervalos); //ej: si hay 6 intervalos, 0.2 * 6 = 1.2, y se queda en el intervalo 1

            // si el valor es menor que la cantidad de intervalos, se incrementa la frecuencia del intervalo correspondiente
            // para evitar casos como 1.0 * 6 = 6, que se quedaria en el intervalo 6, que no existe, se asigna al ultimo intervalo
            if (valor < cantidadIntervalos) {
                frecuenciaObservada[valor]++;
            } else {
                // aqui es donde si sale 1.0 * 6 = 6, se asigna al ultimo intervalo que seria 6-1, asumiendo que hay 6 intervalos (0,1,2,3,4,5)
                frecuenciaObservada[cantidadIntervalos - 1]++;
            }
        }

        double chiCuadrada = 0;
        for (int i = 0; i < cantidadIntervalos; i++) {
            chiCuadrada += Math.pow(frecuenciaObservada[i] - frecuenciaEsperada, 2) / frecuenciaEsperada;
        }
        return chiCuadrada;
    }

    // calcula los grados de libertad que se usa para calcular el punto critico
    public int calGradosLibertad() {
        return cantidadIntervalos - 1;
    }


    // comparamos el valor de chiCuadrada con el punto critico para saber si es uniforme o no
    public boolean isUniforme() {
        double chiCuadradaCalculada = calChiCuadrada();
        double puntoCritico = calPuntoCritico();
        return chiCuadradaCalculada < puntoCritico;
    }

    // calculamos el punto critico con la libreria commons-math3 para no depender de insertar una tabla
    public double calPuntoCritico() {
        int gradosLibertad = calGradosLibertad();
        ChiSquaredDistribution dist = new ChiSquaredDistribution(gradosLibertad);
        return dist.inverseCumulativeProbability(1.0 - tolerancia);
    }

}
