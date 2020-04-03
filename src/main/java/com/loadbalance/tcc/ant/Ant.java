package com.loadbalance.tcc.ant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

public class Ant {

	protected int trailSize;

	protected int indice = 0;

	protected int trail[];
	protected ArrayList<Host> trailHost;

	protected boolean visited[];
	protected boolean visitedHost[];

	public Ant(int tourSize) {
		this.trailSize = tourSize;
		this.trail = new int[tourSize];
		this.visited = new boolean[tourSize];
		
		trailHost = new ArrayList<Host>();
		visitedHost = new boolean[tourSize];
	}

	// protected void visitCity(int currentIndex, int city) {
	// 	trail[currentIndex + 1] = city;
	// 	visited[city] = true;
	// }

	protected void visitMachine(int currentIndex, int indHost, ArrayList<Host> host) {
		trailHost.add(currentIndex + 1, host.get(indHost));
		visitedHost[indHost] = true;
	}

	protected boolean visited(int i) {
		return visited[i];
	}

	public double calculaFitness(Vm vm, ArrayList<Host> hosts) {
		double fitness = 0;

		for (int i = 0; i < hosts.size(); i++) {
			Host host = hosts.get(i);
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

		return fitness + 0.000000001;
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

	// protected double trailLength(double graph[][]) {
	// double length = graph[trail[trailSize - 1]][trail[0]];
	// for (int i = 0; i < trailSize - 1; i++) {
	// length += graph[trail[i]][trail[i + 1]];
	// }
	// return length;
	// }

	protected void clear() {
		for (int i = 0; i < trailSize; i++)
			visited[i] = false;
	}

}
