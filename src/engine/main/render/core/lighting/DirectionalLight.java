package engine.main.render.core.lighting;

import engine.main.render.Render;
import engine.main.render.TileData;
import engine.main.render.sprites.SpriteSheet;

public class DirectionalLight extends LightSource{
	
	public int length;
	public int rotation;
	
	public DirectionalLight(int id, int width, int height, int xPos, int yPos, int sheetX, int sheetY, int length, int rotation, boolean visible, boolean glowing, boolean collidable, SpriteSheet sheet, int animationWidth, int animationHeight, int identifier, int greenData) {
		super(id, width, height, xPos, yPos, sheetX, sheetY, length, visible, glowing, collidable, sheet, animationWidth, animationHeight, identifier, greenData);
		this.length = length;
		this.rotation = rotation;
	}

	//This light source works by creating two diagonal lines moving away from eachother and filling the gap between to give the base
	//Light data
	public Render[] lightRender() {
		Render light = new Render(length * TileData.tileSize, length * TileData.tileSize);
		Render maskLayer = new Render(light.width, light.height);
		int[] distances = new int[maskLayer.pixels.length];
		int[] midDistances = new int[maskLayer.pixels.length];
		int y0 = light.height / 2;
		int x0 = 0;
		
		for(int i = 0; i < light.pixels.length; i++){
			light.pixels[i] = 0xff000000;
		}
		
		//TODO calculate the pixel orientations for each of the four directions
		if(rotation == 0){
			for(int x = 0; x < light.width; x++){
				for(int y = 0; y < light.height; y++){
					if(x > width - 1){
						distances[x + y * light.width] = (int)Math.sqrt(Math.pow((x0 - x), 2) + Math.pow((y0 - y), 2));
						if(y <= light.height / 2){
							midDistances[x + y * light.width] = (int)Math.sqrt(Math.pow(((light.height / 2) - y), 2));
						}
						else{
							midDistances[x + y * light.width] = (int)Math.sqrt(Math.pow((y - light.height / 2), 2));
						}
						if(y <= (light.height / 2) + (x / 2) && y >= (light.height / 2) - (x) / 2){
							light.pixels[x + y * light.width] = 0xffff00ff;
						}
					}
				}
			}
		}
		
		maskLayer = generateMaskData(distances, midDistances, maskLayer, light);
		return new Render[]{light, maskLayer};
	}
	
	//This mask data is very similar to that of an area light however the point it measures distance from is at the edge of the render
	//rectangle and the distance from the centre line is also taken into account to give a more torchlike look to the light.
	private Render generateMaskData(int[] distances, int[] midDistances, Render maskLayer, Render lightLayer){
		Render temp = new Render(maskLayer.width, maskLayer.height);
		int increments = (int)(0xff / maskLayer.width);
		for(int x = 0; x < temp.width; x++){
			for(int y = 0; y < temp.height; y++){
				if(x > width - 1 && y <= (temp.height / 2) + (x / 2) && y >= (temp.height / 2) - (x / 2)){
					int hex = increments * distances[x + y * maskLayer.width] / 2;
					hex += (0xff / (lightLayer.height / 2)) * midDistances[x + y * maskLayer.width] / 4;
					if(hex > 0xff){
						hex = 0xff;
					}
					int colour = (hex << 16) + (hex << 8) + hex;
					temp.pixels[x + y * temp.width] = colour;
				}
			}
		}
		return temp;
	}

}
