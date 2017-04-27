import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class Source {
	//Builded
	private boolean sourceError = false;							//格式 錯誤旗標
	private boolean[] operationError;								//OP code 錯誤旗標
	private boolean[] operandError;									//operand 錯誤旗標
	private boolean[] locationError;								//生成location時，判斷前行是否生成成功的旗標
	private Vector<Boolean> X = new Vector<Boolean>();				//X 旗標
	private Vector<Integer> location = new Vector<Integer>();		//LOC 欄位
	private Vector<String> object = new Vector<String>();			//object code 欄位
	//Source file
	private Vector<String> label = new Vector<String>();			//標籤 欄位
	private Vector<String> operation = new Vector<String>();		//OP code 欄位
	private Vector<String> operand = new Vector<String>();			//operand 欄位
	private Vector<String> comment = new Vector<String>();			//註解 欄位
	//Assist building
	private OpCode op = new OpCode();								//OP code 列表
	
	public Source() {
		super();
		readSrcFile();			//讀檔，生成所有source file 的欄位
		checkCode();			//檢查程式碼合法性
		if(!sourceError){		//若無嚴重格式錯誤才繼續計算Location與Object code
			buildLocation();	//生成Location
			buildObjectcode();	//生成Object code 
		}
	}
	
	private void readSrcFile(){
		Scanner scanner = null;
		String line = new String();	//讀取每行的暫存器
		String[] part;				//分割字串的暫存器
		try {scanner = new Scanner(new File("src/Data/SRCFILE.txt"));} 	//開啟SRCFILE.txt檔
		catch (FileNotFoundException e) {e.printStackTrace();}		
		
		while(scanner.hasNextLine()){
			int i = 0;
			part = new String[4];
			line = scanner.nextLine();
			try{
				StringTokenizer splitLine = new StringTokenizer(line, " ");				//字串分割
				while(splitLine.hasMoreTokens()){part[i++] = splitLine.nextToken();}	//分配分割後的字串
				for(;i < 4; i++)	part[i] = " ";										//補齊剩餘字串
			}
			catch(Exception e){
				sourceError = true;
				break;
			}
			
			try{
				if(op.getOpcode(part[0]) != -1){						//沒有標籤
					label.add(new String(part[3]).toUpperCase());
					operation.add(new String(part[0]).toUpperCase());
					operand.add(new String(part[1]).toUpperCase());
					comment.add(new String(part[2]).toUpperCase());
				}
				else{		//op.getOpcode(part[1]) != -1				//有標籤
					label.add(new String(part[0]).toUpperCase());
					operation.add(new String(part[1]).toUpperCase());
					operand.add(new String(part[2]).toUpperCase());
					comment.add(new String(part[3]).toUpperCase());
				}
			}
			catch(Exception e){											//讀檔例外
				sourceError = true;
				label.add(new String(" "));
				operation.add(new String(" "));
				operand.add(new String(" "));
				comment.add(new String(" "));
			}
		}
	}
	
	private void checkCode(){
		if(sourceError)	return;						//讀檔嚴重錯誤將直接取消檢查
		operationError = new boolean[size()];		//宣告原檔大小的OP code 錯誤旗標
		operandError = new boolean[size()];			//宣告原檔大喜的operand 錯誤旗標
		//檢查START合法性
		if(!operation.get(0).equals("START") || !isInteger(operand.get(0),true))	
			sourceError = true;						//若第一行不為START或 SART後不是接16進位數字(程式起始位置)
		X.add(false);		//SART沒有X
		//檢查code內文合法性(扣除START與END)
		for(int i = 1; i < size()-1; i++){
			//檢查OP code是否在列表中
			if(op.getOpcode(operation.get(i)) == -1)	operationError[i] = true;
			else	operationError[i] = false;
			//檢查是否有X
			StringTokenizer splitOperand = new StringTokenizer(operand.get(i), ",");	//以逗號分割字串
			operand.set(i, splitOperand.nextToken());									//operand = 第一段
			if(splitOperand.hasMoreTokens()){											//如果還有後續段落
				if(splitOperand.nextToken().toUpperCase().equals("X"))	X.add(true);	//檢查是否為'X'
				else{
					X.add(false);														
					operandError[i] = true;
				}
			}
			else	X.add(false);														//沒有後續段落代表無X
			//依OP code的不同，以不同方式檢查operand欄位合法性
			if(operation.get(i).equals("BYTE")){		//若Op code為 BYTE
				if(operand.get(i).charAt(0) == 'X'){								//若operand為 X'   '
					char[] temp = new char[operand.get(i).length()-3];					//字串分割暫存器
					operand.get(i).getChars(2, operand.get(i).length()-1, temp, 0);		//將operand除去 X'' 三個char
					if(!isInteger(new String(temp),true))	operandError[i] = true;		//檢查暫存器內的字串是否為16進位數字
				}
				else if(operand.get(i).charAt(0) != 'C')	operandError[i] = true;	//若operand為 C'   '
			}
			else if(operation.get(i).equals("WORD")){	//若Op code為 WORD
				if(!isInteger(operand.get(i),false))	
					operandError[i] = true;
			}
			else if(operation.get(i).equals("RESW")){	//若Op code為 RESW
				if(!isInteger(operand.get(i),false))	
					operandError[i] = true;
			}
			else if(operation.get(i).equals("RESB")){	//若Op code為 RESB
				if(!isInteger(operand.get(i),false))	
					operandError[i] = true;
			}
			else if(operation.get(i).equals("RSUB")){	//若Op code為 RSUB
				if(!operand.get(i).equals(" "))			
					operandError[i] = true;
			}
			else if(findLabel(operand.get(i)) == size())//其餘的operand必為標籤，檢查該標籤是否被定義過
				operandError[i] = true;
		}
		//檢查END合法性
		if(!operation.get(size()-1).equals("END") || ( findLabel(operand.get(size()-1)) == size() && !operand.get(size()-1).equals(" ")))
			sourceError = true;	//OP code必須為END 且 operand 必須為【被定義過的標籤】或是【空白】
		X.add(false);
	}
	
	private void buildLocation(){
		int org = 0;
		locationError = new boolean[size()];
		location.add(Integer.valueOf(operand.get(0),16));
		for(int i = 1; i < size(); i++){
		//若前一行    位置生成錯誤          或       OP code旗標錯誤        或      operand旗標錯誤
			if(locationError[i-1] || operationError[i-1] || operandError[i-1]){
				locationError[i] = true;
				location.add(0);
				continue;
			}
			
			if(operation.get(i).equals("ORG")){
				if(operand.get(i).equals(" ")){	//如果ORG後面無operand，代表回到ORG之前的位置
					location.add(org);
					org = 0;
				}
				else{	//如果ORG後面有operand，代表移到該標籤位置，以org暫存原本該生成的位置，以下位置計算方式與外層else if計算方式一樣，註解就不寫兩次
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
					location.add(location.get(findLabel(operand.get(i))));	//location = operand 指向的標籤位置
				}
			}
			//若Op code為 RESW 或 RESB，位置 = 前一位置 + 該Op code的format * operand
			else if(operation.get(i-1).equals("RESW") || operation.get(i-1).equals("RESB")){
				location.add(location.get(i-1) + op.getFormat(operation.get(i-1)) * Integer.valueOf(operand.get(i-1)));
			}
			//若Op code為BYTE
			else if(operation.get(i-1).equals("BYTE")){
				if(operand.get(i-1).charAt(0) == 'C') 		//若為C模式
					location.add(location.get(i-1) + (operand.get(i-1).length()-3) );	//C為1個字1Byte，位置 = 前一位置 + '內含的char個數'
				else if(operand.get(i-1).charAt(0) == 'X')	//若為X模式
					location.add(location.get(i-1) + (operand.get(i-1).length()-3) / 2);//X為2個字1Byte，位置 = 前一位置 + '內涵的char個數'除以2
			}
			else	//其餘的位置則依照該Op code的format再加上前一位置(通常為3)
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
