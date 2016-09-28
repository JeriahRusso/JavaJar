package engine.main.render.core.lighting;

import engine.main.render.Render;
import engine.main.render.TileData;
import engine.main.render.sprites.SpriteSheet;

public class AreaLight extends LightSource{
	
	private boolean first = true;
	Render light = new Render(radius * 2 * TileData.tileSize, radius * 2 * TileData.tileSize);
	Render maskLayer = new Render(radius * 2 * TileData.tileSize, radius * 2 * TileData.tileSize);
	
	public AreaLight(int id, int width, int height, int xPos, int yPos, int sheetX, int sheetY, int radius, boolean visible, boolean glowing, boolean collidable, SpriteSheet sheet, int animationWidth, int animationHeight, int identifier, int greenData) {
		super(id, width, height, xPos, yPos, sheetX, sheetY, radius, visible, glowing, collidable, sheet, animationWidth, animationHeight, identifier, greenData);
		this.radius = radius;
	}

	//The calculation for the light works by testing if a pixel is one radius from the centre and if so making it equal to an alpha colour
	//This generates an empty circle which is then run over and filled in giving the base of the light engine
	public Render[] lightRender() {
		if(first){
			int[] distance = new int[light.pixels.length];
			for(int i = 0; i < light.pixels.length; i++){
				light.pixels[i] = 0xff000000;
			}
			int y0 = light.height / 2;
			int x0 = light.width / 2;
			for(int x = 0; x < light.width; x++){
				for(int y = 0; y < light.height; y++){
					distance[x + y * light.width] = (int)Math.sqrt(Math.pow((x0 - x), 2) + Math.pow((y0 - y), 2));
					if(distance[x + y * light.width] == radius * TileData.tileSize - 1){
						light.pixels[x + y * light.width] = 0xffff00ff;
					}
				}
			}
			boolean begin = false;
			int lastY = 0;
			for(int x = 0; x < light.width; x++){
				lastY = 0;
				for(int y = 0; y < light.height; y++){
					if(light.pixels[x + y * light.width] == 0xffff00ff && (lastY == 0 || y - lastY > radius - 1)){
						if(!begin){
							begin = true;
							lastY = y;
						}
						else{
							begin = false;
							break;
						}
					}
					else if(begin){
						light.pixels[x + y * light.width] = 0xffff00ff;
					}
				}
			}
			maskLayer = generateMaskData(distance, maskLayer);
			first = false;
		}
		return new Render[]{light, maskLayer};
	}

	public boolean isCollidable() {
		return false;
	}
	
	//The mask layer is a set of circles that change from black to white as distance from the centre of the light source increases
	//These later have their colours subtracted from the pixel colours being displayed which gives a appealing and somewhat realistic
	//Lighting effect
	private Render generateMaskData(int[] distance, Render maskLayer){
		Render temp = new Render(maskLayer.width, maskLayer.height);
		int radius = maskLayer.width / 2;
		int increments = (int)(0xff / radius);
		if(increments == 0){
			increments = 1;
		}
		for(int i = 0; i < temp.pixels.length; i++){
			int hex = increments * distance[i];
			if(hex > 0xff){
				hex = 0xff;
			}
			int colour = (hex << 16) + (hex << 8) + hex;
			temp.pixels[i] = colour;
		}
		return temp;
	}
}