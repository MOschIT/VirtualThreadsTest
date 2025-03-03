package de.itgain.threads.utils;

public class CPULastMethode {
	public static long verbraucheCPUZeit(long duration) {
		long startZeit = System.nanoTime();
		long zielZeit = startZeit + duration; // 16 Millionen Nanosekunden = 16ms

		long iterationen = 0;

		// Komplexe Berechnungen durchführen
		double ergebnis = 0;
		while (System.nanoTime() < zielZeit) {
			// Mathematische Operationen durchführen
			for (int i = 0; i < 100; i++) {
				ergebnis += Math.sin(i) * Math.cos(i);
				ergebnis *= Math.sqrt(Math.abs(ergebnis));
				ergebnis = Math.pow(ergebnis, 0.1);
			}
			iterationen++;
		}
		return iterationen;
	}

	// Alternative Implementierung mit Matrix-Operationen
	public static void verbraucheCPUZeitMitMatrix() {
		long startZeit = System.nanoTime();
		long zielZeit = startZeit + 16_000_000;

		int matrixGroesse = 50;
		double[][] matrix1 = new double[matrixGroesse][matrixGroesse];
		double[][] matrix2 = new double[matrixGroesse][matrixGroesse];
		double[][] ergebnis = new double[matrixGroesse][matrixGroesse];

		// Matrizen mit Zufallswerten füllen
		for (int i = 0; i < matrixGroesse; i++) {
			for (int j = 0; j < matrixGroesse; j++) {
				matrix1[i][j] = Math.random();
				matrix2[i][j] = Math.random();
			}
		}

		// Matrix-Multiplikation durchführen bis Zielzeit erreicht
		while (System.nanoTime() < zielZeit) {
			for (int i = 0; i < matrixGroesse; i++) {
				for (int j = 0; j < matrixGroesse; j++) {
					ergebnis[i][j] = 0;
					for (int k = 0; k < matrixGroesse; k++) {
						ergebnis[i][j] += matrix1[i][k] * matrix2[k][j];
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		// Test der Methoden
		System.out.println("Starte CPU-Last-Test...");

		long start = System.nanoTime();
		verbraucheCPUZeit(16_000_000);
		long ende = System.nanoTime();

		System.out.printf("Tatsächliche Dauer: %.2f ms%n",
						  (ende - start) / 1_000_000.0);

		start = System.nanoTime();
		verbraucheCPUZeitMitMatrix();
		ende = System.nanoTime();

		System.out.printf("Tatsächliche Dauer (Matrix): %.2f ms%n",
						  (ende - start) / 1_000_000.0);
	}
}