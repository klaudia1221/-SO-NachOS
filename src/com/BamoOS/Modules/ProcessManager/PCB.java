package com.BamoOS.Modules.ProcessManager;

import com.BamoOS.Modules.Communication.Sms;

import java.util.ArrayList;

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
	//Id grupy do kt�rej nale�y proces
	private int PGID;
	//Rejestry
	private int A;
	private int B;
	private int C;
	//Pola dla interpretera oraz cpu
	private int Counter;
	private int Timer;
	private double Tau;
	//public PageTable pageTable;
	//
	private ArrayList<Sms> SmsList;
	
	
	public PCB(int ProcessID, String ProcessName, int ProcessGroup) {
		this.PID = ProcessID;
		this.PGID = ProcessGroup;
		this.ProcessName = ProcessName;
		this.A = 0;
		this.B = 0;
		this.C = 0;
		this.Counter = 0;
		this.Timer = 0;
		this.ProcessState = State.READY;
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
		return this.Counter;
	}
	
	public void setCounter(int counter) {
		this.Counter = counter;
	}

	public int getTimer() {
		return Timer;
	}
	
	public void setTimer(int timmer) {
		this.Timer = timmer;
	}
	
	public void printInfo() {
		System.out.println("PID"+'\t'+"PGID"+'\t'+"A"+'\t'+"B"+'\t'+"C"+'\t'+"Licznik"+'\t'+"Timer"+'\t'+"Stan"+'\t'+"Nazwa");
		System.out.println("------------------------------------------------------------------------");
		System.out.print(this.getPID()+"\t"+this.getPGID()+"\t");
		System.out.print(this.getRegister(Register.A)+"\t"+this.getRegister(Register.B)+"\t"+this.getRegister(Register.C)+"\t");
		System.out.println(this.getCounter()+"\t"+this.getTimer()+"\t"+this.getState()+"\t"+this.getName());
		//TODO wywolaj Condition variable z danej grupy
		//printInfo
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
}
