package com.loadbalance.tcc.ag;

import org.cloudbus.cloudsim.vms.Vm;

public class Population {
    // Holds population of host
    Balanceamento[] machines;

    // Construct a population
    public Population(int hostSize, boolean inicializa) {
        machines = new Balanceamento[hostSize];
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
        machines[index] = tour;
    }
    
    // Gets a tour from population
    public Balanceamento getHost(int index) {
        return machines[index];
    }

    // Gets the best tour in the population
    public Balanceamento getFittest(Vm vm) {
        Balanceamento fittest = machines[0];
        // Loop through individuals to find fittest
        for (int i = 1; i < populationSize(); i++) {
            if (fittest.getFitness(vm) <= getHost(i).getFitness(vm)) {
                fittest = getHost(i);
            }
        }
        return fittest;
    }

    // Gets host size
    public int populationSize() {
        return machines.length;
    }

}