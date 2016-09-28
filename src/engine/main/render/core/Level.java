package engine.main.render.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import engine.main.Clone;
import engine.main.CollisionData;
import engine.main.Main;
import engine.main.keys.InputHandler;
import engine.main.render.Render;
import engine.main.render.TileData;
import engine.main.render.core.lighting.AreaLight;
import engine.main.render.core.lighting.DirectionalLight;
import engine.main.render.core.lighting.LightSource;
import engine.main.render.sprites.SheetList;
import engine.main.render.sprites.entities.AnimatedSprite;
import engine.main.render.sprites.entities.Mob;
import engine.main.render.sprites.entities.Player;
import engine.main.render.sprites.entities.Sprite;

public class Level extends Render {
	
	//This is the major class to have all the calculations done on the level being displayed and run
	//private final int CHUNKSIZE = 10000;
	
	private int levelWidth;
	private int levelHeight;
	private int scale;
	private int xPos = 0;
	private int yPos = 0;
	private int playerXPos = 0;
	private int playerYPos = 0;
	//private int chunkWidth;
	//private int chunkHeight;
	
	private int[] baseAlpha = new int[]{0xffff00ff, 0xff7f007f};
	
	private boolean lit;
	
	private InputHandler input;
	
	private Render background;
	private Render spriteLayer;
	private Render lightLayer;
	private Render lightMaskLayer;
	
	private LevelFile level;
	
	public List<Sprite> spriteList = new ArrayList<Sprite>();
	public List<AnimatedSprite> animatedSpriteList = new ArrayList<AnimatedSprite>();
	public List<Mob> mobList = new ArrayList<Mob>();
	public List<LightSource> lightList = new ArrayList<LightSource>();
	
	public Level(int width, int height, int scale, boolean lit, LevelFile level){
		super(width / scale + TileData.tileSize, height / scale + TileData.tileSize);
		this.levelWidth = level.width * TileData.tileSize + TileData.tileSize;
		this.levelHeight = level.height * TileData.tileSize + TileData.tileSize;
		chunkSetup(levelWidth, levelHeight, scale);
		this.lit = lit;
		this.background = new Render(levelWidth, levelHeight);
		this.spriteLayer = new Render(levelWidth, levelHeight);
		this.lightLayer = new Render(levelWidth, levelHeight);
		this.lightMaskLayer = new Render(levelWidth, levelHeight);
		this.level = level;
		this.scale = scale;
		buildLevel();
	}
	
	public void tick(){
		input = Main.input;
		spriteMove();
		spriteAnimate();
		screenMove();
	}
	
	//The rendering is done in stages so that pixel manipulation was easier
	//Each layer exists as its own render object which is drawn onto the screen
	//The only known issue of this is that large levels cause OutOfMemory exceptions due to the amount of data attempting to be stored
	public void render(){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				pixels[x + y * width] = background.pixels[(x + xPos) + ((y + yPos) * background.width)];
			}
		}
		renderSprites();
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if(spriteLayer.pixels[(x + xPos) + ((y + yPos) * spriteLayer.width)] != 0xffff00ff && spriteLayer.pixels[(x + xPos) + ((y + yPos) * spriteLayer.width)] != 0xff7f007f){
					pixels[x + y * width] = spriteLayer.pixels[(x + xPos) + ((y + yPos) * spriteLayer.width)];
				}
			}
		}
		if(!lit){
			lightCalculate();
			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){
					if(lightLayer.pixels[(x + xPos) + ((y + yPos) * lightLayer.width)] != 0xffff00ff){
						pixels[x + y * width] = lightLayer.pixels[(x + xPos) + ((y + yPos) * lightLayer.width)];
					}
					if(pixels[x + y * width] != 0xff000000){
						Color c = new Color(pixels[x + y * width]);
						Color c2 = new Color(lightMaskLayer.pixels[(x + xPos) + ((y + yPos) * lightLayer.width)]);
						int red = c.getRed() - c2.getRed();
						int green = c.getGreen() - c2.getGreen();
						int blue = c.getBlue() - c2.getBlue();
						if(red < 0){
							red = 0;
						}
						if(green < 0){
							green = 0;
						}
						if(blue < 0){
							blue = 0;
						}
						int newColour = (red << 16) + (green << 8) + blue;
						pixels[x + y * width] = newColour;
					}
				}
			}
			//This is the quickest way I can think of to reset the layers as the calculations that run every tick
			//Require that these be empty or some VERY weird visual glitches occur
			lightMaskLayer.pixels = new int[lightMaskLayer.pixels.length];
			lightLayer.pixels = new int[lightLayer.pixels.length];
		}
	}
	
	public void buildLevel(){
		for(int x = 0; x < level.width; x++){
			for(int y = 0; y < level.height; y++){
				background.draw(level.tileId[x + y * level.width].drawTile(), x * TileData.tileSize, y * TileData.tileSize, baseAlpha);
			}
		}
		generateSprites();
		for(int i = 0; i < lightLayer.pixels.length; i++){
			lightLayer.pixels[i] = 0xff000000;
		}
	}
	
	public void generateSprites(){
		for(int x = 0; x < level.width; x++){
			for(int y = 0; y < level.height; y++){
				if(level.spriteId[x + y * level.width] instanceof Mob){
					if(level.spriteId[x + y * level.width] instanceof Player){
						screenPosition((Player)level.spriteId[x + y * level.width]);
					}
					mobList.add((Mob) level.spriteId[x + y * level.width]);
				}
				else if(level.spriteId[x + y * level.width] instanceof LightSource){
					lightList.add((LightSource) level.spriteId[x + y * level.width]);
				}
				else if(level.spriteId[x + y * level.width] instanceof AnimatedSprite){
					animatedSpriteList.add((AnimatedSprite) level.spriteId[x + y * level.width]);
				}
				else if(level.spriteId[x + y * level.width] != null){
					spriteList.add(level.spriteId[x + y * level.width]);
				}
			}
		}
	}
	
	//Centers the camera relative to the player
	public void screenPosition(Player player){
		if(player.xPos >= width / 2 + TileData.tileSize && player.xPos < levelWidth - width){
			xPos = player.xPos - width / 2;
		}
		if(player.xPos >= levelWidth - width){
			xPos = levelWidth - width;
		}
		if(player.yPos >= height / 2 + TileData.tileSize && player.yPos < levelHeight - height){
			yPos = player.yPos - height / 2;
		}
		if(player.yPos >= levelHeight - height){
			yPos = levelHeight - height;
		}
	}
	
	public void renderSprites(){
		
		for(int i = 0; i < spriteLayer.pixels.length; i++){
			spriteLayer.pixels[i] = 0xffff00ff;
		}
		
		for(int i = 0; i < spriteList.size(); i++){
			if(spriteList.get(i).xPos > xPos - TileData.tileSize * 3 && spriteList.get(i).xPos < xPos + width + TileData.tileSize * 3 && spriteList.get(i).yPos > yPos - TileData.tileSize * 3 && spriteList.get(i).yPos < yPos + height + TileData.tileSize * 3){
				spriteList.get(i).render();
			}
		}
		
		for(int i = 0; i < spriteList.size(); i++){
			if(spriteList.get(i).xPos > xPos - TileData.tileSize * 3 && spriteList.get(i).xPos < xPos + width + TileData.tileSize * 3 && spriteList.get(i).yPos > yPos - TileData.tileSize * 3 && spriteList.get(i).yPos < yPos + height + TileData.tileSize * 3){
				spriteLayer.draw(spriteList.get(i), spriteList.get(i).xPos, spriteList.get(i).yPos, baseAlpha);
			}
		}
		
		for(int i = 0; i < mobList.size(); i++){
			if(mobList.get(i).xPos > xPos - TileData.tileSize * 3 && mobList.get(i).xPos < xPos + width + TileData.tileSize * 3 && mobList.get(i).yPos > yPos - TileData.tileSize * 3 && mobList.get(i).yPos < yPos + height + TileData.tileSize * 3){
				mobList.get(i).render();
			}
		}
		
		for(int i = 0; i < mobList.size(); i++){
			if(mobList.get(i).xPos > xPos - TileData.tileSize * 3 && mobList.get(i).xPos < xPos + width + TileData.tileSize * 3 && mobList.get(i).yPos > yPos - TileData.tileSize * 3 && mobList.get(i).yPos < yPos + height + TileData.tileSize * 3){
				spriteLayer.draw(mobList.get(i), mobList.get(i).xPos, mobList.get(i).yPos, baseAlpha);
			}
		}
		
		for(int i = 0; i < animatedSpriteList.size(); i++){
			if(animatedSpriteList.get(i).xPos > xPos - TileData.tileSize * 3 && animatedSpriteList.get(i).xPos < xPos + width + TileData.tileSize * 3 && animatedSpriteList.get(i).yPos > yPos - TileData.tileSize * 3 && animatedSpriteList.get(i).yPos < yPos + height + TileData.tileSize * 3){
				animatedSpriteList.get(i).render();
			}
		}
		
		for(int i = 0; i < animatedSpriteList.size(); i++){
			if(animatedSpriteList.get(i).xPos > xPos - TileData.tileSize * 3 && animatedSpriteList.get(i).xPos < xPos + width + TileData.tileSize * 3 && animatedSpriteList.get(i).yPos > yPos - TileData.tileSize * 3 && animatedSpriteList.get(i).yPos < yPos + height + TileData.tileSize * 3){
				spriteLayer.draw(animatedSpriteList.get(i), animatedSpriteList.get(i).xPos, animatedSpriteList.get(i).yPos, baseAlpha);
			}
		}
		
		for(int i = 0; i < lightList.size(); i++){
			if(lightList.get(i).xPos > xPos - TileData.tileSize * 3 && lightList.get(i).xPos < xPos + width + TileData.tileSize * 3 && lightList.get(i).yPos > yPos - TileData.tileSize * 3 && lightList.get(i).yPos < yPos + height + TileData.tileSize * 3){
				lightList.get(i).render();
			}
		}
		
		for(int i = 0; i < lightList.size(); i++){
			if(lightList.get(i).xPos > xPos - TileData.tileSize * 3 && lightList.get(i).xPos < xPos + width + TileData.tileSize * 3 && lightList.get(i).yPos > yPos - TileData.tileSize * 3 && lightList.get(i).yPos < yPos + height + TileData.tileSize * 3){
				spriteLayer.draw(lightList.get(i), lightList.get(i).xPos, lightList.get(i).yPos, baseAlpha);
			}
		}
		
	}
	
	public void spriteAnimate(){
		for(int i = 0; i < animatedSpriteList.size(); i++){
			if(animatedSpriteList.get(i).xPos > xPos - TileData.tileSize && animatedSpriteList.get(i).xPos < xPos + width + TileData.tileSize){
				if(animatedSpriteList.get(i).yPos > yPos - TileData.tileSize && animatedSpriteList.get(i).yPos < yPos + height + TileData.tileSize){
					animatedSpriteList.get(i).Animate();
				}
			}
		}
		for(int i = 0; i < lightList.size(); i++){
			if(lightList.get(i).xPos > xPos - TileData.tileSize && lightList.get(i).xPos < xPos + width + TileData.tileSize){
				if(lightList.get(i).yPos > yPos - TileData.tileSize && lightList.get(i).yPos < yPos + height + TileData.tileSize){
					lightList.get(i).Animate();
				}
			}
		}
	}
	
	public void spriteMove(){
		for(int i = 0; i < mobList.size(); i++){
			Mob temp = mobList.get(i);
			CollisionData data = calculateCollision(mobList.get(i), input.up.getPressed(), input.down.getPressed(), input.left.getPressed(), input.right.getPressed());
			mobList.get(i).move(input.up.getPressed(), input.down.getPressed(), input.left.getPressed(), input.right.getPressed(), data);
			updateMobPosition(temp, mobList.get(i));
		}
	}
	
	public void screenMove(){
		for(int i = 0; i < mobList.size(); i++){
			if(mobList.get(i).getId() == Player.id){
				if(mobList.get(i).moving){
					if(input.right.getPressed()){
						if(mobList.get(i).xPos >= width / 2 + TileData.tileSize + xPos && xPos < levelWidth - width){
							int moves = 0;
							while(moves < mobList.get(i).speed && xPos < levelWidth - width){
								xPos++;
								moves++;
							}
						}
					}
					if(input.left.getPressed()){
						if(mobList.get(i).xPos <= (width / 2 - TileData.tileSize * scale) + xPos && xPos > 0){
							int moves = 0;
							while(moves < mobList.get(i).speed && xPos > 0){
								xPos--;
								moves++;
							}
						}
					}
					if(input.down.getPressed()){
						if(mobList.get(i).yPos >= height / 2 + TileData.tileSize + yPos && yPos < levelHeight - height){
							int moves = 0;
							while(moves < mobList.get(i).speed && yPos < levelHeight - height){
								yPos++;
								moves++;
							}
						}
					}
					if(input.up.getPressed()){
						if(mobList.get(i).yPos <= height / 2 - TileData.tileSize + yPos && yPos > 0){
							int moves = 0;
							while(moves < mobList.get(i).speed && yPos > 0){
								yPos--;
								moves++;
							}
						}
					}
				}
				playerXPos = mobList.get(i).xPos;
				playerYPos = mobList.get(i).yPos;				
				return;
			}
		}
	}
	
	//Calculates all the light layer data required and edits it so that it matches all light sources within view
	public void lightCalculate(){
		for(int i = 0; i < lightList.size(); i++){
			if(lightList.get(i).xPos >= xPos - TileData.tileSize * lightList.get(i).getRadius() && lightList.get(i).xPos <= xPos + width + TileData.tileSize * lightList.get(i).getRadius()){
				if(lightList.get(i).yPos >= yPos - TileData.tileSize * lightList.get(i).getRadius() && lightList.get(i).yPos <= yPos + height + TileData.tileSize * lightList.get(i).getRadius() && lightList.get(i) instanceof AreaLight){
					Render[] light = lightList.get(i).lightRender();
					lightLayer.draw(light[0], (lightList.get(i).xPos + TileData.tileSize / 2) - light[0].width / 2, (lightList.get(i).yPos + TileData.tileSize / 2) - light[0].width / 2, new int[]{0xff000000});
					lightMaskLayer.combine(light[1], (lightList.get(i).xPos + TileData.tileSize / 2) - light[0].width / 2, (lightList.get(i).yPos + TileData.tileSize / 2) - light[0].width / 2);
				}
				else if(lightList.get(i).yPos >= yPos - TileData.tileSize * lightList.get(i).getRadius() && lightList.get(i).yPos <= yPos + height + TileData.tileSize * lightList.get(i).getRadius() && lightList.get(i) instanceof DirectionalLight){
					Render[] light = lightList.get(i).lightRender();
					lightLayer.draw(light[0], lightList.get(i).xPos, (lightList.get(i).yPos + TileData.tileSize / 2) - light[0].width / 2, new int[]{0xff000000});
					lightMaskLayer.combine(light[1], lightList.get(i).xPos, (lightList.get(i).yPos + TileData.tileSize / 2) - light[0].width / 2, new int[]{0xffffffff});
				}
			}
		}
		AreaLight player = new AreaLight(0, TileData.tileSize, TileData.tileSize, playerXPos, playerYPos, 1, 1, 2, false, true, false, SheetList.getSheet("voidSprite.png"), 0, 0, 0x00, 0);
		Render[] light = player.lightRender();
		lightLayer.draw(light[0], (player.xPos + TileData.tileSize / 2) - light[0].width / 2, (player.yPos + TileData.tileSize / 2) - light[0].width / 2, new int[]{0xff000000});
		lightMaskLayer.combine(light[1], (player.xPos + TileData.tileSize / 2) - light[0].width / 2, (player.yPos + TileData.tileSize / 2) - light[0].width / 2);
		
		Render temp = new Render(width, height);
		for(int x = 0; x < temp.width; x++){
			for(int y = 0; y < temp.height; y++){
				temp.pixels[x + y * temp.width] = lightLayer.pixels[(xPos + x) + (yPos + y) * lightLayer.width];
			}
		}
		lightLayer.draw(temp, xPos, yPos);
	}
	
	private CollisionData calculateCollision(Mob mob, boolean up, boolean down, boolean left, boolean right){
		int xTile = mob.xPos / TileData.tileSize;
		int yTile = mob.yPos / TileData.tileSize;
		
		int tileWidth = mob.width / TileData.tileSize;
		int tileHeight = mob.height / TileData.tileSize - 1;
		
		CollisionData[] temp = new CollisionData[(tileWidth * 2) + (tileHeight * 2) + 4];
		CollisionData complete = new CollisionData();
		
		if(up){
			for(int i = 0; i < tileWidth; i++){
				if(level.tileId[(xTile + i) + yTile * level.width].getIsCollidable() || level.spriteId[(xTile + i) + yTile * level.width].collidable){
					complete.collisions[0] = true;
					temp[i] = greenConversion(new int[]{level.tileId[(xTile + i) + yTile * level.width].greenData, level.spriteId[(xTile + i) + yTile * level.width].greenData});
				}
			}
			if(xTile % TileData.tileSize != 0){
				if(level.tileId[(xTile + tileWidth) + yTile * level.width].getIsCollidable() || level.spriteId[(xTile + tileWidth) + yTile * level.width].collidable){
					complete.collisions[0] = true;
					temp[tileWidth] = greenConversion(new int[]{level.tileId[(xTile + tileWidth) + yTile * level.width].greenData, level.spriteId[(xTile + tileWidth) + yTile * level.width].greenData});
				}
			}
		}
		if(right){
			for(int i = 0; i < tileHeight; i++){
				if(level.tileId[(xTile + tileWidth) + (yTile + i) * level.width].getIsCollidable() || level.spriteId[(xTile + tileWidth) + (yTile + i) * level.width].collidable){
					complete.collisions[1] = true;
					temp[i + tileWidth] = greenConversion(new int[]{level.tileId[(xTile + tileWidth) + (yTile + i) * level.width].greenData, level.spriteId[(xTile + tileWidth) + (yTile + i) * level.width].greenData});
				}
			}
			if(yTile % TileData.tileSize != 0){
				if(level.tileId[(xTile + tileWidth) + (yTile + tileHeight) * level.width].getIsCollidable() || level.spriteId[(xTile + tileWidth) + (yTile + tileHeight) * level.width].collidable){
					complete.collisions[1] = true;
					temp[tileWidth + tileHeight] = greenConversion(new int[]{level.tileId[(xTile + tileWidth) + (yTile + tileHeight) * level.width].greenData, level.spriteId[(xTile + tileWidth) + (yTile + tileHeight) * level.width].greenData});
				}
			}
		}
		if(down){
			for(int i = 0; i < tileWidth; i++){
				if(level.tileId[(xTile + i) + (yTile + tileHeight) * level.width].getIsCollidable() || level.spriteId[(xTile + i) + (yTile + tileHeight) * level.width].collidable){
					complete.collisions[2] = true;
					temp[i + tileWidth + tileHeight] = greenConversion(new int[]{level.tileId[(xTile + i) + (yTile + tileHeight) * level.width].greenData, level.spriteId[(xTile + i) + (yTile + tileHeight) * level.width].greenData});
				}
			}
			if(xTile % TileData.tileSize != 0){
				if(level.tileId[(xTile + tileWidth) + (yTile + tileHeight) * level.width].getIsCollidable() || level.spriteId[(xTile + tileWidth) + (yTile + tileHeight) * level.width].collidable){
					complete.collisions[2] = true;
					temp[tileWidth + tileHeight] = greenConversion(new int[]{level.tileId[(xTile + tileWidth) + (yTile + tileHeight) * level.width].greenData, level.spriteId[(xTile + tileWidth) + (yTile + tileHeight) * level.width].greenData});
				}
			}
		}
		if(left){
			for(int i = 0; i < tileHeight; i++){
				if(level.tileId[xTile + (yTile + i) * level.width].getIsCollidable() || level.spriteId[xTile + (yTile + i) * level.width].collidable){
					complete.collisions[3] = true;
					temp[i + (tileWidth * 2) + tileHeight] = greenConversion(new int[]{level.tileId[xTile + (yTile + i) * level.width].greenData, level.spriteId[xTile + (yTile + i) * level.width].greenData});
				}
			}
			if(yTile % TileData.tileSize != 0){
				if(level.tileId[xTile + (yTile + tileHeight) * level.width].getIsCollidable() || level.spriteId[xTile + (yTile + tileHeight) * level.width].collidable){
					complete.collisions[3] = true;
					temp[(tileWidth * 2) + (tileHeight * 2)] = greenConversion(new int[]{level.tileId[xTile + (yTile + tileHeight) * level.width].greenData, level.spriteId[xTile + (yTile + tileHeight) * level.width].greenData});
				}
			}
		}
		complete = compileCollisions(temp, complete);
		return complete;
	}
	
	private CollisionData greenConversion(int[] greenData){
		return null;
	}
	
	private CollisionData compileCollisions(CollisionData[] data, CollisionData initial){
		return initial;
	}
	
	//This function was built due to the mob collision being calculated on an id stored in an array
	//As the mob moves this array was not updated and so it was possible to collide with any mob (including their player)
	//By walking into their spawn point, this moves that id and replaces where it used to be with a null sprite.
	private void updateMobPosition(Mob original, Mob mob){
		int newXTile = mob.xPos / TileData.tileSize;
		int newYTile = mob.yPos / TileData.tileSize;
		int oldXTile = original.xPos / TileData.tileSize;
		int oldYTile = original.yPos / TileData.tileSize;
		
		if((newXTile + newYTile) != (oldXTile + oldYTile)){
			if(level.spriteId[newXTile + newYTile * level.width].id == Sprite.nullSprite.id){
				level.spriteId[newXTile + newYTile * level.width] = level.spriteId[oldXTile + oldYTile * level.width];
				level.spriteId[oldXTile + oldYTile * level.width] = Clone.clone(Sprite.nullSprite).animSprite;
			}
			else if(level.spriteId[oldXTile + oldYTile * level.width].id == mob.id){
				level.spriteId[oldXTile + oldYTile * level.width] = Clone.clone(Sprite.nullSprite).animSprite;
			}
		}
	}
	
	private void chunkSetup(int width, int height, int scale){
		//double tempWidth = width * scale / CHUNKSIZE;
		//double tempHeight = height * scale / CHUNKSIZE;
		
	}
}
