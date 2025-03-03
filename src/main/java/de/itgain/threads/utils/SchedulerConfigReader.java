package de.itgain.threads.utils;

import javax.swing.*;

public class SchedulerConfigReader {

	public static void printSchedulerConfig() {
		// Standardwerte, falls die Properties nicht gesetzt sind
		int defaultParallelism = Runtime.getRuntime().availableProcessors();
		int defaultMaxPoolSize = 256; // Laut JDK-Dokumentation
		int defaultMinRunnable = Math.max(1, defaultParallelism / 2);

		// Scheduler-Parameter auslesen
		String parallelismProp = System.getProperty("jdk.virtualThreadScheduler.parallelism");
		String maxPoolSizeProp = System.getProperty("jdk.virtualThreadScheduler.maxPoolSize");
		String minRunnableProp = System.getProperty("jdk.virtualThreadScheduler.minRunnable");

		// Konvertiere die Parameter in int, falls sie angegeben sind, oder verwende die Standardwerte
		int parallelism = parallelismProp != null ? Integer.parseInt(parallelismProp) : defaultParallelism;
		int maxPoolSize = maxPoolSizeProp != null ? Integer.parseInt(maxPoolSizeProp) : defaultMaxPoolSize;
		int minRunnable = minRunnableProp != null ? Integer.parseInt(minRunnableProp) : defaultMinRunnable;

		// Ausgabe in der Konsole
		System.out.println("Scheduler-Konfiguration:");
		System.out.println("Parallelism = " + parallelism + " (Default = " + defaultParallelism + ")");
		System.out.println("MaxPoolSize = " + maxPoolSize + " (Default = 256)");
		System.out.println("MinRunnable = " + minRunnable + " (Default = " + defaultMinRunnable + ")");
	}

	public static JLabel createSchedulerConfigLable(){

		// Standardwerte, falls die Properties nicht gesetzt sind
		int defaultParallelism = Runtime.getRuntime().availableProcessors();
		int defaultMaxPoolSize = 256; // Laut JDK-Dokumentation
		int defaultMinRunnable = Math.max(1, defaultParallelism / 2);

		// Scheduler-Parameter auslesen
		String parallelismProp = System.getProperty("jdk.virtualThreadScheduler.parallelism");
		String maxPoolSizeProp = System.getProperty("jdk.virtualThreadScheduler.maxPoolSize");
		String minRunnableProp = System.getProperty("jdk.virtualThreadScheduler.minRunnable");

		// Konvertiere die Parameter in int, falls sie angegeben sind, oder verwende die Standardwerte
		int parallelism = parallelismProp != null ? Integer.parseInt(parallelismProp) : defaultParallelism;
		int maxPoolSize = maxPoolSizeProp != null ? Integer.parseInt(maxPoolSizeProp) : defaultMaxPoolSize;
		int minRunnable = minRunnableProp != null ? Integer.parseInt(minRunnableProp) : defaultMinRunnable;

		// Ausgabe in der Konsole
		StringBuilder sb = new StringBuilder();
		sb.append("Scheduler-Konfiguration:");
		sb.append(" Parallelism = " + parallelism + " (Default = " + defaultParallelism + ")");
		sb.append(" MaxPoolSize = " + maxPoolSize + " (Default = 256)");
		sb.append(" MinRunnable = " + minRunnable + " (Default = " + defaultMinRunnable + ")");

		JLabel configLabel = new JLabel(sb.toString());
		return configLabel;
	}

	public static void main(String[] args) {
		printSchedulerConfig();
	}
}