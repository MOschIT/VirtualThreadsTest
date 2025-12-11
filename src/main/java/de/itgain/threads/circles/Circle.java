package de.itgain.threads.circles;

import de.itgain.threads.utils.CPULastMethode;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.locks.LockSupport;

// Klasse für bewegende Kreise
public class Circle implements Runnable {

	public int x, y;        // Position
	public final int radius; // Radius
	public Color color; // Farbe
	public String text; // Text im Kreis
	public String subtext; // Text im Kreis
	public boolean sleeper;
	public boolean mover;
	public boolean useCpu;
	public boolean useMemory;
	public int vx, vy;      // Geschwindigkeit
	public int dimX, dimY;
	private final Random random = new Random();

	volatile boolean active = true; // Für die Steuerung des Threads bei Stop

	/**
	 * Konstruktor für Scheduler Tests
	 *
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 * @param text
	 * @param vx
	 * @param vy
	 * @param sleeper
	 * @param useCpu
	 * @param useMemory
	 * @param dimX
	 * @param dimY
	 */
	public Circle(int x,
				  int y,
				  int radius,
				  Color color,
				  String text,
				  int vx,
				  int vy,
				  boolean sleeper,
				  boolean useCpu,
				  boolean useMemory,
				  int dimX,
				  int dimY) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
		this.text = Thread.currentThread().getName();
		this.vx = vx;
		this.vy = vy;
		this.subtext = "0";
		this.dimX = dimX;
		this.dimY = dimY;
		this.sleeper = sleeper;
		this.useCpu = useCpu;
		this.useMemory = useMemory;
		this.mover = true;
	}

	/**
	 * Konstruktor für Performance Tests
	 *
	 * @param x
	 * @param y
	 * @param radius
	 * @param sleeper
	 * @param useCpu
	 * @param useMemory
	 */
	public Circle(int x, int y, int radius, boolean sleeper, boolean useCpu, boolean useMemory) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = Color.RED; // Startfarbe ist rot
		this.sleeper = sleeper;
		this.useCpu = useCpu;
		this.useMemory = useMemory;
		this.mover = false;
	}

	@Override
	public void run() {
		this.text = Thread.currentThread().getName();

		while (active) {
			if (mover){

				move();

				if (sleeper){
					schlafeEtwas(16_000_000L);
				}
				if (useCpu){
					arbeiteEtwas(16_000_000L);
				}
				if (useMemory){
					verbraucheSpeicher(16_000_000L);
				}

			} else {

				this.color = Color.GREEN;

				if (sleeper){
					schlafeEtwas(50_000_000L);
				}
				if (useCpu){
					arbeiteEtwas(50_000_000L);
				}
				if (useMemory){
					verbraucheSpeicher(50_000_000L);
				}

				this.color = Color.RED;

				LockSupport.parkNanos(1_000_000_000L); // 1 Sekunde schlafen
			}
		}
	}

	public void stop() {
		active = false; // Stop-Anweisung
	}

	private synchronized void move() {
		x += vx;
		y += vy;

		// Grenzen prüfen und Richtung umkehren
		if (x < 0 || x + radius * 2 > dimX) { // Breite auf 800 beschränkt
			vx = -vx;
			x = Math.max(0, Math.min(x, dimX - radius * 2)); // Begrenzung innerhalb des Fensters
		}
		if (y < 0 || y + radius * 2 > dimY) { // Höhe auf 600 beschränkt
			vy = -vy;
			y = Math.max(0, Math.min(y, dimY - radius * 2));
		}
	}

	private void arbeiteEtwas(long duration) {
		double iterations = (double) CPULastMethode.verbraucheCPUZeit(duration) /1000.0;
		String usedInterations = String.format("%.1fk", iterations);
		this.subtext = usedInterations;
	}

	private void verbraucheSpeicher(long duration) {
		long end = System.nanoTime() + duration; // 16ms Speicher verbrauchen

		int size = 10_000_000; // Größe des Arrays (z. B. für ca. 40 MB)
		int[] largeArray = new int[size];
		int i = 1;

		while (System.nanoTime() < end && i < size) {
			largeArray[i] = i++;
		}
		String usedInterations = String.format("%.1fk", i/1000.00);
		this.subtext = usedInterations;
	}

	private void schlafeEtwas(long duration){
		this.subtext = "sleep";
		LockSupport.parkNanos(duration);
	}
}