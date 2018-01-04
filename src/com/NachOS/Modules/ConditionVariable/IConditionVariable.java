package com.NachOS.Modules.ConditionVariable;

import com.NachOS.Modules.ProcessManager.PCB;

import java.util.LinkedList;

public interface IConditionVariable {
    void await(boolean forceLock); // zmienia stan aktualnie aktywnego procesu na WAITING i wrzuca do kolejki
    void signal(); // zmienia stan pierwszego procesu w kolejce na READY i usuwa z niej
    void signalAll(); // wywołuje signal() dla wszystkich czekających
    void printInfo(); // wypisywanie informacji dla uzytkownika - do sprawdzania
    boolean getBusy(); // getter tajemniczej zmiennej boolowskiej
    LinkedList<PCB> getWaiting(); // getter listy oczekujących procesów
    int getPgid();
    void setPgid(int pgid);
}