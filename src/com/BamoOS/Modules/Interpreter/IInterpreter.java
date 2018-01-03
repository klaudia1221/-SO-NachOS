package com.BamoOS.Modules.Interpreter;

public interface IInterpreter {
    void set_A();
    void set_B();
    void set_C();
    void set_PC();
    void set_PID();
    int get_A();
    int get_B();
    int get_C();
    int get_PC();
    int get_PID();
    void RegisterStatus();
    void Exe();
}
