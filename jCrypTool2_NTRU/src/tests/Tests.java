package tests;

import java.util.ArrayList;
import java.util.Random;

import polynomials.Polynomial;

/**
 * @author Ján Mikulec
 *
 */

// This script tests math behind NTRU by comparing it's results with results from SageMath with same inputs.
public class Tests {
	
	public Tests() {}
	
	// Return program termination status.
	//	0 - indicates Successful termination
	//	-1 - indicates unsuccessful termination with Exception
	//	1 - indicates Unsuccessful termination
	public int BeginTest() {
		System.out.println("Tests begins.\r\n");
		
		// ########################
		// ### tests parameters ###
		// ########################
		
		// N   p q   df  dg  dr I
		// 167 3 127 61  20  18	1
		// 251 3 127 50  24  16	2
		// 503 3 257 216 72  55	3

		int N = 167;
		int p = 3;
		int q = 127;
		int df = 61;
		int dg = 20;
		int dr = 18;
		// ########################
		// ### ################ ###
		// ########################

		int testsNum = 100;
		int successfulTests = 0;
		Test test = new Test();
		
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] ms = new ArrayList[testsNum];
		
		for(int i = 0; i < testsNum; i++) {
			int[] m = Tests.generateTestingMessageCoeff(10, 4);
			ArrayList<Integer> ma = new ArrayList<Integer>(m.length);
			
			for(int k = 0 ;k < m.length; k++) 
				ma.add(m[k]);
			
			ms[i] = ma;
		}
		
		int[][] res = new int[testsNum][testsNum];
		
		double start = System.nanoTime();
		
		try {			
			for(int i = 0; i < testsNum; i++) {
				res[i] = test.test(i, N, q, p, df, dg, dr, ms);
			}
		} catch(Exception e) {
			return -1;
		}	
		
		double finish = System.nanoTime();
		
		// Nanoseconds to actual time conversion
		int elapsedTime = (int)((finish - start) / (double)1000000000);
		int elapsedTimeSeconds = elapsedTime;
		int elapsedTimeMinutes = 0;
		
		if(elapsedTime > 60) {
			elapsedTimeSeconds = elapsedTime % 60;
			elapsedTimeMinutes = elapsedTime / 60;
		}
		
		System.out.println("Elapsed time: " + elapsedTimeMinutes + "m " + elapsedTimeSeconds + "s\r\n");
		System.out.println("Tests ends.\r\n\nSuccessful tests: " + successfulTests + "/" + (/*testsNum **/ 100) + ".\r\n");
		
		System.out.print("\r\n\r\n\r\n");
		int keyRes = 0;
		int messRes = 0;
		
		System.out.println("Rows (keys): ");
		for(int i = 0; i < res.length; i++) {
			for(int k = 0; k < res[i].length; k++) 
				keyRes += res[i][k];
			
			System.out.print(keyRes + " ");
			keyRes = 0;
		}
		
		System.out.println("\r\nColumns (messages): ");
		for(int i = 0; i < res.length; i++) {
			for(int k = 0; k < res[i].length; k++) 
				messRes += res[k][i];
			
			System.out.print(messRes + " ");
			messRes = 0;
		}
		
		System.out.println("\r\n");
		
		for(int l = 0; l < testsNum; l++) {
			for(int s = 0; s < testsNum ; s++)
				System.out.print(res[l][s] + " ");
			System.out.println();
		}
		
		return 0;
	}
	
	// Example:
	// Array: {-3, 0, -54, 45, 15, 0, 2, 3, 0, 1, 36, 0, -2, 1} <- 0s can be replaced with field...
	// Sage: -3*Q.0^12 - 54*Q.0^10 + 45*Q.0^9 + 15*Q.0^8 + 2*Q.0^6 + 3*Q.0^5 + Q.0^3 + 36*Q.0^2 -2*Q.0 + 1
	public static void coefficientsToSageFormat(Polynomial p) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < p.getCoefficients().length; i++) {
			if(p.getCoefficients()[i] != 0)
				if(i == 0)
					if(p.getCoefficients()[i] == 1)
						sb.append("Q.0^" + (p.getCoefficients().length - i - 1));
					else
						sb.append(p.getCoefficients()[i] + "*Q.0^" + (p.getCoefficients().length - i - 1));
				else if(i != p.getCoefficients().length - 1) {
					if(p.getCoefficients()[i] > 0)
						sb.append(" + ");
					else
						sb.append(" ");
					if(p.getCoefficients()[i] == 1)
						sb.append("Q.0^" + (p.getCoefficients().length - i - 1));
					else
						sb.append(p.getCoefficients()[i] + "*Q.0^" + (p.getCoefficients().length - i - 1));
				}
				else {
					if(p.getCoefficients()[i] > 0)
						sb.append(" + ");
					else
						sb.append(" ");
					sb.append(p.getCoefficients()[i]);
				}
			else
				continue;
		}
		
		System.out.println(sb.toString());
	}
	
	// Generated random array of <-1,1> coefficients of random len between minLen and maxLen
	public static int[] generateTestingMessageCoeff(int maxLen, int minLen) {
		Random random = new Random();
		int mLen = (int) ((Math.random() * (maxLen - minLen)) + minLen);
		int[] m = new int[mLen];
		
		for(int i = 0; i < m.length; i++) {
			m[i] = (int) (random.nextInt(2));
			
			if(random.nextBoolean())
				m[i] *= -1;
		}
		
		return m;
	}
}
