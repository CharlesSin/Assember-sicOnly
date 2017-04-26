import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class Source {
	//Builded
	private Vector<Integer> location = new Vector<Integer>();
	private Vector<String> object = new Vector<String>();
	private Vector<Boolean> X = new Vector<Boolean>();
	private boolean sourceError = false;
	private boolean[] operationError;
	private boolean[] operandError;
	private boolean[] locationError;
	//Source file
	private Vector<String> label = new Vector<String>();
	private Vector<String> operation = new Vector<String>();
	private Vector<String> operand = new Vector<String>();
	private Vector<String> comment = new Vector<String>();
	//Assist building
	private OpCode op = new OpCode();
	
	public Source() {
		super();
		readSrcFile();
		checkCode();
		if(!sourceError){
			buildLocation();
			buildObjectcode();
		}
	}
	
	private void readSrcFile(){
		Scanner scanner = null;
		String line = new String();
		String[] part;
		try {scanner = new Scanner(new File("src/Data/SRCFILE.txt"));} 
		catch (FileNotFoundException e) {e.printStackTrace();}		
		
		while(scanner.hasNextLine()){
			int i = 0;
			part = new String[4];
			line = scanner.nextLine();
			StringTokenizer splitLine = new StringTokenizer(line, " ");			//split line
			while(splitLine.hasMoreTokens()){part[i++] = splitLine.nextToken();}	//read label
			for(;i < 4; i++)	part[i] = " ";
			
			StringTokenizer splitOperand1 = new StringTokenizer(part[1], ",");
			part[1] = splitOperand1.nextToken();
			StringTokenizer splitOperand2 = new StringTokenizer(part[2], ",");
			part[2] = splitOperand2.nextToken();
			if(splitOperand1.hasMoreTokens() || splitOperand2.hasMoreTokens())	X.add(true);
			else	X.add(false);
			try{
				if(op.getOpcode(part[0]) != -1){
					label.add(new String(part[3]).toUpperCase());
					operation.add(new String(part[0]).toUpperCase());
					operand.add(new String(part[1]).toUpperCase());
					comment.add(new String(part[2]).toUpperCase());
				}
				else{		//op.getOpcode(part[1]) != -1
					label.add(new String(part[0]).toUpperCase());
					operation.add(new String(part[1]).toUpperCase());
					operand.add(new String(part[2]).toUpperCase());
					comment.add(new String(part[3]).toUpperCase());
				}
			}
			catch(Exception e){
				sourceError = true;
				label.add(new String(" "));
				operation.add(new String(" "));
				operand.add(new String(" "));
				comment.add(new String(" "));
			}
		}
	}
	
	private void checkCode(){
		if(sourceError)	return;
		operationError = new boolean[size()];
		operandError = new boolean[size()];
		if(!operation.get(0).equals("START") || !isInteger(operand.get(0),true))	sourceError = true;
		for(int i = 1; i < size()-1; i++){
			if(op.getOpcode(operation.get(i)) == -1)	operationError[i] = true;
			else	operationError[i] = false;
			
			if(operation.get(i).equals("BYTE")){
				if(operand.get(i).charAt(0) == 'X'){
					char[] temp = new char[operand.get(i).length()-3];
					operand.get(i).getChars(2, operand.get(i).length()-1, temp, 0);
					if(!isInteger(new String(temp),true))	operandError[i] = true;
				}
				else if(operand.get(i).charAt(0) != 'C')	operandError[i] = true;
			}
			else if(operation.get(i).equals("WORD")){
				if(!isInteger(operand.get(i),false))	
					operandError[i] = true;
			}
			else if(operation.get(i).equals("RESW")){	
				if(!isInteger(operand.get(i),false))	
					operandError[i] = true;
			}
			else if(operation.get(i).equals("RESB")){	
				if(!isInteger(operand.get(i),false))	
					operandError[i] = true;
			}
			else if(operation.get(i).equals("RSUB")){
				if(!operand.get(i).equals(" "))			
					operandError[i] = true;
			}
			else if(findLabel(operand.get(i)) == size())
				operandError[i] = true;
		}
		if(!operation.get(size()-1).equals("END") || ( findLabel(operand.get(size()-1)) == size() && !operand.get(size()-1).equals(" ")))
			sourceError = true;
	}
	
	private void buildLocation(){
		int org = 0;
		locationError = new boolean[size()];
		if(sourceError){
			locationError[0] = true;
			location.add(0);
		}
		else	location.add(Integer.valueOf(operand.get(0),16));
		for(int i = 1; i < size(); i++){
			if(locationError[i-1] || operationError[i-1] || operandError[i-1]){
				locationError[i] = true;
				location.add(0);
				continue;
			}
			
			if(operation.get(i).equals("ORG")){
				if(operand.get(i).equals(" ")){
					location.add(org);
					org = 0;
				}
				else{
					if(operation.get(i-1).equals("RESW") || operation.get(i-1).equals("RESB"))
						org = location.get(i-1) + op.getFormat(operation.get(i-1)) * Integer.valueOf(operand.get(i-1));
					else if(operation.get(i-1).equals("BYTE")){
						if(operand.get(i-1).charAt(0) == 'C')
							org = location.get(i-1) + (operand.get(i-1).length()-3);
						else if(operand.get(i-1).charAt(0) == 'X')
							org = location.get(i-1) + (operand.get(i-1).length()-3) / 2;
					}
					else
						org = location.get(i-1) + op.getFormat(operation.get(i-1));
					location.add(location.get(findLabel(operand.get(i))));
				}
			}
			else if(operation.get(i-1).equals("RESW") || operation.get(i-1).equals("RESB")){
				location.add(location.get(i-1) + op.getFormat(operation.get(i-1)) * Integer.valueOf(operand.get(i-1)));
			}
			else if(operation.get(i-1).equals("BYTE")){
				if(operand.get(i-1).charAt(0) == 'C')
					location.add(location.get(i-1) + (operand.get(i-1).length()-3) );
				else if(operand.get(i-1).charAt(0) == 'X')
					location.add(location.get(i-1) + (operand.get(i-1).length()-3) / 2);
			}
			else
				location.add(location.get(i-1) + op.getFormat(operation.get(i-1)));
		}
	}
	
	private void buildObjectcode(){
		int OP, x, flag;
		char[] temp;
		for(int i = 0; i < size(); i++){
			if(operationError[i] || operandError[i]){
				object.add("000000");
				continue;
			}
			OP = 0; x = 0;
			OP = op.getOpcode(operation.get(i));
			if(X.get(i))	x = 1;
			if(OP == 0xAA)	object.add("FFFFFF");
			else if(operation.get(i).equals("WORD")){
				flag = Integer.valueOf(operand.get(i));
				object.add(String.format("%06X", flag));
			}
			else if(operation.get(i).equals("BYTE")){
				if(operand.get(i).charAt(0) == 'X'){
					temp = new char[operand.get(i).length()-3];
					operand.get(i).getChars(2, operand.get(i).length()-1, temp, 0);
					flag = Integer.valueOf(new String(temp),16);
					object.add(new String(temp));
				}
				else if(operand.get(i).charAt(0) == 'C'){
					flag = 0;
					temp = new char[operand.get(i).length()-3];
					operand.get(i).getChars(2, operand.get(i).length()-1, temp, 0);
					for(int j = 0; j < temp.length; j++)
						flag = flag * 0x100 + (int) new String(temp).charAt(j);
					object.add(String.format("%X", flag));
				}
			}
			else if(operation.get(i).equals("RSUB"))
				object.add(String.format("%06X", OP * 0x10000 + x * 0x8000));
			else
				object.add(String.format("%06X", OP * 0x10000 + x * 0x8000 + location.get(findLabel(operand.get(i)))));
		}
	}
	
	private boolean isInteger(String s, boolean hex) {
		try {Integer.parseInt(s); }
		catch (NumberFormatException ex) {
			if(hex){
				try{Long.parseLong(s, 16);}
				catch(NumberFormatException e){return false;}
			}
			else	return false;
		}
	    return true;
	}
	
	public int size(){
		return label.size();
	}
	
	public int findLabel(String l){
		int i;
		for(i = 0; i < size(); i++)	
			if(label.get(i).equals(l))	break;
		return i;
	}
	
	public Boolean getX(int index) {
		return X.get(index);
	}

	public int getLocation(int index) {
		return location.get(index);
	}
	
	public String getObject(int index) {
		return object.get(index);
	}
	
	public String getLabel(int index) {
		String temp = label.get(index);
		return temp;
	}
	public String getOperation(int index) {
		String temp = new String(operation.get(index));
		return temp;
	}
	public String getOperand(int index) {
		String temp = new String(operand.get(index));
		return temp;
	}
	public String getComment(int index) {
		String temp = new String(comment.get(index));
		return temp;
	}

	public boolean getSourceError() {
		return sourceError;
	}

	public boolean getOperationError(int index) {
		return operationError[index];
	}

	public boolean getOperandError(int index) {
		return operandError[index];
	}
	
	
	
}
