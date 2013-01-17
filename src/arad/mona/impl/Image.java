package arad.mona.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Image {
	
	private double fitness = 0.1; 
	private double probability = 0.0;
	
	private int width = 0; 
	private int height = 0; 
	
	private int[] pixelArray; 
	
	private ArrayList<Triangle> triangles = new ArrayList<Triangle>(); 
	
	private BufferedImage img = null; 
	
	public Image(int width, int height, int[] pixelArray, BufferedImage img) {
		
		this.width = width; 
		this.height = height; 
		
		this.pixelArray = pixelArray; 
		
		this.img = img;
	}

	public Image(int width, int height) {
		
		this.width = width; 
		this.height = height; 
		
		pixelArray = new int[width * height]; 	
	}
	
	/*
	 * Adds a triangle to the image
	 */
	public void addTriangle(Triangle triangle) {
		
		if (triangles.size() < Mona.MAX_TRIANGLES) {
			this.triangles.add(triangle);
		}
	}
	
	/*
	 * Draws all of the image's triangles and creates the pixel array
	 */
	public void draw() {
		
		//initialize buffered image
		if (img == null) {
			img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR); 
		}
	
		//get graphics and draw triangles
		Graphics graphics = img.getGraphics();
		
		graphics.setColor(Color.BLACK); 
		graphics.fillRect(0, 0, width, height); 
		
		for (Triangle t : triangles) {
			graphics.setColor(t.getColor());
			graphics.fillPolygon(t);  
		}
		
		//creates the image's pixel array
		img.getRGB(0, 0, width, height, this.pixelArray, 0, width); 

	}
	
	public BufferedImage getBufferedImage() {
		
		return this.img; 
		
	}
	
	public int getPixel(int x) {
		return this.pixelArray[x]; 
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int[] getPixelArray() {
		return pixelArray;
	}

	public void setPixelArray(int[] pixelArray) {
		this.pixelArray = pixelArray;
	}

	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}

	public void setTriangles(ArrayList<Triangle> triangles) {
		this.triangles = triangles;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	
}
