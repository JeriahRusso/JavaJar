package ide.main.graphics;

import ide.main.Environment;
import ide.main.inputs.InputHandler;

public class DisplayBox extends Render{

	//DisplayBoxes hold sprites (and tiles however internally these are treated as sprites) and allows for their pixel data
	//to be displayed in an interactable fashion
	public int[] defaultAlpha = new int[]{0xffff00ff, 0xff7f007f};
	
	public int colour;
	public Sprite[] sprites;
	
	public boolean scrollableX, scrollableY;
	public boolean mouseActiveX = false;
	public boolean mouseActiveY = false;
	public boolean active = false;
	
	public int xScroll, yScroll, xPos, yPos;
	private int scrollBarSize =  10;
	private int tilesDown;
	
	private double xSliderSize, ySliderSize;
	private double spritesAcross;
	private double visibleYTiles;
	public double heightsDown;
	
	private int padding = 3;
	private int borderSize = 1;
	private int borderColour = 0xffff00;
	
	public DisplayBox(int xPos, int yPos, int width, int height, int backgroundColour, Sprite[] contentSprites) {
		super(width, height);
		spritesAcross = (double)(width) / (double)(TileData.tileSize);
		
		this.xPos = xPos;
		this.yPos = yPos;
		this.colour = backgroundColour;
		this.sprites = contentSprites;
		this.xScroll = 0;
		this.yScroll = 0;
		//These calculations are all required to determine sizing of the display box and if it should include scrollbars or not
		if(contentSprites != null){		
			if((width / TileData.tileSize) % spritesAcross != 0){
				tilesDown = (int)(contentSprites.length / spritesAcross);
			}
			else{
				tilesDown = (int)(contentSprites.length / spritesAcross) + 1;
			}
			
			heightsDown =  ((double)tilesDown * (double)TileData.tileSize) / (double)height;
			if(height / TileData.tileSize < (int)((double)sprites.length / spritesAcross))
				scrollableY = true;
			if(width % TileData.tileSize > 0)
				scrollableX = true;
			visibleYTiles = (double)height / (double)TileData.tileSize;
			xSliderSize = (int)(width / spritesAcross);
			ySliderSize = (int)(height / (tilesDown / visibleYTiles));
		}
	}
	
	public void render(){
		if(sprites != null && active){
			for(int i = 0; i < pixels.length; i++){
				pixels[i] = colour;
			}
			int row = 0;
			int across = 0;
			int spritesAcrossInt = (int)spritesAcross;
			
			if((width / TileData.tileSize) % spritesAcross != 0){
				spritesAcrossInt++;
			}
			
			for(int i = 0; i < sprites.length; i++){
				sprites[i].xPos = across * (TileData.tileSize + padding);
				sprites[i].yPos = row * (TileData.tileSize + padding);
				across++;
				if(across == spritesAcrossInt){
					across = 0;
					row++;
				}
				if((sprites[i].xPos + sprites[i].width >= xScroll && sprites[i].xPos <= width + xScroll) && (sprites[i].yPos + sprites[i].height >= yScroll * heightsDown && sprites[i].yPos <= height + yScroll * heightsDown)){
					sprites[i].render();
					if(sprites[i].isHovered()){
						for(int x = 0; x < sprites[i].width; x++){
							for(int y = 0; y < sprites[i].height; y++){
								if(x < borderSize || x >= sprites[i].width - borderSize || y < borderSize || y >= sprites[i].height - borderSize){
									sprites[i].pixels[x + y * sprites[i].width] = borderColour;
								}
							}
						}
					}
					drawScaleAlpha(sprites[i], sprites[i].xPos - xScroll, (int)(sprites[i].yPos - yScroll * heightsDown), Environment.scale, defaultAlpha);
				}
			}
			
			if(scrollableX){
				for(int y = height - scrollBarSize; y < height; y++){
					for(int x = 0; x < width; x++){
						if(x >= xScroll && x <= xScroll + xSliderSize){
							pixels[x + y * width] = 0xffaaaaaa;
						}
						else{
							pixels[x + y * width] = 0xffcccccc;
						}
					}
				}
			}
			if(scrollableY){
				for(int y = 0; y < height; y++){
					for(int x = width - scrollBarSize; x < width; x++){
						if(y >= yScroll && y <= yScroll + ySliderSize){
							pixels[x + y * width] = 0xffaaaaaa;
						}
						else{
							pixels[x + y * width] = 0xffcccccc;
						}
					}
				}
			}
		}
	}
	
	public void tick(){
		if(active){
			if(sprites != null && active){
				scrollDisplayBox();
				for(int i = 0; i < sprites.length; i++){
					sprites[i].tick(Environment.mouseOnComponent(xPos + (sprites[i].xPos * Environment.scale) - xScroll, yPos + (int)((sprites[i].yPos * Environment.scale) - yScroll * heightsDown), sprites[i].width * Environment.scale, sprites[i].height * Environment.scale));
				}
			}
		}
	}
	
	//This function runs the scrollbars and edits the x, y position of the sprites within so that they display correctly during and after a scroll
	//TODO: Take into account scaling and padding
	public void scrollDisplayBox(){
		if(scrollableX){
			if((Environment.input.getMouseX() >= xPos && Environment.input.getMouseX() <= xPos + width) && (Environment.input.getMouseY() >= yPos + (height - scrollBarSize) && Environment.input.getMouseY() <= yPos + height)){
				if(Environment.input.getMouseReleased()){
					int relativeMouseX = Environment.input.getMouseX() - xPos;
					xScroll = (int)(relativeMouseX - (xSliderSize / 2));
					if(xScroll < 0){
						xScroll = 0;
					}
					else if(xScroll > width - xSliderSize){
						xScroll = (int)(width - xSliderSize);
					}
				}
				else if(InputHandler.mousePressed){
					mouseActiveX = true;
					int relativeMouseX = Environment.input.getMouseX() - xPos;
					xScroll = (int)(relativeMouseX - (xSliderSize / 2));
					if(xScroll < 0){
						xScroll = 0;
					}
					else if(xScroll > width - xSliderSize){
						xScroll = (int)(width - xSliderSize);
					}
				}
			}
			if(mouseActiveX){
				if(Environment.input.getMouseReleased()){
					mouseActiveX = false;
					return;
				}
				int relativeMouseX = Environment.input.getMouseX() - xPos;
				xScroll = (int)(relativeMouseX - (xSliderSize / 2));
				if(xScroll < 0){
					xScroll = 0;
				}
				else if(xScroll > width - xSliderSize){
					xScroll = (int)(width - xSliderSize);
				}
			}
		}
		
		if(scrollableY){
			if((Environment.input.getMouseY() >= yPos && Environment.input.getMouseY() <= yPos + height) && (Environment.input.getMouseX() >= xPos + width - scrollBarSize && Environment.input.getMouseX() <= xPos + width)){
				if(Environment.input.getMouseReleased()){
					int relativeMouseY = Environment.input.getMouseY() - yPos;
					yScroll = (int)(relativeMouseY - (ySliderSize / 2));
					if(yScroll < 0){
						yScroll = 0;
					}
					else if(yScroll > height - ySliderSize){
						yScroll = (int)(height - ySliderSize);
					}
				}
				else if(InputHandler.mousePressed){
					mouseActiveY = true;
					int relativeMouseY = Environment.input.getMouseY() - yPos;
					yScroll = (int)(relativeMouseY - (ySliderSize / 2));
					if(yScroll < 0){
						yScroll = 0;
					}
					else if(yScroll > height - ySliderSize){
						yScroll = (int)(height - ySliderSize);
					}
				}
			}
			if(mouseActiveY){
				if(Environment.input.getMouseReleased()){
					mouseActiveY = false;
					return;
				}
				int relativeMouseY = Environment.input.getMouseY() - yPos;
				yScroll = (int)(relativeMouseY - ySliderSize);
				if(yScroll < 0){
					yScroll = 0;
				}
				else if(yScroll > height - ySliderSize){
					yScroll = (int)(height - ySliderSize);
				}
			}
		}
	}
}