package de.itgain.threads.circles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class MovingCircles extends JPanel implements ActionListener {
	private final Timer timer = new Timer(20, this);
	private final ArrayList<Circle> circles = new ArrayList<>();
	private final Random random = new Random();

	public MovingCircles() {
		// Beispiel: 5 zufällige Kreise erstellen
		for (int i = 0; i < 5; i++) {
			circles.add(new Circle(
					random.nextInt(300),  // zufällige Startposition X
					random.nextInt(300),  // zufällige Startposition Y
					50,                   // Radius
					new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)),  // Zufällige Farbe
					"Text " + (i + 1),    // Beschriftung
					random.nextInt(5) + 1,  // Zufällige Geschwindigkeit X
					random.nextInt(5) + 1   // Zufällige Geschwindigkeit Y
			));
		}
		timer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Alle Kreise zeichnen
		for (Circle circle : circles) {
			g2d.setColor(circle.color);
			g2d.fillOval(circle.x, circle.y, circle.radius * 2, circle.radius * 2);

			g2d.setColor(Color.BLACK);  // Textfarbe
			String text = circle.text;
			FontMetrics fm = g2d.getFontMetrics();
			int textWidth = fm.stringWidth(text);
			int textHeight = fm.getHeight();

			g2d.drawString(
					text,
					circle.x + circle.radius - textWidth / 2,
					circle.y + circle.radius + textHeight / 4
			);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Bewegung der Kreise aktualisieren
		for (Circle circle : circles) {
			circle.x += circle.vx;
			circle.y += circle.vy;

			// Grenzenprüfung (damit die Kreise im Fenster bleiben)
			if (circle.x < 0 || circle.x + circle.radius * 2 > getWidth()) {
				circle.vx = -circle.vx;
			}
			if (circle.y < 0 || circle.y + circle.radius * 2 > getHeight()) {
				circle.vy = -circle.vy;
			}
		}
		repaint();  // Neuzeichnen des Bereichs
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Bewegende Kreise mit Text");
		MovingCircles movingCircles = new MovingCircles();
		frame.add(movingCircles);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	// Interne Klasse für einen Kreis
	static class Circle {
		int x, y;        // Position
		int radius;      // Radius des Kreises
		Color color;     // Farbe des Kreises
		String text;     // Text innerhalb des Kreises
		int vx, vy;      // Geschwindigkeit in X und Y Richtung

		Circle(int x, int y, int radius, Color color, String text, int vx, int vy) {
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.color = color;
			this.text = text;
			this.vx = vx;
			this.vy = vy;
		}
	}
}