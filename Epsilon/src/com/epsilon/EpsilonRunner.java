package com.epsilon;
import java.io.FileInputStream;
import java.io.PrintWriter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
/**
 * @purpose
 * 		The Epsilon Runner class is responsible for reading the source program
 * 		and invoking the Epsilon parser to parse the source program.
 *      Writes the generated intermediate code to a file.
 * 
 * @version  %I%, %G% 
 * 			
 * @author 
 *
 */
public class EpsilonRunner {
	public static void main(String[] args) throws Exception {	
	//to take  file as input
		FileInputStream fileInputStream = new FileInputStream(args[0]);		
		ANTLRInputStream input = new ANTLRInputStream(fileInputStream);

		HelloLexer lexer = new HelloLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		HelloParser parser = new HelloParser(tokens);		
		ParserRuleContext tree = parser.start();
		ParseTreeWalker walker = new ParseTreeWalker(); // parse tree walker object
		NewListener listener = new NewListener();
		
		try {
			PrintWriter writer = new PrintWriter("parseTree.pt", "UTF-8");
			writer.println(tree);	
			writer.println(tree.toStringTree(parser));			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\n Exception caught"+ e.toString());
		}			
		
	}
}
