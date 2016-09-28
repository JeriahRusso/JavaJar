package engine.main.render.sprites.entities;

import java.util.Random;

import engine.main.CollisionData;
import engine.main.render.sprites.SpriteSheet;

public class Mob extends AnimatedSprite{
	//An extension of AnimatedSprite this allows for the generation of mobs within the game.
	//It acts both as a base class for specific mobs such as the player and as a generalised class for mobs
	//Especially for those imported from the IDE
	public String name;
	
	public Random random = new Random();
	
	public int health;
	public int damage;
	public int speed;
	public int dir = 0;
	
	public boolean moving;
	
	public Mob(int id, int width, int height, int xPos, int yPos, int sheetX, int sheetY, int damage, int health, int speed, boolean visible, SpriteSheet sheet, int animationWidth, int animationHeight, int identifier, int greenData) {
		super(id, width, height, xPos, yPos, sheetX, sheetY, visible, true, sheet, animationWidth, animationHeight, identifier, greenData);
		this.damage = damage;
		this.health = health;
		this.speed = speed;
	}
	
	public void move(boolean up, boolean down, boolean left, boolean right, CollisionData data){
		if(random.nextInt(55) > 43){
			dir = random.nextInt(5);
		}
		if(dir == 1){
			xPos += speed;
		}
		if(dir == 2){
			xPos -= speed;
		}
		if(dir == 3){
			yPos += speed;
		}
		if(dir == 4){
			yPos -= speed;
		}
	}
}
