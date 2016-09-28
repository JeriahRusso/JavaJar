package engine.main.render.sprites;

import java.util.ArrayList;
import java.util.List;

public class SheetList {
	//Holder for all the spritesheets being used by a project loaded into the engine
	public static List<SpriteSheet> sheets = new ArrayList<SpriteSheet>();
	
	public static SpriteSheet getSheet(String name){
		for(int i = 0; i < sheets.size(); i++){
			String[] temp = new String[sheets.get(i).path.split("/").length];
			for(int z = 0; z < temp.length; z++){
				temp[z] = sheets.get(i).path.split("/")[z];
			}
			if(temp[temp.length - 1].equals(name)){
				return sheets.get(i);
			}
		}
		return null;
	}
}
