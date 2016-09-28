package ide.main.graphics;

public class ButtonList {

	public SpriteList sList = new SpriteList();
	public DropDownMenuList dList = new DropDownMenuList();

	public Button button1(){
		Button button = new Button(81, 20, 0, 0, 0xffaaaaaa, sList.objects(), dList.button1Menu());
		return button;
	}
	
	public Button button2(){
		Button button = new Button(20, 20, 0, 0, 0xffaaaaaa, sList.button1(), new DisplayBox(0, 0, 0, 0, 0, null));
		return button;
	}
	public Button sprite(){
		Button button = new Button(20, 20, 0, 0, 0xffaaaaaa, sList.sprite(), new DisplayBox(0, 0, 0, 0, 0, null));
		return button;
	}
	
	public Button tool(){
		Button button = new Button(54, 20, 0, 0, 0xffaaaaaa, sList.tool(), dList.toolMenu());
		return button;
	}
	
	public Button button3(){
		Button button = new Button(63, 20, 0, 0, 0xffaaaaaa, sList.level(), dList.button2Menu());
		return button;
	}
	
	public Button button4(){
		Button button = new Button(54, 20, 0, 0, 0xffaaaaaa, sList.game(), dList.button3Menu());
		return button;
	}
	
}
