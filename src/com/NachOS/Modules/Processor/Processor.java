
package com.NachOS.Modules.Processor;

import java.util.ArrayList;

import com.NachOS.Modules.Interpreter.Interpreter;
import com.NachOS.Modules.ProcessManager.IProcessManager;
import com.NachOS.Modules.ProcessManager.PCB;


public class Processor implements IProcessor {

    public double alpha; // Weighting factor od 0 do 1 (postarzanie) okresla poziom istotnosci ostatnej fazy
    public int time;
    private IProcessManager processManager;
    private Interpreter interpreter;

    public Processor(IProcessManager processManager, Interpreter interpreter) {
        this.interpreter = interpreter;
        this.processManager = processManager;
        alpha = 0.5d; // Weighting factor od 0 do 1 (postarzanie) okresla poziom istotnosci ostatnej fazy
        time = 0;
    }
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }
    /*
    Jak działa ten algorytm:
    1. Sprawdzamy czy proces aktywny ma stan FINISED || WAITING && czy lista porceós gotowych jest większa od zero(czy jest jakiś proces w liście procesów gotowy &&
    i czy aktywny proces to nie proces bezczyności. Jeśli tak:
        I. Sprawdź czy aktwnyny PCB nie ma stanu Finished:
            a. Ustaw pole time na wartość Process Countera aktywnego PCB
            b. Zabij proces.
        II. Oblicz Thau dla każdego gotowego procesu.
        III. Posortuj liste gotowych proceós(proces o najmniejszym Thau pierwszy)
        IV. Ustaw pierwszy element listy jako aktywny proces.
        V. Ustaw stan wybranego procesu na aktywny.
    2. Jeśli lista jest pusta, a wybrany proces ma stan FINISED lub WAITING:
        I. Ustaw stan aktywny jako stan bezczynny.
     */
    //TODO NK zweryfikuje to
    public void Scheduler() {
        ArrayList<PCB> processReadyList = processManager.getReadyProcesses();
        if(processManager.getActivePCB().getState() == PCB.State.WAITING || processManager.getActivePCB().getState() == PCB.State.FINISHED ||
                (processReadyList.size() > 0 && processManager.getActivePCB().getPID() == 0)){
            if(processManager.getActivePCB().getState() == PCB.State.FINISHED){
                time = processManager.getActivePCB().getTimer();
                try{
                    processManager.killProcess(processManager.getActivePCB().getPID());
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
            calculateThau(processReadyList);
            sortProcessReadyByThau(processReadyList);

            processManager.getActivePCB().setState(PCB.State.READY);
            processManager.setActivePCB(processReadyList.get(0));
            processManager.getActivePCB().setState(PCB.State.ACTIVE);
        }
        if(processReadyList.size() == 0 &&
                (processManager.getActivePCB().getState() == PCB.State.FINISHED || processManager.getActivePCB().getState() == PCB.State.WAITING)){
             processManager.setActivePCB(processManager.getPCB(0));
             processManager.getActivePCB().setState(PCB.State.ACTIVE);
        }
    }

    private void calculateThau(ArrayList<PCB> readyProcesses) {
        for (PCB pcb : readyProcesses) {
            if (time == 0) {
                pcb.setTau(10.0d);
            } else {
                pcb.setTau(alpha * time + (1.0d - alpha) * processManager.getActivePCB().getTau());
            }
        }
    }

    private void sortProcessReadyByThau(ArrayList<PCB> readyProcesses) {
        readyProcesses.sort((PCB pcb1, PCB pcb2) -> {
            if (pcb1.getTau() < pcb2.getTau()) return -1;
            if (pcb1.getTau() > pcb2.getTau()) return 1;
            return 0;
        });
    }

    public void exe() {   //
        Scheduler();
        try{
            interpreter.Exe();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void printProcesses() {
        System.out.println("Lista procesow gotowych");
        for (PCB proces : processManager.getReadyProcesses()) {
            System.out.println("-------------------------");
            System.out.println("PID " + proces.getPID());
            System.out.println("-------------------------");
        }
    }

}

