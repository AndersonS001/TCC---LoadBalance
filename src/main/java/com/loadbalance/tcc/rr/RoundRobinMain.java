package com.loadbalance.tcc.rr;

import java.util.ArrayList;
import java.util.List;

import com.loadbalance.tcc.eventos.Dados;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

public class RoundRobinMain {

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private Dados dadosSimulacao;
    
    public RoundRobinMain(CloudSim cloud, List<Vm> listaVms, List<Cloudlet> listaCloulet, Dados dadosSimulacao) {
        simulation = cloud;
        vmList = listaVms;
        cloudletList = listaCloulet;

        this.dadosSimulacao = dadosSimulacao;

        broker0 = new DatacenterBrokerSimple(simulation);
        
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        CalculaHosts();
    }

    private void CalculaHosts() {
        List<SimEntity> entityList = simulation.getEntityList();
        DatacenterSimple dcc = (DatacenterSimple) entityList.get(1);

        List<Host> hostsAtivos = new ArrayList<>();
        hostsAtivos.addAll(dcc.getHostList());
        hostsAtivos.removeIf(x -> x.isActive() == false);

        dadosSimulacao.setHostsAtivos(hostsAtivos);
    }
}