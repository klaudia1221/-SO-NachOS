package com.BamoOS.Modules.ProcessManager;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import com.BamoOS.Modules.ConditionVariable.ConditionVariable;
import com.BamoOS.Modules.MemoryManagment.PageTable;
import com.BamoOS.Modules.MemoryManagment.RAM;
import com.BamoOS.Modules.ProcessManager.PCB.Register;
import com.BamoOS.Modules.Processor.IProcessor;


public class ProcessManager implements IProcessManager {
	private ArrayList<ArrayList<PCB>> ProcessGroups;
	private int ProcessCounter;
	private PCB ActivePCB;
	private int GroupsCounter;
	private ArrayList<ConditionVariable> ConditionVariables;
	private RAM ram;

	public ProcessManager(RAM ram) {
	    this.ram = ram;
		this.ProcessGroups = new ArrayList<ArrayList<PCB>>();
		this.ProcessCounter = 0;
		this.GroupsCounter = 0;
		ConditionVariables = new ArrayList<>();
	}
	public void setStartingActivePCB(){
		try {
			ActivePCB = newProcessGroup("Proces bezczynności");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PCB getActivePCB(){
		return ActivePCB;
	}
	public void setActivePCB(PCB activePCB){
	    this.ActivePCB = activePCB;
    }

    //Nowy proces o ile zosta�a wcze�niej utworzona grupa
	public PCB newProcess(String ProcessName, int PGID) throws Exception {
			if(PGID == 0 && checkIfProcessExists(0) != null) throw new Exception("Brak dost�pu do grupy procesu bezczynno�ci");
			ArrayList<PCB> temp = checkIfGroupExists(PGID);
			if(temp != null) {
				PCB pcb = new PCB(this.ProcessCounter, ProcessName, PGID);
				temp.add(pcb);
				this.ProcessCounter++;
				return pcb;
			}
			throw new Exception("Brak grupy o podanym PGID");
	}

	public PCB newProcess(String ProcessName, int PGID, String FileName) throws Exception {
			if(PGID == 0 && checkIfProcessExists(0) != null) throw new Exception("Brak dost�pu do grupy procesu bezczynno�ci");
			ArrayList<PCB> temp = checkIfGroupExists(PGID);
			if(temp != null) {
				String textFileContent = readCommandFile("src/" + FileName + ".txt");
				Map mapLine = new HashMap<Integer, Integer>();
				for(int i = 0, j = 0; i != -1;j++){
					i = textFileContent.indexOf(";", i);
					mapLine.put(j,i-1);
				}
                char[] code = textFileContent.toCharArray();
				PageTable pt1 = new PageTable(this.ProcessCounter, code.length);
				PCB pcb = new PCB(this.ProcessCounter, ProcessName, PGID, pt1, mapLine);
				temp.add(pcb);
		        ram.pageTables.put(this.ProcessCounter, pt1);
				ram.exchangeFile.writeToExchangeFile(this.ProcessCounter, code);
				this.ProcessCounter++;
			}
			throw new Exception("Brak grupy o podanym PGID");
	}
	private String readCommandFile(String relativePathToFile){
		StringBuilder contentBuilder = new StringBuilder();
		try(Stream<String> stream = Files.lines(Paths.get(relativePathToFile), StandardCharsets.UTF_8)){
			stream.forEach(s -> contentBuilder.append(s));
		}catch(IOException e){
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}
	public PCB runNew() throws Exception {
		return newProcess("P"+this.ProcessCounter, this.ActivePCB.getPGID());
	}
	public PCB runNew(String FileName) throws Exception {
		return newProcess("P"+this.ProcessCounter, this.ActivePCB.getPGID(), FileName);
	}
	//Usuwanie procesu
	public void killProcess(int PID) throws Exception {
		if(PID == 0) throw new Exception("Nie mo�lna zabi� procesu bezczynno�ci");
		PCB temp = checkIfProcessExists(PID);
		if(temp != null) {
			ram.deleteProcessData(temp.getPID());
			checkIfGroupExists(temp.getPGID()).remove(temp);
			return;
		}
		throw new Exception("Brak procesu o podanym PID");
	}
	public ConditionVariable findConditionVariable(int PGID) throws Exception {
		for(ConditionVariable cv : ConditionVariables){
			if(cv.getPgid() == PGID){
				return cv;
			}
		}
		throw new Exception("Brak CV dla podanego PGID");
	}
	//Usuwanie grup
	public void killProcessGroup(int PGID) throws Exception {
			if(PGID == 0) throw new Exception("Brak dost�pu do grupy procesu bezczynno�ci");
			ArrayList<PCB> temp = checkIfGroupExists(PGID);
			if(temp != null) {
				deleteDateForProcessesInGroup(temp);
				ProcessGroups.remove(temp);
                ConditionVariables.remove(findConditionVariable(PGID));
			}else throw new Exception("Brak grupy o podanym PGID");
	}
	private void deleteDateForProcessesInGroup(ArrayList<PCB> listOfProcesses){
		for(PCB pcb : listOfProcesses){
			ram.deleteProcessData(pcb.getPID());
		}
	}
	//Tworzenie nowej grupy oraz pierwszego procesu
	public PCB newProcessGroup(String ProcessName) throws Exception {
		//PCB pcb = new PCB(this.ProcessCounter, ProcessName, this.GroupsCounter);
		//PCB pcb = newProcess(ProcessName, this.GroupsCounter);


		PCB pcb = new PCB(this.ProcessCounter, ProcessName, this.GroupsCounter);

		ArrayList<PCB> al = new ArrayList<PCB>();
		al.add(pcb);
		ProcessGroups.add(al);
		try{
			ConditionVariables.add(new ConditionVariable(this, this.GroupsCounter));
		}catch (Exception e){
			e.printStackTrace();
		}
		this.ProcessCounter++;
		this.GroupsCounter++;
		return pcb;
	}

	public PCB newProcessGroup(String ProcessName, String FileName) throws Exception {
		//PCB pcb = new PCB(this.ProcessCounter, ProcessName, this.GroupsCounter);
		//PCB pcb = newProcess(ProcessName, this.GroupsCounter, FileName);
		String textFileContent = readCommandFile("src/" + FileName + ".txt");
		Map mapLine = new HashMap<Integer, Integer>();
		System.out.println("Kod programu: "+ textFileContent);
		mapLine.put(0,0);
		for(int i = 0, j = 1; i != -1 && i+1 < textFileContent.length();j++){
			i = textFileContent.indexOf(";", i+1);
			if(i+1 < textFileContent.length()) {
				mapLine.put(j, i + 1);
				//System.out.println(j + " " + textFileContent.charAt(i + 1));
			}
		}
		//System.out.println("2");
		char[] code = textFileContent.toCharArray();
		PageTable pt1 = new PageTable(this.ProcessCounter, code.length);
		PCB pcb = new PCB(this.ProcessCounter, ProcessName, this.GroupsCounter, pt1, mapLine);
		ram.pageTables.put(this.ProcessCounter, pt1);
		ram.exchangeFile.writeToExchangeFile(this.ProcessCounter, code);


		ArrayList<PCB> al = new ArrayList<PCB>();
		al.add(pcb);
		ProcessGroups.add(al);
		ConditionVariables.add(new ConditionVariable(this, this.GroupsCounter));
		this.ProcessCounter++;
		this.GroupsCounter++;
		return pcb;
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

	public ConditionVariable getConditionVariable(int PID) throws Exception {
		PCB temp = checkIfProcessExists(PID);
		if(temp != null){
			for(ConditionVariable cv : this.ConditionVariables){
				if(cv.getPgid() == temp.getPGID()){
					return cv;
				}
			}
		}
		throw new Exception("Brak procesu o podanym PID");
	}

	public ArrayList<PCB> getReadyProcesses() {
		ArrayList<PCB> Processes = new ArrayList<PCB>();
		for (ArrayList<PCB> arr : this.ProcessGroups){
			for (PCB pcb : arr){
				if(pcb.getState() == PCB.State.READY){
					Processes.add(pcb);
				}
			}
		}
		return Processes;
	}

	public String getCommand(int pointer){
		int firstchar = this.ActivePCB.getMapLine().get(pointer);
		String command = "";
		char ch;
		do{
			ch = ram.getCommand(firstchar,ActivePCB.getPID(), ActivePCB.pageTable);
			if(ch == ';' || ch == '&'){
				break;
			}
			command += ch;
		}while(true);
		return command;
    }
}
