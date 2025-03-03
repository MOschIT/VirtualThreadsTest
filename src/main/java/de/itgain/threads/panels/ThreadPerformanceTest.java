package de.itgain.threads.panels;

import de.itgain.threads.utils.SchedulerConfigReader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

public class ThreadPerformanceTest extends JPanel {
	private final ArrayList<Circle> circles = new ArrayList<>();
	private static final int RADIUS = 9;
	private static final int GAP = 2; // Abstand zwischen den Kreisen
	private static final int NUM_COLUMNS = 20; // Kreise pro Zeile

	public ThreadPerformanceTest() {

		SchedulerConfigReader.printSchedulerConfig();

		// Total Speicher vor Thread-Erstellung
		Runtime runtime = Runtime.getRuntime();
		long initialMemory = runtime.totalMemory() - runtime.freeMemory();

		long startTime = System.nanoTime();
		int threadcount = 1000;
		createThreads(threadcount,true, false, false);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1_000_000;
		System.out.println(threadcount+" Threads erstellt und gestartet in: " + duration + " ms");

		// Total Speicher nach Thread-Erstellung
		long finalMemory = runtime.totalMemory() - runtime.freeMemory();
		long usedMemory = finalMemory - initialMemory;
		System.out.println("Speicherbedarf der Threads im Heap: " + (usedMemory/1024) + "kb / "+threadcount+" = "+ (usedMemory/1024/threadcount)+"kb");

		// Repaint-Timer, um die Kreise regelmäßig zu aktualisieren
		Timer repaintTimer = new Timer(16, e -> repaint());
		repaintTimer.start();
	}

	private void createThreads(int threadcount, boolean virtual, boolean sleeper, boolean memoryTest){
		String threads = virtual ? "virtuelle" : "normale";
		System.out.println("Starte "+ threadcount+" "+threads+" Threads Sleeper: " + sleeper + " MemoryTest: " + memoryTest);

		int panelWidth = 800; // Fenstergröße
		int x = GAP, y = GAP;

		// Erzeuge die Kreise und die virtuellen Threads
		for (int i = 0; i < threadcount; i++) { // Insgesamt 100 Kreise
			if (x + RADIUS * 2 > panelWidth) {
				x = GAP;
				y += RADIUS * 2 + GAP;
			}

			Circle circle = new Circle(x, y, RADIUS, sleeper, memoryTest);
			circles.add(circle);

			if (virtual){
				Thread.ofVirtual().start(circle); // Virtueller Thread für jeden Kreis starten
			} else {
				new Thread(circle).start();
			}

			x += RADIUS * 2 + GAP;

		}
	}

	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		long redCount = 0;
		long greenCount = 0;

		for (Circle circle : circles) {
			// Zeichne den Kreis
			g2d.setColor(circle.color);
			if (circle.color == Color.GREEN) {greenCount++;} else {redCount++;}
			g2d.fillOval(circle.x, circle.y, circle.radius * 2, circle.radius * 2);
		}

		// Zeichne oben links Informationen über die virtuellen Threads
		g2d.setColor(Color.BLACK);
        g2d.drawString("Rote Kreise: " + redCount, getWidth() - 150, getHeight() - 40);
        g2d.drawString("Grüne Kreise: " + greenCount, getWidth() - 150, getHeight() - 20);
		g2d.setFont(new Font("Arial", Font.PLAIN, 12));

		Map<Thread, StackTraceElement[]> allThreads = Thread.getAllStackTraces();

		int y = 20; // Startposition für die Informationen
		g2d.drawString("Thread-Informationen:", 5, y);
		for (Thread thread : allThreads.keySet()) {

			if (thread.getName().contains("ForkJoinPool") || thread.getName().startsWith("Thread-")){
				y += 15;
				g2d.drawString("Thread : " + thread.getName() + " State : " + thread.getState(), 5, y);
			}

		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Virtual Threads Performance Test");
		ThreadPerformanceTest panel = new ThreadPerformanceTest();
		frame.add(panel);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	// Klasse für die Kreise
	class Circle implements Runnable {
		final int x, y;
		final int radius;
		volatile Color color; // Farbe des Kreises (volatile für Thread-Sicherheit)

		boolean sleeper;
		boolean memoryTest;

		Circle(int x, int y, int radius, boolean sleeper, boolean memoryTest) {
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.color = Color.RED; // Startfarbe ist rot
			this.sleeper = sleeper;
			this.memoryTest = memoryTest;
		}

		@Override
		public void run() {

			while (true) {
				color = Color.GREEN;

				if (sleeper) {
					schlafeEtwas();
				} else {
					if (memoryTest) {
						verbraucheSpeicher();
					} else {
						arbeiteEtwas();
					}
				}

				color = Color.RED;

				LockSupport.parkNanos(1_000_000_000L); // 1 Sekunde schlafen
			}

		}

		private void arbeiteEtwas() {
			long end = System.nanoTime() + 50_000_000L; // 50ms CPU-Zeit verbrauchen
			while (System.nanoTime() < end) {
				Math.sin(Math.random()); // Beliebige Arbeit
			}
		}

		private void verbraucheSpeicher() {
			long end = System.nanoTime() + 50_000_000L; // 50ms CPU-Zeit verbrauchen

			int size = 1_000_000; // Größe des Arrays (z. B. für ca. 50 MB)
			int[] largeArray = new int[size];
			int i = 1;

			while (System.nanoTime() < end && i < size) {
				largeArray[i] = i * 2;
				i++;
			}
		}

		private void schlafeEtwas(){
			LockSupport.parkNanos(50_000_000L);
		}
	}
}