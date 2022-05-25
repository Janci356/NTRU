package ntru;

import java.lang.Math;
import java.math.BigDecimal;
import org.apache.commons.math3.special.Gamma;

public class ParameterOptimizer {
	public static void optimizeParameters(NTRU ntru, int k) throws Exception {
		if(k <= 0)
			throw new IllegalArgumentException("In ParameterOptimizer: getN(int k): Integer k has to be non-zero positive integer.");
		
		if(ntru.getN() == 0) 
			throw new NullPointerException("In ParameterOptimizer: getN(int k): N has to be initilized and not-zero value.");
		
		// 1.
		ntru.setN(findNextPrime(k));

		// 2.
		int d = calculate_d(ntru.getN(), k);
		ntru.setDf(d);
		ntru.setDr(d);
		ntru.setDg(ntru.getN() / 2);
		
		// 3.
		@SuppressWarnings("unused")
		int dm = calculate_dm(ntru.getN(), k);
		
		// 4.
		ntru.setQ(findNextPrime((4 * d) + 1));

	}
	
	// Output is prime number bigger then (k * 3) + 8.
	private static int findNextPrime(int k) {
		k = (3 * k) + 8;
		k++;
		
	    for (int i = 2; i < k; i++) {
	    	if(k % i == 0) {
	    		k++;
	            i=2;
	        } else {
	            continue;
	        }
	    }
	    
	    return k;
	}
	
	// Condition: (1/sqrt(N))*[N/2, d/2] > 2^k
	private static int calculate_d(int N, int k) {
		int d = 0;
		
		if(N % 2 == 0)
			while((1 / Math.sqrt((double)(N))) * computeCombinatorialNumber(N/2, d/2) <= (2^k)) d++;
		
		else
			while((1 / Math.sqrt((double)(N))) * computeCombinatorialNumber((double)N/2, d/2) <= (2^k)) d++;
		
		return d;
	}
	
	private static int calculate_dm(int N, int k) {
		int dm = 0;
		int leftSide = 0;
		BigDecimal rightSide = new BigDecimal((double)(2^(-40)));
		System.out.format("%,.020f \n", rightSide);
		while(true) {
			for(int i = 0; i <= dm; i++) 
				leftSide += computeCombinatorialNumber(N, i);
			
			leftSide *= 2^(N - 1);
			
			System.out.print(dm + " ") ;System.out.println(leftSide >= rightSide.longValueExact());
			if(leftSide >= rightSide.longValueExact()){
				dm--;
				break;
			}
			
			dm++;
		}
		
		return dm;
	}
	
	private static int computeCombinatorialNumber(int upper, int lower) throws IllegalArgumentException{
		if(upper <= 0)
			throw new IllegalArgumentException("In ParameterOptimizer: computeCombinatorialNumber(int upper, int lower): Upper integer has to be non-zero positive integer.");
		if(lower < 0)
			throw new IllegalArgumentException("In ParameterOptimizer: computeCombinatorialNumber(int upper, int lower): Lower integer has to be positive integer.");
		if(upper < lower)
			throw new IllegalArgumentException("In ParameterOptimizer: computeCombinatorialNumber(int upper, int lower): Upper integer has to be bigger than the lower one");
		
		return fact(upper)/(fact(lower) * (fact(upper - lower)));
	}
	
	private static double computeCombinatorialNumber(double upper, int lower) throws IllegalArgumentException{
		if(upper <= 0)
			throw new IllegalArgumentException("In ParameterOptimizer: computeCombinatorialNumber(double upper, double lower): Upper integer has to be non-zero positive integer.");
		if(lower < 0)
			throw new IllegalArgumentException("In ParameterOptimizer: computeCombinatorialNumber(double upper, double lower): Lower integer has to be positive integer.");
		if(upper < lower)
			throw new IllegalArgumentException("In ParameterOptimizer: computeCombinatorialNumber(double upper, double lower): Upper integer has to be bigger than the lower one");
		
		return Gamma.gamma(upper + 1) / (Gamma.gamma((double)lower + 1) * Gamma.gamma(upper - (double)lower + 1));
	}
	
	
	private static int fact(int in) throws IllegalArgumentException {
		if(in < 0) 
			throw new IllegalArgumentException("\"In ParameterOptimizer: fact(int in): Input has to be positive integer.");
		if(in == 0) return 1;
		
		int out = in;
		for(int i = in - 1; i > 0; i--)
			out *= i;
		
		return out;
	}
}
