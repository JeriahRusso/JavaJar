package engine.main.render;

import engine.main.render.TileData.Tile;
import engine.main.render.sprites.entities.AnimatedSprite;

public class Rectangle {

	//This is relatively early render code allowing for drawing of coloured rectangles to the screen
	public static Render drawRect(int colour, int width, int height){
		Render rect = new Render(width, height);
		for(int i = 0; i < rect.pixels.length; i++){
			rect.pixels[i] = colour;
		}
		return rect;
	}
	
	public static Render drawTile(Tile tile){
		
		Render rect = new Render(TileData.tileSize * tile.getTileWidth(), TileData.tileSize * tile.getTileHeight());
		
		for(int i = 0; i < rect.pixels.length; i++){
			rect.pixels[i] = tile.pixels[i];
		}
		
		return rect;
	}
	
	public static Render drawSprite(AnimatedSprite sprite){
		Render rect = new Render(sprite.width, sprite.height);
		
		for(int i = 0; i < sprite.pixels.length; i++){
			rect.pixels[i] = sprite.pixels[i];
		}
		
		return rect;
	}
	
}
