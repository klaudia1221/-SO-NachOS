package com.BamoOS.Modules.ConditionVariable;

import java.util.LinkedList;
import com.BamoOS.Modules.ProcessManager.PCB;
import com.BamoOS.Modules.ProcessManager.ProcessManager;

/**
 * Synchronizacja procesów w oparciu o zmienne warunkowe (ang. condition variable).
 */
public class ConditionVariable implements IConditionVariable {
    private LinkedList<PCB> waiting; // kolejka FIFO (bufor)
    private boolean busy; // czy zasób jest zajęty (używany)
    private ProcessManager processManager;

    /**
     * Tworzy obiekt nowy zmiennej warunkowej.
     * Inicjalizuje pustą kolejkę procesów oczekujących (PCB) oraz zmienną busy wskazującą czy zasób jest zajęty.
     *
     * @param processManager obiekt klasy ProcessManager. Potrzebna metoda SetState oraz zmienna ActivePCB.
     * @see ProcessManager
     * @see PCB
     */
    public ConditionVariable(ProcessManager processManager) {
        this.waiting = new LinkedList<>();
        this.busy = false;
        this.processManager = processManager;
    }

    /**
     * Zmienia stan aktualnie aktywnego procesu na `WAITING` i dodaje do kolejki procesów oczekujących.
     * Wywołanie planisty następuje w metodzie SetState.
     */
    public void await() {
        if(this.busy) {
            processManager.ActivePCB.SetState(PCB.State.WAITING); // zmien stan procesu i wywołaj planiste
            this.waiting.addLast(this.processManager.ActivePCB); // dodaj do kolejki
        }
        else {
            this.busy = true;
        }
    }

    /**
     * Zmienia stan pierwszego procesu w kolejce na `READY` i usuwa go z kolejki procesów oczekująych.
     * Wywołanie planisty następuje w metodzie SetState.
     *
     * Brak efektu, gdy w kolejce nie ma oczekujących procesów.
     */
    public void signal() {
        if(!this.waiting.isEmpty()) { // jezeli są procesy oczeujace to wyrzuć pierwszy i zmien na `ready`
            PCB pcb = this.waiting.getFirst(); // weź pierwszy z kolejki (bufora)
            pcb.SetState(PCB.State.READY); // zmien stan procesu na READY i wywolaj planiste
            this.waiting.removeFirst(); // usuń z początku kolejki oczekujących (ten ktorego stan zmienilismy)

        }
        else {
            this.busy = false; // "uwolnij" zasób jezeli lista byla pusta
        }
    }

    /**
     * Wywołuje metodę `signal` dla wszystkich procesów w kolejce oczekująych
     */
    public void signalAll() {
        for (int i=0; i < this.waiting.size(); i++) { // wywołaj signal() tyle razy ile jest procesów oczekujaych
            this.signal();
        }
    }

    /**
     * Getter zmiennej `busy` określającej zajętość zasobu.
     * Zwraca `true`, gdy zajęty lub `false` w przeciwnym wypadku.
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