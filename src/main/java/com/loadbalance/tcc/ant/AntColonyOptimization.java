package com.loadbalance.tcc.ant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class AntColonyOptimization {

    private double c = 1.0;
    private double alpha = 1;
    private double beta = 5;
    private double evaporation = 0.5;
    private double Q = 500;
    private double randomFactor = 0.15;

    private int maxIterations = 1;

    private int numberOfHosts;
    private int numberOfAnts;

    private double bestHostFit;

    private double graph[][];
    private LinkedList<Host> hosts = new LinkedList<Host>();

    private double trails[][];

    private List<Ant> ants = new ArrayList<>();
    private Random random = new Random();
    private double probabilities[];

    private int currentIndex;

    private Host[] bestTourOrder;

    public AntColonyOptimization(int noOfHosts, List<Host> lHosts) {
        graph = generateRandomMatrix(noOfHosts, lHosts);

        numberOfHosts = hosts.size();

        numberOfAnts = 3; 

        trails = new double[numberOfHosts][numberOfHosts];
        probabilities = new double[numberOfHosts];
        IntStream.range(0, numberOfAnts).forEach(i -> ants.add(new Ant(numberOfHosts)));
    }

    /**
     * Generate initial solution
     */
    public double[][] generateRandomMatrix(int n, List<Host> lHosts) {
        double[][] randomMatrix = new double[n][n];
        IntStream.range(0, n).forEach(
                i -> IntStream.range(0, n).forEach(j -> randomMatrix[i][j] = Math.abs(random.nextInt(100) + 1)));

        hosts.clear();
        hosts.addAll(lHosts);

        return randomMatrix;
    }

    /**
     * Perform ant optimization
     * 
     * @return
     */
    public Host[] startAntOptimization(Vm vm) {
        Host[] result = solve(vm);

        return result;
    }

    /**
     * Use this method to run the main logic
     */
    public Host[] solve(Vm vm) {
        setupAnts();
        clearTrails();
        IntStream.range(0, maxIterations).forEach(i -> {
            moveAnts();
            updateTrails(vm);
            // updateBest(vm);
        });

        return bestTourOrder;
    }

    /**
     * Prepare hosts for the simulation
     */
    private void setupAnts() {
        IntStream.range(0, numberOfAnts).forEach(i -> {
            ants.forEach(ant -> {
                ant.clear();
                ant.visitMachine(-1, random.nextInt(numberOfHosts), hosts);
            });
        });
        currentIndex = 0;
    }

    /**
     * At each iteration, move ants
     */
    private void moveAnts() {
        IntStream.range(currentIndex, numberOfHosts - 1).forEach(i -> {
            ants.forEach(ant -> ant.visitMachine(currentIndex, selectNextHost(ant), hosts));
            currentIndex++;
        });
    }

    /**
     * Select next host for each ant
     */
    private int selectNextHost(Ant ant) {
        int t = random.nextInt(numberOfHosts - currentIndex);
        if (random.nextDouble() < randomFactor) {
            OptionalInt hostIndex = IntStream.range(0, numberOfHosts).filter(i -> i == t && !ant.visited(i))
                    .findFirst();
            if (hostIndex.isPresent()) {
                return hostIndex.getAsInt();
            }
        }
        calculateProbabilities(ant);
        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < numberOfHosts; i++) {
            total += probabilities[i];
            if (total >= r) {
                return i;
            }
        }

        throw new RuntimeException("There are no other hosts");
    }

    /**
     * Calculate the next hosts picks probabilites
     */
    public void calculateProbabilities(Ant ant) {
        int i = ant.trail[currentIndex];
        double pheromone = 0.0;
        for (int l = 0; l < numberOfHosts; l++) {
            if (!ant.visited(l)) {
                pheromone += Math.pow(trails[i][l], alpha) * Math.pow(1.0 / graph[i][l], beta);
            }
        }
        for (int j = 0; j < numberOfHosts; j++) {
            if (ant.visited(j)) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta);
                probabilities[j] = numerator / pheromone;
            }
        }
    }

    /**
     * Update trails that ants used
     */
    private void updateTrails(Vm vm) {
        for (int i = 0; i < numberOfHosts; i++) {
            for (int j = 0; j < numberOfHosts; j++) {
                trails[i][j] *= evaporation;
            }
        }

        for (Ant a : ants) {
            double fit = a.calculaFitness(vm, a.trailHost);
            double contribution = fit / Q;

            for (int i = 0; i < a.trailHost.length - 1; i++) {
                trails[a.trail[i]][a.trail[i + 1]] += contribution;
            }

            trails[a.trail[a.trailHost.length - 1]][a.trail[0]] += contribution;

            //update best
            if (bestTourOrder == null) {
                bestHostFit = 0;
            }

            if (fit > bestHostFit) {
                bestHostFit = fit;
                a.atualizaIndice();
                bestTourOrder = a.trailHost;
            }
        }
    }

    /**
     * Clear trails after simulation
     */
    private void clearTrails() {
        IntStream.range(0, numberOfHosts).forEach(i -> {
            IntStream.range(0, numberOfHosts).forEach(j -> trails[i][j] = c);
        });
    }

}
