package com.loadbalance.tcc.ant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

public class Ant {

	protected int hostSize;

	protected int indice = 0;

	protected int trail[];
	protected Host trailHost[];

	protected boolean visitedHost[];

	public Ant(int hostSize) {
		this.hostSize = hostSize;
		this.trail = new int[hostSize];

		trailHost = new Host[hostSize];
		visitedHost = new boolean[hostSize];
	}

	protected void atualizaIndice() {
		Host h = trailHost[0];
		trailHost[0] = trailHost[indice];
		trailHost[indice] = h;
	}

	protected void visitMachine(int currentIndex, int indHost, ArrayList<Host> host) {
		trailHost[currentIndex + 1] = host.get(indHost);

		visitedHost[indHost] = true;
	}

	protected boolean visited(int i) {
		return visitedHost[i];
	}

	public double calculaFitness(Vm vm, Host[] hosts) {
		double fitness = 0;

		for (int i = 0; i < hosts.length; i++) {
			Host host = hosts[i];
			List<Pe> coreDisponivel = calculaPe(host, vm);

			double fit = (host.getTotalAvailableMips() / vm.getCurrentRequestedTotalMips())
					* (host.getRam().getAvailableResource() / vm.getRam().getCapacity())
					* (host.getStorage().getAvailableResource() / vm.getStorage().getCapacity())
					* (coreDisponivel.size() / vm.getCurrentRequestedMips().size())
					* (host.getBw().getAvailableResource() / vm.getCurrentRequestedBw());

			if (fit == 0) {
				hosts = ArrayUtils.remove(hosts, i);
				trailHost = ArrayUtils.remove(trailHost, i);
			}

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

	protected void clear() {
		for (int i = 0; i < hostSize; i++)
			visitedHost[i] = false;
	}
}
