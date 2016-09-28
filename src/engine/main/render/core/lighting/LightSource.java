package engine.main.render.core.lighting;

import engine.main.render.Render;
import engine.main.render.sprites.SpriteSheet;
import engine.main.render.sprites.entities.AnimatedSprite;

public abstract class LightSource extends AnimatedSprite{
	
	public int radius;
	
	public boolean glowing;
	
	public LightSource(int id, int width, int height, int xPos, int yPos, int sheetX, int sheetY, int radius, boolean visible, boolean glowing, boolean collidable, SpriteSheet sheet, int animationWidth, int animationHeight, int identifier, int greenData) {
		super(id, width, height, xPos, yPos, sheetX, sheetY, visible, collidable, sheet, animationWidth, animationHeight, identifier, greenData);
		this.radius = radius;
		this.glowing = glowing;
	}
	
	public abstract Render[] lightRender();
	
	public int getRadius(){
		return radius;
	}
}
