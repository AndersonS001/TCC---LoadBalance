package com.loadbalance.tcc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.loadbalance.tcc.ag.AlgoritmoGeneticoMain;
import com.loadbalance.tcc.ag.BalanceadorAg;
import com.loadbalance.tcc.ant.AntColonyMain;
import com.loadbalance.tcc.ant.BalanceadorAnt;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

public class TccMain {

    private static final int HOSTS = 250;
    private static final int HOST_PES = 12;

    private static final int VMS = 400;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 10;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 100;

    private static CloudSim simulation;

    public static void main(String[] args) {
        simulation = new CloudSim();

        Scanner in = new Scanner(System.in);
        System.out.println("Bem vindo, deseja simular qual algoritmo:");
        System.out.println("1 - Algoritmo Genético");
        System.out.println("2 - Ant Colony");
        System.out.println("3 - Firefly");

        int decision = in.nextInt();

        switch (decision) {
            case 1:
                in.close();
                BalanceadorAG();
                break;
            case 2:
                in.close();
                BalanceadorAnt();
                break;
            default:
                System.out.println("Unknown option");
                in.close();
                break;
        }
    }

    private static void BalanceadorAG(){
        createDatacenter(new BalanceadorAg());
        new AlgoritmoGeneticoMain(simulation, createVms(), createCloudlets());
    }

    private static void BalanceadorAnt() {
        createDatacenter(new BalanceadorAnt());
        new AntColonyMain(simulation, createVms(), createCloudlets());
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private static Datacenter createDatacenter(VmAllocationPolicy vmAllocationPolicy) {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for (int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList, vmAllocationPolicy);
    }

    private static Host createHost() {
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
    private static List<Vm> createVms() {
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
    private static List<Cloudlet> createCloudlets() {
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