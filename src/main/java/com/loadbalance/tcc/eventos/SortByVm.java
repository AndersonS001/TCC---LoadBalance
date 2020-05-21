package com.loadbalance.tcc.eventos;

import java.util.Comparator;

import org.cloudbus.cloudsim.vms.Vm;

public class SortByVm implements Comparator<Vm> {
    public int compare(Vm a, Vm b) 
    { 
        return (int) (b.getRam().getCapacity() - a.getRam().getCapacity());
    } 
}