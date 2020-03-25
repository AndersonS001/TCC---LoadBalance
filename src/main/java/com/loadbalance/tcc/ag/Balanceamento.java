package com.loadbalance.tcc.ag;

import java.util.ArrayList;
import java.util.Collections;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class Balanceamento {
    // Holds our tour of cities
    private ArrayList<Host> hosts = new ArrayList<Host>();
    // Cache
    private double fitness = 0;

    // Constructs a blank tour
    public Balanceamento() {
        for (int i = 0; i < MachineManager.numberOfHosts(); i++) {
            hosts.add(null);
        }
    }

    public Balanceamento(ArrayList<Host> hosts) {
        this.hosts = hosts;
    }

    // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our tour
        for (int cityIndex = 0; cityIndex < MachineManager.numberOfHosts(); cityIndex++) {
            setHost(cityIndex, MachineManager.getHost(cityIndex));
        }
        // Randomly reorder the tour
        Collections.shuffle(hosts);
    }

    // Gets a city from the tour
    public Host getHost(int hostPosition) {
        return (Host) hosts.get(hostPosition);
    }

    // Sets a city in a certain position within a tour
    public void setHost(int hostPosition, Host host) {
        hosts.set(hostPosition, host);
        // If the tours been altered we need to reset the fitness and distance
        fitness = 0;
    }

    // Gets the tours fitness
    public double getFitness(Vm vm) {
        if (fitness == 0) {
            fitness = 1 / calculaFitness(vm);
        }
        return fitness;
    }

    public double calculaFitness(Vm vm) {
        double fitness = 0;

        int cloudlet = 10000;

        Host host = getHost(0);
        fitness = (cloudlet / host.getTotalAvailableMips())
                    + (host.getBwUtilization() / vm.getCurrentRequestedTotalMips());

        // for (int cityIndex = 0; cityIndex < hostSize(); cityIndex++) {
        //     Host host = getHost(cityIndex);

        //     fitness += (cloudlet / host.getTotalAvailableMips())
        //             + (host.getBwUtilization() / vm.getCurrentRequestedTotalMips());

        // }

        return fitness;
    }

    // Get number of hosts
    public int hostSize() {
        return hosts.size();
    }

    // Check if the host list contains the host
    public boolean containsHost(Host host) {
        return hosts.contains(host);
    }
}