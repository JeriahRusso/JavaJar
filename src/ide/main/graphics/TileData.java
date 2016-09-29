package ide.main.graphics;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import engine.main.render.sprites.SpriteSheet;
import ide.main.Environment;

public class TileData {

	//Stores all tile data for the project currently being run
	public static int tileSize;
	
	public static List<Tile> tiles = new ArrayList<Tile>();
	
	public class Tile extends Render{

		public int colourIdentity;
		public int tileX, tileY;
		public int id;
		
		public SpriteSheet sheet;
		
		public Tile(int id, int width, int height, int tileX, int tileY, int colourIdentity, SpriteSheet sheet) {
			super(width, height);
			this.id = id;
			this.colourIdentity = colourIdentity;
			this.tileX = tileX * TileData.tileSize;
			this.tileY = tileY * TileData.tileSize;
			this.colourIdentity = colourIdentity;
			this.sheet = sheet;
			buildTileData();
		}
		
		public void buildTileData(){
			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){
					pixels[x + y * width] = sheet.pixels[(tileX + x) + (tileY + y) * sheet.width];
				}
			}
		}
	}
	
	//Uses the blue channel in the level to determine which tile is being requested at specific x, y coordinates
	public static int tileColourToId(int idColour){
		Color full = new Color(idColour, true);
		int blue = full.getBlue();
		for(int i = 0; i < tiles.size(); i++){
			if(tiles.get(i).colourIdentity == blue){
				return tiles.get(i).id;
			}
		}
		return -1;
	}
	
	//Checks through the colour ids that have been assigned to tiles within this project already and assigns the new tile being created
	//The next sequential colour id
	public static int createColourId(){
		int tempId = 0x01;
		boolean newId = false;
		List<String> colours = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/tiles/colours.dat"));
			while(scanner.hasNext()){
				colours.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(!newId){
			newId = true;
			for(int i = 0; i < colours.size(); i++){
				if(Integer.parseInt(colours.get(i), 16) == tempId){
					newId = false;
				}
			}
			if(!newId){
				tempId += 1;
			}
		}
		Environment.addToFile(Environment.resLocation + "spriteSheets/tiles/colours.dat", new String[]{Integer.toHexString(tempId)});
		return tempId;
	}
	
	//Checks through the id's that have been assigned to tiles so far and assigns the next sequential id to the new tile
	//void tile starts at -1 and all proceeding tiles increase from there
	public static int createId(String tileName){
		int tempId = 0;
		boolean newId = false;
		List<String> ids = new ArrayList<String>();
		try{
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/tiles/id.dat"));
			while(scanner.hasNext()){
				String[] temp = scanner.nextLine().split(",");
				ids.add(temp[0]);
			}
			scanner.close();
		}
		catch(Exception e){
			System.err.println("Error generating ID for tile " + tileName);
		}
		while(!newId){
			newId = true;
			for(int i = 0; i < ids.size(); i++){
				if(Integer.parseInt(ids.get(i)) == tempId){
					newId = false;
				}
			}
			if(!newId)
				tempId++;
		}
		Environment.addToFile(Environment.resLocation + "spriteSheets/tiles/id.dat", new String[]{Integer.toString(tempId) + "," + tileName});
		return tempId;
	}
	
	//Reads the tile id data file and generates an internal tile for each tile within this file
	public static void constructTiles(){
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/tiles/id.dat"));
			List<String[]> data = new ArrayList<String[]>();
			while(scanner.hasNext()){
				data.add(scanner.nextLine().split(","));
			}
			for(int i = 0; i < data.size(); i++){
				tiles.add(loadTiles(Integer.parseInt(data.get(i)[0])));
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Error in the finding and use of tiles/id.dat!");
			System.exit(1);
		}
	}
	
	//After finiding indidual tiles above this function uses that data to read each individual files tiledata file and
	//through that generate internal tiles
	private static Tile loadTiles(int tileId){
		List<String[]> tileData = new ArrayList<String[]>();
		Tile tile = null;
		TileData tileDat = new TileData();
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/tiles/id.dat"));
			while(scanner.hasNext()){
				tileData.add(scanner.nextLine().split(","));
			}
			scanner.close();
			for(int i = 0; i < tileData.size(); i++){
				if(Integer.parseInt(tileData.get(i)[0]) == tileId){
					scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/tiles/" + tileData.get(i)[1] + ".td"));
					List<String> data = new ArrayList<String>();
					while(scanner.hasNext()){
						data.add(scanner.nextLine());
					}
					SpriteSheet sheet = new SpriteSheet(Environment.resLocation + "spriteSheets/" + data.get(3));
					tile = tileDat.new Tile(Integer.parseInt(data.get(1)), TileData.tileSize, TileData.tileSize, Integer.parseInt(data.get(4)), Integer.parseInt(data.get(5)), Integer.parseInt(data.get(2)), sheet);
					scanner.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Error occured in the reading and development of tile data from id!");
			System.exit(1);
		}
		return tile;
	}
	
}
