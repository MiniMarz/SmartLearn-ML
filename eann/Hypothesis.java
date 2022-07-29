/**
 * This abstract class describes the behavior of an ANNHypothesis for use by the genetic algorithm
  * Author: Alex Ksikes
*/
public abstract class Hypothesis
{
  /**************** Variables ********************/

  static int numInputNeurons;                           // Number of input neurons
  static int numOutputNeurons;                          // Number of output neurons
  static DataSet trainingSet;
  static DataSet evaluationSet;

  /**************** Methods **********************/

  // Give to this hypothesis a random representation
  abstract public void setToRandom();

  // Compute fitness for this Hypothesis
  abstract public void computeFitness();

  // Crossover with another Hypothesis
  abstract public void crossover(Hypothesis otherParent);

  // Mutate this Hypothesis
  abstract public void mutate();

  // Get the representation of this hypothesis
  abstract public double[][][] getRepresentation();

  // Set the representation of this hypothesis
  abstract public void setRepresentation(double[][][] newRepresentation);

  // Returns the fitness of this hypothesis
  abstract public double getFitness();

  // Pretty print this hypothesis
  abstract public String toString();

  // Set the weights