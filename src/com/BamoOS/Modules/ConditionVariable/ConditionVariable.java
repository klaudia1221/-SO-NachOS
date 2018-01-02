package com.BamoOS.Modules.ConditionVariable;

import java.util.LinkedList;

/**
 * Synchronizacja procesów w oparciu o zmienne warunkowe (ang. condition variable).
 */
public class ConditionVariable implements IConditionVariable {
    private LinkedList<PCB> waiting; // kolejka FIFO (bufor)
    private boolean busy; // czy zasób jest zajęty (używany)

    /**
     * Konstruktor bezargumentowy.
     * Inicjalizuje pustą kolejkę procesów oczekujących oraz zmienną busy wskazującą czy zasób jest zajęty.
     */
    public ConditionVariable() {
        this.waiting = new LinkedList<PCB>();
        this.busy = false;
    }

    /**
     * Zmienia stan aktualnie aktywnego procesu na `WAITING` i dodaje do kolejki w zmiennej warunkowej.
     * Wywołuje planistę.
     */
    public void await() {
        CURRENT_PCB.changeState(IProcessManager.State.WAITING); // zmien stan procesu i wywołaj planiste
        this.waiting.addLast(CURRENT_PCB); // dodaj do kolejki
    }

    /**
     * Zmienia stan pierwszego procesu w kolejce na `READY` i usuwa go z kolejki procesów oczekująych.
     * Wywołuje planistę.
     *
     * Brak efektu, gdy w kolejce nie ma oczekujących procesów.
     */
    public void signal() {
        if(!this.waiting.isEmpty()) { // jezeli sa oczekujace procesy
            PCB pcb = this.waiting.getFirst(); // weź pierwszy z kolejki (bufora)
            pcb.changeState(IProcessManager.State.READY); // zmien stan procesu na READY i wywolaj planiste
            this.waiting.removeFirst(); // usuń z początku kolejki oczekujących (ten ktorego stan zmienilismy)
        }
    }

    /**
     * Wywołuje metodę `signal` dla wszystkich procesów w kolejce oczekująych
     */
    public void signalAll() {
        for (PCB pcb: this.waiting) { // dla wszystkich procesow oczekujacych
            pcb.signal(); // wywolaj funkcje signal()
        }
    }

    /**
     * Getter zmiennej `busy` określającej zajętość zasobu.
     * Zwraca `true`, gdy zajęty i `false` w przeciwnym wypadku.
     */
    public boolean getBusy() {
        return this.busy;
    }

    /**
     * Getter zmiennej `waiting`. Zwraca listę oczekujących procesów.
     */
    public LinkedList<PCB> getWaiting() {
        return this.waiting;
    }
}