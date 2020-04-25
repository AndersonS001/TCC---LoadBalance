package com.loadbalance.tcc.firefly;

import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public interface FireflyAlgorithm {
	public void initPop();

	public void calcuLight();

	public double calcDistance(double[] a, double[] b);

	public void fireflyMove(Vm vm);

	public Host start(Vm vm);

	public double calculaFitness(Vm vm, List<Host> hosts);
}
