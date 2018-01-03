package com.BamoOS.Modules.ConditionVariable;

import java.util.LinkedList;
import com.BamoOS.Modules.ProcessManager.PCB;

public interface IConditionVariable {
    void await(); // zmienia stan aktualnie aktywnego procesu na WAITING i wrzuca do kolejki
    void signal(); // zmienia stan pierwszego procesu w kolejce na READY i usuwa z niej
    void signalAll(); // j.w. dla wszystkich w kolejce
    boolean getBusy(); // getter tajemniczej zmiennej boolowskiej
    LinkedList<PCB> getWaiting(); // getter listy oczekujących procesów
    void printInfo(); // wypisywanie informacji dla uzytkownika - do sprawdzania
}