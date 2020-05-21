package com.loadbalance.tcc.ag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

public class Balanceamento {
    private LinkedList<Host> hosts = new LinkedList<Host>();
    // Cache
    private double fitness = 0;
    private int indice = 0;

    // Constructs a blank tour
    public Balanceamento() {
        for (int i = 0; i < MachineManager.numberOfHosts(); i++) {
            hosts.add(null);
        }
    }

    public Balanceamento(int qtdHost) {
        for (int i = 0; i < qtdHost; i++) {
            hosts.add(null);
        }
    }

    public Balanceamento(LinkedList<Host> hosts) {
        this.hosts = hosts;
    }

    // Creates a random individual
    public void generateIndividual() {
        for (int hostIndex = 0; hostIndex < MachineManager.numberOfHosts(); hostIndex++) {
            setHost(hostIndex, MachineManager.getHost(hostIndex));
        }
        // Randomly reorder the list
        Collections.shuffle(hosts);
    }

    // Gets a host from the list
    public Host getHost(int hostPosition) {
        return (Host) hosts.get(hostPosition);
    }

    public Host getMaquinaOficial() {
        return hosts.get(indice);
    }

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

            double fit = (1 * (host.getTotalAvailableMips() / vm.getCurrentRequestedTotalMips()))
                    * (2 * (host.getRam().getAvailableResource() / vm.getRam().getCapacity()))
                    * (1 * (host.getStorage().getAvailableResource() / vm.getStorage().getCapacity()))
                    * (1 * (coreDisponivel.size() / vm.getCurrentRequestedMips().size()))
                    * (1 * (host.getBw().getAvailableResource() / vm.getCurrentRequestedBw()));

            if (fit == 0) {
                hosts.remove(host);
            }

            if (fitness < fit) {
                fitness = fit;
                indice = i;
            }
        }

        return fitness;
    }

    public List<Pe> calculaPe(Host host, Vm vm) {
        final List<Pe> selectedPes = new ArrayList<>();

        try {
            final List<Pe> freePeList = host.getFreePeList();
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