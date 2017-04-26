import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Output {
	private String line = null;
	Output(Source p, Card c){
		buildTable(p);
		buildCard(c);
	}
	
	private void buildTable(Source program){
		BufferedWriter output = null;
		try {
            output = new BufferedWriter(new FileWriter(new File("table.txt")));
            for(int i = 0; i < program.size(); i++){
            	if(program.getSourceError()){
            		output.write("\t*********格式錯誤*********\r\n");
    				break;
    			}
    			if(program.getObject(i) == "FFFFFF")
    				line = String.format("%04X %-8s %-6s %-18s      \t%-31s\r\n", program.getLocation(i), program.getLabel(i), program.getOperation(i), program.getOperand(i), program.getComment(i));
    			else if(program.getX(i))
    				line = String.format("%04X %-8s %-6s %-18s %-6s\t%-31s\r\n", program.getLocation(i), program.getLabel(i), program.getOperation(i), program.getOperand(i) + ",X", program.getObject(i), program.getComment(i));
    			else
    				line = String.format("%04X %-8s %-6s %-18s %-6s\t%-31s\r\n", program.getLocation(i), program.getLabel(i), program.getOperation(i), program.getOperand(i), program.getObject(i), program.getComment(i));
    			output.write(line);
    			
    			if(program.getOperationError(i))
    				output.write("\t*********未定義的OP Code*********\r\n");
    			if(program.getOperandError(i))
    				output.write("\t*********未定義的標籤名稱********\r\n");
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
          if ( output != null ) {
            try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
          }
        }
	}
	
	private void buildCard(Card card){
		BufferedWriter output = null;
		try {
            output = new BufferedWriter(new FileWriter(new File("card.txt")));
            output.write(String.format("H " + card.getH() + "\r\n"));
    		for(int i = 0; i < card.SizeOfT(); i++)
    			output.write(String.format("T " + card.getT(i) + "\r\n"));
    		output.write(String.format("E " + card.getE() + "\r\n"));
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
          if ( output != null ) {
            try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
          }
        }
	}
}
