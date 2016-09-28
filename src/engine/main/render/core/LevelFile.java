package engine.main.render.core;

import java.awt.Color;

import engine.main.Clone;
import engine.main.CloneData;
import engine.main.render.TileData;
import engine.main.render.TileData.Tile;
import engine.main.render.sprites.SpriteSheet;
import engine.main.render.sprites.entities.Sprite;

public class LevelFile {

	//This class holds the data for each individual level for loading
	public Tile[] tileId;
	public Sprite[] spriteId;
	public int id;
	public int width;
	public int height;
	
	private SpriteSheet sheet;
	
	public LevelFile(String path, int id){
		sheet = new SpriteSheet(path);
		this.id = id;
		this.tileId = new Tile[sheet.width * sheet.height];
		this.spriteId = new Sprite[sheet.width * sheet.height];
		this.width = sheet.width;
		this.height = sheet.height;
		generateTileId();
		generateSpriteId();
	}
	
	//Generates a tile to be held based off of the blue channel on the levels spritesheet
	private void generateTileId(){
		Tile[] tiles = TileData.tiles;
		for(int x = 0; x < sheet.width; x++){
			for(int y = 0; y < sheet.height; y++){
				for(int i = 0; i < tiles.length; i++){
					if(tileIdentifier(sheet.pixels[x + y * sheet.width]) == tiles[i].identifier){
						tileId[x + y * sheet.width] = Clone.clone(tiles[i]);
						break;
					}
				}
			}
		}
	}
	
	//Generates sprites to be stored based on the red channel in the levels spritesheet
	public void generateSpriteId(){
		for(int y = 0; y < sheet.height; y++){
			for(int x = 0; x < sheet.width; x++){
				for(int i = 0; i < Sprite.sprites.length; i++){
					if(Sprite.sprites[i].identifier == spriteIdentifier(sheet.pixels[x + y * sheet.width])){
						CloneData temp = new CloneData();
						temp = Clone.clone(Sprite.sprites[i]);
						if(temp.player != null){
							temp.player.xPos = x * TileData.tileSize;
							temp.player.yPos = y * TileData.tileSize;
							spriteId[x + y * sheet.width] = temp.player;
							break;
						}
						else if(temp.animSprite != null){
							if(spriteId[x + y * sheet.width] == null){
								temp.animSprite.xPos = x * TileData.tileSize;
								temp.animSprite.yPos = y * TileData.tileSize;
								temp.animSprite.greenData = greenChannel(sheet.pixels[x + y * sheet.width]);
								spriteId[x + y * sheet.width] = temp.animSprite;
								if(temp.animSprite.width > 1 || temp.animSprite.height > 1){
									for(int xx = x; xx < x + (temp.animSprite.width / TileData.tileSize); xx++){
										for(int yy = y; yy < y + (temp.animSprite.height / TileData.tileSize); yy++){
											if(spriteId[xx + yy * sheet.width] == null){
												CloneData data = Clone.clone(Sprite.barrier);
												data.animSprite.xPos = xx * TileData.tileSize;
												data.animSprite.yPos = yy * TileData.tileSize;
												data.animSprite.greenData = greenChannel(sheet.pixels[x + y * sheet.width]);
												spriteId[xx + yy * sheet.width] = data.animSprite;
											}
										}
									}
								}
							}
							break;
						}
						else if(temp.areaLight != null){
							if(spriteId[x + y * sheet.width] == null){
								temp.areaLight.xPos = x * TileData.tileSize;
								temp.areaLight.yPos = y * TileData.tileSize;
								temp.areaLight.greenData = greenChannel(sheet.pixels[x + y * sheet.width]);
								spriteId[x + y * sheet.width] = temp.areaLight;
								if(temp.areaLight.width > 1 || temp.areaLight.height > 1){
									for(int xx = x; xx < x + (temp.areaLight.width / TileData.tileSize); xx++){
										for(int yy = y; yy < y + (temp.areaLight.height / TileData.tileSize); yy++){
											if(spriteId[xx + yy * sheet.width] == null){
												CloneData data = Clone.clone(Sprite.barrier);
												data.animSprite.xPos = xx * TileData.tileSize;
												data.animSprite.yPos = yy * TileData.tileSize;
												data.animSprite.greenData = greenChannel(sheet.pixels[x + y * sheet.width]);
												spriteId[xx + yy * sheet.width] = data.animSprite;
											}
										}
									}
								}
							}
							break;
						}
						else if(temp.directionLight != null){
							if(spriteId[x + y * sheet.width] == null){
								temp.directionLight.xPos = x * TileData.tileSize;
								temp.directionLight.yPos = y * TileData.tileSize;
								spriteId[x + y * sheet.width] = temp.directionLight;
							}
							break;
						}
					}
				}
			}
		}
	}
	
	public int tileIdentifier(int pixelColour){
		Color c = new Color(pixelColour, false);
		return c.getBlue();
	}
	
	public int spriteIdentifier(int pixelColour){
		Color c = new Color(pixelColour, false);
		return c.getRed();
	}
	
	public int greenChannel(int pixelColour){
		Color c = new Color(pixelColour, false);
		return c.getGreen();
	}
	
}
