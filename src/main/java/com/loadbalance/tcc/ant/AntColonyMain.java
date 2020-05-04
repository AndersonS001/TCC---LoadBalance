package com.loadbalance.tcc.ant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.loadbalance.tcc.eventos.Dados;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

/**
 * AlgoritmoGenetico
 */
public class AntColonyMain {

    private static final int HOSTS = 250;
    private static final int HOST_PES = 12;

    private static final int VMS = 400;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 10;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 100;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private Dados dadosSimulacao;

    public static void main(String[] args) {
        new AntColonyMain();
    }

    public AntColonyMain(CloudSim cloud, List<Vm> listaVms, List<Cloudlet> listaCloulet, Dados dadosSimulacao) {
        simulation = cloud;
        this.dadosSimulacao = dadosSimulacao;

        vmList = listaVms;
        cloudletList = listaCloulet;

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

    private AntColonyMain() {
        simulation = new CloudSim();

        vmList = createVms();
        cloudletList = createCloudlets();
        createDatacenter();

        // Creates a broker that is a software acting on behalf a cloud customer to
        // manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        // List<SimEntity> xxxx = simulation.getEntityList();
        // DatacenterSimple dcc = (DatacenterSimple) xxxx.get(1);
        // List<Host> ddd = new ArrayList<>();
        // for (Host host : dcc.getHostList()) {
        // if (host.isActive())
        // ddd.add(host);
        // }

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        finishedCloudlets.sort(Comparator.comparingLong(cloudlet -> cloudlet.getVm().getId()));
        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for (int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList, new BalanceadorAnt());
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        // List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            // Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple((0.1 + new Random().nextDouble()) * 6000));
        }

        final long ram = (long) ((0.5 + new Random().nextDouble()) * 30000); // in Megabytes
        final long bw = (long) ((0.5 + new Random().nextDouble()) * 60000); // in Megabits/s
        final long storage = (long) ((0.1 + new Random().nextDouble()) * 15000); // in Megabytes

        /*
         * Uses ResourceProvisionerSimple by default for RAM and BW provisioning and
         * VmSchedulerSpaceShared for VM scheduling.
         */
        return new HostSimple(ram, bw, storage, peList, false);
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        int tam = 2400;
        for (int i = 0; i < VMS; i++) {
            // Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple((0.1 + new Random().nextDouble()) * tam, VM_PES);
            vm.setRam((long) ((0.5 + new Random().nextDouble()) * 2 * tam))
                    .setBw((long) ((0.5 + new Random().nextDouble()) * 2 * tam))
                    .setSize((long) ((0.1 + new Random().nextDouble()) * tam));
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        // UtilizationModel defining the Cloudlets use only 50% of any resource all the
        // time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(new Random().nextInt(100) + 1);
            list.add(cloudlet);
        }

        return list;
    }
}