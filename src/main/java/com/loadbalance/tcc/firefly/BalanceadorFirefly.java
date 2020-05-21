package com.loadbalance.tcc.firefly;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.loadbalance.tcc.eventos.Dados;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class BalanceadorFirefly extends VmAllocationPolicyAbstract {
    private Dados dados;

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        dados = Dados.getInstance();
        Long tInicio = System.currentTimeMillis();

        final List<Host> hostList = getHostList();

        ObjectiveFun objectiveFun = new ObjectiveFun();
        FANormal faNormal = FANormal.builder().popNum(4).maxGen(5).dim(2).alpha(0.2).initAttraction(1.0).gamma(1.0)
                .isAdaptive(true).objectiveFun(objectiveFun).hostList(new LinkedList<Host>(hostList)).build();

        Host h = faNormal.start(vm);

        try {
            dados.adicionaTempo(System.currentTimeMillis() - tInicio);
            return Optional.of(h);
        } catch (Exception e) {
            dados.adicionaTempo(System.currentTimeMillis() - tInicio);
            return Optional.empty();
        }
    }
}
