package engine.main;

import engine.main.render.TileData.Tile;
import engine.main.render.core.lighting.AreaLight;
import engine.main.render.core.lighting.DirectionalLight;
import engine.main.render.sprites.entities.AnimatedSprite;
import engine.main.render.sprites.entities.Player;
import engine.main.render.sprites.entities.Sprite;

public class Clone {

	//This class was required due to the need to be able to duplicate resources such as tiles and sprites
	//Without this only one instance of them is created and so only one of them is ever drawn to screen
	public static CloneData clone(Sprite sprite){
		CloneData values = new CloneData();
		if(sprite instanceof AreaLight){
			values.areaLight = areaClone((AreaLight) sprite);
			return values;
		}
		else if(sprite instanceof Player){
			values.player = playerClone((Player) sprite);
			return values;
		}
		else if(sprite instanceof DirectionalLight){
			values.directionLight = directionClone((DirectionalLight) sprite);
			return values;
		}
		else if(sprite instanceof AnimatedSprite){
			values.animSprite = animClone((AnimatedSprite) sprite);
			return values;
		}
		return null;
	}
	
	public static Tile clone(Tile tile){
		Tile tile2 = new Tile(tile.id, tile.sheetX, tile.sheetY, tile.tileWidth, tile.tileHeight, tile.sheet, tile.isCollidable, tile.identifier, tile.greenData);
		return tile2;
	}
	
	private static AreaLight areaClone(AreaLight original){
		return new AreaLight(original.id, original.width, original.height, original.xPos, original.yPos, original.sheetX, original.sheetY, original.radius, original.visible, original.glowing, original.collidable, original.sheet, original.animationWidth, original.animationHeight, original.identifier, original.greenData);
	}
	
	private static AnimatedSprite animClone(AnimatedSprite original){
		return new AnimatedSprite(original.id, original.width, original.height, original.xPos, original.yPos, original.sheetX, original.sheetY, original.visible, original.collidable, original.sheet, original.animationWidth, original.animationHeight, original.identifier, original.greenData);
	}
	
	private static Player playerClone(Player original){
		return new Player(original.xPos, original.yPos, original.width, original.height, original.sheetX, original.sheetY, original.damage, original.health, original.speed, original.animationWidth, original.animationHeight, original.identifier, original.sheet);
	}
	
	private static DirectionalLight directionClone(DirectionalLight original){
		return new DirectionalLight(original.id, original.width, original.height, original.xPos, original.yPos, original.sheetX, original.sheetY, original.length, original.rotation, original.visible, original.glowing, original.collidable, original.sheet, original.animationWidth, original.animationHeight, original.identifier, original.greenData);
	}
	
}
