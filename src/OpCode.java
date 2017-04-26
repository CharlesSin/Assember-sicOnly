

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class OpCode {
	private Scanner scanner = null;
	private Vector<String> mnemonic = new Vector<String>();
	private Vector<Integer> format = new Vector<Integer>();
	private Vector<Integer> opcode = new Vector<Integer>();
	public OpCode() {
		super();
		try {scanner = new Scanner(new File("src/Data/opcode.txt"));} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		while(scanner.hasNextLine()){
			mnemonic.add(scanner.next());
			format.add(scanner.nextInt(10));
			opcode.add(scanner.nextInt(16));
		}
	}
	
	public int getOpcode(String m){
		int i;
		for(i = 0; i < mnemonic.size(); i++)
			if(mnemonic.get(i).equals(m.toUpperCase()))	break;
		if(i == mnemonic.size())	return -1;
		return opcode.get(i);
	}
	public int getFormat(String m){
		int i;
		for(i = 0; i < mnemonic.size(); i++)
			if(mnemonic.get(i).equals(m.toUpperCase()))	break;
		if(i == mnemonic.size())	return -1;
		return format.get(i);
	}
	
	public int size(){
		return format.size();
	}
}
