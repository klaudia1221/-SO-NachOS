
package com.BamoOS.Modules.Processor;
import java.util.ArrayList;

import Interpreter.Interpreter;
import ProcessMenager.PCB;
import ProcessMenager.PCB.State;
import ProcessMenager.ProcessManager;
import ProcessMenager.IProcessManager;
import com.BamoOS.Modules.Interpreter.Interpreter;
import com.BamoOS.Modules.ProcessManager.IProcessManager;
import com.BamoOS.Modules.ProcessManager.PCB;


// do zmiany argumenty wszedzie tam gdzie jest PID jak zobacze jak to wyglada u Bartka
public class Processor implements Processor.ProcessorInterface {
	private ArrayList<PCB> lista_procesow_gotowych = new ArrayList<PCB>();
//	private ArrayList<PCB> lista_procesow_gotowych2 = new ArrayList<PCB>();

	private PCB Active;
	private PCB proces;
	public double alpha; // Weighting factor od 0 do 1 (postarzanie) okresla poziom istotnosci ostatnej fazy
	public int time;
	private IProcessManager processManager;
	private Interpreter interpreter;
	 //private IProcessManager ProcessManager;
	ArrayList<ArrayList<PCB>> lista = processManager.getProcessList();
public Processor(IProcessManager processManager,Interpreter interpreter) {
	this.interpreter = interpreter;
	this.processManager = processManager;
	alpha = 0.5; // Weighting factor od 0 do 1 (postarzanie) okresla poziom istotnosci ostatnej fazy
	//Running = processManager.getMain().pcb;
	time = 0;
}
public void dodaj_proces() { // dodaje na liste procesow gotowych proces // nie mam pojecia czy tu moze tak byc ale tylko cos
	// takiego przyszlo mi do glowy , ogolnie chodzi o to ze przejrzy liste procesow znajdzie te gotowe i doda na liste moja
	for(ArrayList<PCB> processlist : this.lista) {
		for(PCB pcb : processlist) {
	proces = processManager.getPCB(pcb.getPID());
	if(proces.getState() == State.READY) {
	lista_procesow_gotowych.add(proces);

	}
		}
	}
}


//jesli istnieje i ma stan zakonczony to go usuwa wedlug metody Bartka
public void Scheduler() {
	if (Active != null && Active.getState() == PCB.State.FINISHED) {
		time = Active.getTimer();
		processManager.killProcess(Active.getPID());
		//Running = processManager.getMain().pcb;
	}
	if(Active != null && Active.getState() != PCB.State.ACTIVE)
	{
		if (lista_procesow_gotowych.size() > 0)
		{
			for (int i = 0; i < lista_procesow_gotowych.size(); i++) {
				PCB p = lista_procesow_gotowych.get(i);
			if (time == 0) {
					p.setThau(10);
			}
					else{
						p.setThau((int) alpha * time + (1 - alpha) * Running.getThau());
					}

			}
			int index_nastepnego = 0;
			PCB nastepny = lista_procesow_gotowych.get(0);
			//System.out.println();
			for (int i = 0; i < lista_procesow_gotowych.size(); i++) {
				PCB ptemp = lista_procesow_gotowych.get(i);
				//System.out.println(ptemp.getThau());
				if (ptemp.getThau() < nastepny.getThau()) {
					nastepny = ptemp;
					index_nastepnego = i;
				}
			}
			lista_procesow_gotowych.remove(index_nastepnego);
			Running = nastepny;
			Running.setState(PCB.State.ACTIVE);// chodzi o to zeby ustawic stan na ACTIVE nie wiem jak sie do tego dobrac
		}// konczy size>0
		else
		{
			System.out.println("Nie ma zadnego procesu na liscie procesow gotwych .");
		}
	} // konczy jesli stan nie jest aktywny
} // konczy shedulera

public void wykonaj(String order[]) {   //
	Scheduler();
	if(Running != null&& Running.getPID() != 0) {
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

