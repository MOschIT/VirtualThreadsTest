package de.itgain.threads.utils;

import java.util.Map;

public class ThreadAnalyzer {


	public static void analysiereThreads() {
		// Zugriff auf alle aktiven Threads und ihre Stack-Traces
		Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();

		System.out.println("Aktive Threads:");
		for (Thread thread : threads.keySet()) {
			// Informationen zu jedem Thread ausgeben
			System.out.printf("Thread Name: %s%n", thread.getName());
			System.out.printf("Thread Typ: %s%n",
							  thread.isVirtual() ? "Virtueller Thread" : "Platform Thread");
			System.out.printf("Thread Status: %s%n", thread.getState());
			System.out.println("------------------------"+getActiveVirtualThreads());
		}
	}

	public static long getActiveVirtualThreads() {
		return Thread.getAllStackTraces().keySet().stream()
					 .filter(Thread::isVirtual)
					 .count();
	}

}
