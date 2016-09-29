package ide.main.graphics;

import engine.main.render.sprites.SpriteSheet;
import ide.main.Environment;
import ide.main.data.LevelData;
import ide.main.graphics.TileData.Tile;

public class LevelDisplayZone extends Render{
	//Need to edit this class and a few related to allow for scaling of the display zone
	public int[] tileIds;
	public int[] spriteIds;
	public int[] tilePixels;
	public int xScroll, yScroll;
	public int tilesAcross, tilesDown;
	public int levelWidth, levelHeight;
	public int xPos, yPos;
	public int movementSpeed;
	
	public Tile[] tileData;
	public Sprite[] spriteData;
	
	//This is the editable area within the IDE that the user draws their level into
	public LevelDisplayZone(int width, int height, int xPos, int yPos) {
		super(width, height);
		SpriteSheet level = new SpriteSheet(Environment.resLocation + "levels/" + LevelData.levelName + "/" + LevelData.levelName + ".png");
		levelWidth = level.width * TileData.tileSize;
		levelHeight = level.height * TileData.tileSize;
		movementSpeed = TileData.tileSize / 10;
		tilePixels = new int[levelWidth * levelHeight];
		this.xPos = xPos;
		this.yPos = yPos;
		tilesAcross = levelWidth / TileData.tileSize;
		tilesDown = levelHeight / TileData.tileSize;
		tileIds = new int[tilesAcross * tilesDown];
		spriteIds = new int[tilesAcross * tilesDown];
		tileData = new Tile[tilesAcross * tilesDown];
		spriteData = new Sprite[tilesAcross * tilesDown];
		xScroll = 0;
		yScroll = 0;
		loadTileData();
		loadSpriteData();
		for(int i = 0; i < tileData.length; i++){
			tileData[i] = getTileData(tileIds[i]);
		}
		for(int z = 0; z < spriteData.length; z++){
			spriteData[z] = getSpriteData(spriteIds[z]);
		}
		updatePixelData();
	}
	
	public void tick(){
		if(Environment.input.up.getPressed() && yScroll >= movementSpeed){
			yScroll -= movementSpeed;
		}
		if(Environment.input.down.getPressed() && yScroll <=  levelHeight - height - movementSpeed){
			yScroll += movementSpeed;
		}
		if(Environment.input.right.getPressed() && xScroll <= levelWidth - width - movementSpeed){
			xScroll += movementSpeed;
		}
		if(Environment.input.left.getPressed() && xScroll >= movementSpeed){
			xScroll -= movementSpeed;
		}
	}
	
	public void render(){
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = 0;
		}
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				pixels[x + y * width] = tilePixels[(x + xScroll) + (y + yScroll) * levelWidth];
			}
		}
	}
	
	//gets the pixel data of tiles depended on the ids stored from loading the level
	private Tile getTileData(int tileId){
		for(int i = 0; i < TileData.tiles.size(); i++){
			if(TileData.tiles.get(i).id == tileId){
				return TileData.tiles.get(i);
			}
		}
		for(int i = 0; i < TileData.tiles.size(); i++){
			if(TileData.tiles.get(i).id == -1){
				return TileData.tiles.get(i);
			}
		}
		System.err.println("Void tile and requested tile both missing!");
		System.exit(1);
		return null;
	}
	
	//gets the pixel data of sprites depended on the ids stored from loading the level
	private Sprite getSpriteData(int spriteId){
		for(int i = 0; i < Sprite.sprites.size(); i++){
			if(Sprite.sprites.get(i).id == spriteId){
				return Sprite.sprites.get(i);
			}
		}
		for(int i = 0; i < Sprite.sprites.size(); i++){
			if(Sprite.sprites.get(i).id == -1){
				return Sprite.sprites.get(i);
			}
		}
		System.err.println("Void sprite and requested sprite both missing!");
		System.exit(1);
		return null;
	}
	
	//determines a tile id from the pixel colour in the level and stores it
	private void loadTileData(){
		SpriteSheet level = new SpriteSheet(Environment.resLocation + "levels/" + LevelData.levelName + "/" + LevelData.levelName + ".png");
		for(int i = 0; i < level.pixels.length; i++){
			tileIds[i] = TileData.tileColourToId(level.pixels[i]);
		}
	}
	
	//determines a sprite id from the pixel colour in the level and stores it
	private void loadSpriteData(){
		SpriteSheet level = new SpriteSheet(Environment.resLocation + "levels/" + LevelData.levelName + "/" + LevelData.levelName + ".png");
		for(int i = 0; i < level.pixels.length; i++){
			spriteIds[i] = Sprite.spriteColourToId(level.pixels[i]);
		}
	}
	
	//updates all pixel data for the entire level being displayed (used majorly on startup)
	public void updatePixelData(){
		int[] tempPixels;
		int[] tempSpritePixels;
		for(int x = 0; x < tilesAcross; x++){
			for(int y = 0; y < tilesDown; y++){
				tempPixels = tileData[x + y * tilesAcross].pixels;
				tempSpritePixels = spriteData[x + y * tilesAcross].pixels;
				for(int xx = 0; xx < TileData.tileSize; xx++){
					for(int yy = 0; yy < TileData.tileSize; yy++){
						tilePixels[(xx + (x * TileData.tileSize)) + (yy + (y * TileData.tileSize)) * levelWidth] = tempPixels[xx + yy * TileData.tileSize];
						if(tempSpritePixels[xx + yy * TileData.tileSize] != 0xffff00ff && tempSpritePixels[xx + yy * TileData.tileSize] != 0xff7f007f){
							tilePixels[(xx + (x * TileData.tileSize)) + (yy + (y * TileData.tileSize)) * levelWidth] = tempSpritePixels[xx + yy * TileData.tileSize];
						}
					}
				}
			}
		}
	}
	
	//Updates a single tile being displayed in the IDE due to the user editing it
	public void updateTilePixels(int xTile, int yTile, int[] pixels){
		xTile = xTile * TileData.tileSize + ((xScroll / TileData.tileSize) * TileData.tileSize);
		yTile = yTile * TileData.tileSize + ((yScroll / TileData.tileSize) * TileData.tileSize);
		for(int x = 0; x < TileData.tileSize; x++){
			for(int y = 0; y < TileData.tileSize; y++){
				tilePixels[(x + xTile) + (y + yTile) * levelWidth] = pixels[x + y * TileData.tileSize];
			}
		}
	}
	
	//Updates a single sprite being displated in the IDE due to the user editing it
	public void updateSpritePixels(int xTile, int yTile, int[] pixels){
		xTile = xTile * TileData.tileSize + ((xScroll / TileData.tileSize) * TileData.tileSize);
		yTile = yTile * TileData.tileSize + ((yScroll / TileData.tileSize) * TileData.tileSize);
		for(int x = 0; x < TileData.tileSize; x++){
			for(int y = 0; y < TileData.tileSize; y++){
				if(pixels[x + y * TileData.tileSize] != 0xffff00ff && pixels[x + y * TileData.tileSize] != 0xff7f007f){
					tilePixels[(x + xTile) + (y + yTile) * levelWidth] = pixels[x + y * TileData.tileSize];
				}
			}
		}
	}
}
