package engine.main.render;

public class Render {

	public int width;
	public int height;
	public int[] pixels;
	
	public Render(int width, int height){
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
	}
	
	//This method allows for two classes the extend render to combine pixel data with the one calling
	//The draw function being the base pixels and the other being drawn ontop
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
	
	//Similar to other draw but will accept "alpha colours" which are left blank
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
	
	//Majorly for lighting this function takes two renders and only edits one if the colour is closer to white
	//In the render being passed in
	public void combine(Render render, int xOff, int yOff){
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
				if(pixels[xx + yy * width] == 0){
					pixels[xx + yy * width] = render.pixels[x + y * render.width];
				}
				else if(pixels[xx + yy * width] > render.pixels[x + y * render.width]){
					pixels[xx + yy * width] = render.pixels[x + y * render.width];
				}
			}
		}
	}
	
	public void combine(Render render, int xOff, int yOff, int[] alpha){
		for(int x = 0; x < render.width; x++){
			int xx = x + xOff;
			if(xx >= width || xx < 0){
				continue;
			}
			b:for(int y = 0; y < render.height; y++){
				int yy = y + yOff;
				if(yy >= height || yy < 0){
					continue;
				}
				for(int i = 0; i < alpha.length; i++){
					if(render.pixels[x + y * render.width] == alpha[i]){
						break b;
					}
				}
				if(pixels[xx + yy * width] == 0){
					pixels[xx + yy * width] = render.pixels[x + y * render.width];
				}
				else if(pixels[xx + yy * width] > render.pixels[x + y * render.width]){
					pixels[xx + yy * width] = render.pixels[x + y * render.width];
				}
			}
		}
	}
}
