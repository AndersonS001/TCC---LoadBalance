package com.loadbalance.tcc.eventos;

import java.util.Comparator;

import org.cloudbus.cloudsim.hosts.Host;

public class SortByHost implements Comparator<Host> {
    public int compare(Host a, Host b) 
    { 
        return (int) (a.getRam().getCapacity() - b.getRam().getCapacity());
    } 
}