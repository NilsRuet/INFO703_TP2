package fr.usmb.m1isc.compilation.tp;

import java_cup.runtime.Symbol;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) throws Exception  {
		 LexicalAnalyzer yy;
		 if (args.length > 0){
			 yy = new LexicalAnalyzer(new FileReader(args[0])) ;
		 } else
		 	yy = new LexicalAnalyzer(new InputStreamReader(System.in)) ;
		@SuppressWarnings("deprecation")
		parser p = new parser (yy);
		Symbol s = p.parse();
		LambadaTree program = (LambadaTree)s.value;
		Context ctx = new Context(program);
		Compiler cp = new Compiler(program);
		System.out.println(program);
		String compiled = cp.compile();
		System.out.println(compiled);
		saveCompiled(compiled);
	}

	private static void saveCompiled(String program){
		try {
			FileWriter myWriter = new FileWriter("asm.txt");
			myWriter.write(program);
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
