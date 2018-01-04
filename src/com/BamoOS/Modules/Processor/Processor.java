
package com.BamoOS.Modules.Processor;

import java.util.ArrayList;

import com.BamoOS.Modules.Interpreter.Interpreter;
import com.BamoOS.Modules.ProcessManager.IProcessManager;
import com.BamoOS.Modules.ProcessManager.PCB;


public class Processor implements IProcessor {

    private PCB Active;
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
        ArrayList<PCB> readyProcesses = processManager.getReadyProcesses();
        if(readyProcesses.size() != 0){
            if (Active != null && Active.getState() == PCB.State.FINISHED) {
                time = Active.getTimer();
                try {
                    processManager.killProcess(Active.getPID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (readyProcesses.size() == 0) {
                Active = processManager.getPCB(0);
                Active.setState(PCB.State.ACTIVE);
                processManager.setActivePCB(Active);
            } else {
                calculateThau(readyProcesses);
                sortProcessReadyByThau(readyProcesses);
                for (PCB pcb : readyProcesses) {
                    if (!(pcb.getState() == PCB.State.WAITING) && !(pcb.getPID() == 0)) {
                        Active = pcb;
                        pcb.setState(PCB.State.ACTIVE);
                        processManager.setActivePCB(Active);
                        return;
                    }
                }
                Active = processManager.getPCB(0);
                Active.setState(PCB.State.ACTIVE);
            }
        }

    }

    private void calculateThau(ArrayList<PCB> readyProcesses) {
        for (PCB pcb : readyProcesses) {
            if (time == 0) {
                pcb.setTau(10.0d);
            } else {
                pcb.setTau(alpha * time + (1.0d - alpha) * Active.getTau());
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

