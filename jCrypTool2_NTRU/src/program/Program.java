/**
 * 
 */
package program;

import tests.Tests;
import ui.UI_Console;


/**
 * @author Ján Mikulec
 *
 */
public class Program {	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/*
		 *  NTRU console interface
		 */
		//##########################################################################
		
//		UI_Console ui = new UI_Console();
//		int programResult = ui.Start();
		
		Tests tests = new Tests();
		int programResult = tests.BeginTest();
		
		//##########################################################################

		System.out.println("Program exit: " + programResult);
	}
}