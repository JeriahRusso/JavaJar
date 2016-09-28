package engine.main.render.sprites;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ide.main.Environment;

//Spritesheet handling code developed by VanZeben

public class SpriteSheet {

	public String path;
	
	public int height;
	public int width;
	
	public int[] pixels;
	
	public SpriteSheet(String path){
		BufferedImage image = null;
		
		try{
			image = ImageIO.read(SpriteSheet.class.getResourceAsStream(path));
		}catch(Exception e){
			try{
				image = ImageIO.read(new File(path));
				
				if(image == null){
					return;
				}
				
				this.path = path;
				this.width = image.getWidth();
				this.height = image.getHeight();
				
				pixels = image.getRGB(0, 0, width, height, null, 0, width);
			}catch(Exception e1){
				Environment.debug(new String[]{"Unavailable Resource!"});
			}
		}

		if(image == null){
			return;
		}
		
		this.path = path;
		this.width = image.getWidth();
		this.height = image.getHeight();
		
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
	}
	
}
