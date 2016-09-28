package engine.main.render.core;

import engine.main.render.Render;
import engine.main.render.TileData;
import engine.main.render.sprites.SpriteSheet;

public class Font {

	//This class is still a work in progress with the main idea being to generate custom text boxes using fonts that are imported as
	//a spritesheet, became low priority but still will be implemented for later release
	
	private int[] baseAlpha = new int[]{0xffff00ff, 0xff7f007f};
	
	private Letter[] letterList;
	private String[] letters = new String[]{" ","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
			"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
			"/","\\","\"","?","!","+","-",",","."};
	
	public Font(SpriteSheet sheet){
		letterList = new Letter[sheet.width / TileData.tileSize + sheet.height / TileData.tileSize];
		int x = 0;
		int y = 0;
		for(int i = 0; i < letters.length; i++){
			if(i * TileData.tileSize < sheet.width){
				x = i * TileData.tileSize;
				y = 0;
			}
			else{
				y = i * TileData.tileSize / sheet.width;
				x = (i * TileData.tileSize) - (y * sheet.width);
			}
			letterList[i] = new Letter(letters[i], x, y, sheet);
		}
	}
	
	//Generation of the textbox itself
	public Render textBox(String sentence, int rows, int backgroundColour, int borderColour){
		String[] sentenceBreak = sentence.split("");
		int height = rows * TileData.tileSize; 
		int width = sentenceBreak.length / rows * TileData.tileSize;
		Render data = new Render(width, height);
				
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if(x == 0 || x == width - 1 || y == 0 || y == height - 1){
					data.pixels[x + y * width] = borderColour;
				}
				else{
					data.pixels[x + y * width] = backgroundColour;
				}
			}
		}
		
		for(int i = 0; i < sentenceBreak.length; i++){
			for(int z = 0; z < letters.length; z++){
				if(sentenceBreak[i] == letterList[z].letter){
					data.draw(letterList[z], (i * TileData.tileSize) - (data.width * ((i * TileData.tileSize) / data.width)), (i * TileData.tileSize) / data.width, baseAlpha);
					break;
				}
			}
		}
		return data;
	}
	
	public class Letter extends Render{
		
		public String letter;
		public int xPos;
		public int yPos;
		public SpriteSheet sheet;
		
		public Letter(String letter, int xPos, int yPos, SpriteSheet sheet){
			super(TileData.tileSize, TileData.tileSize);
			this.letter = letter;
			this.xPos = xPos * TileData.tileSize;
			this.yPos = yPos * TileData.tileSize;
			this.sheet = sheet;
			buildPixels();
		}
		
		private void buildPixels(){
			for(int x = 0; x < TileData.tileSize; x++){
				for(int y = 0; y < TileData.tileSize; y++){
					pixels[x + y * TileData.tileSize] = sheet.pixels[(xPos + x) + (yPos + y) * sheet.width];
				}
			}
		}
		
	}
	
}
