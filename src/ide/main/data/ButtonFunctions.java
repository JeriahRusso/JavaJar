package ide.main.data;


import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import engine.main.Main;
import engine.main.render.sprites.SpriteSheet;
import ide.main.Engine;
import ide.main.Environment;
import ide.main.graphics.DisplayBox;
import ide.main.graphics.Sprite;
import ide.main.graphics.TileData;
import ide.main.graphics.TileData.Tile;

public class ButtonFunctions {
	
	//This class is a holder of all potential function I could want buttons to perform
	//In truth most calls are made from DropDownMenus but functionally it is the same
	public static void runFunctionId(int id){
		if(id == 0){
			codeBuild(calculateSpriteHover());
		}
		else if(id == 1){
			try {
				importSprites();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(id == 2){
			buildSprite();
		}
		else if(id == 3){
			loadLevel();
		}
		else if(id == 4){
			buildNewTile();
		}
		else if(id == 5){
			buildLevel();
		}
		else if(id == 6){
			saveLevel();
		}
		else if(id == 7){
			runGame();
		}
		else if(id == 8){
			fill();
		}
	}
	
	//This function works and builds an editor of sort for custom code however currently exportation of custom code is not implemented
	public static void codeBuild(SpriteInt si){
		Sprite sprite = si.sprite;
		int id = si.Int;
		if(sprite == null || id != 0){
			return;
		}
		List<String> content = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "codes/" + sprite.id + ".jjc"));
			while(scanner.hasNext()){
				content.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "codes/" + sprite.id + ".jjc"), "UTF8"))){
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.err.println("Error creating code for sprite id " + sprite.id);
			}
		}
		
		JFrame frame = new JFrame("Java Jar Code Editor");
		JPanel panel = new JPanel();
		frame.setSize(600, 600);
		panel.setLayout(new GridBagLayout());
		panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		GridBagConstraints c = new GridBagConstraints();
		
		JTextArea jt = new JTextArea();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.gridwidth = 4;
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "codes/" + sprite.id + ".jjc"));
			while(scanner.hasNext()){
				jt.append(scanner.nextLine() + "\n");
			}
			scanner.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.err.println("Failed to create code for sprite with id " + sprite.id);
			System.exit(1);
		}
		panel.add(new JScrollPane(jt), c);
		
		JButton save = new JButton("Save");
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.5;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		save.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				saveCode(jt, sprite.id);
			}
		
		});
		panel.add(save, c);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				saveCode(jt, sprite.id);
				frame.dispose();
			}
			
		});
		c.gridx = 2;
		c.gridy = 3;
		panel.add(close, c);
		
		frame.add(panel);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	private static void saveCode(JTextArea code, int id){
		String[] split = code.getText().split("\\n");
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "codes/" + id + ".jjc"), "UTF8"))){
			for(int i = 0; i < split.length; i++){
				writer.write(split[i]);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not save code for sprite with id " + id);
		}
	}
	
	//This function allows for the importing of spriteSheets from external locations through a file browser
	@SuppressWarnings("resource")
	public static void importSprites() throws IOException{
		JFrame frame = new JFrame();
		final JFileChooser fc = new JFileChooser();
		int returnValue = fc.showOpenDialog(frame);
		
		if(returnValue == JFileChooser.APPROVE_OPTION){
			File file = fc.getSelectedFile();
			FileChannel inputChannel = null;
			FileChannel outputChannel = null;
			try{
				inputChannel = new FileInputStream(file.getPath()).getChannel();
				outputChannel = new FileOutputStream(Environment.resLocation + "spriteSheets/" + file.getName()).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
				if(Environment.addToFile(Environment.resLocation + "spriteSheets/config.cfg", new String[]{file.getName()})){}
				else{
					try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "spriteSheets/config.cfg"), "UTF8"))){
						writer.write(file.getName());
						writer.close();
					} catch(Exception e){
						System.err.println("Error when attempting to create config file for spritesheets in project!");
						System.exit(1);
					}
				}
			}finally{
				inputChannel.close();
				outputChannel.close();
			}
		}
	}
	
	//Gets the user to input all required variables about a sprite that are then stored for use within the engine
	//TODO: Change this to use a visual system to allow for easier creation
	public static void buildSprite(){
		if(getImportedSheets() == null){
			System.err.println("No sprite sheets imported so no sprites could be created!");
			return;
		}
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		frame.setSize(600, 400);
		frame.setTitle("Java Jar Sprite Creator");
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel name = new JLabel("Name of sprite: ");
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(name, gbc);
		
		JTextField nameField = new JTextField();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		panel.add(nameField, gbc);
		
		JLabel spriteSheets = new JLabel("Sprite Sheet: ");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		panel.add(spriteSheets, gbc);
		
		JComboBox<String> spriteSheetSelector = new JComboBox<String>(getImportedSheets());
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.5;
		panel.add(spriteSheetSelector, gbc);
		
		JLabel xPos = new JLabel("Tiles across on sprite sheet (Note start at 0): ");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		panel.add(xPos, gbc);
		
		JTextField xPosField = new JTextField();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 0.5;
		panel.add(xPosField, gbc);
		
		JLabel yPos = new JLabel("Tiles down on sprite sheet (Note start at 0): ");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0;
		panel.add(yPos, gbc);
		
		JTextField yPosField = new JTextField();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.weightx = 0.5;
		panel.add(yPosField, gbc);
		
		JLabel width = new JLabel("Width of the sprite in tiles: ");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 0;
		panel.add(width, gbc);
		
		JTextField widthField = new JTextField();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.weightx = 0.5;
		panel.add(widthField, gbc);
		
		JLabel height = new JLabel("Height of the sprite in tiles: ");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.weightx = 0;
		panel.add(height, gbc);
		
		JTextField heightField = new JTextField();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.weightx = 0.5;
		panel.add(heightField, gbc);
		
		JLabel type = new JLabel("Type of sprite: ");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.weightx = 0;
		panel.add(type, gbc);
		
		JComboBox<String> typeSelector = new JComboBox<String>(new String[]{"Sprite", "Animated Sprite", "Light Source", "Mob", "Player"});
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.weightx = 0.5;
		panel.add(typeSelector, gbc);
		
		JLabel animationWidth = new JLabel("Width of animations (0 for no animation):");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.weightx = 0;
		panel.add(animationWidth, gbc);
		
		JTextField animationWidthField = new JTextField("0");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.weightx = 0.5;
		panel.add(animationWidthField, gbc);
		
		JLabel animationHeight = new JLabel("Height of animations (0 for no animation):");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.weightx = 0;
		panel.add(animationHeight, gbc);
		
		JTextField animationHeightField = new JTextField("0");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 8;
		gbc.weightx = 0.5;
		panel.add(animationHeightField, gbc);
		
		JButton confirm = new JButton("Confirm");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.weightx = 0.5;
		confirm.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if(Environment.stringIntCheck(new String[]{xPosField.getText(), yPosField.getText(), widthField.getText(), heightField.getText(), animationWidthField.getText(), animationHeightField.getText()})){
					nameField.setText(Sprite.nameCheck(nameField.getText()));
					int id = Sprite.generateSpriteId(nameField.getText());
					int colourId = Sprite.generateSpriteColourId();
					try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "spriteSheets/sprites/" + nameField.getText() + ".sd"), "UTF8"))){
						writer.write(Integer.toString(id));
						writer.newLine();
						writer.write(nameField.getText());
						writer.newLine();
						writer.write(spriteSheetSelector.getItemAt(spriteSheetSelector.getSelectedIndex()));
						writer.newLine();
						writer.write(xPosField.getText());
						writer.newLine();
						writer.write(yPosField.getText());
						writer.newLine();
						writer.write(widthField.getText());
						writer.newLine();
						writer.write(heightField.getText());
						writer.newLine();
						writer.write(Integer.toHexString(colourId));
						writer.newLine();
						writer.write(typeSelector.getItemAt(typeSelector.getSelectedIndex()));
						writer.newLine();
						writer.write(animationWidthField.getText());
						writer.newLine();
						writer.write(animationHeightField.getText());
						writer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						System.err.println("Error occured in the creation of the sprite " + nameField.getText() + "!");
						System.exit(1);
					}
					System.out.println(Sprite.sprites.size());
					Sprite.sprites.add(new Sprite(id, 0, 0, Integer.parseInt(xPosField.getText()), Integer.parseInt(yPosField.getText()), Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()), colourId, Environment.resLocation + "spriteSheets/" + spriteSheetSelector.getItemAt(spriteSheetSelector.getSelectedIndex())));
					System.out.println(Sprite.sprites.size());
					reloadSprites();
					frame.dispose();
				}
			}
		});
			
		panel.add(confirm, gbc);
		
		JButton cancel = new JButton("Cancel");
		gbc.gridx = 1;
		gbc.gridy = 9;
		gbc.weightx = 0;
		cancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
			
		});
		panel.add(cancel, gbc);
		
		frame.add(panel);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
	
	//Loads a level from all possible levels and sets its spritesheet to the active one
	public static void loadLevel(){
		getLevels();
		JFrame frame = new JFrame("Level Load");
		frame.setSize(600, 150);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel label = new JLabel("Choose level: ");
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(label, gbc);
		
		JComboBox<String> levelSelector = new JComboBox<String>(getLevels());
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(levelSelector, gbc);
		
		JButton confirm = new JButton("Confirm");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 1;
		confirm.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				LevelData.loadLevel(getLevelId(levelSelector.getItemAt(levelSelector.getSelectedIndex())));
				frame.dispose();
				Engine.ldz = LevelData.ldz;
				Engine.ldz.width = 800;
				Engine.ldz.height = 800;
				Engine.ldz.pixels = new int[800 * 800];
				Engine.ldz.xPos = 0;
				Engine.ldz.yPos = 20;
				Engine.ldz.updatePixelData();
			}
			
		});
		panel.add(confirm, gbc);
		
		JButton cancel = new JButton("Cancel");
		gbc.gridx = 1;
		gbc.gridy = 1;
		cancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
			
		});
		panel.add(cancel, gbc);
		
		frame.add(panel);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
	
	//Requests the data from the user to construct a new tile and then stores the data in a TileData file
	public static void buildNewTile(){
		if(getImportedSheets() == null)
			return;
		JFrame frame = new JFrame("Tile Creation");
		frame.setSize(600, 600);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel name = new JLabel("Tile Name:");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		panel.add(name, gbc);
		
		JLabel spriteSheet = new JLabel("Sprite Sheet:");
		gbc.gridy = 1;
		panel.add(spriteSheet, gbc);
		
		JLabel tileX = new JLabel("Tiles across on sheet(start at 0)");
		gbc.gridy = 2;
		panel.add(tileX, gbc);
		
		JLabel tileY = new JLabel("Tiles down on sheet(start at 0)");
		gbc.gridy = 3;
		panel.add(tileY, gbc);
		
		JTextField nameField = new JTextField();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(nameField, gbc);
		
		JComboBox<String> spriteSheets = new JComboBox<String>(getImportedSheets());
		gbc.gridy = 1;
		panel.add(spriteSheets, gbc);
		
		JTextField xField = new JTextField();
		gbc.gridy = 2;
		panel.add(xField, gbc);
		
		JTextField yField = new JTextField();
		gbc.gridy = 3;
		panel.add(yField, gbc);
		
		JButton confirm = new JButton("Confirm");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 5;
		confirm.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if(Environment.stringIntCheck(new String[]{xField.getText(), yField.getText()})){
					buildTile(nameField.getText(), spriteSheets.getItemAt(spriteSheets.getSelectedIndex()), xField.getText(), yField.getText());
					reloadTiles();
					frame.dispose();
				} else{
					System.err.println("Some of the fields had incorrect data to build a tile!");
				}
			}
			
		});
		panel.add(confirm, gbc);
		
		JButton cancel = new JButton("Cancel");
		gbc.gridx = 1;
		cancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
			
		});
		panel.add(cancel);
		
		frame.add(panel);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
	}
	
	//Generates a new level and new level spritesheet based on the inputs given by a user
	public static void buildLevel(){
		JFrame frame = new JFrame("Level Builder");
		frame.setSize(500, 500);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel name = new JLabel("Level Name:");
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(name, gbc);
		
		JLabel width = new JLabel("Level Width (in tiles):");
		gbc.gridy = 1;
		panel.add(width, gbc);
		
		JLabel height = new JLabel("Level Height (in tiles):");
		gbc.gridy = 2;
		panel.add(height, gbc);
		
		JLabel lit = new JLabel("Level lit:");
		gbc.gridy = 3;
		panel.add(lit, gbc);
		
		JTextField nameField = new JTextField();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(nameField, gbc);
		
		JTextField widthField = new JTextField();
		gbc.gridy = 1;
		panel.add(widthField, gbc);
		
		JTextField heightField = new JTextField();
		gbc.gridy = 2;
		panel.add(heightField, gbc);
		
		JRadioButton litChoiceYes = new JRadioButton("Yes", true);
		JRadioButton litChoiceNo = new JRadioButton("No", false);
		gbc.gridy = 3;
		panel.add(litChoiceYes, gbc);
		gbc.gridx = 2;
		panel.add(litChoiceNo, gbc);
		ButtonGroup group = new ButtonGroup();
		group.add(litChoiceYes);
		group.add(litChoiceNo);
		
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
			
		});
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 1;
		gbc.gridy = 4;
		panel.add(close, gbc);
		
		JButton confirm = new JButton("Confirm");
		confirm.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if(Environment.stringIntCheck(new String[]{widthField.getText(), heightField.getText()})){
					int lit = 0;
					if(litChoiceYes.isSelected()){
						lit = 1;
					}
					else if(litChoiceNo.isSelected()){
						lit = 0;
					}
					LevelData.generateLevel(nameField.getText(), Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()), LevelData.generateId(), lit);
					frame.dispose();
				}
				else{
					System.err.println("Some fields contained incorrect data!");
				}
			}
			
		});
		gbc.gridx = 0;
		panel.add(confirm, gbc);
		
		frame.add(panel);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
	
	//Saves the current level by getting the colour id's of tiles and sprites and then editing the pixel data of the level spritesheet
	//to match. Through this method a possible 255 sprites and 255 tiles is possible before clashing occurs
	public static void saveLevel(){
		int[] pixels = new int[Engine.ldz.tileIds.length];
		for(int i = 0; i < Engine.ldz.tileIds.length; i++){
			pixels[i] = Engine.spriteFromId(Engine.ldz.spriteIds[i]).colourId << 16 | Engine.tileFromId(Engine.ldz.tileIds[i]).colourIdentity;
		}
		BufferedImage image = new BufferedImage(LevelData.width, LevelData.height, BufferedImage.TYPE_INT_RGB);
		int[] imgPixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		System.arraycopy(pixels, 0, imgPixels, 0, pixels.length);
		try{
			ImageIO.write(image, "png", new File(Environment.resLocation + "levels/" + LevelData.levelName + "/" + LevelData.levelName + ".png"));
		} catch (IOException e) {  
			e.printStackTrace();
			System.err.println("Error in creating the level image!");
			System.exit(1);
		}
	}
	
	public static void runGame(){
		Main.debugLoad(Environment.resLocation);
	}
	
	public static void fill(){
		if(Engine.tile){
			Tile tile = Engine.tileFromId(Engine.mouseHoldingId);
			for(int x = 0; x < Engine.ldz.tilesAcross; x++){
				for(int y = 0; y < Engine.ldz.tilesDown; y++){
					Engine.ldz.updateTilePixels(x, y, tile.pixels);
					Engine.ldz.tileData[x + y * Engine.ldz.tilesAcross] = tile;
					Engine.ldz.tileIds[x + y * Engine.ldz.tilesAcross] = tile.id;
					Sprite sprite = Engine.spriteFromId(Engine.ldz.spriteData[x + y * Engine.ldz.tilesAcross].id);
					Engine.ldz.updateSpritePixels(x, y, sprite.pixels);
				}
			}
		}
	}
	
	//Reloads the tile DisplayBox after a new tile has been added so that the new tile is displayed
	private static void reloadTiles(){
		List<Sprite> sprites = new ArrayList<Sprite>();
		for(int i = 0; i < TileData.tiles.size(); i++){
			sprites.add(new Sprite(TileData.tiles.get(i).id, 0, 0, TileData.tiles.get(i).tileX, TileData.tiles.get(i).tileY, TileData.tiles.get(i).width, TileData.tiles.get(i).height, TileData.tiles.get(i).colourIdentity, TileData.tiles.get(i).sheet.path));
		}
		Sprite[] spriteArray = new Sprite[sprites.size()];
		for(int i = 0; i < sprites.size(); i++){
			spriteArray[i] = sprites.get(i);
		}
		Engine.tiles = new DisplayBox(Engine.boxes.get(1).xPos, Engine.boxes.get(1).yPos, Engine.boxes.get(1).width, Engine.boxes.get(1).height, Engine.boxes.get(1).colour, spriteArray);
		Engine.boxes.remove(1);
		Engine.boxes.add(1, Engine.tiles);
	}
	
	//Reloads the sprite DisplayBox after a new sprite has been added so that the new sprite is displayed
	private static void reloadSprites(){
		Sprite[] sprites = new Sprite[Sprite.sprites.size()];
		for(int i = 0; i < sprites.length; i++){
			sprites[i] = Sprite.sprites.get(i);
		}
		Engine.sprite1 = new DisplayBox(Engine.boxes.get(0).xPos, Engine.boxes.get(0).yPos, Engine.boxes.get(0).width, Engine.boxes.get(0).height, Engine.boxes.get(0).colour, sprites);
		Engine.boxes.remove(0);
		Engine.boxes.add(0, Engine.sprite1);
	}
	
	//Stores a new tiles data into a tileData file
	private static void buildTile(String name, String spriteSheet, String xTile, String yTile){
		File nameCheck = new File(Environment.resLocation + "spriteSheets/tiles/" + name + ".td");
		int i = 1;
		while(nameCheck.exists()){
			nameCheck = new File(Environment.resLocation + "spriteSheets/tiles/" + name + "(" + i + ")" + ".td");
			name = name + "(" + i + ")";
			i++;
		}
		int id = TileData.createId(name);
		int colourId = TileData.createColourId();
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nameCheck), "UTF8"))){
			writer.write(name);
			writer.newLine();
			writer.write(Integer.toString(id));
			writer.newLine();
			writer.write(Integer.toString(colourId));
			writer.newLine();
			writer.write(spriteSheet);
			writer.newLine();
			writer.write(xTile);
			writer.newLine();
			writer.write(yTile);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		TileData cl = new TileData();
		TileData.tiles.add(cl.new Tile(id, TileData.tileSize, TileData.tileSize, Integer.parseInt(xTile), Integer.parseInt(yTile), colourId, new SpriteSheet(Environment.resLocation + "spriteSheets/" + spriteSheet)));
		reloadTiles();
	}
	
	//Reads the levels config file and gets all available levels through this
	private static String[] getLevels(){
		List<String> levelHolder = new ArrayList<String>();
		String[] levels = null;
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "levels/config.cfg"));
			while(scanner.hasNext()){
				levelHolder.add(scanner.nextLine());
			}
			levels = new String[levelHolder.size() / 4];
			for(int i = 0; i < levelHolder.size(); i += 4){
				levels[i / 4] = levelHolder.get(i);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.err.println("Level config file not found!");
			LevelData.loadLevel(-1);
			levels = new String[]{"level1.png"};
		}
		return levels;
	}
	
	private static int getLevelId(String levelName){
		try {
			int id = -1;
			int z = 0;
			String[] temp = new String[4];
			List<String[]> levelHolder = new ArrayList<String[]>();
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "levels/config.cfg"));
			while(scanner.hasNext()){
				temp[z] = scanner.nextLine();
				z++;
				if(z == 4){
					levelHolder.add(temp);
					z = 0;
					temp = new String[4];
				}
			}
			for(int i = 0; i < levelHolder.size(); i++){
				if(levelHolder.get(i)[0].equals(levelName)){
					id = Integer.parseInt(levelHolder.get(i)[2]);
					break;
				}
			}
			scanner.close();
			return id;
		} catch (FileNotFoundException e) {
			System.err.println("Could not generate level ID for level name " + levelName);
			return -1;
		}
	}
	
	//Due to the way sprites are stored in display boxes this function uses relative mouse positions to calculate if the mouse is
	//hovered over a specific sprite within a displaybox and if so what one
	private static SpriteInt calculateSpriteHover(){
		DisplayBox temp = null;
		SpriteInt sprite = new SpriteInt(null, -1);
		for(int i = 0; i < Engine.boxes.size(); i++){
			if(Environment.mouseOnComponent(Engine.boxes.get(i).xPos, Engine.boxes.get(i).yPos, Engine.boxes.get(i).width, Engine.boxes.get(i).height) && Engine.boxes.get(i).active){
				temp = Engine.boxes.get(i);
				sprite.Int = i;
				break;
			}
		}
		if(temp != null){
			for(int i = 0; i < temp.sprites.length; i++){
				if(Environment.mouseOnComponent(temp.xPos + temp.sprites[i].xPos - temp.xScroll, temp.yPos + (int)(temp.sprites[i].yPos - temp.yScroll * temp.heightsDown), temp.sprites[i].width, temp.sprites[i].height)){
					sprite.sprite = temp.sprites[i];
					break;
				}
			}
		}
		return sprite;
	}
	
	//Returns all sprite sheets that have been imported into the IDE so far
	private static String[] getImportedSheets(){
		List<String> temp = new ArrayList<String>();
		String[] sprites = null;
		try {
			Scanner scanner = new Scanner(new FileReader(Environment.resLocation + "spriteSheets/config.cfg"));
			while(scanner.hasNext()){
				temp.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(temp.size() > 0){
			sprites = new String[temp.size()];
			for(int i = 0; i < sprites.length; i++){
				sprites[i] = temp.get(i);
			}
		}
		return sprites;
	}
}
