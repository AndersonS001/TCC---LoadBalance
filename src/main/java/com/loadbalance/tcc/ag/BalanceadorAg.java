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

        MachineManager.limpaHost();

        for (Host host : hostList) {
            MachineManager.addHost(host);
        }

        Population pop = new Population(50, true);

        for (int i = 0; i < 50; i++) {
            pop = GA.evolvePopulation(pop, vm);
        }

        // Print final results
        System.out.println("Finished");

        Balanceamento p = pop.getFittest(vm);

        return Optional.of(p.getMaquinaOficial());
    }
}
