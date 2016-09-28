package ide.main;

import ide.main.graphics.Render;

public class Screen extends Render{
	
	public Engine system;
	
	public Screen(int width, int height){
		super(width, height);
		system = new Engine(width, height);
	}
	
	public void clear(){
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = 0;
		}
		system.clear();
	}
	
	public void tick(){
		system.tick();
	}
	
	public void render(){
		system.render();
		draw(system, 0, 0);
	}
	
}
