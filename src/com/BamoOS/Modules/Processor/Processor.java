import java.util.ArrayList;

import java.util.Vector;
import interpreter.Interpreter;
import processManager.PCB; // czy jak tam jest modul Bartka nazwany
import processManager.PCB.Stany;
import processManager.Process;

// do zmiany argumenty wszedzie tam gdzie jest PID jak zobacze jak to wyglada u Bartka
public class Processor {
	private ArrayList<PCB> lista_procesow_gotowych = new ArrayList<PCB>();
	private ArrayList<PCB> lista_procesow_gotowych2 = new ArrayList<PCB>();
	public Vector indexRozkazu=new Vector();

	public PCB Running;
	public PCB proces;
	public int temp;
	public double alpha; // stala do wzrou 
	public int time;
	private ProcessManager processManager;
	private Interpreter interpreter;

public Processor(ProcessManager processManager,Interpreter interpreter) {
	this.interpreter = interpreter;
	this.processManager = processManager;
	alpha = 0.5;
	Running = processManager.getMain().pcb;
	time = 0;
}
public void dodaj_proces(String file) { // dodaje na liste procesow gotowych proces
	int nowyProces = processManager.newProcess(file);// lub po nazwie albo jeszcze inaczej zalezy od modulu Bartka
	proces = processManager.getProces(nowyProces).pcb;
	//proces.state = Stany.GOTOWY; 
	lista_procesow_gotowych.add(proces);
}
public void Scheduler() {
	if (Running != null && Running.state == Stany.ZAKONCZONY) {
		time = Running.timer;
		processManager.kill(Running.PID);
		Running = processManager.getMain().pcb;
	} // jesli istnieje i ma stan zakonczony to go usuwa wedlug metody Bartka i zwraca na process glowny(bezczynosci)
	if (Running != null  && Running.state == Stany.Aktywny) { // sprawdza czy jest juz jakis proces aktywny jesli tak to nie podejmuje dzialan
		return;
}
	if(Running!=null && Running.state!=Stany.AKTYWNY)
	{
		if (lista_procesow_gotowych.size() > 0)
		{
			boolean bezpiecznik = false;
			for (int i = 0; i < lista_procesow_gotowych.size(); i++) {
				PCB p = lista_procesow_gotowych.get(i);
				PCB p2;
				if (time == 0) {
					p.thau = 10;
					lista_procesow_gotowych2.add(p);
				} else {
					if (bezpiecznik == false) {
						p.thau = (int) alpha * time + (1 - alpha) * Running.thau;
						bezpiecznik = true;
					} else {
						p2 = lista_procesow_gotowych2.get(lista_procesow_gotowych2.size() - 1);
						p.thau = (int) alpha * time + (1 - alpha) * p2.thau;
						lista_procesow_gotowych2.add(p);
					}

				}
			}
			bezpiecznik = false;
			for (int iterator = lista_procesow_gotowych.size() - 1; iterator >= 0; iterator--) {
				lista_procesow_gotowych.remove(iterator);
			}

		} else {
			if (!(lista_procesow_gotowych2.size() > 0)) {
				System.out.println("Nie ma zadnego procesu na liscie.");
			}
			Running = processManager.getMain().pcb;
		}

		int index_nastepnego = 0;

		if (lista_procesow_gotowych2.size() > 0) {
			PCB nastepny = lista_procesow_gotowych2.get(0);
			//System.out.println();
			for (int i = 0; i < lista_procesow_gotowych2.size(); i++) {
				PCB ptemp = lista_procesow_gotowych2.get(i);
				System.out.println(ptemp.thau);
				if (ptemp.thau < nastepny.thau) {
					nastepny = ptemp;
					index_nastepnego = i;
				}
			}
			lista_procesow_gotowych2.remove(index_nastepnego);
			Running = nastepny;
			Running.state = Stany.AKTYWNY;

		}

	}

}

public void wykonaj() { // tez do zmiany jak zobacze modul Kamili 
	Scheduler();
	if(Running != null) {
		Process process = processManager.getProces(Running.PID);
		String rozkaz[] = process.getNextRozkaz();
		//Process.tmp;
		interpreter.set_regA(process.pcb.A);
		interpreter.set_regB(process.pcb.B);
		interpreter.set_regC(process.pcb.C);
		interpreter.set_PC(process.pcb.counter);
		interpreter.set_flag_F(process.pcb.flag_F);
		
		interpreter.exe(rozkaz, Running.PID);
		Running.A = interpreter.get_regA();
		Running.B = interpreter.get_regB();
		Running.C = interpreter.get_regC();
		Running.counter = interpreter.get_PC();
		Running.flag_F = interpreter.get_flag_F();
		
		System.out.println("PID: " + Running.PID);
		interpreter.showRegisters();
}
}

	public void wyswietl_liste_procesow_gotowych() {
		System.out.println("Lista procesow gotowych");
		for (PCB proces : lista_procesow_gotowych) {
			System.out.println("-------------------------");
			System.out.println("PID " + proces.PID);
			System.out.println("-------------------------");
		}
	}
}
		

