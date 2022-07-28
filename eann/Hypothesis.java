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
  abstract publ