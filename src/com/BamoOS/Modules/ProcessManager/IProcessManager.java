package com.BamoOS.Modules.ProcessManager;

import java.util.ArrayList;

public interface IProcessManager {
    void newProcess(String ProcessName, int PGID);
    void newProcess(String ProcessName, int PGID, String FileName);
    void runNew();
    void runNew(String FileName);
    void killProcess(int PID);
    void killProcessGroup(int PGID);
    void newProcessGroup(String ProcessName);
    ArrayList<PCB> checkIfGroupExists(int PGID);
    PCB checkIfProcessExists(int PID);
    PCB getPCB(int PID);
    ArrayList<ArrayList<PCB>> getProcessList();
    void PrintProcesses();
    PCB getActivePCB();
    void setActivePCB(PCB activePCB);
}
