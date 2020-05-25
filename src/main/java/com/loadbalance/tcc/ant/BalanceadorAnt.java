package com.loadbalance.tcc.ant;

import java.util.List;
import java.util.Optional;

import com.loadbalance.tcc.eventos.Dados;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class BalanceadorAnt extends VmAllocationPolicyAbstract {
    private Dados dados;

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        dados = Dados.getInstance();
        Long tInicio = System.currentTimeMillis();

        final List<Host> hostList = getHostList();

        AntColonyOptimization ant = new AntColonyOptimization(hostList.size(), hostList);

        try {
            Host[] h = ant.startAntOptimization(vm);

            dados.adicionaTempo(System.currentTimeMillis() - tInicio);
            return Optional.of(h[0]);
        } catch (Exception e) {
            dados.adicionaTempo(System.currentTimeMillis() - tInicio);
            return Optional.empty();
        }
    }
}
