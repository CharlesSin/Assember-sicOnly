import java.util.Vector;

public class Card {
	private String H = new String();
	private Vector<String> T = new Vector<String>();
	private String E = new String();
	Card(Source source){
		super();
		if(source.getSourceError())	return;					//若有嚴重格式錯誤，不產生卡片
		int count = 0;										//卡片長度計數器
		int location = source.getLocation(0);				//卡片起始位址，初始化為START位址
		String content = new String();						//卡片內容緩衝區
		//產生H卡片
		String programName,programStart,programLength;
		programName = source.getLabel(0);
		programStart = String.format(" %06X", source.getLocation(0));
		programLength = String.format(" %06X", source.getLocation(source.size()-1) - source.getLocation(0));
		H = programName + programStart + programLength;
		//產生T卡片
		for(int i = 0; i < source.size()-1; i++){
			//判斷是否需要換卡片  【加上此段object code會大於30Bytes】 或   【此行object code為虛擬指令】
			if(count +  (source.getObject(i).length() / 2) > 30 || source.getObject(i) == "FFFFFF"){
				if(content.equals("")){	//若沒有T卡片內容尚未輸出，直接讀取下一行object code
					location = source.getLocation(i+1);
					continue;
				}
				content = String.format("%06X %02X ", location, count) + content;	//將緩衝區內容前面加上卡片起始位址、卡片長度
				T.add(content);										//將緩衝區輸出到T卡片向量
				count = 0;	content = new String();					//初始化計數器與緩衝區
				if(source.getObject(i) == "FFFFFF"){
					location = source.getLocation(i+1);				//若此行為虛擬指令，卡片起始位址設為下一行後直接讀取下一行
					continue;
				}
				location = source.getLocation(i);					//將卡片起始位址設為此行位址
			}
			content += String.format(" %s", source.getObject(i));	//將此行object code加進緩衝區
			count += source.getObject(i).length() / 2;				//卡片長度設為object code長度除二 (2字1Byte)
		}
		if(!content.equals("")){	//產生最一張T卡片
			content = String.format("%06X ", location) + String.format("%02X", count) + content;
			T.add(content);
		}
		//產生E卡片
		E = String.format("%06X", source.getLocation(source.findLabel(source.getOperand(source.size()-1))));
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
