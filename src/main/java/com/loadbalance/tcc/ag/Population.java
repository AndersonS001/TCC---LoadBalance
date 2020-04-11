package com.loadbalance.tcc.ag;

import org.cloudbus.cloudsim.vms.Vm;

public class Population {
    // Holds population of host
    Balanceamento[] solucoes;

    // Construct a population
    public Population(int hostSize, boolean inicializa) {
        solucoes = new Balanceamento[hostSize];
        // If we need to initialise a population of tours do so
        if (inicializa) {
            // Loop and create individuals
            for (int i = 0; i < populationSize(); i++) {
                Balanceamento newTour = new Balanceamento();
                newTour.generateIndividual();
                saveTour(i, newTour);
            }
        }
    }

    // Saves a tour
    public void saveTour(int index, Balanceamento tour) {
        solucoes[index] = tour;
    }
    
    // Gets a tour from population
    public Balanceamento getSolucao(int index) {
        return solucoes[index];
    }

    // Gets the best tour in the population
    public Balanceamento getFittest(Vm vm) {
        Balanceamento fittest = solucoes[0];
        // Loop through individuals to find fittest
        for (int i = 1; i < populationSize(); i++) {
            if (fittest.getFitness(vm) < getSolucao(i).getFitness(vm)) {
                fittest = getSolucao(i);
            }
        }
        return fittest;
    }

    // Gets host size
    public int populationSize() {
        return solucoes.length;
    }

}