package de.itgain.threads.panels;

import de.itgain.threads.utils.SchedulerConfigReader;
import de.itgain.threads.utils.ThreadAnalyzer;
import de.itgain.threads.circles.Circle;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class CircleThreadPanel extends JPanel {

	private static final int RADIUS = 10;
	private static final int GAP = 2; // Abstand zwischen den Kreisen
	private final CopyOnWriteArrayList<Circle> circles = new CopyOnWriteArrayList<>();
	private final DefaultListModel<String> threadListModel = new DefaultListModel<>();
	private final JList<String> threadList = new JList<>(threadListModel);
	private volatile boolean running = false;
	private boolean performance = false;

	public CircleThreadPanel() {

		SchedulerConfigReader.printSchedulerConfig();

		setLayout(new BorderLayout());

		// Obere Leiste mit Checkboxen und Buttons
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel modusLabel = new JLabel("Representation of the threads: ");
		controlPanel.add(modusLabel);

		String[] optionenModus = {"Performance Mode (Many Threads)", "Scheduling Mode (Moving Circles)"};
		JComboBox<String> modusComboBox = new JComboBox<>(optionenModus);
		modusComboBox.setSelectedIndex(0); // Standardmäßig erste Option auswählen
		controlPanel.add(modusComboBox);

		JLabel functionLabel = new JLabel("What should the threads do?: ");
		controlPanel.add(functionLabel);

		// Erstelle die JComboBox mit den Optionen
		String[] optionen = {"Blocking Function Sleep", "High CPU Load", "High Memory Load"};
		JComboBox<String> functionComboBox = new JComboBox<>(optionen);
		functionComboBox.setSelectedIndex(0); // Standardmäßig erste Option auswählen
		controlPanel.add(functionComboBox);

		JCheckBox virtualThreads1 = new JCheckBox("Virtual Threads");
		virtualThreads1.setSelected(true);
		controlPanel.add(virtualThreads1);

		// Label und Textfeld für die Eingabe der Anzahl
		JLabel numberLabel = new JLabel("Amount of Threads: ");
		controlPanel.add(numberLabel);

		JTextField numberField = new JTextField(5); // Textfeld mit Platz für 5 Zeichen
		numberField.setText("20"); // Standardwert
		controlPanel.add(numberField);

		JButton startButton = new JButton("Start");
		controlPanel.add(startButton);

		JButton stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		controlPanel.add(stopButton);

		add(controlPanel, BorderLayout.NORTH);

		// Rechte Liste mit laufenden Threads
		JPanel threadPanel = new JPanel();
		threadPanel.setLayout(new BorderLayout());
		threadPanel.add(new JLabel("Running Threads:"), BorderLayout.NORTH);

		threadList.setFont(new Font("Monospaced", Font.PLAIN, 12));
		threadPanel.add(new JScrollPane(threadList), BorderLayout.CENTER);

		add(threadPanel, BorderLayout.EAST);

		// Untere Leiste mit Status Infos
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		statusPanel.add(SchedulerConfigReader.createSchedulerConfigLable());

		// UI-Komponenten für die CPU- und Speicherauslastung erstellen
		JProgressBar cpuProgressBar = new JProgressBar(0, 100); // CPU-Auslastung
		cpuProgressBar.setStringPainted(true);
		cpuProgressBar.setString("CPU: 0%");

		JProgressBar memoryProgressBar = new JProgressBar(0, 100); // Speicher-Auslastung
		memoryProgressBar.setStringPainted(true);
		memoryProgressBar.setString("Memory: 0%");

		// Füge die ProgressBars zur Statusleiste hinzu
		statusPanel.add(cpuProgressBar);
		statusPanel.add(memoryProgressBar);

		add(statusPanel, BorderLayout.SOUTH);

		// Hauptanzeigebereich für die Kreise
		JPanel drawingPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;

				// Alle Kreise zeichnen
				for (Circle circle : circles) {
					g2d.setColor(circle.color);
					g2d.fillOval(circle.x, circle.y, circle.radius * 2, circle.radius * 2);

					if (!performance){
						g2d.setColor(Color.BLACK); // Farbe des Textes
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

				}
			}
		};

		drawingPanel.setBackground(Color.WHITE);
		add(drawingPanel, BorderLayout.CENTER);

		// Start-Button-Aktion
		startButton.addActionListener(e -> {
			if (!running) {
				running = true;
				stopButton.setEnabled(true);
				startButton.setEnabled(false);

				int anzahl = numberField.getText().isEmpty() ? 10 : Integer.parseInt(numberField.getText());
				boolean virtualThreads = virtualThreads1.isSelected();

				String ausgewaehlteOption = (String) functionComboBox.getSelectedItem();
				boolean sleeper = false;
				boolean useCpu = false;
				boolean useMemory = false;
				switch (ausgewaehlteOption) {
				case "Blocking Function Sleep":
					sleeper = true;
					break;
				case "High CPU Load":
					useCpu = true;
					break;
				case "High Memory Load":
					useMemory = true;
					break;
				}

				switch ((String) modusComboBox.getSelectedItem()) {
				case "Performance Mode (Many Threads)":
					this.performance = true;
					this.createCirclesPerformance(anzahl, virtualThreads, sleeper, useCpu, useMemory, drawingPanel.getWidth(), drawingPanel.getHeight());
					break;
				case "Scheduling Mode (Moving Circles)":
					this.performance = false;
					this.createCircles(anzahl, virtualThreads, sleeper, useCpu, useMemory, drawingPanel.getWidth(), drawingPanel.getHeight());
					break;
				}

				ThreadAnalyzer.analysiereThreads();
			}
		});

		// Stop-Button-Aktion
		stopButton.addActionListener(e -> {
			running = false;
			stopButton.setEnabled(false);
			startButton.setEnabled(true);

			// Beende alle Threads sauber durch Zugriff auf die stop()-Methode der Kreise
			for (Circle circle : circles) {
				circle.stop(); // Threads durch Setzen von active = false stoppen
			}

			// Threads anhalten und Kreise entfernen
			circles.clear();
			threadListModel.clear();

			// Repaint für das Löschen der Kreise
			repaint();
		});

		// Repaint-Timer für die Kreise
		Timer timer = new Timer(16, e -> repaint());
		timer.start();

		// Aktualisieren der Thread-Liste
		Timer threadListTimer = new Timer(1000, e -> updateThreadList());
		threadListTimer.start();

		// Aktualisieren der Auslastung
		Timer performanceTimer = new Timer(1000, e -> printApplicationPerformance());
		performanceTimer.start();

		// Timer zur Aktualisierung der ProgressBars
		Timer progressBarUpdateTimer = new Timer(1000, e -> {
			updatePerformanceMetrics(cpuProgressBar, memoryProgressBar);
		});
		progressBarUpdateTimer.start();
	}

	private void createCircles(int anzahl, boolean virtualThreads, boolean sleeper, boolean useCpu, boolean useMemory, int dimX, int dimY){
		Random random = new Random();
		for (int i = 0; i < anzahl; i++) {
			Circle circle = new Circle(random.nextInt(300),   // Zufällige X-Position
									   random.nextInt(300),   // Zufällige Y-Position
									   50,                    // Radius
									   new Color(random.nextInt(255),
												 random.nextInt(255),
												 random.nextInt(255)),  // Zufällige Farbe
									   "Text " + (i + 1),     // Text
									   random.nextInt(2) + 1, // Zufällige Geschwindigkeit X
									   random.nextInt(2) + 1,  // Zufällige Geschwindigkeit Y
									   sleeper,
									   useCpu,
									   useMemory,
									   dimX,
									   dimY);
			circles.add(circle);

			Thread thread = null;
			if (virtualThreads){
				// Starte einen virtuellen Thread für jeden Kreis und setze den Namen
				thread = Thread.ofVirtual().name("VirtualThread-" + (i + 1)).start(circle);
			} else {
				thread = new Thread(circle);
				thread.start();
			}
			threadListModel.addElement(thread.getName());
		}
	}

	private void createCirclesPerformance(int anzahl, boolean virtualThreads, boolean sleeper, boolean useCpu, boolean useMemory, int dimX, int dimY){

		int panelWidth = dimX;
		int x = GAP, y = GAP;

		// Erzeuge die Kreise und die Threads
		for (int i = 0; i < anzahl; i++) { // Insgesamt 100 Kreise
			if (x + RADIUS * 2 > panelWidth) {
				x = GAP;
				y += RADIUS * 2 + GAP;
			}

			Circle circle = new Circle(x, y, RADIUS, sleeper, useCpu, useMemory);
			circles.add(circle);

			Thread thread = null;
			if (virtualThreads){
				// Starte einen virtuellen Thread für jeden Kreis und setze den Namen
				thread = Thread.ofVirtual().name("VirtualThread-" + (i + 1)).start(circle);
			} else {
				thread = new Thread(circle);
				thread.start();
			}
			threadListModel.addElement(thread.getName());

			x += RADIUS * 2 + GAP;
		}
	}

	private void updateThreadList() {
		threadListModel.clear();

		// Alle aktiven Threads sammeln und anzeigen
		Map<Thread, StackTraceElement[]> allThreads = Thread.getAllStackTraces();
		for (Thread thread : allThreads.keySet()) {
			if ((thread.getName().startsWith("Thread-")||thread.getName().startsWith("ForkJoinPool")) && thread.isAlive()) {
				threadListModel.addElement(thread.getName() + " - " + thread.getState());
			}
		}
	}

	public void printApplicationPerformance() {
		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
		// Prüfen, ob die erweiterte API verfügbar ist (für die CPU-Auslastung)
		if (osBean instanceof com.sun.management.OperatingSystemMXBean extendedOsBean) {
			double processCpuLoad = extendedOsBean.getProcessCpuLoad() * 100;
			System.out.printf("CPU-Auslastung: %.2f%%\n", processCpuLoad);
		} else {
			System.out.println("CPU-Auslastung konnte nicht ermittelt werden.");
		}

		// Speicherverbrauch ermitteln
		Runtime runtime = Runtime.getRuntime();
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		long maxMemory = runtime.maxMemory();

		System.out.printf("Speicherverbrauch: %d MB von maximal %d MB\n", usedMemory / (1024 * 1024), maxMemory / (1024 * 1024));
	}

	private void updatePerformanceMetrics(JProgressBar cpuProgressBar, JProgressBar memoryProgressBar) {
		// CPU-Auslastung abrufen
		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
		if (osBean instanceof com.sun.management.OperatingSystemMXBean extendedOsBean) {
			double processCpuLoad = extendedOsBean.getProcessCpuLoad() * 100; // CPU in Prozent
			cpuProgressBar.setValue((int) processCpuLoad);
			cpuProgressBar.setString(String.format("CPU: %.2f%%", processCpuLoad));
		} else {
			cpuProgressBar.setString("CPU: N/A");
		}

		// Speicherauslastung abrufen
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		int memoryUsage = (int) ((usedMemory * 100) / maxMemory); // Speicher in Prozent
		memoryProgressBar.setValue(memoryUsage);
		memoryProgressBar.setString(String.format("Memory: %d%%", memoryUsage));
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Circles and Threads");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920, 1080);
		frame.add(new CircleThreadPanel());
		frame.setVisible(true);
	}
}