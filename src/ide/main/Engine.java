package ide.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ide.main.data.LevelData;
import ide.main.graphics.Button;
import ide.main.graphics.ButtonList;
import ide.main.graphics.DisplayBox;
import ide.main.graphics.LevelDisplayZone;
import ide.main.graphics.Render;
import ide.main.graphics.Sprite;
import ide.main.graphics.SpriteList;
import ide.main.graphics.TileData;
import ide.main.graphics.TileData.Tile;
import ide.main.graphics.ToolBar;
import ide.main.inputs.InputHandler;

public class Engine extends Render{
	//This acts somewhat like a level within the engine with the main development environment being loaded here
	public static int mouseHoldingId = -1;
	public int levelSelectedId = -1;
	
	public static boolean sprite = true;
	public static boolean tile = false;
	
	public String resLocation = Environment.resLocation;
	
	public List<String> config = new ArrayList<String>();
	
	public ButtonList bList = new ButtonList();
	public SpriteList sList = new SpriteList();
	
	public static List<ToolBar> bars = new ArrayList<ToolBar>();
	public static List<DisplayBox> boxes = new ArrayList<DisplayBox>();
	
	public static LevelDisplayZone ldz;
	
	private ToolBar mainBar = new ToolBar(width, 20, 0, 0, 0, new Button[]{bList.button1(), bList.button3(), bList.button4(), bList.tool()});
	private ToolBar boxBar;
	
	public static DisplayBox sprite1;
	public static DisplayBox tiles;
	
	public Engine(int width, int height) {
		super(width, height);
		Sprite.loadSprites();
		TileData.constructTiles();
		LevelData.loadLevel(0);
		sprite1 = new DisplayBox(width - 200, mainBar.height + 20, 200, height - mainBar.height, 0xffffffff, getSprites());
		tiles = new DisplayBox(width - 200, mainBar.height + 20, 200, height - mainBar.height, 0xffffffff, getTiles());
		boxBar = new ToolBar(200, 20, width - 200, mainBar.height, 0, new Button[]{bList.sprite(), bList.button2()});
		ldz = LevelData.ldz;
		ldz.width = width - tiles.width;
		ldz.height = height - mainBar.height;
		ldz.pixels = new int[ldz.width * ldz.height];
		ldz.xPos = 0;
		ldz.yPos = mainBar.height;
		loadConfig();
		setupLists();
	}
	
	public void tick(){
		ldz.tick();
		for(int i = 0; i < boxes.size(); i++){
			if(boxes.get(i).active){
				boxes.get(i).tick();
				if(Environment.input.getLeftReleased()){
					for(int b = 0; b < boxes.get(i).sprites.length; b++){
						if(Environment.mouseOnComponent((boxes.get(i).sprites[b].xPos * Environment.scale) + boxes.get(i).xPos, (boxes.get(i).sprites[b].yPos * Environment.scale) + boxes.get(i).yPos, boxes.get(i).sprites[b].width * Environment.scale, boxes.get(i).sprites[b].height * Environment.scale)){
							mouseHoldingId = boxes.get(i).sprites[b].id;
							if(sprite1.active){
								sprite = true;
								tile = false;
							}
							else if(tiles.active){
								tile = true;
								sprite = false;
							}
						}
					}
				}
			}
		}
		for(int i = 0; i < bars.size(); i++){
			bars.get(i).tick();
			if(bars.get(i).icons[bars.get(i).activeButton].menu == null){
				for(int ii = 0; ii < boxes.size(); ii++){
					boxes.get(ii).active = false;
				}
				boxes.get(bars.get(i).activeButton).active = true;
			}
		}
		levelUpdate();
	}
	
	public void render(){
		ldz.render();
		draw(ldz, ldz.xPos, ldz.yPos);
		for(int i = 0; i < bars.size(); i++){
			bars.get(i).render();
			draw(bars.get(i), bars.get(i).xPos, bars.get(i).yPos);
			for(int z = 0; z < bars.get(i).icons.length; z++){
				if(bars.get(i).icons[z].menu != null){
					if(bars.get(i).icons[z].menu.active){
						draw(bars.get(i).icons[z].menu, bars.get(i).icons[z].menu.xPos, bars.get(i).icons[z].menu.yPos);
					}
				}
			}
		}
		
		for(int i = 0; i < boxes.size(); i++){
			if(boxes.get(i).active){
				boxes.get(i).render();
				draw(boxes.get(i), boxes.get(i).xPos, boxes.get(i).yPos);
				for(int z = 0; z < boxes.get(i).sprites.length; z++){
					if(boxes.get(i).sprites[z].menu.active){
						draw(boxes.get(i).sprites[z].menu, boxes.get(i).sprites[z].menu.xPos, boxes.get(i).sprites[z].menu.yPos);
					}
				}
			}
		}
	}
	
	public void clear(){
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = 0;
		}
	}
	
	public void loadConfig(){
		try{
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "config.cfg"));
			while(scanner.hasNext()){
				config.add(scanner.nextLine());
			}
			scanner.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Config file at " + Environment.resLocation + "config.cfg was not found!");
			System.exit(1);
		}
	}
	
	//Adds all the components such as toolbars and display boxes to lists for easier access
	private void setupLists(){
		bars.add(mainBar);
		bars.add(boxBar);
		
		boxes.add(sprite1);
		boxes.add(tiles);
		
		boxBar.icons[0].box = sprite1;
		boxBar.icons[1].box = tiles;
	}
	
	//This class runs through the sprite config file and then generates a sprite based on each sprite present
	private Sprite[] getSprites(){
		List<String[]> spriteData = new ArrayList<String[]>();
		List<Sprite> sprites = new ArrayList<Sprite>();
		Sprite[] finalSprites = null;
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/sprites/id.dat"));
			while(scanner.hasNext()){
				spriteData.add(scanner.nextLine().split(","));
			}
			for(int i = 0; i < spriteData.size(); i++){
				List<String> data = new ArrayList<String>();
				scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/sprites/" + spriteData.get(i)[1] + ".sd"));
				while(scanner.hasNext()){
					data.add(scanner.nextLine());
				}
				sprites.add(new Sprite(Integer.parseInt(data.get(0)), 0, 0, Integer.parseInt(data.get(3)), Integer.parseInt(data.get(4)), Integer.parseInt(data.get(5)) * TileData.tileSize, Integer.parseInt(data.get(6)) * TileData.tileSize, Integer.parseInt(data.get(7), 16), Environment.resLocation + "spriteSheets/" + data.get(2)));
			}
			finalSprites = new Sprite[sprites.size()];
			for(int i = 0; i < finalSprites.length; i++){
				finalSprites[i] = sprites.get(i);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Error occured in retrieving tile data, files missing or corrupted!");
			System.exit(1);
		}
		return finalSprites;
	}
	
	//This function runs through the tiles config file and then generates a tile based on each tile present
	private Sprite[] getTiles(){
		List<String[]> tileData = new ArrayList<String[]>();
		List<Sprite> tiles = new ArrayList<Sprite>();
		Sprite[] finalTiles = null;
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/tiles/id.dat"));
			while(scanner.hasNext()){
				tileData.add(scanner.nextLine().split(","));
			}
			for(int i = 0; i < tileData.size(); i++){
				List<String> data = new ArrayList<String>();
				scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/tiles/" + tileData.get(i)[1] + ".td"));
				while(scanner.hasNext()){
					data.add(scanner.nextLine());
				}
				tiles.add(new Sprite(Integer.parseInt(data.get(1)), 0, 0, Integer.parseInt(data.get(4)), Integer.parseInt(data.get(5)), TileData.tileSize, TileData.tileSize, 0, Environment.resLocation + "spriteSheets/" + data.get(3)));
			}
			finalTiles = new Sprite[tiles.size()];
			for(int i = 0; i < finalTiles.length; i++){
				finalTiles[i] = tiles.get(i);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Error occured in retrieving tile data, files missing or corrupted!");
			System.exit(1);
		}
		return finalTiles;
	}
	
	//This is called to update visually and in memory the area in which the editing takes place
	//Visually pixels are being edited and in the background different id's are being set to store the data
	//This occurs for both sprites and tiles
	public void levelUpdate(){
		if((Environment.input.getLeftReleased() || InputHandler.mousePressed) && !menuOpen()){
			if(Environment.mouseOnComponent(ldz.xPos, ldz.yPos, ldz.width, ldz.height) && tile){
				int relXPos = (Environment.input.getMouseX() - ldz.xPos) / TileData.tileSize;
				int relYPos = (Environment.input.getMouseY() - ldz.yPos) / TileData.tileSize;
				Tile tile = tileFromId(mouseHoldingId);
				ldz.updateTilePixels(relXPos, relYPos, tile.pixels);
				ldz.tileData[relXPos + relYPos * ldz.tilesAcross] = tile;
				ldz.tileIds[relXPos + relYPos * ldz.tilesAcross] = tile.id;
				Sprite sprite = spriteFromId(ldz.spriteData[relXPos + relYPos * ldz.tilesAcross].id);
				ldz.updateSpritePixels(relXPos, relYPos, sprite.pixels);
			}
			else if(Environment.mouseOnComponent(ldz.xPos, ldz.yPos, ldz.width, ldz.height) && sprite){
				int relXPos = (Environment.input.getMouseX() - ldz.xPos) / TileData.tileSize;
				int relYPos = (Environment.input.getMouseY() - ldz.yPos) / TileData.tileSize;
				Sprite sprite = spriteFromId(mouseHoldingId);
				Tile tile = tileFromId(ldz.tileData[relXPos + relYPos * ldz.tilesAcross].id);
				ldz.updateTilePixels(relXPos, relYPos, tile.pixels);
				ldz.updateSpritePixels(relXPos, relYPos, sprite.pixels);
				ldz.spriteData[relXPos + relYPos * ldz.tilesAcross] = sprite;
				ldz.spriteIds[relXPos + relYPos * ldz.tilesAcross] = sprite.id;
			}
		}
	}
	
	public static Tile tileFromId(int id){
		for(int i = 0; i < TileData.tiles.size(); i++){
			if(TileData.tiles.get(i).id == id){
				return TileData.tiles.get(i);
			}
		}
		System.err.println("Error finding the requested tile with id " + id);
		return null;
	}
	
	public static Sprite spriteFromId(int id){
		if(Sprite.sprites.size() != 0){
			for(int i = 0; i < Sprite.sprites.size(); i++){
				if(Sprite.sprites.get(i).id == id){
					return Sprite.sprites.get(i);
				}
			}
			System.err.println("Error finding the requested sprite with id " + id);
			return null;
		}
		return null;
	}
	
	public static boolean menuOpen(){
		for(int i = 0; i < bars.size(); i++){
			for(int ii = 0; ii < bars.get(i).icons.length; ii++){
				if(bars.get(i).icons[ii].menu != null){
					if(bars.get(i).icons[ii].menu.active){
						return true;
					}
				}
			}
		}
		return false;
	}

}
