package com.NachOS.Modules.Processor;

import com.NachOS.Modules.Interpreter.Interpreter;

public interface IProcessor {
	void Scheduler();
	void exe() throws Exception ;
	void printProcesses();
	void setInterpreter(Interpreter interpreter);
}
