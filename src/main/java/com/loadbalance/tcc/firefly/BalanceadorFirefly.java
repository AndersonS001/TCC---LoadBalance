package com.loadbalance.tcc.firefly;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class BalanceadorFirefly extends VmAllocationPolicyAbstract {

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final List<Host> hostList = getHostList();

        ObjectiveFun objectiveFun = new ObjectiveFun();
        FANormal faNormal = FANormal.builder().popNum(4).maxGen(5).dim(2).alpha(0.2).initAttraction(1.0).gamma(1.0)
                .isAdaptive(true).objectiveFun(objectiveFun).hostList(new LinkedList<Host>(hostList)).build();

        Host h = faNormal.start(vm);

        return Optional.of(h);
    }
}
