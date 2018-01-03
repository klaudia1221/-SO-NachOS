
package com.BamoOS.Modules.Processor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Interpreter.Interpreter;
import ProcessMenager.PCB;
import ProcessMenager.PCB.State;
import ProcessMenager.ProcessManager;
import ProcessMenager.IProcessManager;
import com.BamoOS.Modules.Interpreter.Interpreter;
import com.BamoOS.Modules.ProcessManager.IProcessManager;
import com.BamoOS.Modules.ProcessManager.PCB;


// do zmiany argumenty wszedzie tam gdzie jest PID jak zobacze jak to wyglada u Bartka
public class Processor implements IProcessor {
//	private ArrayList<PCB> lista_procesow_gotowych2 = new ArrayList<PCB>();

	private PCB Active;
	private PCB proces;
	public double alpha; // Weighting factor od 0 do 1 (postarzanie) okresla poziom istotnosci ostatnej fazy
	public int time;
	private IProcessManager processManager;
	private Interpreter interpreter;
	 //private IProcessManager ProcessManager;
//	ArrayList<ArrayList<PCB>> lista = processManager.getProcessList();
public Processor(IProcessManager processManager,Interpreter interpreter) {
	this.interpreter = interpreter;
	this.processManager = processManager;
	alpha = 0.5d; // Weighting factor od 0 do 1 (postarzanie) okresla poziom istotnosci ostatnej fazy
	//Running = processManager.getMain().pcb;
	time = 0;
}

//jesli istnieje i ma stan zakonczony to go usuwa wedlug metody Bartka
public void Scheduler() {
    ArrayList<PCB> readyProcesses = processManager.getReadyProcesses();

    if(Active != null && Active.getState() == PCB.State.FINISHED){
        time = Active.getTimer();
        processManager.killProcess(Active.getPID());
    }
    if(readyProcesses.size() < 0){
        Active = processManager.getPCB(0);
        Active.setState(PCB.State.ACTIVE);
        processManager.setActivePCB(Active);
    }else{
        calculateThau(readyProcesses);
        sortProcessReadyByThau(readyProcesses);
        for(PCB pcb : readyProcesses){
            if(!(pcb.getState() == PCB.State.WAITING)){
                Active = pcb;
                pcb.setState(PCB.State.ACTIVE);
                processManager.setActivePCB(Active);
                return;
            }
        }
        processManager.getPCB(Active.getPID()).setState();
        Active.

    }
} // konczy shedulera
private void calculateThau(ArrayList<PCB> readyProcesses){
    for(PCB pcb : readyProcesses){
        if(time == 0){
            pcb.setTau(10.0d);
        }else{
            pcb.setTau(alpha * time + (1.0d - alpha) * Active.getTau());
        }
    }
}
private void sortProcessReadyByThau(ArrayList<PCB> readyProcesses){
    readyProcesses.sort((PCB pcb1, PCB pcb2)-> {
        if(pcb1.getTau() < pcb2.getTau()) return -1;
        if(pcb1.getTau() > pcb2.getTau()) return 1;
        return 0;
    });
}
public void wykonaj() {   //
	Scheduler();
	if(Active != null && Active.getPID() != 0) {
		try{
		interpreter.Exe(order);
		}
		catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
            System.out.println("Incorrect order");
            Running.setState(PCB.State.FINISHED);
            Scheduler(); // jak bedzie blad i nie moze wykonac to zmieni stan procesu na Finished odapli Shedulera
            // i potem wroci do procesu glownego

		}
	}

}
	public void wyswietl_liste_procesow_gotowych() {
		System.out.println("Lista procesow gotowych");
		for (PCB proces : lista_procesow_gotowych) {
			System.out.println("-------------------------");
			System.out.println("PID " + proces.getPID());
			System.out.println("-------------------------");
		}
	}

}

