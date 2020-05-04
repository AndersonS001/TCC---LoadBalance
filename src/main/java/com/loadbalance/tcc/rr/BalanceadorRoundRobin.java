package com.loadbalance.tcc.rr;

import java.util.List;
import java.util.Optional;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class BalanceadorRoundRobin extends VmAllocationPolicyAbstract {
    private int lastHostIndex;

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final List<Host> hostList = getHostList();
        /* The for loop just defines the maximum number of Hosts to try.
         * When a suitable Host is found, the method returns immediately. */
        final int maxTries = hostList.size();
        for (int i = 0; i < maxTries; i++) {
            final Host host = hostList.get(lastHostIndex);
            //Different from the FirstFit policy, it always increments the host index.
            lastHostIndex = ++lastHostIndex % hostList.size();
            if (host.isSuitableForVm(vm)) {
                return Optional.of(host);
            }
        }

        return Optional.empty();
    }
}