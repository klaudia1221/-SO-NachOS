package com.BamoOS.Modules.Processor;

import com.BamoOS.Modules.Interpreter.Interpreter;

public interface IProcessor {
	void Scheduler();
	void exe() throws Exception ;
	void printProcesses();
	void setInterpreter(Interpreter interpreter);
}
