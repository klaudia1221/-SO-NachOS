package com.BamoOS.Modules.ConditionVariable;

import java.util.LinkedList;

import com.BamoOS.Modules.ProcessManager.IProcessManager;
import com.BamoOS.Modules.ProcessManager.ProcessManager;
import com.BamoOS.Modules.ProcessManager.PCB;

/**
 * Synchronizacja procesów w oparciu o zmienne warunkowe (ang. condition variable).
 */
public class ConditionVariable implements IConditionVariable {
    private LinkedList<PCB> waiting; // kolejka FIFO (bufor)
    private boolean busy; // czy zasób jest zajęty (używany)
    private IProcessManager processManager;
    private int pgid;


    /**
     * Tworzy nowy obiekt zmiennej warunkowej do synchronizacji.
     * Inicjalizuje pustą kolejkę procesów oczekujących (PCB) oraz zmienną busy wskazującą czy zasób jest zajęty.
     *
     * @param processManager obiekt klasy ProcessManager. Potrzebna metoda SetState oraz zmienna ActivePCB.
     * @see ProcessManager
     * @see PCB
     */
    public ConditionVariable(IProcessManager processManager) {
        this.waiting = new LinkedList<>();
        this.busy = false;
        this.processManager = processManager;
        this.pgid = 0;
    }

    /**
     * Tworzy obiekt nowy zmiennej warunkowej do sychronizacji komunikatów (synchronizacja w grupie procesów)
     * Inicjalizuje pustą kolejkę procesów oczekujących (PCB) oraz zmienną busy wskazującą czy zasób jest zajęty.
     *
     * @param processManager obiekt klasy ProcessManager. Potrzebna metoda SetState oraz zmienna ActivePCB.
     * @param pgid id grupy do ktorej nalezy dana zmienna warunkowa.
     * @see ProcessManager
     * @see PCB
     */
    public ConditionVariable(IProcessManager processManager, int pgid) {
        this.waiting = new LinkedList<>();
        this.busy = false;
        this.processManager = processManager;
        this.pgid = pgid;
    }

    /**
     * Zmienia stan aktualnie aktywnego procesu na `WAITING` i dodaje do kolejki procesów oczekujących.
     * Wywołanie planisty następuje w metodzie SetState.
     *
     * @param forceLock jezeli `true` powoduje bezwarunkowe zablokowanie procesu bez sprawdzania czy zasób jest zajęty
     *                  i nie powoduje ustawienia pola `busy` na `true`
     */
    public void await(boolean forceLock) {
        if (this.busy || forceLock) {
            processManager.getActivePCB().setState(PCB.State.WAITING); // zmien stan procesu i wywołaj planis // zmien stan procesu i wywołaj planiste
            this.waiting.addLast(this.processManager.getActivePCB()); // dodaj do kolejki
        } else {
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
            pcb.setState(PCB.State.READY); // zmien stan procesu na READY i wywolaj planiste
            this.waiting.removeFirst(); // usuń z początku kolejki oczekujących (ten ktorego stan zmienilismy)
        }
        this.busy = false; // "uwolnij" zasób
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
     * Wyswietla informacje o zmiennej warunkowej dla uzytkownika
     */
    public void printInfo() {
        if(this.waiting.isEmpty()) {
            System.out.println("Brak procesow oczekujących w zmiennej warunkowej.");
        }
        else {
            System.out.println("Procesy oczekujące w kolejce zmiennej warunkowej:");
            for(PCB pcb : this.waiting){
                pcb.printInfo();
            }
        }

        if(this.busy){
            System.out.println("Zasób jest `zajęty` (busy).");
        }
        else {
            System.out.println("Zasób jest `wolny` (not busy).");
        }
    }

    public boolean getBusy() {
        return this.busy;
    }

    public LinkedList<PCB> getWaiting() {
        return this.waiting;
    }

    public int getPgid() {
        return pgid;
    }

    public void setPgid(int pgid) {
        this.pgid = pgid;
    }
}