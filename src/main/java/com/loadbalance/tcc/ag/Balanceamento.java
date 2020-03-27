package com.loadbalance.tcc.ag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

public class Balanceamento {
    // Holds our tour of cities
    private ArrayList<Host> hosts = new ArrayList<Host>();
    // Cache
    private double fitness = 0;
    private int indice = 0;

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

    public Host getMaquinaOficial() {
        return hosts.get(indice);
    }

    // Sets a city in a certain position within a tour
    public void setHost(int hostPosition, Host host) {
        hosts.set(hostPosition, host);
        // If the tours been altered we need to reset the fitness and distance
        fitness = 0;
        indice = 0;
    }

    // Gets the tours fitness
    public double getFitness(Vm vm) {
        if (fitness == 0) {
            fitness = calculaFitness(vm);
        }
        return fitness;
    }

    public double calculaFitness(Vm vm) {
        double fitness = 0;

        for (int i = 0; i < hostSize(); i++) {
            Host host = getHost(i);
            List<Pe> coreDisponivel = calculaPe(host, vm);

            double fit = (host.getTotalAvailableMips() / vm.getCurrentRequestedTotalMips())
                    * (host.getRam().getAvailableResource() / vm.getRam().getCapacity())
                    * (host.getStorage().getAvailableResource() / vm.getStorage().getCapacity())
                    * (coreDisponivel.size() / vm.getCurrentRequestedMips().size())
                    * (host.getBw().getAvailableResource() / vm.getCurrentRequestedBw());

            if (fitness < fit) {
                fitness = fit;
                indice = i;
            }
        }

        return fitness;
    }

    public List<Pe> calculaPe(Host host, Vm vm) {
        final List<Pe> freePeList = host.getFreePeList();
        final List<Pe> selectedPes = new ArrayList<>();
        try {
            final Iterator<Pe> peIterator = freePeList.iterator();
            Pe pe = peIterator.next();
            for (final double mips : vm.getCurrentRequestedMips()) {
                if (mips <= pe.getCapacity()) {
                    selectedPes.add(pe);
                    if (!peIterator.hasNext()) {
                        break;
                    }
                    pe = peIterator.next();
                }
            }
        } catch (Exception e) {
        }

        return selectedPes;
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