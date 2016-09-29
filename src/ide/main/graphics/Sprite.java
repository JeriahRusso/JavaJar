package ide.main.graphics;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import engine.main.render.sprites.SpriteSheet;
import ide.main.Environment;
import ide.main.inputs.InputHandler;

public class Sprite extends Render{

	public int id, xPos, yPos, sheetX, sheetY;
	public int colourId;
	public SpriteSheet sheet;
	
	private DropDownMenuList ds = new DropDownMenuList();
	public DropDownMenu menu = ds.rightClick();
	
	public static List<Sprite> sprites;
	
	private boolean hovered = false;
	
	//Generic sprite holder class
	public Sprite(int id, int xPos, int yPos, int sheetX, int sheetY, int width, int height, int colourId, String spriteSheet) {
		super(width, height);
		this.id = id;
		this.xPos = xPos;
		this.yPos = yPos;
		this.sheetX = sheetX * TileData.tileSize;
		this.sheetY = sheetY * TileData.tileSize;
		this.colourId = colourId;
		this.sheet = new SpriteSheet(spriteSheet);
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				pixels[x + y * width] = sheet.pixels[(sheetX + x) + (sheetY + y) * sheet.width];
			}
		}
	}
	
	public void tick(boolean mouseOnSprite){
		setHovered(mouseOnSprite);
		if(mouseOnSprite){
			if(Environment.input.getMouseReleased() && InputHandler.rightButton){
				menu.xPos = Environment.input.getMouseX() + 1;
				menu.yPos = Environment.input.getMouseY() + 1;
				menu.active = true;
			}
		}
		if(menu.active){
			menu.tick();
		}
	}
	
	public void render(){
		if(menu.active){
			menu.render();
		}
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				pixels[x + y * width] = sheet.pixels[(x + sheetX) + (y + sheetY) * sheet.width];
			}
		}
	}
	
	//Runs through the sprite config file and loads a new spirte in the system for each sprite in storage
	public static void loadSprites(){
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/sprites/id.dat"));
			List<String> spriteNames = new ArrayList<String>();
			while(scanner.hasNext()){
				spriteNames.add(scanner.nextLine().split(",")[1]);
			}
			scanner.close();
			List<Sprite> rawSprites = new ArrayList<Sprite>();
			for(int i = 0; i < spriteNames.size(); i++){
				List<String> tempData = new ArrayList<String>();
				scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/sprites/" + spriteNames.get(i) + ".sd"));
				while(scanner.hasNext()){
					tempData.add(scanner.nextLine());
				}
				rawSprites.add(new Sprite(Integer.parseInt(tempData.get(0), 16), 0, 0, Integer.parseInt(tempData.get(3)), Integer.parseInt(tempData.get(4)) * TileData.tileSize, Integer.parseInt(tempData.get(5)) * TileData.tileSize, Integer.parseInt(tempData.get(6)) * TileData.tileSize, Integer.parseInt(tempData.get(7), 16), Environment.resLocation + "spriteSheets/" + tempData.get(2)));
			}
			sprites = rawSprites;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Error occured in loading sprite data!");
			System.exit(1);
		}
	}
	
	//gets a sprite id dependent on the red channel being read from the level
	public static int spriteColourToId(int idColour){
		Color full = new Color(idColour, true);
		int red = full.getRed();
		for(int i = 0; i < sprites.size(); i++){
			if(sprites.get(i).colourId == red){
				return sprites.get(i).id;
			}
		}
		return -1;
	}
	
	//When sprites are built by the user this generates a unique colour id for the sprite (done sequentially from the last)
	//Due to the read channel representing the sprites 255 unique sprites may be loaded
	public static int generateSpriteColourId(){
		int id = 0x00;
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/sprites/colours.dat"));
			List<String> colours = new ArrayList<String>();
			while(scanner.hasNext()){
				colours.add(scanner.nextLine());
			}
			scanner.close();
			boolean newId = false;
			while(!newId){
				newId = true;
				for(int i = 0; i < colours.size(); i++){
					if(Integer.parseInt(colours.get(i)) == id){
						newId = false;
						id++;
						break;
					}
				}
			}
			Environment.addToFile(Environment.resLocation + "spriteSheets/sprites/colours.dat", new String[]{Integer.toHexString(id)});
			return id;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Error generating the colout id for sprites!");
			System.exit(1);
		}
		return id;
	}
	
	//generates the id that a sprite uses to identify itself when being processed by giving it the next sequential no assigned id
	//void tile has id -1 and then it increases from there
	public static int generateSpriteId(String name){
		int id = -1;
		try {
			List<String[]> rawData = new ArrayList<String[]>();
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/sprites/id.dat"));
			while(scanner.hasNext()){
				rawData.add(scanner.nextLine().split(","));
			}
			boolean newId = false;
			while(!newId){
				newId = true;
				for(int i = 0; i < rawData.size(); i++){
					if(Integer.parseInt(rawData.get(i)[0]) == id){
						newId = false;
						id++;
						break;
					}
				}
			}
			scanner.close();
			Environment.addToFile(Environment.resLocation + "spriteSheets/sprites/id.dat", new String[]{id + "," + name});
			return id;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Error occured in the creation of ID for the requested sprite!");
			System.exit(1);
		}
		return id;
	}
	
	//Checks that the name being given for a specific sprite is unique and changes it to name + ( *number* ) if it is not
	public static String nameCheck(String name){
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/sprites/id.dat"));
			List<String[]> data = new ArrayList<String[]>();
			while(scanner.hasNext()){
				data.add(scanner.nextLine().split(","));
			}
			scanner.close();
			boolean newName = false;
			int z = 0;
			while(!newName){
				newName = true;
				for(int i = 0; i < data.size(); i++){
					if(data.get(i)[1].equals(name)){
						name = name + "(" + z + ")";
						z++;
						newName = false;
						break;
					}
				}
			}
			return name;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Unable to check name for sprite " + name + " it is possible an old sprite has been overwritten!");
		}
		return null;
	}
	
	public void setHovered(boolean hovered){
		this.hovered = hovered;
	}
	
	public boolean isHovered(){
		return hovered;
	}
}
