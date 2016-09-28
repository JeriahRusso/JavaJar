package ide.main.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import ide.main.Environment;
import ide.main.graphics.LevelDisplayZone;

public class LevelData {

	//Function names here are fairly self explanitory but this class is for the creation, storage and changing of levels
	
	public static int width, height;
	
	public static LevelDisplayZone ldz;
	
	public static String levelName;
	
	public static void loadLevel(int levelId){
		try {
			if(levelId < 0){
				throw new FileNotFoundException();
			}
			List<String[]> temp = new ArrayList<String[]>();
			String[] holder = new String[4];
			String[] details = new String[4];
			int z = 0;
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "levels/config.cfg"));
			while(scanner.hasNext()){
				holder[z] = scanner.nextLine();
				z++;
				if(z == 4){
					temp.add(holder);
					z = 0;
					holder = new String[4];
				}
			}
			for(int i = 0; i < temp.size(); i++){
				if(Integer.parseInt(temp.get(i)[2]) == levelId){
					details = temp.get(i);
					break;
				}
			}
			levelName = details[0];
			width = Environment.stringConvert(details[1].split(" ")[0]);
			height = Environment.stringConvert(details[1].split(" ")[1]);
			scanner.close();
			ldz = new LevelDisplayZone(0, 0, 0, 0);
		} catch (FileNotFoundException e) {
			System.err.println("The level with id " + levelId + " could not be loaded, may have been deleted or moved.");
			try{
				Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "levels/config.cfg"));
				String levelName = scanner.nextLine();
				String dimensions = scanner.nextLine();
				LevelData.levelName = levelName;
				System.err.println("Level could not be found so reverted to first level!");
				width = Environment.stringConvert(dimensions.split(" ")[0]);
				height = Environment.stringConvert(dimensions.split(" ")[1]);
				scanner.close();
			} catch(FileNotFoundException e2){
				System.err.println("No level found at " + Environment.resLocation + " default level created");
				generateDefaultLevel();
			}
		}
	}
	
	//Called if no level has been generated yet as the IDE requires some form of level present to operate
	private static void generateDefaultLevel(){
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB); 
		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, 100, 100);
		boolean success = (new File(Environment.resLocation + "levels/level1")).mkdir();
		if(success){
			try {  
				ImageIO.write(image, "png", new File(Environment.resLocation + "levels/level1/level1.png")); 
			} catch (IOException e) {  
				e.printStackTrace();
				System.err.println("Error in creating a default level!");
				System.exit(1);
			}
			
			if(!Environment.addToFile(Environment.resLocation + "levels/config.cfg", new String[]{"level1.png", "100 100", "0"})){
				try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "levels/config.cfg"), "UTF8"))){
					writer.write("level1");
					writer.newLine();
					writer.write("100 100");
					writer.newLine();
					writer.write("0");
					writer.newLine();
					writer.write("0");
				} catch(Exception e){
					System.err.println("No config file found and error in creating a new one for " + Environment.resLocation);
					System.exit(1);
				}
			}
			loadLevel(0);
		}
		else{
			System.err.println("Could not create the folder for the default level");
		}
	}
	
	//Generates a level and its required spriteSheet but this is based on user input and may be any size
	public static void generateLevel(String title, int width, int height, int id, int lit){
		title = levelNameCheck(title);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width, height);
		boolean success = (new File(Environment.resLocation + "levels/" + title)).mkdir();
		if(success){
			try {  
				ImageIO.write(image, "jpg", new File(Environment.resLocation + "levels/" + title + "/" + title + ".png")); 
			} catch (IOException e) {  
				e.printStackTrace();
				System.err.println("Error in creating the level image!");
				System.exit(1);
			}
			
			if(!Environment.addToFile(Environment.resLocation + "levels/config.cfg", new String[]{title, Integer.toString(width) + " " + Integer.toString(height), Integer.toString(id), Integer.toString(lit)})){
				try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "levels/config.cfg"), "UTF8"))){
					writer.write(title);
					writer.newLine();
					writer.write(Integer.toString(width) + " " + Integer.toString(height));
					writer.newLine();
					writer.write(Integer.toString(id));
					writer.newLine();
					writer.write(Integer.toString(lit));
					writer.close();
				} catch(Exception e){
					System.err.println("No config file found and error in creating a new one for " + Environment.resLocation + "levels");
					System.exit(1);
				}
			}
			loadLevel(id);
		}
		else{
			System.err.println("Could not create the folder for the level " + title);
		}
	}
	
	public static int generateId(){
		try {
			List<String[]> ids = new ArrayList<String[]>();
			String[] temp = new String[4];
			int z = 0;
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "levels/config.cfg"));
			while(scanner.hasNext()){
				temp[z] = scanner.nextLine();
				z++;
				if(z == 4){
					ids.add(temp);
					z = 0;
					temp = new String[4];
				}
			}
			scanner.close();
			int id = 0;
			boolean newId = false;
			while(!newId){
				newId = true;
				for(int i = 0; i < ids.size(); i++){
					if(Integer.parseInt(ids.get(i)[2]) == id){
						newId = false;
						id++;
						break;
					}
				}
			}
			return id;
		} catch (FileNotFoundException e) {
			System.err.println("Error in the creation of level id!");
			System.exit(1);
		}
		return -1;
	}
	
	//Checks that the requested level name has not been used in the past and edits it to include ( number ) until it is a new name
	private static String levelNameCheck(String name){
		String title = name;
		int i = 1;
		File temp = new File(Environment.resLocation + "levels/" + title);
		while(temp.exists()){
			title = title + "(" + i + ")";
			temp = new File(Environment.resLocation + "levels/" + title);
		}
		return title;
	}
}
