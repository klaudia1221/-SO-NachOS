
package com.NachOS.Modules.Processor;

import java.util.ArrayList;

import com.NachOS.Modules.Exceptions.DivideZeroException;
import com.NachOS.Modules.Exceptions.FileSystemException;
import com.NachOS.Modules.Exceptions.InterpreterException;
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

    public void Scheduler() {
        PCB active = processManager.getActivePCB();
        ArrayList<PCB> processReadyList = processManager.getReadyProcesses();
        if(processManager.getActivePCB().getState() == PCB.State.WAITING || processManager.getActivePCB().getState() == PCB.State.FINISHED ||
                (processReadyList.size() > 0 && processManager.getActivePCB().getPGID() == 0)){
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
            processManager.setStateOfActivePCB(PCB.State.READY);
            processManager.setActivePCB(processReadyList.get(0));
            processManager.setStateOfActivePCB(PCB.State.ACTIVE);
        }
        if(processReadyList.size() == 0 &&
                (processManager.getActivePCB().getState() == PCB.State.FINISHED || processManager.getActivePCB().getState() == PCB.State.WAITING)){
             processManager.setActivePCB(processManager.getPCB(0));
             processManager.setStateOfActivePCB(PCB.State.ACTIVE);

        }
    }
    //TODO Obliczanie czasu.
    private void calculateThau(ArrayList<PCB> readyProcesses) {
        for (PCB pcb : readyProcesses) {
            if (time == 0) {
                pcb.setTau(10.0d);
            } else {
                pcb.setTau(alpha * processManager.getActivePCB().getTimer() + alpha * time);
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

    public void exe() throws Exception {   //
        Scheduler();
        try{
            interpreter.Exe();
        }catch(InterpreterException e){
            System.out.println(e.getMessage());
            System.out.println("Blad, zabijam proces " + processManager.getActivePCB().getPID() + " " + processManager.getActivePCB().getName());
            processManager.killProcess(processManager.getActivePCB().getPID());
        }catch(FileSystemException e){
            System.out.println(e.getMessage());
            System.out.println("Blad: " + processManager.getActivePCB().getPID() + " " + processManager.getActivePCB().getName());
            processManager.killProcess(processManager.getActivePCB().getPID());
        }catch(Exception e){
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

