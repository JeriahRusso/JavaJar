package ide.main.graphics;

import ide.main.Environment;

public class Button extends Render{
	
	public int xPos, yPos, colour, selectionColour;
	public int[] alphaColours = new int[]{0xffff00ff, 0xff7f007f};
	
	public boolean active = false;
	
	public Sprite icon;
	public DropDownMenu menu;
	public DisplayBox box;
	
	//The two different instances of Button are required as it is possible to have them toggle a displaybox or activate a 
	//dropdownmenu and so both were included
	public Button(int width, int height, int xPos, int yPos, int colour, Sprite icon, DropDownMenu menu) {
		super(width, height);
		this.xPos = xPos;
		this.yPos = yPos;
		this.icon = icon;
		this.colour = colour;
		this.selectionColour = 0xff111111;
		this.menu = menu;
		this.box = null;
		menu.xPos = xPos;
		menu.yPos = yPos + height;
	}
	
	public Button(int width, int height, int xPos, int yPos, int colour, Sprite icon, DisplayBox box) {
		super(width, height);
		this.xPos = xPos;
		this.yPos = yPos;
		this.icon = icon;
		this.colour = colour;
		this.selectionColour = 0xff111111;
		this.menu = null;
		this.box = box;
	}
	
	public void tick(){
		if(menu != null){
			if(menu.active){
				menu.tick();
			}
		}
	}
	
	public void render(){
		icon.render();
		if(menu != null){
			if(menu.active){
				menu.render();
			}
		}
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = colour;
		}
		draw(icon, 0, 0, alphaColours);
		if(selected()){
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					if(x == 0 || x == width - 1 || y == 0 || y == height - 1){
						pixels[x + y * width] = selectionColour;
					}
				}
			}
		}
	}
	
	//Returns true if the cursor is hovered over the button
	public boolean selected(){
		if(xPos < Environment.input.getMouseX() && Environment.input.getMouseX() < xPos + width && yPos < Environment.input.getMouseY() && Environment.input.getMouseY() < yPos + height){
			return true;
		}
		return false;
	}
	
	//changes the x, y position of the button relative to the screen, required due to placement being done relative to toolbars and the like
	public void setPosition(int xPos, int yPos){
		this.xPos = xPos;
		this.yPos = yPos;
		if(menu != null){
			this.menu.xPos = xPos;
			this.menu.yPos = yPos + height;
		}
	}
	
	//checks if a button should be in an active state or if it should put its menu into an active state
	//does so if the button has been clicked
	public void checkMenuActivation(){
		if(selected() && Environment.input.getMouseReleased() && menu != null){
			menu.active = true;
		}
		else if(selected() && Environment.input.getMouseReleased() && menu == null){
			active = true;
		}
	}
}
