package polynomials;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PolynomialOperationsNTRU {
	//Generate single polynomial
	// For now  are koeficients set to be between -1 & 1 <=> abs(1)
	public static Polynomial generateRandomPolynomial(int N, int d, int field) throws InterruptedException {
		int boundary = 1;
		List<Integer> takenPositions = new ArrayList<Integer>();
		int[] coefficients = new int[N];
		
		// generate positives
		for(int i = 0; i < d; i++) 
			generateRecursion(N, takenPositions, coefficients, boundary, true);
		
		// generate negatives
		for(int i = 0; i < d - 1; i++) 
			generateRecursion(N, takenPositions, coefficients,  boundary, false);
		
		return new Polynomial(coefficients, field);
	}
	
	// Generate single polynomial and return list of this polynomial in two fields
	// Koeficients are set to be between -1 & 1 <=> abs(1)
	public static ArrayList<Polynomial> generateRandomPolynomialOfTwoDifferentFields(int N, int d, int field1, int field2) throws InterruptedException {
		int boundary = 1;
		List<Integer> takenPositions = new ArrayList<Integer>();
		ArrayList<Polynomial> ret = new ArrayList<Polynomial>();
		int[] coefficients = new int[N];
		
		// generate positives
		for(int i = 0; i < d; i++) 
			generateRecursion(N, takenPositions, coefficients, boundary, true);
		
		// generate negatives
		for(int i = 0; i < d - 1; i++) 
			generateRecursion(N, takenPositions, coefficients,  boundary, false);
		
		ret.add(new Polynomial(coefficients, field1));
		ret.add(new Polynomial(coefficients, field2));
		return ret;
	}
	
	private static void generateRecursion(int N, List<Integer> takenPositions, int[] coefficients, int boundary, boolean positive){
		int position = ThreadLocalRandom.current().nextInt(0, N);
		if(takenPositions.contains(position)) 
			generateRecursion(N, takenPositions, coefficients, boundary, positive);
		else {
			takenPositions.add(position);
			if(positive)
				coefficients[position] = ThreadLocalRandom.current().nextInt(1, boundary + 1);
			else
				coefficients[position] = -ThreadLocalRandom.current().nextInt(1, boundary + 1);
		}
	}
	
	// Return polynomial in form: x^N - x^0
	public static Polynomial createXN1(int N, int field) throws IllegalArgumentException {
		if(N < 1)
			throw new IllegalArgumentException("In PolynomialOperationsNTRU: createXN1(int N, int field): N has to be positive, not zero integer.");
		
		int[] xn1Coef = new int[N + 1];
		xn1Coef[0] = 1;
		xn1Coef[N] = -1;
		
		Polynomial xn1 = new Polynomial(xn1Coef, field);
		
		return xn1;
	}
}