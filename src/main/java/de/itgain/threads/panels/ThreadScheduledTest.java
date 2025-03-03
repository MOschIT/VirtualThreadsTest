package de.itgain.threads.panels;

import de.itgain.threads.utils.SchedulerConfigReader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadScheduledTest extends JPanel {
	private final List<Circle> circles = new ArrayList<>();
	private static final int RADIUS = 14;
	private static final int GAP = 1; // Abstand zwischen den Kreisen
	private static final int NUM_COLUMNS = 20; // Kreise pro Zeile
	private ScheduledExecutorService scheduler;

	public ThreadScheduledTest(boolean virtual) {

		SchedulerConfigReader.printSchedulerConfig();

		if (virtual) {
			scheduler = new ScheduledThreadPoolExecutor(100, Thread.ofVirtual().name("Circle").factory());
		} else {
			scheduler = Executors.newScheduledThreadPool(100);
		}

		int threadCount = 500;

		long startTime = System.nanoTime();
		createScheduledTasks(threadCount);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1_000_000;
		System.out.println(threadCount + " Scheduled Tasks erstellt und gestartet in: " + duration + " ms");

		// Repaint-Timer, um die Kreise regelmäßig zu aktualisieren
		Timer repaintTimer = new Timer(16, e -> repaint());
		repaintTimer.start();
	}

	private void createScheduledTasks(int threadCount) {
		int panelWidth = 800; // Fenstergröße
		int x = GAP, y = GAP;

		for (int i = 0; i < threadCount; i++) {
			if (x + RADIUS * 2 > panelWidth) {
				x = GAP;
				y += RADIUS * 2 + GAP;
			}

			Circle circle = new Circle(i, x, y, RADIUS);
			circles.add(circle);

			// Plane regelmäßige Aufgaben mit ScheduledThreadPoolExecutor
			scheduler.scheduleAtFixedRate(circle::arbeiteEtwas, 0, 2, TimeUnit.SECONDS);

			x += RADIUS * 2 + GAP;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		for (Circle circle : circles) {
			// Zeichne den Kreis
			g2d.setColor(circle.color);
			g2d.fillOval(circle.x, circle.y, circle.radius * 2, circle.radius * 2);

			// Zeichne die ID des Kreises in der Mitte des Kreises
			g2d.setColor(Color.BLACK);
			String text = String.valueOf(circle.id);
			FontMetrics fm = g2d.getFontMetrics();
			int textWidth = fm.stringWidth(text); // Breite des Textes
			int textHeight = fm.getAscent(); // Höhe des Textes (ascent = Oberkante)

			int textX = circle.x + circle.radius - textWidth / 2;
			int textY = circle.y + circle.radius + textHeight / 4;

			g2d.drawString(text, textX, textY);
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Scheduled Thread Pool Test");
		ThreadScheduledTest panel = new ThreadScheduledTest(false);
		frame.add(panel);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	static class Circle {

		final int id;
		final int x, y;
		final int radius;
		volatile Color color; // Farbe des Kreises (volatile für Thread-Sicherheit)
		private long lastExecutionTime; // Zeitpunkt der letzten Ausführung (in Nanosekunden)

		Circle(int id, int x, int y, int radius) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.color = Color.RED; // Startfarbe ist rot
			this.lastExecutionTime = System.nanoTime(); // Initial auf die aktuelle Zeit
		}

		public void arbeiteEtwas() {
			try {
				// Berechne Zeit in Sekunden seit der letzten Ausführung
				long currentExecutionTime = System.nanoTime();
				long elapsedTimeInSeconds = (currentExecutionTime - lastExecutionTime) / 1_000_000_000L;
				lastExecutionTime = currentExecutionTime; // Aktualisiere die letzte Ausführungszeit

				// Schreibe die Zeitspanne in die Konsole
				System.out.println("Zeit seit der letzten Ausführung: " + elapsedTimeInSeconds + " Sekunden");

				// "Arbeiten" (grün werden)
				color = Color.GREEN;
				long end = System.nanoTime() + 1_000_000_000L; // 1 Sekunde (in Nanosekunden)
				while (System.nanoTime() < end) {
					Math.sin(Math.random()); // Beliebige Arbeit
				}

				// Nach der Arbeit wieder "Ruhezustand" (rot werden)
				color = Color.RED;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}