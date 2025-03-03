package de.itgain.threads.panels;

import de.itgain.threads.circles.Circle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class CircleThreads extends JPanel {
	private final ArrayList<Circle> circles = new ArrayList<>();
	private final Random random = new Random();

	public CircleThreads() {
		// Erzeuge 5 zufällige Kreise
		for (int i = 0; i < 10; i++) {
			Circle circle = new Circle(random.nextInt(300),   // Zufällige X-Position
									   random.nextInt(300),   // Zufällige Y-Position
									   50,                    // Radius
									   new Color(random.nextInt(255),
												 random.nextInt(255),
												 random.nextInt(255)),  // Zufällige Farbe
									   "Text " + (i + 1),     // Text
									   random.nextInt(2) + 1, // Zufällige Geschwindigkeit X
									   random.nextInt(2) + 1,  // Zufällige Geschwindigkeit Y
									   false, true, false, this.getWidth(), this.getHeight());
			circles.add(circle);
			new Thread(circle).start(); // Jeder Circle läuft in einem eigenen Thread
		}

		// Repaint-Thread für das Panel
		Timer repaintTimer = new Timer(16, e -> repaint());
		repaintTimer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Zeichne alle Kreise
		for (Circle circle : circles) {
			g2d.setColor(circle.color);
			g2d.fillOval(circle.x, circle.y, circle.radius * 2, circle.radius * 2);

			g2d.setColor(Color.BLACK);  // Farbe des Textes
			String text = circle.text;
			FontMetrics fm = g2d.getFontMetrics();
			int textWidth = fm.stringWidth(text);
			int textHeight = fm.getHeight();

			g2d.drawString(
					text,
					circle.x + circle.radius - textWidth / 2,
					circle.y + circle.radius + textHeight / 4
			);

			// Subtext unterhalb des Haupttextes zeichnen
			Font originalFont = g2d.getFont(); // Aktuelle Schriftart speichern
			Font boldLargeFont = originalFont.deriveFont(Font.BOLD, originalFont.getSize() + 4); // Erstelle größere und fette Schriftart
			g2d.setFont(boldLargeFont); // Setze die angepasste Schriftart

			int subtextWidth = g2d.getFontMetrics().stringWidth(circle.subtext);
			int subtextVerticalOffset = textHeight + 5; // 5 Pixel als Abstand zwischen Text und Subtext

			g2d.drawString(
					circle.subtext,
					circle.x + circle.radius - subtextWidth / 2,
					circle.y + circle.radius + textHeight / 4 + subtextVerticalOffset
			);

			g2d.setFont(originalFont); // Ursprüngliche Schriftart wiederherstellen
		}

		// Zeichne oben links Informationen über die Threads
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Arial", Font.PLAIN, 12));

		Map<Thread, StackTraceElement[]> allThreads = Thread.getAllStackTraces();

		int y = 20; // Startposition für die Informationen
		g2d.drawString("Thread-Informationen:", 5, y);
		for (Thread thread : allThreads.keySet()) {

			if (thread.getName().contains("Thread-")){
				y += 15;
				g2d.drawString("Thread : " + thread.getName() + " State : " + thread.getState(), 5, y);
			}

		}

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Kreise mit Threads");
		CircleThreads panel = new CircleThreads();
		frame.add(panel);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}