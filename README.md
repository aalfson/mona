
Mona is program that utilizes a genetic algorithm to generate a copy of an input image and will be used to generate an interpretation of Generate Mondrian's Victory Boogie Woogie for the following contest: http://www.elegant.setup.nl/

Mona was inspired by an evolutionary algorithm project by Roger Alsing, which you can find here: http://rogeralsing.com/2008/12/07/genetic-programming-evolution-of-mona-lisa/?hn

Mona generates a number of images, each of which consists of multiple triangles drawn on to a black background. The size, color, transparency, and spacial orientation of each triangle is determined randomly. Each generation consists of a population of multiple images that are each scored utilizing a fitness function similar to the one employed by Roger Alising. For each pixel in the image, the fitness function calculates the difference between the RGBA values of the source image and the generated image. The sum of every pixel's differences constitutes the image's score, with values closer to zero being better. The bottom 50% of images in each generation are removed and replaced by crossing over images from the top 50% of the population. In order to introduce mutation, during crossover there is some probability that a given triangle will be replaced by a randomly generated triangle not part of either parent. An image is selected randomly for crossover using the roulette wheel selection, which you can read more about here: http://en.wikipedia.org/wiki/Fitness_proportionate_selection

The following image was the best generated after approximately 1,000,000 generations. The population size for each generation was 36. Each image in every generation consisted of 50 triangles. Each triangle had a 5% chance of being replaced by a randomly generated triangle during crossover. 

[monaLisa.jpg|align=left|frame|Source Image]
[best.png|align=left|frame|Best image]
