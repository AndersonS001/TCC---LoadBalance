package com.loadbalance.tcc.ant;

import java.util.List;
import java.util.Optional;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class BalanceadorAnt extends VmAllocationPolicyAbstract {

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final List<Host> hostList = getHostList();

        AntColonyOptimization ant = new AntColonyOptimization(hostList.size(), hostList);

        Host[] h = ant.startAntOptimization(vm);

        // Print final results
        System.out.println("Finished");

        return Optional.of(h[0]);
    }
}
