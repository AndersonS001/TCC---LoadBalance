package com.loadbalance.tcc.ag;

import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.hosts.Host;

public class MachineManager {

    // Holds our hosts
    private static LinkedList<Host> hosts = new LinkedList<Host>();

    // Adds a destination host
    public static void addHost(Host host) {
        hosts.add(host);
    }

    public static void addHost(List<Host> host) {
        hosts.clear();
        hosts.addAll(host);
    }
    
    // Get a host
    public static Host getHost(int index){
        return (Host)hosts.get(index);
    }
    
    // Get the number of destination hosts
    public static int numberOfHosts(){
        return hosts.size();
    }

    public static void limpaHost(){
        hosts.clear();
    }
}