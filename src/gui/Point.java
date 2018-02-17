package gui;

public class Point {
	public int x;
	public int y;
	
	Point() {
		this(0,0);
	}
	
	Point (int pX, int pY) {
		x = pX;
		y = pY;
	}
	
	Point minus(Point other) {
		return new Point(x - other.x, y - other.y); 
	}
	
	Point subtract(Point other) {
		x -= other.x;
		y -= other.y;
		return this;
	}
	
	Point subtract(int pX, int pY) {
		x -= pX;
		y -= pY;
		return this;
	}
	
	Point plus(Point other) {
		return new Point(x + other.x, y + other.y);
	}
	
	Point add(Point other) {
		x += other.x;
		y += other.y;
		return this;
	}
	
	Point add(int pX, int pY) {
		x += pX;
		y += pY;
		return this;
	}
}
