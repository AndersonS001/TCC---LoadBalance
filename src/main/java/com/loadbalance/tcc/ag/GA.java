package com.loadbalance.tcc.ag;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class GA {
    /* GA parameters */
    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 5;
    private static final boolean elitism = false;

    // Evolves a population over one generation
    public static Population evolvePopulation(Population pop, Vm vm) {
        Population newPopulation = new Population(pop.populationSize(), false);

        // Keep our best individual if elitism is enabled
        int elitismOffset = 0;
        if (elitism) {
            newPopulation.saveTour(0, pop.getFittest(vm));
            elitismOffset = 1;
        }

        // Crossover population
        // Loop over the new population's size and create individuals from
        // Current population
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            // Select parents
            Balanceamento parent1 = tournamentSelection(pop, vm);
            Balanceamento parent2 = tournamentSelection(pop, vm);
            // Crossover parents
            Balanceamento child = crossover(parent1, parent2);
            // Add child to new population
            newPopulation.saveTour(i, child);
        }

        // Mutate the new population a bit to add some new genetic material
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            mutate(newPopulation.getSolucao(i), vm);
        }

        return newPopulation;
    }

    // Applies crossover to a set of parents and creates offspring
    public static Balanceamento crossover(Balanceamento parent1, Balanceamento parent2) {
        int hP1 = parent1.hostSize();
        int hP2 = parent2.hostSize();

        // Create new child tour
        Balanceamento child = new Balanceamento((hP1 > hP2) ? hP2 : hP1);

        // Get start and end sub tour positions for parent1's tour
        int startPos = (int) (Math.random() * parent1.hostSize());
        int endPos = (int) (Math.random() * parent1.hostSize());

        // Loop and add the sub tour from parent1 to our child
        for (int i = 0; i < child.hostSize(); i++) {
            // If our start position is less than the end position
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setHost(i, parent1.getHost(i));
            } // If our start position is larger
            else if (startPos > endPos) {
                if (!(i < startPos && i > endPos)) {
                    child.setHost(i, parent1.getHost(i));
                }
            }
        }

        for (int i = 0; i < child.hostSize(); i++) {
            // If child doesn't have the host add it
            if (!child.containsHost(parent2.getHost(i))) {
                // Loop to find a spare position in the child's tour
                for (int ii = 0; ii < child.hostSize(); ii++) {
                    // Spare position found, add host
                    if (child.getHost(ii) == null) {
                        child.setHost(ii, parent2.getHost(i));
                        break;
                    }
                }
            }
        }

        return child;
    }

    // Mutate a tour using swap mutation
    private static void mutate(Balanceamento tour, Vm vm) {
        // Loop through free hots
        for (int tourPos1 = 0; tourPos1 < tour.hostSize(); tourPos1++) {
            // Apply mutation rate
            if (Math.random() < mutationRate) {
                // Get a second random position in the list
                int tourPos2 = (int) (tour.hostSize() * Math.random());

                // Get the hosts at target position in list
                Host host1 = tour.getHost(tourPos1);
                Host host2 = tour.getHost(tourPos2);

                // Swap them around
                tour.setHost(tourPos2, host1);
                tour.setHost(tourPos1, host2);
            }
        }
    }

    // Selects candidate tour for crossover
    private static Balanceamento tournamentSelection(Population pop, Vm vm) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false);
        // For each place in the tournament get a random candidate tour and
        // add it
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.populationSize());
            tournament.saveTour(i, pop.getSolucao(randomId));
        }
        // Get the fittest tour
        Balanceamento fittest = tournament.getFittest(vm);
        return fittest;
    }
}