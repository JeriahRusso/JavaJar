package ide.main.graphics;

public class ToolBar extends Render{

	public int colour = 0xffaaaaaa;
	public int xPos, yPos, orientation;
	
	public Button[] icons;
	public int activeButton = 0;
	//Collections of buttons at set positions for ease of use
	public ToolBar(int xPos, int yPos, int orientation, Button[] icons) {
		super(icons[0].width * icons.length + 3 * icons.length, icons[0].height);
		this.icons = icons;
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
	public ToolBar(int width, int height, int xPos, int yPos, int orientation, Button[] icons) {
		super(width, height);
		this.icons = icons;
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
	public void tick(){
		for(int i = 0; i < icons.length; i++){
			if(icons[i].menu != null){
				icons[i].checkMenuActivation();
				icons[i].tick();
			}
			else{
				icons[i].checkMenuActivation();
				if(icons[i].active && i != activeButton){
					icons[activeButton].active = false;
					activeButton = i;
				}
			}
		}
	}
	
	public void render(){
		for(int i = 0; i < icons.length; i++){
			icons[i].render();
		}
		
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = colour;
		}
		
		//Sets the button positions so that they always have a 3 pixel gap between them
		if(orientation == 0){
			for(int i = 0; i < icons.length; i++){
				if(i > 0){
					draw(icons[i], ((icons[i - 1].xPos - xPos) + icons[i - 1].width) + i * 3, 0);
					icons[i].setPosition((icons[i - 1].xPos + icons[i - 1].width) + i * 3, yPos);
				}
				else{
					draw(icons[i], 0, 0);
					icons[i].setPosition(xPos, yPos);
				}
			}
		}
		else if(orientation == 1){
			for(int i = 0; i < icons.length; i++){
				for(int x = 0; x < icons[i].width; x++){
					int xx = xPos;
					for(int y = 0; y < icons[i].height; y++){
						int yy = (3 * i) + (i * icons[i].height) + y;
						pixels[xx + yy * width] = icons[i].pixels[x + y * icons[i].width];
					}
				}
			}
		}
	}

}
