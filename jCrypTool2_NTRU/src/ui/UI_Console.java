package ui;

import java.util.ArrayList;
import java.util.Scanner;

import ntru.NTRU;
import polynomials.Polynomial;

/**
 * @author Ján Mikulec
 *
 */
public class UI_Console {
	private String userInput;
	private NTRU ntru;
	private Scanner scanner;

	public UI_Console(){}
	
	// Returns code of success/failure.
	//				         1/-1
	public int Start() {
		try {
			this.ntru = new NTRU();
			this.scanner = new Scanner(System.in);
			
			Prompt.WELCOME.print();
			
			while(true) {
				Prompt.USERREQUEST.print();
				
				boolean unsuccess = true;
				while(unsuccess) {	
					this.userInput = scanner.nextLine();
				
					while(!checkUserInput(this.userInput)) {
						Prompt.INCORRECTENTRY.print();
						Prompt.USERREQUEST.print();
						this.userInput = scanner.nextLine();
					}
					
					this.ntru.setM(userInputToCoefficients(userInput));
					
					int counter = 0;
					while(true) {	
						ntru.generateKeys();
						
						Prompt.KEYGEN.print();			
						System.out.println("Generated private key (f, fp): (" 
											+ this.ntru.getFq() + ", "
											+ this.ntru.getFp() + ")\r\n");
						
						ntru.encrypt();
						
						Prompt.ENCRYPT.print();
						System.out.println("Encrypted message e: "
											+ this.ntru.getE() + "\r\n");
						
						ntru.decrypt();
						
						Polynomial testM = new Polynomial(ntru.getM(), ntru.getP());
						Polynomial testC = new Polynomial(ntru.getC(), ntru.getQ());
						
						if(!testC.isEqual(new Polynomial(testM, ntru.getQ()))) {
							Prompt.ALGORITHMFAILED.print();
							counter++;
							
							if(counter == 5) {
								Prompt.DISFUNCTIONALMESSAE.print();
								break;
							}
						}

						else {
							Prompt.DECRYPT.print();
							unsuccess = false;
							break;
						}
					}
				}
				
				Polynomial testM = new Polynomial(ntru.getM(), ntru.getP());
				Polynomial testC = new Polynomial(ntru.getC(), ntru.getQ());
			
				System.out.println("Original open text message m: "
									+ new Polynomial(testM, ntru.getQ()) + "\r\n"
									+ "Decrypted message e:          "
									+ testC + "\r\n");
				
				Prompt.EXITASK.print();
				
				this.userInput = scanner.nextLine();
				
				if(this.userInput.length() == 1)
					if(this.userInput.charAt(0) == '0') 
						break;
				
				unsuccess = true;
			}
		} catch(Exception e) {
			return -1;
		}
		
		return 1;
	}
	
	private static boolean checkUserInput(String userInput) {
		char checkedChar;
		for(int i = 0; i < userInput.length(); i++) {
			checkedChar = userInput.charAt(i);
			if((checkedChar >= '0' && checkedChar <= '9') || checkedChar == ' ' || checkedChar == '\n' || checkedChar == '\r')
				continue;
			else return false;
		}
		
		return true;
	}
	
	public static int[] userInputToCoefficients(String userInput) {
		ArrayList<Integer> coefficients = new ArrayList<Integer>();

		String[] array = userInput.split("[^\\d]+");

		for(int i = 0; i < array.length; i++)
		    coefficients.add(Integer.parseInt(array[i]));

		return coefficients.stream().filter(i -> i != null).mapToInt(i -> i).toArray();
	}
}
