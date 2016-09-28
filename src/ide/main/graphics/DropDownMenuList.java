package ide.main.graphics;

public class DropDownMenuList {

	public DropDownMenu button1Menu(){
		DropDownMenu menu = new DropDownMenu(new String[]{"Build Tile", "Import Sprite Sheet", "Build Sprite"}, 0xffaaaaaa, 0, 0, new int[]{4, 1, 2});
		return menu;
	}
	
	public DropDownMenu button2Menu(){
		DropDownMenu menu = new DropDownMenu(new String[]{"New Level", "Load Level", "Save Level"}, 0xffaaaaaa, 0, 0, new int[]{5, 3, 6});
		return menu;
	}
	
	public DropDownMenu button3Menu(){
		DropDownMenu menu = new DropDownMenu(new String[]{"Test Game"}, 0xffaaaaaa, 0, 0, new int[]{7});
		return menu;
	}
	
	public DropDownMenu toolMenu(){
		DropDownMenu menu = new DropDownMenu(new String[]{"Fill"}, 0xffaaaaaa, 0, 0, new int[]{8});
		return menu;
	}
	
	public DropDownMenu rightClick(){
		DropDownMenu menu = new DropDownMenu(new String[]{"Access Code"}, 0xffaaaaaa, 0, 0, new int[]{0});
		return menu;
	}
}
