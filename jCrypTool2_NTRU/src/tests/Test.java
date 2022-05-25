package tests;

import java.util.ArrayList;
import java.util.List;

import polynomials.Polynomial;
import polynomials.PolynomialOperations;
import polynomials.PolynomialOperationsNTRU;

public class Test {
	// return 0 if test failed
	// return 1 if succeeded
	public int[] test(int identifier, int N, int q, int p, int df, int dg, int dr, List<Integer>[] ms) throws Exception {		
		int[] res = new int[100];

		try {
			Polynomial c, r, tmp, e, a, b, mp, fp, fq;

			System.out.println("Test " + (identifier + 1) + " begins.\r\n");
			
			Polynomial g = PolynomialOperationsNTRU.generateRandomPolynomial(N, dg,  q);
			List<Polynomial> fpq = PolynomialOperationsNTRU.generateRandomPolynomialOfTwoDifferentFields(N, df, p, q);
			
			Polynomial xn1p = PolynomialOperationsNTRU.createXN1(N, p);
			Polynomial xn1q = PolynomialOperationsNTRU.createXN1(N, q);
			
			ArrayList<Polynomial> fp_BezoutIdentity = PolynomialOperations.xgcd(fpq.get(0), xn1p);
			ArrayList<Polynomial> fq_BezoutIdentity = PolynomialOperations.xgcd(fpq.get(1), xn1q);
		
			List<ArrayList<Polynomial>> BI_pq = PolynomialOperations.checkMultipInverse(fp_BezoutIdentity, fq_BezoutIdentity, xn1p, xn1q, N, df, p, q);
			fp_BezoutIdentity = BI_pq.get(0);
			fq_BezoutIdentity = BI_pq.get(1);
			
			// If new f was generate, replace old one.
			if(fp_BezoutIdentity.size() == 4 && fq_BezoutIdentity.size() == 4) {
				fpq.set(0, fp_BezoutIdentity.get(3));
				fpq.set(1, fq_BezoutIdentity.get(3));
			}
			
//			if(!PolynomialOperations.isBezoutIdentity(fp_BezoutIdentity.get(0), fp_BezoutIdentity.get(1), fp_BezoutIdentity.get(2), fpq.get(0), xn1p))
//				return 0;
//			if(!PolynomialOperations.isBezoutIdentity(fq_BezoutIdentity.get(0), fq_BezoutIdentity.get(1), fq_BezoutIdentity.get(2), fpq.get(1), xn1q))
//				return 0;
				
			fp = fp_BezoutIdentity.get(1);
			fq = fq_BezoutIdentity.get(1);
		
			int[] pp_coef = {p};
			Polynomial pp = new Polynomial(pp_coef, fq.getField());
			Polynomial h = PolynomialOperations.convolution(g, PolynomialOperations.times(pp, fq));
		
			for(int k = 0; k < 100; k++) {
				int[] m = ms[k].stream().mapToInt(Integer::intValue).toArray();
				mp = new Polynomial(m, q);
				
				r = PolynomialOperationsNTRU.generateRandomPolynomial(N, dr, q);
				tmp = PolynomialOperations.convolution(r, h);
				e = PolynomialOperations.plus(tmp, mp);
								
				a = PolynomialOperations.convolution(e, fpq.get(1));
				b = new Polynomial(a, p);
				c = PolynomialOperations.convolution(b, fp);
				
				Polynomial testM = new Polynomial(mp, p);
				Polynomial testC = new Polynomial(c, q);
									
//				if(testM.isEqual(new Polynomial(testC, p))) 
//					break;
				
				if(!testM.isEqual(new Polynomial(testC, p))) 
					res[k] = 0;
				else
					res[k] = 1;
			}		
			
		} catch (Exception e) {
			throw e;
//			return 0;
		}	
		
		return res;
	}
}
