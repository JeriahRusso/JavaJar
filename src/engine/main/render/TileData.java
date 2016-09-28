package engine.main.render;

import engine.main.render.sprites.SpriteSheet;

public class TileData {

	public static int tileSize = 10;
	
	public static Tile[] tiles = new Tile[]{};
	
	public static class Tile{
		
		public int id;
		public int sheetX;
		public int sheetY;
		public int tileWidth;
		public int tileHeight;
		public int identifier;
		public int greenData;
		
		public boolean isCollidable;
		
		public int[] pixels;
		
		public SpriteSheet sheet;
		
		public Tile(int id, int sheetX, int sheetY, int tileWidth, int tileHeight, SpriteSheet sheet, boolean isCollidable, int identifier, int greenData){
			this.id = id;
			this.sheetX = sheetX;
			this.sheetY = sheetY;
			this.tileWidth = tileWidth;
			this.tileHeight = tileHeight;
			this.identifier = identifier;
			this.sheet = sheet;
			this.isCollidable = isCollidable;
			this.greenData = greenData;
			this.pixels = new int[TileData.tileSize * tileWidth * TileData.tileSize * tileHeight];
			for(int x = 0; x < tileWidth * TileData.tileSize; x++){
				for(int y = 0; y < tileHeight * TileData.tileSize; y++){
					pixels[x + y * (tileWidth * TileData.tileSize)] = sheet.pixels[(sheetX * TileData.tileSize + x) + ((sheetY * TileData.tileSize + y) * (sheet.width))];
				}
			}
		}
		
		public Render drawTile(){
			Render temp = new Render(tileWidth * TileData.tileSize, tileHeight * TileData.tileSize);
			temp.pixels = pixels;
			return temp;
		}
		
		public int getId() {
			return id;
		}

		public int getSheetX() {
			return sheetX;
		}

		public int getSheetY() {
			return sheetY;
		}

		public int getTileWidth() {
			return tileWidth;
		}

		public int getTileHeight() {
			return tileHeight;
		}
		
		public SpriteSheet getSheet(){
			return sheet;
		}
		
		public boolean getIsCollidable(){
			return isCollidable;
		}
	}
	
}
