package engine.main.render.sprites.entities;

import engine.main.Timer;
import engine.main.render.TileData;
import engine.main.render.sprites.SpriteSheet;

public class AnimatedSprite extends Sprite{
	//This class is a simple extension of the base Sprite class that allows for an animation to play out over time
	public int animX;
	public int animY;
	public int animationWidth;
	public int animationHeight;
	
	private int lastAnimTick;
	private int animSpeed;
	
	public AnimatedSprite(int id, int width, int height, int xPos, int yPos, int sheetX, int sheetY, boolean visible, boolean collidable, SpriteSheet sheet, int animationWidth, int animationHeight, int identifier, int greenData){
		super(id, width, height, xPos, yPos, sheetX, sheetY, visible, collidable, sheet, identifier, greenData);
		this.animX = 0;
		this.animY = 0;
		this.lastAnimTick = 0;
		this.animSpeed = 5;
		this.animationWidth = animationWidth;
		this.animationHeight = animationHeight;
	}
	
	public void Animate(){
		if(Timer.getTicks() - lastAnimTick >= 60 / animSpeed){
			lastAnimTick = Timer.getTicks();
			if(animX < animationWidth){
				animX++;
				return;
			}
			else if(animY < animationHeight){
				animX = 0;
				animY++;
				return;
			}
			else{
				animX = 0;
				animY = 0;
				return;
			}
		}
	}
	
	public void render(){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				pixels[x + y * width] = sheet.pixels[((sheetX + animX) * TileData.tileSize + x) + ((sheetY + animY) * TileData.tileSize + y) * sheet.width];
			}
		}
	}	
}
