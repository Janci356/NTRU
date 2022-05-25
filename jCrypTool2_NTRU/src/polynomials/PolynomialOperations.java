/**
 * 
 */
package polynomials;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ján Mikulec
 *
 */
public class PolynomialOperations {
	public static Polynomial times(Polynomial a, Polynomial b) throws IllegalArgumentException {
		if(a.getField() != b.getField())
			throw new IllegalArgumentException("In PolynomialOperations: minus(Polynomial a, Polynomial b): Polynomials have to be in same field.");
		if(a.isNull() || b.isNull())
			return new Polynomial(a.getField());
		
		int[] coefficients = new int[a.getDegree() + b.getDegree() + 1];
		
		for(int i = 0; i < a.getCoefficients().length; i++) {
			for(int j = 0; j < b.getCoefficients().length; j++) {
				coefficients[i+j] += (a.getCoefficients()[i] * b.getCoefficients()[j]);
			}
		}
		
		return mod(new Polynomial(coefficients, a.getField()));
	}
	
	public static Polynomial plus(Polynomial a, Polynomial b) throws IllegalArgumentException {
		if(a.getField() != b.getField())
			throw new IllegalArgumentException("In PolynomialOperations: minus(Polynomial a, Polynomial b): Polynomials have to be in same field.");
	
		int newDeg = Math.max(a.getDegree(), b.getDegree());
		int[] newCoeff = new int[newDeg + 1];
		int[] newA = fillWithZeros(a.getCoefficients(), newDeg + 1);
		int[] newB = fillWithZeros(b.getCoefficients(), newDeg + 1);
        
		for (int i = 0; i < newCoeff.length; i++) newCoeff[i] += newA[i];
        for (int i = 0; i < newCoeff.length; i++) newCoeff[i] += newB[i];
        
        return new Polynomial(newCoeff, a.getField());
	}
	
	// Second polynomial parameter is deducted from first. (a - b)
	public static Polynomial minus(Polynomial a, Polynomial b) throws Exception {
		if(a.getField() != b.getField())
			throw new IllegalArgumentException("In PolynomialOperations: minus(Polynomial a, Polynomial b): Polynomials have to be in same field.");
	
		int newDeg = Math.max(a.getDegree(), b.getDegree());
		int[] newCoeff = new int[newDeg + 1];
		int[] newA = fillWithZeros(a.getCoefficients(), newDeg + 1);	
		int[] newB = fillWithZeros(b.getCoefficients(), newDeg + 1);

		for (int i = 0; i < newCoeff.length; i++) 
			newCoeff[i] += newA[i];

		for (int i = 0; i < newCoeff.length; i++) 
        	newCoeff[i] -= newB[i];
        
        return new Polynomial(newCoeff, a.getField());
	}
	
	// Compute greatest common divisor with Euclidean Algorithm.
	public static Polynomial gcd(Polynomial a, Polynomial b) throws Exception {
		if(a.getField() != b.getField())
			throw new IllegalArgumentException("In PolynomialOperations: minus(Polynomial a, Polynomial b): Polynomials have to be in same field.");
		if(b.isNull() || b.getDegree() == 0)
			throw new IllegalArgumentException("In PolynomialOperations: divide(Polynomial a, Polynomial b): Divisor cannot be null or 0.");
				
		int degree;
		int[] coefficient;
		Polynomial tmpPoly1, tmpPoly2, dividend, divisor;
		Polynomial retPoly = new Polynomial(a.getField());
		
		if(a.getDegree() >= b.getDegree()) { 
			dividend = a;
			divisor = b;
		}
		
		else {
			dividend = b;
			divisor =  a;
		}
		
		while(!dividend.isNull()) {
			if(dividend.getDegree() < divisor.getDegree()){
				// Switch polynomials
				Polynomial tmp = new Polynomial(dividend);
				dividend = divisor;
				divisor = tmp;
			}

			try {
				coefficient = findCoefficient(dividend.findFirstNotNullCoefficient(), divisor.findFirstNotNullCoefficient(), a.getField());
			} catch (Exception e) {
				throw e;
			}
			degree = dividend.getDegree() - divisor.getDegree();
			
			tmpPoly1 = mod(new Polynomial(coefficient[0], degree));
			Polynomial multipRes = times(divisor, tmpPoly1);
			tmpPoly2 = new Polynomial(divisor);
			divisor = minus(dividend, multipRes);
			dividend = tmpPoly2;
			
			if(!divisor.isNull()) 
				retPoly = divisor;
		}
		
		return retPoly;
	}

	// Returns ArrayList {gcd, Bézout coefficient 1, Bézout coefficient 2}
	public static ArrayList<Polynomial> xgcd(Polynomial a, Polynomial b) throws Exception {
		if( (a == null || b == null) ||
			(a.getDegree() == 0 || b.getDegree() == 0))
			throw new IllegalArgumentException("In PolynomialOperations: xgcd(Polynomial a, Polynomial b): Polynomial entries cannot be null or 0." );
		if(a.getField() != b.getField())
			throw new IllegalArgumentException("In PolynomialOperations: minus(Polynomial a, Polynomial b): Polynomials have to be in same field.");
		if(b.isNull() || b.getDegree() == 0)
			throw new IllegalArgumentException("In PolynomialOperations: divide(Polynomial a, Polynomial b): Divisor cannot be null or 0.");
		
		if(a.getDegree() < b.getDegree()) {
			ArrayList<Polynomial> res = xgcd(b, a);
			Polynomial temp = new Polynomial(res.get(1));
			res.set(1, res.get(2));
			res.set(2, temp);
			return res;
		}
		
		ArrayList<Polynomial> res = new ArrayList<>();
		
		Polynomial r0 = new Polynomial(a);
		Polynomial r1 = new Polynomial(b);

		int[] zero = {0};
		int[] one = {1};

		Polynomial s0 = new Polynomial(one, a.getField());
		Polynomial s1 =  new Polynomial(zero, a.getField());
		
		Polynomial t0 =  new Polynomial(zero, a.getField());
		Polynomial t1 =  new Polynomial(one, a.getField());

		while(!r1.isNull()) {
			Polynomial q = xgcdDivision(r0, r1);

			Polynomial r2 = minus(r0, times(r1, q));
			Polynomial s2 = minus(s0, times(s1, q));
			Polynomial t2 = minus(t0, times(t1, q));
			
			r0 = new Polynomial(r1);
			r1 = new Polynomial(r2);
			s0 = new Polynomial(s1);
			s1 = new Polynomial(s2);
			t0 = new Polynomial(t1);
			t1 = new Polynomial(t2);	
		}
		
		res.add(r0);
		res.add(s0);
		res.add(t0);
		
		return res;
	}
	
	// r0 = q * r1 + r2
	// dividend = divisor * multiplier + remainder
	// a = b * m + r
	// return quotient
	private static Polynomial xgcdDivision(Polynomial a, Polynomial b) throws Exception{
		if(a.getField() != b.getField())
			throw new IllegalArgumentException("In PolynomialOperations: minus(Polynomial a, Polynomial b): Polynomials have to be in same field.");
		if(b.isNull())
			throw new IllegalArgumentException("In PolynomialOperations: divide(Polynomial a, Polynomial b): Divisor cannot be null or 0.");	
		
		int degree;
		int[] coefficient = {0,0};
		Polynomial tmpPoly, dividend, divisor;
		int[] quotientInit = {0};
		Polynomial quotient = new Polynomial(quotientInit, a.getField());
		
		if(a.getDegree() >= b.getDegree()) { 
			dividend = a;
			divisor = b;
		}
		
		else {
			dividend = b;
			divisor =  a;
		}
				
		while(true) {
			if(dividend.getDegree() < divisor.getDegree()){
				// Switch polynomials
				Polynomial tmp = new Polynomial(dividend);
				dividend = divisor;
				divisor = tmp;
			}
			
			try {
				coefficient = findCoefficient(dividend.findFirstNotNullCoefficient(), divisor.findFirstNotNullCoefficient(), a.getField());
			} catch (Exception e) {
				throw e;
			}
			degree = dividend.getDegree() - divisor.getDegree();
			
			if(coefficient[1] == 1) {
				dividend.multiplyTopCoefficient(coefficient[0]);
				int[] tempArr = {coefficient[0]}; 
				dividend = times(dividend, new Polynomial(tempArr, dividend.getField()));
				coefficient[0] = 1;
			}
			
			tmpPoly = mod(new Polynomial(coefficient[0], degree, a.getField()));
			quotient = plus(quotient, tmpPoly);
			Polynomial multipRes = times(divisor, tmpPoly);
			try {
				dividend = minus(dividend, multipRes);
			} catch (Exception e) {
				throw e;
			}
			
			if(divisor.getDegree() > dividend.getDegree() || dividend.isNull()) 
				break;
		}
		
		if(minus(a, times(b, quotient)).getDegree() >= b.getDegree() && !minus(a, times(b, quotient)).isNull()) {
			Polynomial add = new Polynomial(xgcdDivision(b, minus(a, times(b, quotient))));
			quotient = plus(quotient, add);
		}
		
		return quotient;
	}
	
	// returns {x, y} : int
	// x: coefficient
	// y: sympotom signalizing where multiplicative inversion occured
	private static int[] findCoefficient(int a, int b, int field) throws RuntimeException {
		int aPositive, aNegative;
		int []result = new int[2];

		int[] aRep = findRepresentants(a, field);
		if(aRep[0] > aRep[1]) {
			aPositive = aRep[0];
			aNegative = aRep[1];
		}
		else {
			aPositive = aRep[1];
			aNegative = aRep[0];
		}
		
		result[1] = 0;
		result[0] = findCoefficientIteration(aPositive, b, field);

		if(result[0] == -1) {
			result[1] = 0;
			result[0] = findCoefficientIteration(aNegative, b, field);
		}
		
		if(result[0] == -1) {

			result[1] = 1;
			result[0] = findCoefficientIteration(b, aPositive, field);
		}
		
		if(result[0] == -1) {
			result[1] = 1;
			result[0] = findCoefficientIteration(b, aNegative, field);
		}
		
		if(result[0] == -1) {
			result[1] = -1;
			throw new RuntimeException("In findCoefficient error occured: Internal error.");
		}
		
		return result;

	}
	
	public static Polynomial mod(Polynomial polynomial) {
		if(polynomial.isNull()) return polynomial;
		
		int[] coefficients = polynomial.getCoefficients();
		
		for(int i = 0; i < coefficients.length; i++) 
			coefficients[i] = mod(coefficients[i], polynomial.getField());
		
		return new Polynomial(coefficients, polynomial.getField());
	}
	
	public static Polynomial convolution(Polynomial a, Polynomial b) {	
		int newSize = Math.max(a.getCoefficients().length, b.getCoefficients().length);
		int[] af = a.getCoefficients();
		int[] bf = b.getCoefficients();
		int[][] nf = new int[newSize][newSize];
		int[][] tmpf = new int[newSize][newSize];
		int[] rf = new int[newSize];
		
		if(af.length != newSize)
			af = prependZeroCoeffitients(af, newSize - af.length);
		else if(bf.length != newSize)
			bf = prependZeroCoeffitients(bf, newSize - bf.length);
		
		for(int i = 0; i < newSize; i++) 
			for(int k = 0; k < newSize; k++) 
				nf[i][k] = af[i] * bf[k];
		
		tmpf = moveRight_2D(nf);
		
		
		for(int i = 0; i < newSize; i++) 
			for(int k = 0; k < newSize; k++) 
				rf[i] += tmpf[k][i];

		
		return mod(new Polynomial(rf, a.getField()));
	}
	
	public static int[] mod(int[] coefficients, int field) {
		int[] reducedCoefficients = new int[coefficients.length];
		
		for(int i = 0; i < reducedCoefficients.length; i++) 
			reducedCoefficients[i] = mod(coefficients[i], field);
		
		return reducedCoefficients;
	}

	public static int mod(int number, int field) {
		int[] representants = findRepresentants(number, field);
		
		if(Math.abs(representants[0]) < Math.abs(representants[1])) 
			return representants[0];
		else 
			return representants[1];
	}
	
	public static int multiplicativeInverse(int num, int field) {
		for(int i = 1; i < field; i++) {
			int n = mod(num * i, field);
			
			if(n == 1) 
				return i;
		}
		
		return num;
	}
	
	public static List<ArrayList<Polynomial>> checkMultipInverse(ArrayList<Polynomial> fp_BI, ArrayList<Polynomial> fq_BI, Polynomial fp_xn1, Polynomial fq_xn1, int N, int df, int p, int q) throws Exception{
		int fp_BI_res = checkSingleMultipInverse(fp_BI);
		int fq_BI_res = checkSingleMultipInverse(fq_BI);
		
		if(fp_BI_res == 2 || fq_BI_res == 2) {
			try {
				ArrayList<Polynomial> fpq = PolynomialOperationsNTRU.generateRandomPolynomialOfTwoDifferentFields(N, df, p, q);
				
				ArrayList<Polynomial> fp_BezoutIdentity = PolynomialOperations.xgcd(fpq.get(0), fp_xn1);
				fp_BezoutIdentity.add(fpq.get(0));
				ArrayList<Polynomial> fq_BezoutIdentity = PolynomialOperations.xgcd(fpq.get(1), fq_xn1);
				fq_BezoutIdentity.add(fpq.get(1));
				
				return checkMultipInverse(fp_BezoutIdentity, fq_BezoutIdentity, fp_xn1, fq_xn1, N, df, p, q);
			} catch (Exception e) {
				throw e;
			}
		}
		
		if(fp_BI_res == 1 || fq_BI_res == 1) {
			List<ArrayList<Polynomial>> ret = new ArrayList<ArrayList<Polynomial>>();
			
			if(fp_BI_res == 1) {
				int[] inverseCoef = {PolynomialOperations.multiplicativeInverse(fp_BI.get(0).getCoefficients()[0], p)};
				Polynomial inversePoly = new Polynomial(inverseCoef, p);
				ArrayList<Polynomial> new_fp_BI = new ArrayList<Polynomial>();
				
				new_fp_BI.add(PolynomialOperations.times(fp_BI.get(0), inversePoly));
				new_fp_BI.add(PolynomialOperations.times(fp_BI.get(1), inversePoly));
				new_fp_BI.add(PolynomialOperations.times(fp_BI.get(2), inversePoly));
				
				if(fp_BI.size() == 4)
					new_fp_BI.add(fp_BI.get(3));
				
				ret.add(new_fp_BI);
			}
			
			else 
				ret.add(fp_BI);
		
			if(fq_BI_res == 1) {
				int[] inverseCoef = {PolynomialOperations.multiplicativeInverse(fq_BI.get(0).getCoefficients()[0], q)};
				Polynomial inversePoly = new Polynomial(inverseCoef, q);
				ArrayList<Polynomial> new_fq_BI = new ArrayList<Polynomial>();

				new_fq_BI.add(PolynomialOperations.times(fq_BI.get(0), inversePoly));
				new_fq_BI.add(PolynomialOperations.times(fq_BI.get(1), inversePoly));
				new_fq_BI.add(PolynomialOperations.times(fq_BI.get(2), inversePoly));
				
				if(fq_BI.size() == 4)
					new_fq_BI.add(fq_BI.get(3));

				ret.add(new_fq_BI);
			}
			
			else 
				ret.add(fq_BI);
					
			return ret;
		}
		
		else {
			List<ArrayList<Polynomial>> ret = new ArrayList<ArrayList<Polynomial>>();
			
			ret.add(fp_BI);
			ret.add(fq_BI);
			
			return ret;
		}
	}
	
	// Let x and y be polynomials with greatest common divisor d. 
	// Then there exist polynomials a and b such that ax + by = d
	public static boolean isBezoutIdentity(Polynomial d, Polynomial a, Polynomial b, Polynomial x, Polynomial y) {
		Polynomial left = PolynomialOperations.plus(PolynomialOperations.times(a, x), PolynomialOperations.times(b, y));
		
		if(!left.isEqual(d))
			return false;
		
		return true;
	}

	private static int[] findRepresentants(int number, int field) throws IllegalArgumentException{
		if(field < 1) 
			throw new IllegalArgumentException("In PolynomialOperations: findRepresentants(int number, int field): Field has to be positive integer.");
		
		int[] representants = new int[2];
		
		if(number % field == 0) {
			representants[0] = 0;
			representants[1] = 0;
		}
		
		else {
			int tmpField = field;
			while(Math.abs(number) > tmpField) tmpField += field;
			if(number < 0) {
				representants[0] = tmpField - Math.abs(number);
				representants[1] = representants[0] - field;
			}
			else {
				representants[0] = Math.abs(number) - tmpField;
				representants[1] = field - Math.abs(representants[0]); 
			}
		}
				
		return representants;
	}
	
	private static int[] prependZeroCoeffitients(int[] field, int number) {
		int i;
		int[] newField = new int[field.length + number];
		
		for(i = 0; i < number; i++) 
			newField[i] = 0;
		
		for(int k = i; k < newField.length; k++)
			newField[k] = field[k - i];
		
		return newField;
	}
	
	private static int[] moveRight(int[] array, int move) throws IllegalArgumentException {
		if(move == array.length)
			return array;
		if(move > array.length)
			throw new IllegalArgumentException("In PolynomialOperations: moveRight(int[] field, int move): move cannot be greater than array size.");
		
		int[] newArray = new int[array.length];
		
		for(int i = 0; i < move; i++)
			newArray[i] = array[array.length - move + i];

		
		for(int i = 0; i < array.length - move; i++)
			newArray[i + move] = array[i];

		return newArray;
	}
	
	private static int[][] moveRight_2D(int[][] array) throws IllegalArgumentException {
		int[][] newArray = new int[array.length][array.length];
		int[] tmp;
		
		for(int i = 0; i < newArray[0].length; i++) {
			if(array.length != array[i].length)
				throw new IllegalArgumentException("In PolynomialOperations: moveRight_2D(int[][] array): Dimensions have to be equal size.");
			tmp = moveRight(array[i], i + 1);
			for(int k = 0; k < tmp.length; k++) 
				newArray[i][k] = tmp[k];
		}

		return newArray;
	}
	
	private static int[] fillWithZeros(int[] arr, int newLen) throws IllegalArgumentException {
		if(arr.length == newLen) 
			return arr;
		if(arr.length > newLen) 
			throw new IllegalArgumentException("In PolynomialOperations: fillWithZeros(int[] arr, int newLen): New lenght has to be bigger or at least equal to old lenght. Old: " + arr.length + " New: " + newLen);
		
		int[] newArr = new int[newLen];
		
		for(int i = newLen - arr.length; i < newLen; i++) 
			newArr[i] = arr[i - (newLen - arr.length)];
		
		return newArr;
	}
	
	private static int findCoefficientIteration(int a, int b, int field) {
		int iterator = 1;
		while(true) {
			int[] bRep = findRepresentants(b * iterator, field);
			
			for(int i = 0; i < bRep.length; i++)
				if(bRep[i] == a)
					return iterator;
			
			if(iterator == field) 
				return -1;
			
			iterator++;
		}		
	}
	
	// Returns 
	//			0 if no action is needed
	//			1 if multiplicative inverse is needed 
	//			2 if new polynomial generation is needed
	private static int checkSingleMultipInverse(ArrayList<Polynomial> BI){
		int[] sampleCoef = {1};
		
		if(!BI.get(0).isEqual(new Polynomial(sampleCoef, BI.get(0).getField()))) {
			if(BI.get(0).getDegree() != 0)
				return 2;
			
			else
				return 1;
		}
		
		return 0;
	}
}