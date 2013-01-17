package arad.mona.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Mona {

	//Constants
	public static final int MAX_TRIANGLES = 50; 
	private static final int NUM_GENERATIONS = 1000000; 
	private static final int POPULATION_MAX_SIZE = 36; 
	private static final int NUMBER_TO_PRUNE = 18; 
	public static final double MUTATION_RATE = 0.05; 
	public static final String OUTPUT_FILE_EXTENSION = ".png";
	
	//arguments
	private static final String originalFileName = "monaLisa.jpg";
	
	//instance variables
	private static ArrayList<Image> population = new ArrayList<Image>(); 
	private static Image original = null;
	private static Random rgen = new Random(); 
	private static double bestFitness = 10000; 
	public static int generation = 0; 
	
	//services
	private static ImageService imgService = new ImageService(); 
	
	public static void main(String [] args) {
		
		initialize();
		
		for (int i = 0; i < NUM_GENERATIONS; i++) {
			
			crossOver(); 
			calculateFitness(); 
			removeWeakest(); 
			generation++;  
		}	
	}
	
	/*
	 * Removes the NUMBER_TO_PRUNE weakest images from the population after each generation
	 */
	private static void removeWeakest() {
		
		//sort population
		sortPopulation(); 
		
		int size = population.size(); 
		
		//remove n least fit from population
		for (int i = 0; i < NUMBER_TO_PRUNE; i++) {
			population.remove(size - 1 - i); 
		}
	}
	
	/*
	 * Helper method that sorts the population based on each image's fitness
	 */
	private static void sortPopulation() {
		
		Collections.sort(population, new Comparator<Image>() {

			@Override
			public int compare(Image a, Image b) {
				
				if (a.getFitness() < b.getFitness())
					return -1; 
				else if (a.getFitness() == b.getFitness())
					return 0; 
				else
					return 1; 
				
			}
		});
	}
	
	/*
	 * Calculates the fitness for each image in the population
	 */
	private static void calculateFitness() {
		
		Image best = population.get(0); 
		
		//calculate fitness for each image in population
		for (Image img : population) {
			
			imgService.calcuateFitness(img, original); 
			
			//compare to current best
			if (img.getFitness() <= best.getFitness()) {
				best = img;
			}
		}
		
		//LOG data on generation and current best fitness
		if (generation % 100 == 0) {
			System.out.println("Generation: " + generation + ", fitness: " + best.getFitness()); 
		}
		
		//save image if current best is better than previous best
		if (best.getFitness() != bestFitness) {
			bestFitness = best.getFitness(); 
			imgService.saveImage(best);
		}
	}
	
	/*
	 * For each image x in the population, calculates the probability that x will be selected
	 * to cross with another image.  
	 */
	private static void crossOver() {
		
		//roulette wheel selection
		double sum = 0; 
		for (Image image : population) {
			sum += (1.0 / image.getFitness());
		}
		
		double sumOfProbabilities = 0.0;
		
		for (Image image : population) {
			
			double probability = sumOfProbabilities + ( (1.0 / image.getFitness()) / sum);
			image.setProbability(probability); 
			sumOfProbabilities += probability;
		}
		
		crossPairs(); 		
	}
	
	/*
	 * Helper method that selects and crosses each pair in order to replace the members of the population lost to pruning. 
	 */
	private static void crossPairs() {
		
		while (population.size() < POPULATION_MAX_SIZE) {
			
			Image a = null;
			Image b = null;		

			double minimum = 0.0; 
			
			//selects pairs
			while (a == null || b == null) {
				
				double random = rgen.nextDouble();

				for (int j = 0; j < population.size(); j++) {
					
					double maximum = population.get(j).getProbability(); 
					
					/*
					 * Selects pairs by checking to see if random falls between (minimum and maximum]. Ensures
					 * that pairs are not the same object.  
					 */
					if (random > minimum && random <= maximum) {
						
						if (a == null) {
							a = population.get(j);
						}
						else if (population.get(j) != a){
							b = population.get(j);
						}
						break;
					}
					else {
						minimum = maximum; 
					}
				}	
			}
		
			//cross images and add new one to population
			population.add(imgService.crossImages(a, b));
		}
	}

	private static void generatePopulation() {
		
		int populationSize = population.size(); 
		
		//add new images to population until popluation.size == POPULATON_MAX_SIZE
		for (int i = 0; i < POPULATION_MAX_SIZE - populationSize; i++) {
			
			Image img = imgService.generateImage(original.getWidth(), original.getHeight()); 
			population.add(img); 
		}
	}

	
	private static void initialize() {
		
		original = imgService.loadImage(originalFileName); 
		generatePopulation();
		calculateFitness();
		removeWeakest();
	}
	
}
