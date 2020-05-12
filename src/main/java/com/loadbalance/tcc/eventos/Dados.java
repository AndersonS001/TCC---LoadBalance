package com.loadbalance.tcc.eventos;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import lombok.Data;
import lombok.ToString.Exclude;
import lombok.ToString.Include;

@Data
public class Dados {

    // create an object of SingleObject
    private static Dados instance = new Dados();

    // make the constructor private so that this class cannot be
    // instantiated
    private Dados() {
    }

    // Get the only object available
    public static Dados getInstance() {
        if (instance == null) {
            instance = new Dados();
        }

        return instance;
    }

    public static void killInstance() {
        instance = null;
    }

    private long TempoDeExecSimula; // milisegundos
    private int QtdRetentativa = 0;

    public void AddQtdRetentativa(int qtd) {
        if(qtd > QtdRetentativa)
            QtdRetentativa = qtd;
    }

    @Exclude
    private List<Host> hostsAtivos = new LinkedList<Host>();

    @Include
    public int QtdHostAtivos() {
        return hostsAtivos.size();
    }

    @Include
    public int MaiorNumeroDeVmsAlocadasPorHost() {
        int count = 0;

        for (Host host : hostsAtivos) {
            if (host.getVmList().size() > count)
                count = host.getVmList().size();
        }

        return count;
    }

    public void TrataMaquina() {
        this.hostsAtivos.forEach(x -> x.destroyAllVms());
        final List<Host> hostsAtivos = this.hostsAtivos.stream().collect(Collectors.toList());

        for (Host host : hostsAtivos) {
            int index = this.hostsAtivos.indexOf(host);
            int max = host.getVmCreatedList().size();

            for (int i = 0; i < max; i++) {
                Vm vm = host.getVmCreatedList().get(i);

                this.hostsAtivos.get(index).createVm(vm);
            }
        }
    }

    @Include
    public double MipsUtilization() {
        double disponivel = 0;
        double utilizado = 0;

        for (Host host : hostsAtivos) {
            disponivel = host.getTotalMipsCapacity();
            utilizado = host.getMips();
        }

        double percentual = utilizado / disponivel;

        return percentual;
    }

    @Include
    public double RamUtilization() {
        double disponivel = 0;
        double utilizado = 0;

        for (Host host : hostsAtivos) {
            disponivel = host.getRam().getCapacity();
            utilizado = host.getRam().getAllocatedResource();
        }

        double percentual = utilizado / disponivel;

        return percentual;
    }

    @Include
    public double BwUtilization() {
        double disponivel = 0;
        double utilizado = 0;

        for (Host host : hostsAtivos) {
            disponivel = host.getBw().getCapacity();
            utilizado = host.getBw().getAllocatedResource();
        }

        double percentual = utilizado / disponivel;

        return percentual;
    }

    @Include
    public double StorageUtilization() {
        double disponivel = 0;
        double utilizado = 0;

        for (Host host : hostsAtivos) {
            disponivel = host.getStorage().getCapacity();
            utilizado = host.getStorage().getAllocatedResource();
        }

        double percentual = utilizado / disponivel;

        return percentual;
    }

    @Include
    public double CoreUtilization() {
        double somaUtilizacao = 0;
        double coreOriginal = 0;

        for (Host host : hostsAtivos) {
            somaUtilizacao += host.getPeList().size() - host.getFreePesNumber();
            coreOriginal += host.getPeList().size();
        }

        somaUtilizacao = somaUtilizacao / coreOriginal;

        return somaUtilizacao;
    }

    // double fit = (host.getTotalAvailableMips() /
    // vm.getCurrentRequestedTotalMips())
    // * (host.getRam().getAvailableResource() / vm.getRam().getCapacity())
    // * (host.getStorage().getAvailableResource() / vm.getStorage().getCapacity())
    // * (coreDisponivel.size() / vm.getCurrentRequestedMips().size())
    // * (host.getBw().getAvailableResource() / vm.getCurrentRequestedBw());
}