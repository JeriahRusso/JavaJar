package engine.main.render.sprites.entities;


import engine.main.CollisionData;
import engine.main.Timer;
import engine.main.render.sprites.SpriteSheet;

public class Player extends Mob {
	//A specilised mob the main difference is that the players movement is controled through input instead of chance and algorithm
	public static int id = 0;
	public int lastTime = Timer.getTicks();
	//private boolean lastUp, lastRight, lastDown, lastLeft;
	private boolean stopUp, stopRight, stopDown, stopLeft;
	
	public Player(int xPos, int yPos, int width, int height, int sheetX, int sheetY, int damage, int health, int speed, int animationWidth, int animationHeight, int identifier, SpriteSheet sheet) {
		super(0, width, height, xPos, yPos, sheetX, sheetY, damage, health, speed, true, sheet, animationWidth, animationHeight, identifier, 0);
	}

	@Override
	public void move(boolean up, boolean down, boolean left, boolean right, CollisionData data){
		moving = false;
		
		if(right && !data.collisions[1]){
			xPos += speed;
			moving = true;
		}
		if(left && !data.collisions[3]){
			xPos -= speed;
			moving = true;
		}
		if(up && !data.collisions[0]){
			yPos -= speed;
			moving = true;
		}
		if(down && !data.collisions[2]){
			yPos += speed;
			moving = true;
		}
		
		if(data.collisions[0]){
			stopUp = true;
		}
		if(data.collisions[1]){
			stopRight = true;
		}
		if(data.collisions[2]){
			stopDown = true;
		}
		if(data.collisions[3]){
			stopLeft = true;
		}
		
		if(stopUp && !up){
			stopUp = false;
			yPos += speed;
		}
		if(stopRight && !right){
			stopRight = false;
			xPos -= speed;
		}
		if(stopDown && !down){
			stopDown = false;
			yPos -= speed;
		}
		if(stopLeft && !left){
			stopLeft = false;
			xPos += speed;
		}
		
		//AnimateMovement(up, down, left, right);
	}

	//animates the movement depending on the movement of the player and how long they have been moving in that direction
	/*private void AnimateMovement(boolean up, boolean down, boolean left, boolean right) {
		if(moving == false){
			lastTime = Timer.getTicks();
		}
		if(down){
			lastDown = true;
			if(Timer.getTicks() - lastTime >= 60 / 3){
				lastTime = Timer.getTicks();
				animX += 1;
				if(animX >= 3){
					animX = 1;
				}
			}
		}
		else if(up){
			lastUp = true;
			if(Timer.getTicks() - lastTime >= 60 / 3){
				if(animX < 3){
					animX = 2;
				}
				lastTime = Timer.getTicks();
				animX += 1;
				if(animX >= 6){
					animX = 4;
				}
			}
		}
		else if(right){
			lastRight = true;
			if(Timer.getTicks() - lastTime >= 60 / 3){
				if(animX < 6){
					animX = 5;
				}
				lastTime = Timer.getTicks();
				animX += 1;
				if(animX >= 9){
					animX = 6;
				}
			}
		}
		else if(left){
			lastLeft = true;
			if(Timer.getTicks() - lastTime >= 60 / 3){
				lastTime = Timer.getTicks();
				animX += 1;
				if(animX >= 3){
					animX = 1;
				}
			}
		}
		
		if(!up && !right && !down && !left){
			if(lastDown){
				animX = 0;
				lastDown = false;
			}
			else if(lastUp){
				animX = 3;
				lastUp = false;
			}
			else if(lastRight){
				animX = 6;
				lastRight = false;
			}
			else if(lastLeft){
				animX = 0;
				lastLeft = false;
			}
		}
	}*/
	
}
