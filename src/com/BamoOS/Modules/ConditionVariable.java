package com.BamoOS.Modules;

import java.util.LinkedList;

public class ConditionVariable implements IConditionVariable {
    private LinkedList<PCB> waiting; // kolejka FIFO (bufor)
    private boolean busy; // czy zasób jest zajęty (używany)

    public ConditionVariable() {
        this.waiting = new LinkedList<PCB>();
        this.busy = false;
    }

    public void await() {
        CURRENT_PCB.changeState(IProcessManager.State.WAITING); // metoda do zmiany stanu procesu
        this.waiting.addLast(CURRENT_PCB); // dodaj proces na koniec kolejki
    }

    public void signal() {
        PCB pcb = this.waiting.getFirst(); // weź pierwszy z kolejki (bufora)
        pcb.changeState(IProcessManager.State.READY); // metoda do zmiany stanu procesu
        this.waiting.removeFirst(); // usuń z początku kolejki oczekujących
    }

    public void signalAll() {
        for (PCB pcb: this.waiting) {
            pcb.signal(); // wywal wszystkie z kolejki
        }
    }

    public boolean getBusy() {
        return this.busy;
    }

    public LinkedList<PCB> getWaiting() {
        return this.waiting;
    }

    public void printWaiting() {

    }
}