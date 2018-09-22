package world;

import org.joml.Vector2f;

public abstract class TileElement {

	public float[][] height;
	public float[] color;
	
	public void append(int posX, int posY, float amount) {
		if (amount <= 0) return;
		if (amount > 100) System.err.println("High amount added to TileElement");

		// This tile
		int x = posX;
		int y = posY;
		height[x][y] += amount/5;
		if (height[x][y] > 1f) {
			height[x][y] = 1f;
		}
		
		x = World.west(posX);
		y = posY;
		height[x][y] += amount/5;
		if (height[x][y] > 1f) {
			height[x][y] = 1f;
		}
		
		x = World.east(posX);
		y = posY;
		height[x][y] += amount/5;
		if (height[x][y] > 1f) {
			height[x][y] = 1f;
		}

		x = posX;
		y = World.north(posY);
		height[x][y] += amount/5;
		if (height[x][y] > 1f) {
			height[x][y] = 1f;
		}
		
		x = posX;
		y = World.south(posY);
		height[x][y] += amount/5;
		if (height[x][y] > 1f) {
			height[x][y] = 1f;
		}
	}
	
	public abstract float harvest(float amount, int x, int y);
	
	public float seekHeight(Vector2f dir, int posX, int posY) {
		
		int bestDirX = -1;
		int bestDirY = -1;
		float best = 0;
		
		// This tile
		int x = posX;
		int y = posY;
		if (height[x][y] > best) {
			best = height[x][y];
			bestDirX = x;
			bestDirY = y;
		}
		
		x = World.west(posX);
		y = posY;
		if (height[x][y] > best) {
			best = height[x][y];
			bestDirX = x;
			bestDirY = y;
		}
		
		x = World.east(posX);
		y = posY;
		if (height[x][y] > best) {
			best = height[x][y];
			bestDirX = x;
			bestDirY = y;
		}

		x = posX;
		y = World.north(posY);
		if (height[x][y] > best) {
			best = height[x][y];
			bestDirX = x;
			bestDirY = y;
		}
		
		x = posX;
		y = World.south(posY);
		if (height[x][y] > best) {
			best = height[x][y];
			bestDirX = x;
			bestDirY = y;
		}
		
		if (best > 0) {
			dir.set(bestDirX - posX, bestDirY - posY);
			if (dir.length() > 1f) {
				dir.normalize();
			}
			return best;
		}
		return 0;
	}
}
