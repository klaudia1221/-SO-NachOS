package com.BamoOS.Modules.ConditionVariable;

import com.BamoOS.Modules.ProcessManager.PCB;

import java.util.LinkedList;

public interface IConditionVariable {
    void await(boolean forceLock); // zmienia stan aktualnie aktywnego procesu na WAITING i wrzuca do kolejki
    void signal(); // zmienia stan pierwszego procesu w kolejce na READY i usuwa z niej
    void signalAll(); // j.w. dla wszystkich w kolejce
    void printInfo(); // wypisywanie informacji dla uzytkownika - do sprawdzania
    boolean getBusy(); // getter tajemniczej zmiennej boolowskiej
    LinkedList<PCB> getWaiting(); // getter listy oczekujących procesów
    int getPgid();
    void setPgid(int pgid);
}