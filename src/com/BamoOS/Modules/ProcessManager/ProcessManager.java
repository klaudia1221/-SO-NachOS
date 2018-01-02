package com.BamoOS.Modules.ProcessManager;

import java.util.ArrayList;

import com.BamoOS.Modules.ProcessManager.PCB.Register;

public class ProcessManager implements IProcessManager {
	ArrayList<ArrayList<PCB>> ProcessGroups;
	private int ProcessCounter;
	private int GroupsCounter;
	public PCB ActivePCB;
	
	public ProcessManager() {
		this.ProcessGroups = new ArrayList<ArrayList<PCB>>();
		this.ProcessCounter = 0;
		this.GroupsCounter = 0;
		newProcessGroup("Proces bezczynno�ci");
	}
	public PCB getActivePCB(){
		return ActivePCB;
	}
	//Nowy proces o ile zosta�a wcze�niej utworzona grupa
	public void newProcess(String ProcessName, int PGID) {
		try {
			if(PGID == 0) throw new Exception("Brak dost�pu do grupy procesu bezczynno�ci");
			ArrayList<PCB> temp = checkIfGroupExists(PGID);
			if(temp != null) {
				PCB pcb = new PCB(this.ProcessCounter, ProcessName, PGID);
				temp.add(pcb);
				this.ProcessCounter++;
			}else throw new Exception("Brak grupy o podanym PGID");
		}catch(Exception e) {System.out.println(e);}
	}
	public void newProcess(String ProcessName, int PGID, String FileName) {
		try {
			if(PGID == 0) throw new Exception("Brak dost�pu do grupy procesu bezczynno�ci");
			ArrayList<PCB> temp = checkIfGroupExists(PGID);
			if(temp != null) {
				PCB pcb = new PCB(this.ProcessCounter, ProcessName, PGID);
				temp.add(pcb);
				//TODO odczyt z pliku
				//TODO �adowanie programu z pliku do pami�ci
				//PageTable pt1 = new PageTable("proces1", testTable1.length);
		        //ram.pageTables.put(pt1.processName, pt1);
				//ram.exchangeFile.writeToExchangeFile("proces1", testTable1);
				this.ProcessCounter++;
			}else throw new Exception("Brak grupy o podanym PGID");
		}catch(Exception e) {System.out.println(e);}
	}
	public void runNew() {
		newProcess("P"+this.ProcessCounter, this.ActivePCB.getPGID());
	}
	public void runNew(String FileName) {
		newProcess("P"+this.ProcessCounter, this.ActivePCB.getPGID(), FileName);
	}
	//Usuwanie procesu
	public void killProcess(int PID) {
		//TODO powiadomi� inne modu�y ?pami��?
		try {
			if(PID == 0) throw new Exception("Nie mo�na zabi� procesu bezczynno�ci");
			PCB temp = checkIfProcessExists(PID);
			if(temp != null) {
				checkIfGroupExists(temp.getPGID()).remove(temp);
			}else throw new Exception("Brak procesu o podanym PID");
		}catch(Exception e) {System.out.println(e);}
	}
	//Usuwanie grup
	public void killProcessGroup(int PGID) {
		//TODO powiadomi� inne modu�y ?pami��?
		try {
			if(PGID == 0) throw new Exception("Brak dost�pu do grupy procesu bezczynno�ci");
			ArrayList<PCB> temp = checkIfGroupExists(PGID);
			if(temp != null) {
				ProcessGroups.remove(temp);
			}else throw new Exception("Brak grupy o podanym PGID");
		}catch(Exception e) {System.out.println(e);}
	}
	//Tworzenie nowej grupy oraz pierwszego procesu
	public void newProcessGroup(String ProcessName) {
		PCB pcb = new PCB(this.ProcessCounter, ProcessName, this.GroupsCounter);
		ArrayList<PCB> al = new ArrayList<PCB>();
		al.add(pcb);
		ProcessGroups.add(al);
		this.ProcessCounter++;
		this.GroupsCounter++;
	}
	
	public ArrayList<PCB> checkIfGroupExists(int PGID) {
		for(ArrayList<PCB> processlist : this.ProcessGroups) {
			if(!processlist.isEmpty()) {
				if(processlist.get(0).getPGID() == PGID) {
					return processlist;
				}
			}
		}
		return null;
	}
	public PCB checkIfProcessExists(int PID) {
		for(ArrayList<PCB> processlist : this.ProcessGroups) {
			for(PCB pcb : processlist) {
				if(pcb.getPID() == PID) {
					return pcb;
				}
			}
		}
		return null;
	}
	//Get ProcessControlBlock
	public PCB getPCB(int PID) {
		try {
		PCB temp = checkIfProcessExists(PID);
		if (temp == null) throw new Exception("Brak procesu o podanym PID");
		return temp;
		}catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	//Zwracanie listy proces�w
	public ArrayList<ArrayList<PCB>> getProcessList(){
		return this.ProcessGroups;
	}
	
	public void PrintGroupInfo(int PGID) {
		try {
			ArrayList<PCB> temp = checkIfGroupExists(PGID);
			if (temp == null) throw new Exception("Brak grupy proces�w o podanym PGID");
			//System.out.println("PID"+'\t'+"PGID"+'\t'+"Nazwa");
			//System.out.println("------------------------");
			System.out.println("PID"+'\t'+"PGID"+'\t'+"A"+'\t'+"B"+'\t'+"C"+'\t'+"Licznik"+'\t'+"Timer"+'\t'+"Stan"+'\t'+"Nazwa");
			System.out.println("------------------------------------------------------------------------");
			for(PCB pcb : temp) {
				//System.out.println(pcb.GetPID()+"\t"+pcb.GetPGID()+"\t"+pcb.GetName());
				System.out.print(pcb.getPID()+"\t"+pcb.getPGID()+"\t");
				System.out.print(pcb.getRegister(Register.A)+"\t"+pcb.getRegister(Register.B)+"\t"+pcb.getRegister(Register.C)+"\t");
				System.out.println(pcb.getCounter()+"\t"+pcb.getTimer()+"\t"+pcb.getState()+"\t"+pcb.getName());
			}
		}catch (Exception e) {System.out.println(e);}
	}
	
	public void PrintProcesses() {
		//System.out.println("PID"+'\t'+"PGID"+'\t'+"Nazwa");
		//System.out.println("------------------------");
		System.out.println("PID"+'\t'+"PGID"+'\t'+"A"+'\t'+"B"+'\t'+"C"+'\t'+"Licznik"+'\t'+"Timer"+'\t'+"Stan"+'\t'+"Nazwa");
		System.out.println("------------------------------------------------------------------------");
		for(ArrayList<PCB> processlist : this.ProcessGroups) {
			for(PCB pcb : processlist) {
				//System.out.println(pcb.GetPID()+"\t"+pcb.GetPGID()+"\t"+pcb.GetName());
				System.out.print(pcb.getPID()+"\t"+pcb.getPGID()+"\t");
				System.out.print(pcb.getRegister(Register.A)+"\t"+pcb.getRegister(Register.B)+"\t"+pcb.getRegister(Register.C)+"\t");
				System.out.println(pcb.getCounter()+"\t"+pcb.getTimer()+"\t"+pcb.getState()+"\t"+pcb.getName());
			}
		}
	}
}
