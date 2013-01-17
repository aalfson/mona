package arad.mona.impl;

import java.awt.Color;
import java.awt.Polygon;

public class Triangle extends Polygon {
	
	Color color; 
	
	public void setColor(Color color) {
		this.color = color; 
	}

	public Color getColor() {
		return this.color; 
	}
	
	public Triangle clone() {
		
		Triangle t = new Triangle();
		
		t.setColor(color);
	
		int[] xpoints = this.xpoints; 
		int[] ypoints = this.ypoints; 
		
		t.addPoint(xpoints[0], ypoints[0]); 
		t.addPoint(xpoints[1], ypoints[1]); 
		t.addPoint(xpoints[2], ypoints[2]);
		
		return t;
	}
}
