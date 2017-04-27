import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Output {
	Output(Source p, Card c){
		buildTable(p);
		buildCard(c);
	}
	
	private void buildTable(Source program){
		BufferedWriter output = null;
		try {
            output = new BufferedWriter(new FileWriter(new File("src/Data/table.txt")));	//開啟table.txt
            if(program.getSourceError())
            	output.write("\t*********格式錯誤*********\r\n");
            for(int i = 0; i < program.size(); i++){
            	if(program.getSourceError())		//若格式錯誤，一行都不印
    				break;
    			if(program.getObject(i) == "FFFFFF")//若為虛擬指令，不印Object code
    				output.write(String.format("%04X %-8s %-6s %-18s      \t%-31s\r\n", program.getLocation(i), program.getLabel(i), program.getOperation(i), program.getOperand(i), program.getComment(i)));
    			else if(program.getX(i))			//若有X，印出Object,X
    				output.write(String.format("%04X %-8s %-6s %-18s %-6s\t%-31s\r\n", program.getLocation(i), program.getLabel(i), program.getOperation(i), program.getOperand(i) + ",X", program.getObject(i), program.getComment(i)));
    			else								//其餘的完整印出
    				output.write(String.format("%04X %-8s %-6s %-18s %-6s\t%-31s\r\n", program.getLocation(i), program.getLabel(i), program.getOperation(i), program.getOperand(i), program.getObject(i), program.getComment(i)));
    			
    			if(program.getOperationError(i))	//如果OP code被標註Error，印出提示訊息
    				output.write("\t*********未定義的operation*********\r\n");
    			if(program.getOperandError(i))		//如果標籤欄位被標註Error，印出提示訊息
    				output.write("\t**********未定義的operand**********\r\n");
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
          if ( output != null ) {
            try {
				output.close();			//關檔
			} catch (IOException e) {
				e.printStackTrace();
			}
          }
        }
		System.out.println("table.txt Create Successful");
	}
	
	private void buildCard(Card card){
		BufferedWriter output = null;
		try {
            output = new BufferedWriter(new FileWriter(new File("src/Data/card.txt")));	//開啟card.txt檔
            output.write(String.format("H " + card.getH() + "\r\n"));		//印出H卡片
    		for(int i = 0; i < card.SizeOfT(); i++)							//印出T卡片
    			output.write(String.format("T "	 + card.getT(i) + "\r\n"));
    		output.write(String.format("E " + card.getE() + "\r\n"));		//印出E卡片
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
          if ( output != null ) {
            try {
				output.close();												//關檔
			} catch (IOException e) {
				e.printStackTrace();
			}
          }
        }
		System.out.println("card.txt Create Successful");
	}
}
