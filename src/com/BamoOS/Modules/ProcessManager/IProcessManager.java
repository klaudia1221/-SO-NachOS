package com.BamoOS.Modules.ProcessManager;

import java.util.ArrayList;

public interface IProcessManager {
    PCB newProcess(String ProcessName, int PGID) throws Exception;
    PCB newProcess(String ProcessName, int PGID, String FileName) throws Exception;
    PCB runNew() throws Exception;
    PCB runNew(String FileName) throws Exception;
    void killProcess(int PID) throws Exception;
    void killProcessGroup(int PGID);
    PCB newProcessGroup(String ProcessName) throws Exception;
    ArrayList<PCB> checkIfGroupExists(int PGID);
    PCB checkIfProcessExists(int PID);
    PCB getPCB(int PID);
    ArrayList<ArrayList<PCB>> getProcessList();
    void PrintProcesses();
    PCB getActivePCB();
    void setActivePCB(PCB activePCB);
    ArrayList<PCB> getReadyProcesses();
}
