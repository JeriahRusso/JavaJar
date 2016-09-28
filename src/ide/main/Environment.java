package ide.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import engine.main.Timer;
import ide.main.graphics.TileData;
import ide.main.inputs.InputHandler;

public class Environment extends Canvas implements Runnable{

	//This is the main class of the IDE
	private static final long serialVersionUID = 1L;
	public static final String TITLE = "Java Jar";
	public static int width;
	public static int height;
	public static int scale;
	
	public static String[] resLocationStrings;
	public static String resLocation;
	public static String location = "/";

	public static boolean running = false;
	public boolean ready = false;
	
	public BufferedImage img;
	public int[] pixels;
	
	private Screen screen;
	public static InputHandler input;

	public Environment(){
		setUp();
		Dimension size = new Dimension(width, height);
		
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
	}
	
	public synchronized void start(){
		if(running) return;
		running = true;
		selectProject();
		run();
	}
	
	public synchronized void stop(){
		if(!running) return;
		running = false;
		System.exit(0);
	}
	
	//This is always called at the beginning of a session to allow for a project to be selected by the user
	public void selectProject(){
		JFrame frame = new JFrame();
		Container content = frame.getContentPane();
		frame.setSize(800, 300);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Project Selection");
		frame.setResizable(false);
		
		String[] locationNames = new String[resLocationStrings.length];
		for(int i = 0; i < locationNames.length; i++){
			locationNames[i] = resLocationStrings[i];
		}
		
		JPanel selections = new JPanel();
		JPanel buttons = new JPanel();
		
		selections.setLayout(new GridLayout(1, 1));
		buttons.setLayout(new GridLayout(1, 2, 10, 0));
		
		JComboBox<String> projects = new JComboBox<String>(locationNames);
		projects.setVisible(true);
		projects.setSelectedItem(0);
		
		JButton newProject = new JButton("New Project");
		newProject.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				buildProject();
				frame.dispose();
			}
			
		});

		JButton select = new JButton("Select Project");
		select.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				loadProject(projects.getSelectedIndex());
				frame.dispose();
				ready = true;
			}
			
		});
		
		selections.add(projects);
		buttons.add(select);
		buttons.add(newProject);
		
		content.add(selections, BorderLayout.NORTH);
		content.add(buttons, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	//Once a project is selected this sets up all the necessary variables to allow the IDE to run the specific Project
	public void loadProject(int index){
		resLocation = resLocationStrings[index].split(" -")[0];
		List<String> information = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new FileReader(resLocation + "config.cfg"));
			while(scanner.hasNext()){
				information.add(scanner.nextLine());
			}
			try{
				TileData.tileSize = stringConvert(information.get(4));
				scale = stringConvert(information.get(3));
			}
			catch(Exception e){
				System.err.println("The config file at " + resLocation + "config.cfg has errors. File may be corrupt!");
				System.exit(1);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.err.println("The selected projects config file is missing!");
			System.exit(1);
		}
	}
	
	//This makes the screen in which a project is built, it does no actually set all of the variables for storage however, that is another function
	public void buildProject(){
		JFrame frame = new JFrame();
		Container content = frame.getContentPane();
		frame.setSize(800, 300);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Project Creation");
		frame.setResizable(false);
		
		content.setLayout(new BorderLayout());
		
		JPanel textFields = new JPanel();
		JPanel buttons = new JPanel();
		
		textFields.setLayout(new GridLayout(5, 2, 0, 10));
		buttons.setLayout(new GridLayout(1, 2, 10, 0));
		
		JTextField title = new JTextField();
		JTextField width = new JTextField();
		JTextField height = new JTextField();
		JTextField scale = new JTextField();
		JTextField tileSize = new JTextField();
		
		JLabel titleLabel = new JLabel("Project Name");
		JLabel widthLabel = new JLabel("Screen Width");
		JLabel heightLabel = new JLabel("Screen Height");
		JLabel scaleLabel = new JLabel("Scale");
		JLabel tileSizeLabel = new JLabel("Tile Size");
		
		textFields.add(titleLabel);
		textFields.add(title);
		textFields.add(widthLabel);
		textFields.add(width);
		textFields.add(heightLabel);
		textFields.add(height);
		textFields.add(scaleLabel);
		textFields.add(scale);
		textFields.add(tileSizeLabel);
		textFields.add(tileSize);
		
		JButton back = new JButton("Back");
		back.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				selectProject();
			}
			
		});
		
		JButton confirm = new JButton("Confirm");
		confirm.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if(stringIntCheck(new String[]{width.getText(), height.getText(), scale.getText(), tileSize.getText()})){
					frame.dispose();
					createProject(title.getText(), width.getText(), height.getText(), scale.getText(), tileSize.getText());
					ready = true;
				}
				else{
					System.err.println("Some input fields have incorrect data");
				}
			}
			
		});
		
		buttons.add(back);
		buttons.add(confirm);
		
		content.add(textFields, BorderLayout.CENTER);
		content.add(buttons, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	//Once correct data has been input to build a function this is called so that the function and all its folders and basic requirements
	//Will be generated and stored
	public void createProject(String title, String width, String height, String scale, String tileSize){
		boolean success = (new File("res/Projects/" + title)).mkdir();
		if(!success){
			int i = 1;
			while(!(new File("res/Projects/" + title + "(" + i + ")")).mkdir()){
				i++;
			}
			System.err.println(title + " was already a project so name was changed to " + title + "(" + i + ")");
			title = title + "(" + i + ")";
		}
		success = (new File("res/Projects/" + title + "/levels")).mkdir();
		success = (new File("res/Projects/" + title + "/spriteSheets")).mkdir();
		success = (new File("res/Projects/" + title + "/spriteSheets/tiles")).mkdir();
		success = (new File("res/Projects/" + title + "/spriteSheets/sprites")).mkdir();
		success = (new File("res/Projects/" + title + "/codes")).mkdir();
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("res/Projects/" + title + "/config.cfg"), "UTF8"))){
			writer.write("res/Projects/" + title + "/");
			writer.newLine();
			writer.write(width);
			writer.newLine();
			writer.write(height);
			writer.newLine();
			writer.write(scale);
			writer.newLine();
			writer.write(tileSize);
			writer.close();
			addToFile("res/config.cfg", new String[]{"res/Projects/" + title + "/ - " + title});
			resLocation = "res/Projects/" + title + "/";
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to generate config file for project " + title);
			System.exit(1);
		}
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "spriteSheets/tiles/void.td"), "UTF8"))){
			writer.write("void");
			writer.newLine();
			writer.write("-1");
			writer.newLine();
			writer.write("00");
			writer.newLine();
			writer.write("void.png");
			writer.newLine();
			writer.write("0");
			writer.newLine();
			writer.write("0");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "spriteSheets/tiles/colours.dat"), "UTF8"))){
			writer.write("00");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "spriteSheets/tiles/id.dat"), "UTF8"))){
			writer.write("-1,void");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "spriteSheets/sprites/void.sd"), "UTF8"))){
			writer.write("-1");
			writer.newLine();
			writer.write("void");
			writer.newLine();
			writer.write("voidSprite.png");
			writer.newLine();
			writer.write("0");
			writer.newLine();
			writer.write("0");
			writer.newLine();
			writer.write("1");
			writer.newLine();
			writer.write("1");
			writer.newLine();
			writer.write("00");
			writer.newLine();
			writer.write("Sprite");
			writer.newLine();
			writer.write("0");
			writer.newLine();
			writer.write("0");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error in the creation of the void sprite!");
			System.exit(1);
		}
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "spriteSheets/sprites/colours.dat"), "UTF8"))){
			writer.write("00");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.resLocation + "spriteSheets/sprites/id.dat"), "UTF8"))){
			writer.write("-1,void");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage image = new BufferedImage(Integer.parseInt(tileSize), Integer.parseInt(tileSize), BufferedImage.TYPE_INT_RGB); 
		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, Integer.parseInt(tileSize), Integer.parseInt(tileSize));
		try {
			ImageIO.write(image, "png", new File(Environment.resLocation + "spriteSheets/void.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error occured in the creation of the default void tile!");
			System.exit(1);
		}
		
		image = new BufferedImage(Integer.parseInt(tileSize), Integer.parseInt(tileSize), BufferedImage.TYPE_INT_RGB); 
		g = image.getGraphics();
		g.setColor(new Color(0xff00ff));
		g.fillRect(0, 0, Integer.parseInt(tileSize), Integer.parseInt(tileSize));
		try {
			ImageIO.write(image, "png", new File(Environment.resLocation + "spriteSheets/voidSprite.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error occured in the creation of the default void sprite!");
			System.exit(1);
		}
		
		setUp();
		loadProject(resLocationStrings.length - 1);
	}
	
	//A class built due to BufferedWriter's overwriting the entire file rather than adding to it
	//While somewhat inefficient to read an entire file and then write back to it, it is one of the easiest solutions
	//I could develop at the time and functions correctly
	public static boolean addToFile(String fileLocation, String[] addedContent){
		List<String> content = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new FileReader(fileLocation));
			while(scanner.hasNext()){
				content.add(scanner.nextLine());
			}
			scanner.close();
			for(int i = 0; i < addedContent.length; i++){
				content.add(addedContent[i]);
			}
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLocation), "UTF8"))){
				for(int i = 0; i < content.size(); i++){
					writer.write(content.get(i));
					writer.newLine();
				}
				writer.close();
			} 
			catch (IOException e) {
				return false;
			}
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find file " + fileLocation);
			return false;
		}
	}
	
	//Returns boolean value dependent on if all strings in an array can be converted to integers or not
	public static boolean stringIntCheck(String[] strings){
		boolean areIntegers = true;
		for(int i = 0; i < strings.length; i++){
			try{
				Integer.parseInt(strings[i]);
			}
			catch(Exception e){
				areIntegers = false;
				break;
			}
		}
		return areIntegers;
	}
	
	public void init(){
		String[] temp = resLocation.split("/");
		for(int i = 1; i < temp.length; i++){
			location += temp[i];
			location += "/";
		}
		screen = new Screen(width, height);
		input = new InputHandler(this);
	}
	
	public void run() {
		while(!ready){
			//For some reason this must be running in this while loop for the boolean change to be detected
			try{
				Thread.sleep(1);
			}
			catch(Exception e){
				
			}
		}
		init();
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/60.0;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			
			while(delta >= 1){
				delta--;
				ticks++;
				tick();
			}
			frames++;
			render();
			
			if(System.currentTimeMillis() - lastTimer >= 1000){
				lastTimer += 1000;
				System.out.println("Frames: " + frames + " Ticks: " + ticks);
				frames = 0;
				ticks = 0;
			}
		}
		stop();
	}
	
	public void tick(){
		Timer.ticks++;
		screen.tick();
	}
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		
		screen.clear();
		screen.render();
		
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = screen.pixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(img, 0, 0, width, height, null);
		
		g.dispose();
		bs.show();
	}
	
	public static int stringConvert(String number){
		try{
			int i = Integer.parseInt(number);
			return i;
		}
		catch(Exception e){
			System.err.println("Attempted to convert " + number + " to int but was unsuccesful");
			return 0;
		}
	}
	
	//Sets the variables such as height of a window to match the dimensions of the users screen
	public void setUp(){
		List<String> locs = new ArrayList<String>();
		boolean useDefault = false;
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
		
		try {
			Scanner scanner = new Scanner(new FileReader("res/config.cfg"));
			while(scanner.hasNext()){
				locs.add(scanner.nextLine());
			}
			if(locs.size() == 0){
				useDefault = true;
			}
			scanner.close();
		} 
		catch (FileNotFoundException e){
			System.err.println("The config file has been moved or deleted");
			createBaseFiles();
			setUp();
			return;
		}
		
		if(useDefault){
			try{
				Scanner scanner = new Scanner(new FileReader("res/default.cfg"));
				while(scanner.hasNext()){
					locs.add(scanner.nextLine());
				}
				scanner.close();
			} 
			catch(FileNotFoundException e){
				System.err.println("Resorted to default config but the file was missing");
				System.exit(1);
			}
		}
		
		if(locs.size() == 1){
			resLocationStrings = new String[1];
			resLocationStrings[0] = locs.get(0);
		}
		else if(locs.size() > 1){
			resLocationStrings = new String[locs.size()];
			for(int i = 0; i < locs.size(); i++){
				resLocationStrings[i] = locs.get(i);
			}
		}
		else{
			System.err.println("Default config and config file empty or not found!");
			System.exit(1);
		}
	}
	
	//Returns true if the mouses x and y position collide with that of the positions passed to check if the mouse is hovered over
	//The component on the screen
	public static boolean mouseOnComponent(int xPos, int yPos, int width, int height){
		if(input.getMouseX() >= xPos && input.getMouseX() <= xPos + width){
			if(input.getMouseY() >= yPos && input.getMouseY() <= yPos + height){
				return true;
			}
		}
		return false;
	}
	
	private void createBaseFiles(){
		new File("res").mkdir();
		new File("res/Projects").mkdir();
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("res/config.cfg"), "UTF8"))){
			writer.write("");
			writer.close();
		}
		catch (IOException e) {
			return;
		}
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("res/default.cfg"), "UTF8"))){
			writer.write("res/Projects/default - Default");
			writer.close();
		} 
		catch (IOException e) {
			return;
		}
	}
	
	public static void debug(String[] info){
		JFrame frame = new JFrame("Java Jar Debug Consol");
		JPanel panel = new JPanel();
		frame.setSize(600, 600);
		panel.setLayout(new GridBagLayout());
		panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		GridBagConstraints c = new GridBagConstraints();
		
		JTextArea jt = new JTextArea();
		c.fill = GridBagConstraints.BOTH;
		for(int i = 0; i < info.length; i++){
			jt.append(info[i] + "\n");
		}
		panel.add(new JScrollPane(jt), c);
		frame.add(panel);
		frame.setVisible(true);
	}
	
	public static void main(String args[]){
		Environment en = new Environment();
		JFrame frame = new JFrame();
		frame.add(en);
		frame.setTitle(TITLE);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();
		frame.setLocation(-2, 0);
		
		en.start();
	}
}
