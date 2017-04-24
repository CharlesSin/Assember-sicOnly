import java.util.Vector;

public class Card {
	private String H = new String();
	private Vector<String> T = new Vector<String>();
	private String E = new String();
	Card(Source source){
		super();
		int count = 0, location = source.getLocation(0);
		String content = new String();
		String a,b,c;
		a = source.getLabel(0);
		b = String.format("%06X", source.getLocation(0));
		c = String.format("%06X", source.getLocation(source.getCount()-1) - source.getLocation(0));
		H = a + " " + b + " " + c;
		//H = source.getLabel(0) + " " + String.format("%06s", source.getOperation(0)) + " " + String.format("%06d", source.getLocation(source.getCount()-1) - source.getLocation(0) + 1);
		for(int i = 0; i < source.getCount()-1; i++){
			if(count +  source.getLocation(i+1) - source.getLocation(i) > 30 || source.getObject(i) == "FFFFFF"){
				if(content.equals("")){
					location = source.getLocation(i+1);
					continue;
				}
				content = String.format("%06X ", location) + String.format("%02X", count) + content;
				T.add(content);
				count = 0;	content = new String();
				if(source.getObject(i) == "FFFFFF"){
					location = source.getLocation(i+1);
					continue;
				}
				location = source.getLocation(i);
			}
			content += String.format(" %s", source.getObject(i));
			count += source.getLocation(i+1) - source.getLocation(i);
		}
		if(!content.equals("")){
			content = String.format("%06X ", location) + String.format("%02X", count) + content;
			T.add(content);
		}
		E = String.format("%06X", source.getLocation(source.findlabel(source.getOperand(source.getCount()-1))));
	}
	
	public int SizeOfT(){
		return T.size();
	}
	
	public String getH() {
		return H;
	}
	
	public String getT(int index) {
		return T.get(index);
	}
	
	public String getE() {
		return E;
	}
}
