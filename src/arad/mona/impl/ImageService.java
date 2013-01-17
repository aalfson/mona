package arad.mona.impl;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageService {
	
	private static final String OUTPUT_FILE_NAME = "best.png"; 
	
	private static Random rgen = new Random();  
	
	/*
	 * Generates the image. 
	 */
	public Image generateImage(int width, int height) {

		//create image object
		Image img = new Image(width, height); 
		
		//add triangles
		for (int i = 0; i < Mona.MAX_TRIANGLES; i++) {
			img.addTriangle(generateTriangle(width, height)); 
		}
		
		//draw the image - create new buffered image and pixel array
		img.draw(); 
		
		return img; 
	}
	
	/*
	 * Generates a triangle that is located within the bounds defined by imageWidth and imageHeight. 
	 * The color of the triangle is randomly generated. 
	 */
	public Triangle generateTriangle(int imageWidth, int imageHeight) {
		
		Triangle t = new Triangle(); 
		
		for (int i = 0; i < 3; i++)
			t.addPoint(rgen.nextInt(imageWidth), rgen.nextInt(imageHeight)); 
		
		Color c = new Color(rgen.nextInt(255), rgen.nextInt(255), rgen.nextInt(255), rgen.nextInt(255)); 
		t.setColor(c); 
		
		return t; 
	}
	
	/*
	 * Loads the original image
	 */
	public Image loadImage(String name) {
		
		//read image file
		BufferedImage buffImg = null;
		try {
			buffImg = ImageIO.read(new File(name)); 
		}
		catch (IOException e) {
			e.printStackTrace(); 
		}
		
		//create pixel array and image object
		int width = buffImg.getWidth(); 
		int height = buffImg.getHeight(); 
	 
		int[] pixelArray = new int[width * height];  
		
		buffImg.getRGB(0, 0, width, height, pixelArray, 0, width); 
	
		Image img = new Image(width, height, pixelArray, buffImg);
		
		return img;
	}
	
	/*
	 * Saves the image twice - Once to update the current best image and once to create a visual record of
	 * how the algorithm functions over time. The visual record images are stored in 'images/' and are given
	 * their generation number as a file name. 
	 */
	public void saveImage(Image image) {
		
		//create buffered image
		BufferedImage img = image.getBufferedImage(); 
		
		if (img == null) {

			img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR); 
			img.setRGB(0, 0, image.getWidth(), image.getHeight(), image.getPixelArray(), 0, 0); 
		}
				
		//write current best image
		File output = new File(OUTPUT_FILE_NAME);
		try {
			ImageIO.write(img, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		//write image with unique name for record of changes over time. 
		output = new File("images/" + Mona.generation + Mona.OUTPUT_FILE_EXTENSION);
		try {
			ImageIO.write(img, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Fitness function. Calculates each image's fitness by summing the difference between original image's and the
	 * corresponding generated image's RGB values for every pixel in the image. 
	 */
	public double calcuateFitness(Image image, Image original) {
		
		if (image.getFitness() != 0.1) {
			return image.getFitness(); 
		}
		
		ColorModel iColorModel = image.getBufferedImage().getColorModel().getRGBdefault(); 
		ColorModel oColorModel = original.getBufferedImage().getColorModel().getRGBdefault(); 
		
		double fitness = 0; 
		
		for (int i = 0; i < image.getPixelArray().length; i++) {
			
			int iPixel = image.getPixel(i); 
			int oPixel = original.getPixel(i); 
			
			int deltaR = (oColorModel.getRed(oPixel) - iColorModel.getRed(iPixel)) * (oColorModel.getRed(oPixel) - iColorModel.getRed(iPixel));
			int deltaG = (oColorModel.getGreen(oPixel) - iColorModel.getGreen(iPixel)) * (oColorModel.getGreen(oPixel) - iColorModel.getGreen(iPixel));
			int deltaB = (oColorModel.getBlue(oPixel) - iColorModel.getBlue(iPixel)) * (oColorModel.getBlue(oPixel) - iColorModel.getBlue(iPixel));
			int deltaA = (oColorModel.getAlpha(oPixel) - iColorModel.getAlpha(iPixel)) * (oColorModel.getAlpha(oPixel) - iColorModel.getAlpha(iPixel));
			
			fitness += (deltaR + deltaG + deltaB + deltaA);
		} 
		
		image.setFitness(fitness); 
		
		return fitness;  
	}
	
	public Image crossImages(Image a, Image b) {
		
		//return standardCrossOver(a, b); 
		return randomCrossOver(a, b); 
	}
	
	/*
	 * Cross over technique that selects a pivot element in the child images array. 
	 * All images that come before the pivot are selected from the a parent; the pivot
	 * element and all elements that come after are selected from the b parent. Additionally, 
	 * each element in the child's triangle array is subject to mutation by creating a new triangle. 
	 */
	private Image standardCrossOver(Image a, Image b) {
		
		ArrayList<Triangle> aTriangles = a.getTriangles(); 
		ArrayList<Triangle> bTriangles = b.getTriangles(); 
		
		int width = a.getWidth(); 
		int height = a.getHeight(); 
		
		Image child = new Image(width, height);
		
		int pivot = rgen.nextInt(aTriangles.size()); 
		
		for (int i = 0; i < aTriangles.size(); i++) {
			
			if (rgen.nextDouble() >= 1.0 - Mona.MUTATION_RATE ) {
				child.addTriangle(generateTriangle(width, height)); 	
			}
			else if (i < pivot) {
				child.addTriangle(aTriangles.get(i)); 
			}
			else {
				child.addTriangle(bTriangles.get(i)); 
			}
		}
		
		child.draw(); 
		return child;
	}
	
	/*
	 * Crossover technique where each element in the child's triangle array has a (1 - Mona.MUTATION_RATE) / 2 probability
	 * of being drawn from either parent and a Mona.MUTATION_RATE probability of being a newly created triangle.  
	 */
	private Image randomCrossOver(Image a, Image b) {
		
		ArrayList<Triangle> aTriangles = a.getTriangles(); 
		ArrayList<Triangle> bTriangles = b.getTriangles(); 
		
		int width = a.getWidth(); 
		int height = a.getHeight(); 
		
		Image child = new Image(width, height);  
		
		for (int i = 0; i < aTriangles.size(); i++) {
			
			double x = rgen.nextDouble();
			
			if (x >= 1.0 - Mona.MUTATION_RATE) {
				child.addTriangle(generateTriangle(width, height)); 
			}
			else if (x < (1.0 - Mona.MUTATION_RATE) / 2) {
				child.addTriangle(aTriangles.get(i).clone()); 
			}
			else 
				child.addTriangle(bTriangles.get(i).clone()); 
		}
		
		
		child.draw(); 
		
		return child; 
	}
}
