

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class OpCode {
	private Scanner scanner = null;
	private Vector<String> mnemonic = new Vector<String>();
	private Vector<Integer> format = new Vector<Integer>();
	private Vector<Integer> opcode = new Vector<Integer>();
	public OpCode() {	//建構子，將opcode.txt內容讀進來
		super();
		try {scanner = new Scanner(new File("src/Data/opcode.txt"));} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		while(scanner.hasNextLine()){
			mnemonic.add(scanner.next());
			format.add(scanner.nextInt(10));
			opcode.add(scanner.nextInt(16));
		}
	}
	
	public int getOpcode(String m){	//以Op code搜尋OP指令
		int i;
		for(i = 0; i < mnemonic.size(); i++)
			if(mnemonic.get(i).equals(m.toUpperCase()))	break;
		if(i == mnemonic.size())	return -1;	//未搜尋到以-1回傳
		return opcode.get(i);
	}
	public int getFormat(String m){	//以Op code搜尋指令長度
		int i;
		for(i = 0; i < mnemonic.size(); i++)
			if(mnemonic.get(i).equals(m.toUpperCase()))	break;
		if(i == mnemonic.size())	return -1;	//未搜尋到以-1回傳
		return format.get(i);
	}
	
	public int size(){
		return format.size();
	}
}
