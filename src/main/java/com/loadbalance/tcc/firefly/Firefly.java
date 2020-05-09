package com.loadbalance.tcc.firefly;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import org.cloudbus.cloudsim.hosts.Host;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("rawtypes")
@Setter
@Getter
public class Firefly implements Comparable {

	private double light;
	private double maxAttraction;
	private Position position;
	private double[] moveDirection;
	private Host[] hostList;

	@Override
	public int compareTo(Object o) {
		if (o == null) {
			return 0;
		}
		double tmp = this.light - ((Firefly) (o)).getLight();
		if (tmp < 0)
			return -1;
		if (tmp > 0)
			return 1;
		return 0;
	}

	public Firefly(Position position, List<Host> hostList) {
		super();
		this.position = position;
		this.hostList = hostList.toArray(new Host[hostList.size()]);
	}

	public void move() {
		if (maxAttraction > 0) {
			double[] newPositionCOde = IntStream.range(0, position.getDimension())
					.mapToDouble(i -> position.getPositionCode()[i] + moveDirection[i]).toArray();
			this.position.setPositionCode(newPositionCOde);
			maxAttraction = 0;
		}
	}

	public String toString() {
		return "Firefly [ " + Arrays.toString(position.getPositionCode()) + ", light=" + light + "]";
	}

	protected List<Host> atualizaIndice(int indice, List<Host> lHosts) {
		hostList = lHosts.toArray(new Host[lHosts.size()]);

		Host h = hostList[0];
		hostList[0] = hostList[indice];
		hostList[indice] = h;

		return new LinkedList<Host>(Arrays.asList(hostList));
	}
}
