package de.itgain.threads;

import de.itgain.threads.panels.CircleThreadPanel;

import javax.swing.*;

public class VirtualThreadsTest {

	public static void main(String[] args) {

		JFrame frame = new JFrame("Circles and Threads");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920, 1080);
		frame.add(new CircleThreadPanel());
		frame.setVisible(true);

	}
}
