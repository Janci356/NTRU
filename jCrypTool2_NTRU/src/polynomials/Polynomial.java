/**
 * 
 */
package polynomials;

/**
 * @author Ján Mikulec
 *
 */
public final class Polynomial {
	private int[] coefficients;
	private int degree;
	private int field;
	
	public Polynomial(int field) {
		this.field = field;
		int[] coef = {};
		this.coefficients = coef;
		computeDegree();
	}
	
	public Polynomial(int[] coefficients, int field) {
		this.field = field;
		this.coefficients = PolynomialOperations.mod(coefficients, this.field);
		removeUnnecessaryZeroCoefficients();
		computeDegree();
	}
	
	public Polynomial(int degree, int field) {
		this.coefficients = new int[degree + 1];
		this.degree = degree;
		this.field = field;
	}

	public Polynomial(Polynomial poly) {
		this.coefficients = PolynomialOperations.mod(poly.coefficients, poly.getField());
		this.degree = poly.getDegree();
		this.field = poly.getField();
		removeUnnecessaryZeroCoefficients();
	}
	
	public Polynomial(Polynomial poly, int field) {
		this.coefficients = PolynomialOperations.mod(poly.coefficients, field);
		this.field = field;
		computeDegree();
		removeUnnecessaryZeroCoefficients();
	}
	
	public Polynomial(int coefficient, int degree, int field) {
		this.coefficients = new int[degree + 1];
		this.field = field;		
		this.coefficients[0] = PolynomialOperations.mod(coefficient, this.field);
		for(int i = 1; i < this.coefficients.length; i++) {
			this.coefficients[i] = 0;
		}
		this.degree = degree;
	}
	
	public int[] getCoefficients() {return this.coefficients;}
	public int getDegree() {return this.degree;}
	public int getField() {return this.field;}
	
	public void setField(int field) {this.field = field;}
	
	public void setCoefficients(int[] coefficients) throws Exception {
		if(coefficients.length != this.coefficients.length)
			throw new IllegalArgumentException("In Polynomial: setCoefficients(int[] coefficients): During polynomial coefficients setup: polynomial size does not match.");
		this.coefficients = PolynomialOperations.mod(coefficients, this.field);
	}
	
	public void setCoefficient(int position, int value) throws IllegalArgumentException {
		if(position >= this.coefficients.length)
			throw new IllegalArgumentException("In Polynomial: setCoefficient(int position, int value): Chosen position protrudes from polynomial size.");
		this.coefficients[position] = PolynomialOperations.mod(value, this.field);
	}
	
	public void raiseCoefficient(int position, int value) throws Exception {
		if(position >= this.coefficients.length)
			throw new IllegalArgumentException("In Polynomial: setCoefficient(int position, int value): Chosen position protrudes from polynomial size.");
		this.coefficients[position] += value;
	}
	
	public void computeDegree() {
		if(this.isNull()) {
			this.degree = -1;
			return;
		}
		
        for (int i = 0; i < this.coefficients.length - 1; i++) {
        	if (this.coefficients[i] != 0) {
                this.degree = this.coefficients.length - i - 1;
                return;
            }
        }
    }
	
	public boolean isNull() {
		if(this.degree == -1)
			return true;
		
		if(this.coefficients.length != 0 || this.coefficients != null)
			for(int i = 0; i < this.coefficients.length; i++)
				if(this.coefficients[i] != 0)
					return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		if(this.coefficients.length == 0 || this.coefficients == null)
			return "()";
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("(");
		
		for(int i = 0; i < this.coefficients.length - 1; i++)
			if(this.coefficients[i] != 0)
				stringBuilder.append(String.valueOf(this.coefficients[i]) + "x^" + (this.getCoefficients().length - 1 - i)  + " ");
		
		if(this.coefficients[this.coefficients.length - 1] != 0)
			stringBuilder.append(this.coefficients[this.coefficients.length - 1]);
		
		stringBuilder.append(")");
		
		return stringBuilder.toString();
	}
	
	public int findFirstNotNullCoefficient() {
		for(int i = 0; i < this.coefficients.length; i++) 
			if(this.coefficients[i] != 0)
				return this.coefficients[i];

		return 0;
	}
		
	public void multiplyTopCoefficient(int multiplier) {
		for(int i = 0; i < this.coefficients.length; i++) 
			if(this.coefficients[i] != 0)
				this.coefficients[i] *= multiplier;
		
		this.coefficients = PolynomialOperations.mod(this.coefficients, this.field);
	}

	
	public void removeUnnecessaryZeroCoefficients() {
		int[] newCoefficients;
		int counter = 0;
				
		for(int i = 0; i < this.coefficients.length; i++) {
			if(this.coefficients[i] != 0) { 
				newCoefficients = new int[this.coefficients.length - i];
				for(int j = i; j < this.coefficients.length; j++)
					newCoefficients[j - counter] = this.coefficients[j];
				this.coefficients = newCoefficients;
				break;
			}
			else
				counter++;
		}
		
		this.computeDegree();
	}
	
	public boolean isEqual(Polynomial a){
		if(a.getCoefficients().length != this.getCoefficients().length ||
		   a.getDegree() != this.getDegree() ||
		   a.getField() != this.getField())
			return false;
		
		for(int i = 0; i < a.getCoefficients().length; i++) 
			if(a.getCoefficients()[i] != this.getCoefficients()[i]) 
				return false;
		
		return true;
	}
}