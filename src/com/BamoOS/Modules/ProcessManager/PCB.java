package com.BamoOS.Modules.ProcessManager;

public class PCB {
	
    public enum State{
        ACTIVE,
        WAITING,
    	READY,
    	NEW,
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
	//public PageTable pageTable;
	
	
	public PCB(int ProcessID, String ProcessName, int ProcessGroup) {
		this.PID = ProcessID;
		this.PGID = ProcessGroup;
		this.ProcessName = ProcessName;
		this.A = 0;
		this.B = 0;
		this.C = 0;
		this.Counter = 0;
		this.Timer = 0;
		this.ProcessState = State.NEW;
	}
	
	public int GetPID() {
		return this.PID;
	}
	
	public int GetPGID() {
		return this.PGID;
	}
	
	public String GetName() {
		return this.ProcessName;
	}
	
	public State GetState() {
		return this.ProcessState;
	}
	
	public void SetState(State state) {
		this.ProcessState = state;
	}
	
	public int GetRegister(Register register) {
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
	
	public void SetRegister(Register register, int val) {
		switch(register) {
		case A:
			this.A = val;
		case B:
			this.B = val;
		case C:
			this.C = val;
		}
	}
	
	public int GetCounter() {
		return this.Counter;
	}
	
	public void SetCounter(int counter) {
		this.Counter = counter;
	}

	
	public int GetTimer() {
		return Timer;
	}
	
	public void SetTimer(int timmer) {
		this.Timer = timmer;
	}
	
	public void PrintInfo() {
		System.out.println("PID"+'\t'+"PGID"+'\t'+"A"+'\t'+"B"+'\t'+"C"+'\t'+"Licznik"+'\t'+"Timer"+'\t'+"Stan"+'\t'+"Nazwa");
		System.out.println("------------------------------------------------------------------------");
		System.out.print(this.GetPID()+"\t"+this.GetPGID()+"\t");
		System.out.print(this.GetRegister(Register.A)+"\t"+this.GetRegister(Register.B)+"\t"+this.GetRegister(Register.C)+"\t");
		System.out.println(this.GetCounter()+"\t"+this.GetTimer()+"\t"+this.GetState()+"\t"+this.GetName());
	}
	//Gettery i Settery dla p�l wiadomo�ci, interpretera oraz cpu
	
}
