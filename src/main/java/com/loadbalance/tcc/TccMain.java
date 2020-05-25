package com.loadbalance.tcc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.loadbalance.tcc.ag.AlgoritmoGeneticoMain;
import com.loadbalance.tcc.ag.BalanceadorAg;
import com.loadbalance.tcc.ant.AntColonyMain;
import com.loadbalance.tcc.ant.BalanceadorAnt;
import com.loadbalance.tcc.eventos.Dados;
import com.loadbalance.tcc.eventos.SortByHost;
import com.loadbalance.tcc.eventos.SortByVm;
import com.loadbalance.tcc.firefly.BalanceadorFirefly;
import com.loadbalance.tcc.firefly.FireflyMain;
import com.loadbalance.tcc.rr.BalanceadorRoundRobin;
import com.loadbalance.tcc.rr.RoundRobinMain;

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

    private static final int HOSTS = 230;
    private static final int HOST_PES = 6;

    private static final int VMS = 500;
    private static final int VM_PES = 2;

    private static final int CLOUDLETS = 0;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private static CloudSim simulation;

    private static Dados dadosSimulacao;

    public static void main(String[] args) throws IOException {

        Scanner in = new Scanner(System.in);
        System.out.println("Bem vindo, deseja simular qual algoritmo:");
        System.out.println("1 - Algoritmo Genético");
        System.out.println("2 - Ant Colony");
        System.out.println("3 - Firefly");

        // int decision = in.nextInt();
        for (int j = 1; j <= 3; j++) {
            int decision = j;
            LogToFile("Algoritmo " + j);

            for (int i = 0; i < 100; i++) {
                dadosSimulacao = Dados.getInstance();
                simulation = new CloudSim();

                switch (decision) {
                    case 1:
                        in.close();
                        BalanceadorAG();
                        break;
                    case 2:
                        in.close();
                        BalanceadorAnt();
                        break;
                    case 3:
                        in.close();
                        BalanceadorFirefly();
                        break;
                    default:
                        in.close();
                        BalanceadorRR();
                        break;
                }

                dadosSimulacao.TrataMaquina();
                LogToFile(dadosSimulacao.toString());
                Dados.killInstance();
            }
        }

    }

    private static void LogToFile(String msg) throws IOException {
        new File("dados.txt").createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter("dados.txt", true));
        writer.newLine(); // Add new line
        writer.write(msg);
        writer.close();
    }

    private static void BalanceadorRR() {
        List<Vm> vmList = createVms();
        Collections.sort(vmList, new SortByVm());

        long tempoInicio = System.currentTimeMillis();
        createDatacenter(new BalanceadorRoundRobin());

        new RoundRobinMain(simulation, vmList, createCloudlets(), dadosSimulacao);
        long tempoFim = System.currentTimeMillis();

        dadosSimulacao.setTempoDeExecSimula((tempoFim - tempoInicio));
    }

    private static void BalanceadorAG() {
        List<Vm> vmList = createVms();
        Collections.sort(vmList, new SortByVm());

        long tempoInicio = System.currentTimeMillis();
        createDatacenter(new BalanceadorAg());

        new AlgoritmoGeneticoMain(simulation, vmList, createCloudlets(), dadosSimulacao);
        long tempoFim = System.currentTimeMillis();

        dadosSimulacao.setTempoDeExecSimula((tempoFim - tempoInicio));
    }

    private static void BalanceadorAnt() {
        long tempoInicio = System.currentTimeMillis();
        createDatacenter(new BalanceadorAnt());
        new AntColonyMain(simulation, createVms(), createCloudlets(), dadosSimulacao);
        long tempoFim = System.currentTimeMillis();

        dadosSimulacao.setTempoDeExecSimula((tempoFim - tempoInicio));
    }

    private static void BalanceadorFirefly() {
        long tempoInicio = System.currentTimeMillis();
        createDatacenter(new BalanceadorFirefly());
        new FireflyMain(simulation, createVms(), createCloudlets(), dadosSimulacao);
        long tempoFim = System.currentTimeMillis();

        dadosSimulacao.setTempoDeExecSimula((tempoFim - tempoInicio));
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private static Datacenter createDatacenter(VmAllocationPolicy vmAllocationPolicy) {
        final List<Host> hostList = new LinkedList<Host>();
        for (int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        Collections.sort(hostList, new SortByHost());

        return new DatacenterSimple(simulation, hostList, vmAllocationPolicy);
    }

    private static Host createHost() {
        final List<Pe> peList = new LinkedList<Pe>();
        // List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            // Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple((0.3 + new Random().nextDouble()) * 3000));
        }

        final long ram = (long) ((0.5 + new Random().nextDouble()) * 10000); // in Megabytes
        final long bw = (long) ((0.5 + new Random().nextDouble()) * 10000); // in Megabits/s
        final long storage = (long) ((0.5 + new Random().nextDouble()) * 8000); // in Megabytes

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
        final List<Vm> list = new LinkedList<Vm>();
        int tam = 2400;
        for (int i = 0; i < VMS; i++) {
            int pe = new Random().nextInt(VM_PES);

            while (pe == 0)
                pe = new Random().nextInt(VM_PES);

            // Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple((0.01 + new Random().nextDouble()) * tam, pe);
            vm.setRam((long) ((0.05 + new Random().nextDouble()) * 2 * tam))
                    .setBw((long) ((0.03 + new Random().nextDouble()) * 2 * tam))
                    .setSize((long) ((0.01 + new Random().nextDouble()) * tam));
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */
    private static List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new LinkedList<Cloudlet>();

        // UtilizationModel defining the Cloudlets use only 50% of any resource all the
        // time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.7);

        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(new Random().nextInt(1000) + 1);
            list.add(cloudlet);
        }

        return list;
    }
}