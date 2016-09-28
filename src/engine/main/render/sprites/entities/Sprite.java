package engine.main.render.sprites.entities;

import java.util.ArrayList;
import java.util.List;

import engine.main.render.Render;
import engine.main.render.TileData;
import engine.main.render.core.lighting.AreaLight;
import engine.main.render.core.lighting.DirectionalLight;
import engine.main.render.sprites.SheetList;
import engine.main.render.sprites.SpriteSheet;

public abstract class Sprite extends Render{
	//The base abstract class of all sprites within the game
	public int id;
	public int xPos;
	public int yPos;
	public int sheetX;
	public int sheetY;
	public int identifier;
	public int greenData;
	
	public boolean collidable;
	public boolean visible;
	
	public SpriteSheet sheet;
	
	public static SheetList sheetList = new SheetList();
	
	public static Player player = null;
	
	public static AnimatedSprite nullSprite = null;
	public static AnimatedSprite barrier = null;
	
	//These lists hold all the sprite data required to run the game
	public static List<AnimatedSprite> animSprites = new ArrayList<AnimatedSprite>();
	public static List<AreaLight> areaLights = new ArrayList<AreaLight>();
	public static List<DirectionalLight> dirLights = new ArrayList<DirectionalLight>();
	public static List<Mob> mobs = new ArrayList<Mob>();
	
	public static Sprite[] sprites = new Sprite[]{};
	
	public Sprite(int id, int width, int height, int xPos, int yPos, int sheetX, int sheetY, boolean visible, boolean collidable, SpriteSheet sheet, int identifier, int greenData){
		super(width, height);
		this.id = id;
		this.xPos = xPos;
		this.yPos = yPos;
		this.sheetX = sheetX;
		this.sheetY = sheetY;
		this.sheet = sheet;
		this.visible = visible;
		this.identifier = identifier;
		this.collidable = collidable;
		this.greenData = greenData;
	}
	
	public void render(){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				pixels[x + y * width] = sheet.pixels[(sheetX * TileData.tileSize + x) + (sheetY * TileData.tileSize + y) * sheet.width];
			}
		}
	}
	
	public int getId(){
		return id;
	}
	
}
