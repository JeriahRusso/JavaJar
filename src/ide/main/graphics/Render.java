package ide.main.graphics;

public class Render {

	public int width;
	public int height;
	public int[] pixels;
	//Render class functions identically to that in the engine
	public Render(int width, int height){
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
	}
	
	public void draw(Render render, int xOff, int yOff){
		for(int x = 0; x < render.width; x++){
			int xx = x + xOff;
			if(xx >= width || xx < 0){
				continue;
			}
			for(int y = 0; y < render.height; y++){
				int yy = y + yOff;
				if(yy >= height || yy < 0){
					continue;
				}
				pixels[xx + yy * width] = render.pixels[x + y * render.width];
			}
		}
	}
	
	public void draw(Render render, int xOff, int yOff, int[] alphaColours){
		boolean isAlpha = false;
		for(int x = 0; x < render.width; x++){
			int xx = x + xOff;
			if(xx >= width || xx < 0){
				continue;
			}
			for(int y = 0; y < render.height; y++){
				int yy = y + yOff;
				if(yy >= height || yy < 0){
					continue;
				}
				for(int i = 0; i < alphaColours.length; i++){
					if(render.pixels[x + y * render.width] == alphaColours[i]){
						isAlpha = true;
						break;
					}
				}
				if(!isAlpha){
					pixels[xx + yy * width] = render.pixels[x + y * render.width];
				}
				isAlpha = false;
			}
		}
	}
	
	public void drawScale(Render render, int xOff, int yOff, int scale){
		for(int x = 0; x < render.width; x++){
			int xx = (x + xOff) * scale;
			if(xx >= width || xx < 0){
				continue;
			}
			for(int y = 0; y < render.height; y++){
				int yy = (y + yOff) * scale;
				if(yy >= height || yy < 0){
					continue;
				}
				for(int i = 0; i < scale; i++){
					for(int ii = 0; ii < scale; ii++){
						pixels[(xx + i) + (yy + ii) * width] = render.pixels[x + y * render.width];
					}
				}
			}
		}
	}
	
	public void drawScaleAlpha(Render render, int xOff, int yOff, int scale, int[] alphaColours){
		boolean isAlpha = false;
		for(int x = 0; x < render.width; x++){
			int xx = (x + xOff) * scale;
			if(xx >= width || xx < 0){
				continue;
			}
			for(int y = 0; y < render.height; y++){
				int yy = (y + yOff) * scale;
				if(yy >= height || yy < 0){
					continue;
				}
				for(int i = 0; i < alphaColours.length; i++){
					if(render.pixels[x + y * render.width] == alphaColours[i]){
						isAlpha = true;
						break;
					}
				}
				if(!isAlpha){
					for(int i = 0; i < scale; i++){
						for(int ii = 0; ii < scale; ii++){
							pixels[(xx + i) + (yy + ii) * width] = render.pixels[x + y * render.width];
						}
					}
				}
				isAlpha = false;
			}
		}
	}
}
