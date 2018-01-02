package com.BamoOS.Modules.ProcessManager;

import java.util.ArrayList;

import com.BamoOS.Modules.ProcessManager.PCB.Register;

public class ProcessManager {
	ArrayList<ArrayList<PCB>> ProcessGroups;
	private int ProcessCounter;
	private int GroupsCounter;
	public PCB ActivePCB;
	
	public ProcessManager() {
		this.ProcessGroups = new ArrayList<ArrayList<PCB>>();
		this.ProcessCounter = 0;
		this.GroupsCounter = 0;
		NewProcessGroup("Proces bezczynno�ci");
	}
	//Nowy proces o ile zosta�a wcze�niej utworzona grupa
	public void NewProcess(String ProcessName, int PGID) {
		try {
			if(PGID == 0) throw new Exception("Brak dost�pu do grupy procesu bezczynno�ci");
			ArrayList<PCB> temp = CheckIfGroupExists(PGID);
			if(temp != null) {
				PCB pcb = new PCB(this.ProcessCounter, ProcessName, PGID);
				temp.add(pcb);
				this.ProcessCounter++;
			}else throw new Exception("Brak grupy o podanym PGID");
		}catch(Exception e) {System.out.println(e);}
	}
	public void NewProcess(String ProcessName, int PGID, String FileName) {
		try {
			if(PGID == 0) throw new Exception("Brak dost�pu do grupy procesu bezczynno�ci");
			ArrayList<PCB> temp = CheckIfGroupExists(PGID);
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
	public void RunNew() {
		NewProcess("P"+this.ProcessCounter, this.ActivePCB.GetPGID());
	}
	public void RunNew(String FileName) {
		NewProcess("P"+this.ProcessCounter, this.ActivePCB.GetPGID(), FileName);
	}
	//Usuwanie procesu
	public void KillProcess(int PID) {
		//TODO powiadomi� inne modu�y ?pami��?
		try {
			if(PID == 0) throw new Exception("Nie mo�na zabi� procesu bezczynno�ci");
			PCB temp = CheckIfProcessExists(PID);
			if(temp != null) {
				CheckIfGroupExists(temp.GetPGID()).remove(temp);
			}else throw new Exception("Brak procesu o podanym PID");
		}catch(Exception e) {System.out.println(e);}
	}
	//Usuwanie grup
	public void KillProcessGroup(int PGID) {
		//TODO powiadomi� inne modu�y ?pami��?
		try {
			if(PGID == 0) throw new Exception("Brak dost�pu do grupy procesu bezczynno�ci");
			ArrayList<PCB> temp = CheckIfGroupExists(PGID);
			if(temp != null) {
				ProcessGroups.remove(temp);
			}else throw new Exception("Brak grupy o podanym PGID");
		}catch(Exception e) {System.out.println(e);}
	}
	//Tworzenie nowej grupy oraz pierwszego procesu
	public void NewProcessGroup(String ProcessName) {
		PCB pcb = new PCB(this.ProcessCounter, ProcessName, this.GroupsCounter);
		ArrayList<PCB> al = new ArrayList<PCB>();
		al.add(pcb);
		ProcessGroups.add(al);
		this.ProcessCounter++;
		this.GroupsCounter++;
	}
	
	public ArrayList<PCB> CheckIfGroupExists(int PGID) {
		for(ArrayList<PCB> processlist : this.ProcessGroups) {
			if(!processlist.isEmpty()) {
				if(processlist.get(0).GetPGID() == PGID) {
					return processlist;
				}
			}
		}
		return null;
	}
	public PCB CheckIfProcessExists(int PID) {
		for(ArrayList<PCB> processlist : this.ProcessGroups) {
			for(PCB pcb : processlist) {
				if(pcb.GetPID() == PID) {
					return pcb;
				}
			}
		}
		return null;
	}
	//Get ProcessControlBlock
	public PCB GetPCB(int PID) {
		try {
		PCB temp = CheckIfProcessExists(PID);
		if (temp == null) throw new Exception("Brak procesu o podanym PID");
		return temp;
		}catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	//Zwracanie listy proces�w
	public ArrayList<ArrayList<PCB>> GetProcessList(){
		return this.ProcessGroups;
	}
	
	public void PrintGroupInfo(int PGID) {
		try {
			ArrayList<PCB> temp = CheckIfGroupExists(PGID);
			if (temp == null) throw new Exception("Brak grupy proces�w o podanym PGID");
			//System.out.println("PID"+'\t'+"PGID"+'\t'+"Nazwa");
			//System.out.println("------------------------");
			System.out.println("PID"+'\t'+"PGID"+'\t'+"A"+'\t'+"B"+'\t'+"C"+'\t'+"Licznik"+'\t'+"Timer"+'\t'+"Stan"+'\t'+"Nazwa");
			System.out.println("------------------------------------------------------------------------");
			for(PCB pcb : temp) {
				//System.out.println(pcb.GetPID()+"\t"+pcb.GetPGID()+"\t"+pcb.GetName());
				System.out.print(pcb.GetPID()+"\t"+pcb.GetPGID()+"\t");
				System.out.print(pcb.GetRegister(Register.A)+"\t"+pcb.GetRegister(Register.B)+"\t"+pcb.GetRegister(Register.C)+"\t");
				System.out.println(pcb.GetCounter()+"\t"+pcb.GetTimer()+"\t"+pcb.GetState()+"\t"+pcb.GetName());
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
				System.out.print(pcb.GetPID()+"\t"+pcb.GetPGID()+"\t");
				System.out.print(pcb.GetRegister(Register.A)+"\t"+pcb.GetRegister(Register.B)+"\t"+pcb.GetRegister(Register.C)+"\t");
				System.out.println(pcb.GetCounter()+"\t"+pcb.GetTimer()+"\t"+pcb.GetState()+"\t"+pcb.GetName());
			}
		}
	}
}
