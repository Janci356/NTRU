package ui;

/**
 * @author Ján Mikulec
 *
 */
public enum Prompt {
	GENERALINFO("NTRU is an asymmetric cryptographic system. It provides key generation, encryption and decryption.\r\n"
			+ "NTRU security is associated with very hard problems such as border reduction, one of these problems is the so-called problem of the shortest vector (SVP)\r\n"),
	KEYGEN("Key generation consists of creating a private (f, fp) and public h key. \r\n"
			+ "First, random polynomials f, g ∈ R with coefficients significantly smaller than q are generated. For f must apply:\r\n"
			+ "• f is an invertible mod p,\r\n"
			+ "• f is the invertible mode q,\r\n"
			+ "• f is small."
			+ "Next we calculate fp as the inversion of f (mod p) defined as:\r\n"
			+ "f · fp = 1 (mod p).\r\n"
			+ "Than we calculate fq as the inversion of f (mod q) defined as:\r\n"
			+ "f · fq = 1 (mod q).\r\n"
			+ "Finally we calculate the polynomial h:\r\n"
			+ "h = g ∗ pfq (mod xN −1) (mod q)"),
	ENCRYPT("We have open text in the form of a polynomial m with coefficients mod p. We choose a random\r\n"
			+ "sticky message r ∈ R with low coefficients. The message is then encrypted:\r\n"
			+ "e = (r ∗ h + m) (mod xN −1) (mod q)."),
	DECRYPT("Decryption returns an open message m from encrypted text e, using private\r\n"
			+ "keys (f, fp):\r\n"
			+ "First we calculate:\r\n"
			+ "a = e ∗ f (mod xN −1) (mod q),\r\n"
			+ "where we choose such coefficients that satisfies: (−q / 2) ≤ a_i ≤ (q / 2).\r\n"
			+ "Secondly we reduce a mod p:\r\n"
			+ "b = a (mod p).\r\n"
			+ "Than we calculate:\r\n"
			+ "c = b ∗ fp (mod xN −1) (mod p).\r\n"
			+ "Finally we get the original open text as:\r\n"
			+ "m = c (mod p)"),
	WELCOME("NTRU educational aid UI\r\n"
			+ "First let's see what NTRU is and how does it works.\r\n"),
	USERREQUEST("Please enter open message in form of polynomial positive only coefficients.\r\n"
			+ "Keep trail of whitespaces and signs, enter only numbers and whitespaces.\r\n"
			+ "Following example: 1 0 4 5 1 0 4 2\r\n"
			+ "User input:"),
	INCORRECTENTRY("You entered wrong input,\r\n"
			+ "please follow instructions.\r\n"),
	ALGORITHMFAILED("Algorithm failed. This tend to happend in approximately 12% cases.\r\n"
			+ "Process will now repeat from beginning. Entered message remains the same.\r\n" 
			+ "If this happens 5-times, please reenter message.\r\n"),
	DISFUNCTIONALMESSAE("Algortihm failed 5-times, please choose different message.\r\n"
			+ "Try to use lower coefficients.\r\n"),
	EXITASK("To continue enter any key,\r\n"
			+ "to exit enter 0:");
	
	private final String prompt;
	
	Prompt(final String prompt) {
        this.prompt = prompt;
    }

	@Override
	public String toString() {
		return this.prompt;
	}
	
	public void print() {
		System.out.println(this.prompt);
	}
}
