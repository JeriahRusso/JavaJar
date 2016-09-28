package ide.main.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import ide.main.Environment;
import ide.main.data.ButtonFunctions;

public class DropDownMenu extends Render{

	public String[] components;
	
	private int[] functionId;
	public int colour, selectColour, xPos, yPos, componentWidth, componentHeight;
	
	public boolean active = false;
	public boolean entered = false;
	public boolean mouseActive = false;
	
	//These menus are almost always linked to buttons and allow for lists of functionality
	public DropDownMenu(String[] components, int colour, int xPos, int yPos, int[] functionId){
		super(0,0);
		this.functionId = functionId;
		this.components = components;
		this.colour = colour;
		this.selectColour = colour + 0x333333;
		if(this.selectColour > 0xffffffff){
			this.selectColour = 0xffffffff;
		}
		this.xPos = xPos;
		this.yPos = yPos;
		generateSize(components);
		pixels = new int[width * height * components.length];
	}
	
	public void render(){
		if(active){
			Font font = new Font("sanserif", Font.PLAIN, 12);
			for(int i = 0; i < components.length; i++){
				BufferedImage img = new BufferedImage(componentWidth, componentHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2D = img.createGraphics();
				g2D.setFont(font);
				g2D.setColor(Color.black);
				g2D.drawString(components[i], 0, componentHeight - 4);
				g2D.dispose();
				for(int x = 0; x < componentWidth; x++){
					for(int y = 0; y < componentHeight; y++){
						if(img.getRGB(x, y) != 0){
							pixels[x + (y + (i * componentHeight)) * width] = 0;
						}
						else{
							if(xPos < Environment.input.getMouseX() && Environment.input.getMouseX() < xPos + componentWidth && yPos + i * componentHeight < Environment.input.getMouseY() && Environment.input.getMouseY() < yPos + i * componentHeight + componentHeight){
								pixels[x + (y + (i * componentHeight)) * width] = selectColour;
							}
							else{
								pixels[x + (y + (i * componentHeight)) * width] = colour;
							}
						}
					}
				}
			}
		}
	}
	
	public void tick(){
		deactivateCheck();
		for(int i = 0; i < functionId.length; i++){
			if(Environment.mouseOnComponent(xPos, yPos + i * componentHeight, componentWidth, componentHeight)){
				if(Environment.input.getMouseReleased()){
					optionSelect(i);
				}
			}
		}
	}
	
	//Determines the size of each component of the drop down menu by calculating the size of the text that is going to be written onto them
	//The largest width and height is taken and applied to all componentes so that they are constant while facilitating the largest element
	public void generateSize(String[] components){
		int tempWidth = 0;
		int tempHeight = 0;
		
		Font font = new Font("sanserif", Font.PLAIN, 12);
		FontMetrics metrics = new JLabel().getFontMetrics(font);
		for(int i = 0; i < components.length; i++){
			if(metrics.stringWidth(components[i]) > tempWidth){
				tempWidth = metrics.stringWidth(components[i]);
			}
			if(metrics.getMaxAscent() > tempHeight){
				tempHeight = metrics.getMaxAscent();
			}
		}
		componentWidth = tempWidth + 5;
		width = componentWidth;
		componentHeight = tempHeight + 4;
		height = componentHeight * components.length;
	}
	
	//runs during the time that a drop down menu is active and if the user clicks off of the menu or enteres and then exits the menu
	//it will close
	public void deactivateCheck(){
		if(mouseActive && Environment.input.getMouseReleased()){
			active = false;
			mouseActive = false;
			entered = false;
		}
		if(active){
			mouseActive = true;
			if(entered){
				if(Environment.input.getMouseX() < xPos || Environment.input.getMouseX() > xPos + width || Environment.input.getMouseY() < yPos || Environment.input.getMouseY() > yPos + height){
					active = false;
					entered = false;
					mouseActive = false;
				}
			}
			else{
				if((Environment.input.getMouseX() >= xPos && Environment.input.getMouseX() <= xPos + width) && (Environment.input.getMouseY() >= yPos && Environment.input.getMouseY() <= yPos + height)){
					entered = true;
				}
			}
		}
	}
	
	public void optionSelect(int location){
		ButtonFunctions.runFunctionId(functionId[location]);
	}
	
}
