package com.loadbalance.tcc.firefly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class FANormal implements FireflyAlgorithm {
	protected List<Firefly> fireflies;
	private int maxGen;
	private int popNum;
	private int dim;

	@Builder.Default
	private double initAttraction = 1.0;

	@Builder.Default
	private double gamma = 1.0;

	@Builder.Default
	private double alpha = 0.2;

	private ObjectiveFun objectiveFun;

	@Builder.Default
	private boolean isAdaptive = false;

	@Builder.Default
	private double delta = 0.97;

	@Builder.Default
	private int iterator = 0;

	@Builder.Default
	private Random random = new Random();

	@Builder.Default
	private boolean isDraw = true;

	private List<Host> hostList;

	@Builder.Default
	private int indice = 0;

	public FANormal(int maxGen, int popNum, int dim) {

	}

	public void incrementIter() {
		iterator++;
		if (isAdaptive) {
			alpha *= delta;
		}
	}

	@Override
	public void initPop() {
		System.out.println("**********��Ⱥ��ʼ��**********");
		fireflies = new ArrayList<>();
		for (int i = 0; i < popNum; i++) {
			fireflies.add(new Firefly(new Position(this.dim, objectiveFun.getRange()), hostList));
		}
	}

	@Override
	public double calculaFitness(Vm vm, List<Host> hosts) {
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

	@Override
	public double calcDistance(double[] a, double[] b) {
		assert a.length == b.length;
		double distance = 0;
		int n = a.length;
		distance = IntStream.range(0, n).mapToDouble(i -> (a[i] - b[i]) * (a[i] - b[i])).sum();
		return Math.sqrt(distance);
	}

	@Override
	public void fireflyMove(Vm vm) {
		for (int i = 0; i < popNum; i++) {
			for (int j = 0; j < popNum; j++) {
				Firefly fireflyi = fireflies.get(i);
				Firefly fireflyj = fireflies.get(j);
				if (i != j && fireflyj.getLight() > fireflyi.getLight()) {
					double[] codei = fireflyi.getPosition().getPositionCode();
					double[] codej = fireflyj.getPosition().getPositionCode();
					double disij = calculaFitness(vm, hostList) / 100000;
					double attraction = initAttraction * Math.pow(Math.E, -gamma * disij * disij);
					double[] scale = fireflyi.getPosition().getRange().getScale();
					double[] newPositionCode = IntStream.range(0, this.dim).mapToDouble(ind -> codei[ind]
							+ attraction * (codej[ind] - codei[ind]) + alpha * (random.nextDouble() - 0.5) * scale[ind])
							.toArray();
					fireflyi.getPosition().setPositionCode(newPositionCode);
				}
			}
		}

		Firefly bestFirefly = fireflies.get(popNum - 1);
		double[] scale = bestFirefly.getPosition().getRange().getScale();
		double[] newPositionCode = IntStream.range(0, dim).mapToDouble(
				i -> bestFirefly.getPosition().getPositionCode()[i] + alpha * (random.nextDouble() - 0.5) * scale[i])
				.toArray();
		bestFirefly.getPosition().setPositionCode(newPositionCode);
		hostList = bestFirefly.atualizaIndice(indice);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void calcuLight() {
		for (Firefly firefly : fireflies) {
			firefly.setLight(this.getObjectiveFun().getObjValue((firefly.getPosition().getPositionCode())));
		}
		Collections.sort(fireflies);
	}

	public void printFirflies() {
		for (Firefly firefly : fireflies) {
			System.out.println(firefly);
		}
	}

	@Override
	public Host start(Vm vm) {
		initPop();
		while (this.iterator < maxGen) {
			calcuLight();
			fireflyMove(vm);
			incrementIter();
		}

		return hostList.get(0);
	}
}
