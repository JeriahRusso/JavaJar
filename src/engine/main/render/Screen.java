package engine.main.render;

import java.util.ArrayList;
import java.util.List;

import engine.main.render.core.Level;
import engine.main.render.core.LevelFile;

public class Screen extends Render{
	
	private Level level;
	
	private int scale;
	
	public List<LevelFile> levels = new ArrayList<LevelFile>();
	
	//This class is a mid-point between the screen and all the calculations done in the engine
	public Screen(int width, int height, int scale, String initialLocation) {
		super(width, height);
		this.scale = scale;
		addLevel(initialLocation, 0);
		changeLevel(0);
	}
	
	public void tick(){
		level.tick();
	}
	
	public void clear(){
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = 0;
		}
	}
	
	public void render(){
		level.render();
		draw(level, 0, 0);
	}
	
	public void addLevel(String location, int id){
		if(id < levels.size()){
			System.err.println("Duplicate level id " + id);
			System.exit(1);
		}
		levels.add(id, new LevelFile(location, id));
	}
	
	public void changeLevel(int levelId){
		if(levelId > levels.size()){
			System.err.println("Invalid Level Load ID " + levelId);
			System.exit(1);
		}
		level = new Level(width, height, scale, false, levels.get(levelId));
	}

}
