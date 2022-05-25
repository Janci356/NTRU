/**
 * 
 */
package ntru;

import java.util.ArrayList;
import java.util.List;

import polynomials.Polynomial;
import polynomials.PolynomialOperations;
import polynomials.PolynomialOperationsNTRU;

/**
 * @author Ján Mikulec
 *
 */
public class NTRU {
	/*
	 * h -> public key
	 * fp, fq -> private key
	 */
	private int N, q, p, df, dg, dr;
	private Polynomial fq, fp, g, h, r, e, m, c; 
	private List<Polynomial> fpq;
	
	// Moderate security set as default.
	// Recommended parameters options:
	// N   p q   df  dg  dr
	// 167 3 128 61  20  18
	// 251 3 128 50  24  1 kon6
	// 503 3 256 216 72  55
	// 167 2 127 45  35  18
	// 251 2 127 35  35  22
	// 503 2 253 155 100 65
	
	// According to NIST recommended optional security levels are:
	// ntruhps2048509,
	// ntruhps2048677,
	// ntruhps4096821.
	public NTRU() {
		this.N = 167;
		this.q = 127;
		this.p = 3;
		this.df = 61;
		this.dg = 20;
		this.dr = 18;
	}
	
	public NTRU(int N, int p) {
		this.N = N;
		this.p = p;
	}
	
	public NTRU(int N, int q, int p, int df, int dg) {
		if(N < df * 2 - 1 || N < dg * 2 - 1)
			throw new IllegalArgumentException ("In NTRU: NTRU(int N, int q, int p, int df, int dg): df and dg have to be smaller than N.");
			
		if(df < 1 || dg < 1)
			throw new IllegalArgumentException ("In NTRU: NTRU(int N, int q, int p, int df, int dg): df and dg have to bigger than 0.");
		
		this.N = N;
		this.p = p;
		this.q = q;
		this.df = df;
		this.dg = dg;
	}
	
	public void setN(int N) {this.N = N;}
	public void setP(int p) {this.p = p;}
	public void setQ(int q) {this.q = q;}
	public void setR(Polynomial r) {this.r = r;}
	public void setDf(int df) {this.df = df;}
	public void setDg(int dg) {this.dg = dg;}
	public void setDr(int dr) {this.dr = dr;}
	public void setE(Polynomial e) {this.e = e;}
	public void setM(Polynomial m) {this.m = m;}
	public void setM(int[] coeffitients) {
		Polynomial m = new Polynomial(coeffitients, this.q);
		this.m = m;
	}
	
	public int getN() {return this.N;}
	public int getP() {return this.p;}
	public int getQ() {return this.q;}
	public Polynomial getR() {return this.r;}
	public int getDf() {return this.df;}
	public int getDg() {return this.dg;}
	public int getDr() {return this.dr;}
	public Polynomial getFp() {return this.fp;}
	public Polynomial getFq() {return this.fq;}
	public Polynomial getG() {return this.g;}
	public Polynomial getH() {return this.h;}
	public Polynomial getE() {return this.e;}
	public Polynomial getM() {return this.m;}
	public Polynomial getC() {return this.c;}
	
	public void generateKeys() throws Exception {
		this.g = PolynomialOperationsNTRU.generateRandomPolynomial(N, dg,  q);
		this.fpq = PolynomialOperationsNTRU.generateRandomPolynomialOfTwoDifferentFields(N, df, p, q);
		
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
		
		if(!PolynomialOperations.isBezoutIdentity(fp_BezoutIdentity.get(0), fp_BezoutIdentity.get(1), fp_BezoutIdentity.get(2), fpq.get(0), xn1p))
			throw new InternalError("In NTRU: generateKeys(): Bezout Identity check failed.");
		if(!PolynomialOperations.isBezoutIdentity(fq_BezoutIdentity.get(0), fq_BezoutIdentity.get(1), fq_BezoutIdentity.get(2), fpq.get(1), xn1q))
			throw new InternalError("In NTRU: generateKeys(): Bezout Identity check failed.");
		
		fp = fp_BezoutIdentity.get(1);
		fq = fq_BezoutIdentity.get(1);
	
	
		int[] pp_coef = {p};
		Polynomial pp = new Polynomial(pp_coef, fq.getField());
		this.h = PolynomialOperations.convolution(g, PolynomialOperations.times(pp, fq));
	}
	
	public void encrypt() throws Exception{
		if(this.m.isNull())
			throw new IllegalStateException("In NTRU: encrypt(): Message of polynomial type has to be instantiated inside NTRU object before encryption occurs.");
		if(this.h.isNull())
			throw new IllegalStateException("In NTRU: encrypt(): Key pair has to be established before enryption occurs.");
		
		this.r = PolynomialOperationsNTRU.generateRandomPolynomial(N, dr, q);
		Polynomial tmp = PolynomialOperations.convolution(r, h);
		this.e = PolynomialOperations.plus(tmp, this.m);
	}
	
	public void decrypt() {
		if(this.fp.isNull())
			throw new IllegalStateException("In NTRU: decrypt(): Key pair has to be established before deryption occurs.");
		
		Polynomial a = PolynomialOperations.convolution(this.e, this.fpq.get(1));
		Polynomial b = new Polynomial(a, this.p);
		
		this.c = PolynomialOperations.convolution(b, this.fp);
	}

}
