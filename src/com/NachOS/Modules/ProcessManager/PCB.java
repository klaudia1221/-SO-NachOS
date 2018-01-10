package com.NachOS.Modules.ProcessManager;

import com.NachOS.Modules.Communication.Sms;
import com.NachOS.Modules.MemoryManagment.PageTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PCB {
	
    public enum State{
        ACTIVE,
        WAITING,
    	READY,
    	FINISHED
    }

    public enum Register{
    	A,
    	B,
    	C
    }
	
	//Id procesu
	private int PID;
	//Stan procesu, wstrzymany, gotowy itd
	private State ProcessState;
	//Nazwa procesu
	private String ProcessName;
	//Id grupy do której należy proces
	private int PGID;
	//Rejestry
	private int A;
	private int B;
	private int C;
	//Licznik etykiet
	private int ecounter;
	//Mapa konwertujaca etykiety na konkretną komórkę w pamięci
	private Map<Integer,Integer> mapLine = new HashMap<>();
	//Licznik ostatniej zajętej pamieci (kod programu)
	private int mcounter;
	private int Timer;
	private double Tau;
	//Tablica stronnic
	public PageTable pageTable;
	//Pola do komunikacji między procesorowej
	private ArrayList<Sms> SmsList = new ArrayList<>();
	private int LastSenderID;


	
	public PCB(int ProcessID, String ProcessName, int ProcessGroup) {
		this.PID = ProcessID;
		this.PGID = ProcessGroup;
	 	this.ProcessName = ProcessName;
		this.A = 0;
		this.B = 0;
		this.C = 0;
		this.ecounter = 0;
        this.mcounter = 0;
		this.Timer = 0;
		this.ProcessState = State.READY;
        mapLine = new HashMap<>();
	}

	public PCB(int ProcessID, String ProcessName, int ProcessGroup, PageTable pt, Map ml) {
		this.PID = ProcessID;
		this.PGID = ProcessGroup;
		this.ProcessName = ProcessName;
		this.A = 0;
		this.B = 0;
		this.C = 0;
        this.ecounter = 0;
        this.mcounter = 0;
		this.Timer = 0;
		this.ProcessState = State.READY;
		this.pageTable = pt;
		this.mapLine = ml;
	}
	
	public int getPID() {
		return this.PID;
	}
	
	public int getPGID() {
		return this.PGID;
	}
	
	public String getName() {
		return this.ProcessName;
	}
	
	public State getState() {
		return this.ProcessState;
	}
	
	public void setState(State state) {
		this.ProcessState = state;
	}
	
	public int getRegister(Register register) {
		switch(register) {
		case A:
			return this.A;
		case B:
			return this.B;
		case C:
			return this.C;
		}
		return A;
	}
	
	public void setRegister(Register register, int val) {
		switch(register) {
		case A:
			this.A = val;
		case B:
			this.B = val;
		case C:
			this.C = val;
		}
	}
	
	public int getCounter() {
		return this.ecounter;
	}
	
	public void setCounter(int counter) {
		this.ecounter = counter;
	}

	public int getTimer() {
		return Timer;
	}
	
	public void setTimer(int timmer) {
		this.Timer = timmer;
	}
	
	public void printInfo() {
		System.out.println("PID"+'\t'+"PGID"+'\t'+"A"+'\t'+"B"+'\t'+"C"+'\t'+"Licznik"+'\t'+"Timer"+'\t'+"Tau"+'\t'+"Stan"+'\t'+"Nazwa");
		System.out.println("--------------------------------------------------------------------------------");
		System.out.print(this.getPID()+"\t"+this.getPGID()+"\t");
		System.out.print(this.getRegister(Register.A)+"\t"+this.getRegister(Register.B)+"\t"+this.getRegister(Register.C)+"\t");
		System.out.println(this.getCounter()+"\t"+this.getTimer()+"\t"+this.getTau()+"\t"+this.getState()+"\t"+this.getName());
//		try {
//			PM.findConditionVariable(this.PGID).printInfo();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	//Gettery i Settery dla p�l wiadomo�ci, interpretera oraz cpu

	public ArrayList<Sms> getSmsList() {
		return SmsList;
	}

	public void setSmsList(ArrayList<Sms> smsList) {
		SmsList = smsList;
	}

	public double getTau() {
		return Tau;
	}

	public void setTau(double tau) {
		Tau = tau;
	}

	public int getLastSenderID() {
		return LastSenderID;
	}

	public void setLastSenderID(int lastSenderID) {
		LastSenderID = lastSenderID;
	}

	public Map<Integer, Integer> getMapLine() {
		return mapLine;
	}

	public void setMapLine(Map<Integer, Integer> mapLine) {
		this.mapLine = mapLine;
	}

	public int getMcounter(){return mcounter;}

	public void setMcounter(int m){this.mcounter = m;}
}
