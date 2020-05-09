package com.loadbalance.tcc.ag;

import java.util.List;
import java.util.Optional;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class BalanceadorAg extends VmAllocationPolicyAbstract {

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final List<Host> hostList = getHostList();

        MachineManager.addHost(hostList);

        Population pop = new Population(8, true);

        pop = GA.evolvePopulation(pop, vm);

        Balanceamento p = pop.getFittest(vm);

        return Optional.of(p.getMaquinaOficial());
    }
}
