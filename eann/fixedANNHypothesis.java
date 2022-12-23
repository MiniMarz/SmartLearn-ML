
/**
 * This class describes an ANN hypothesis where the number of hidden neurons
 * are assumed to be constant.
  * Author: Alex Ksikes
*/

import java.util.*;

public class fixedANNHypothesis extends Hypothesis
{

  /**************** Variables ********************/

  static int numHiddenNeurons;            // Number of hidden neurons of this hypothesis

  // Used to evaluate the fitness
  static NeuralNet neuralNet;

  // Each coordinate i represents the weights of the neuron in position i in the hidden layer
  // The weights of each neuron i are represented as a 2d array
  // The first coordinate of the array represents the weights (from left to right)
  // of the backward edges, the second coordinate represents the forward edges (from left to right)
  private double[][][] representation;

  // The fitness of this hypothesis is the RMS of the corressponding neural net
  double fitness;

  // Training accuracy      // may be removed
  double trainingAcc;
  double evalAcc;

  /**************** Methods **********************/

  // Constructor
  public fixedANNHypothesis()
  {
    representation=new double[numHiddenNeurons][][];
    for (int i=0;i<numHiddenNeurons;i++)
    {
      representation[i]=new double[numInputNeurons][numOutputNeurons];
    }
  }

  // Give to this hypothesis random representation
  // Every weight is initialized to a random value between -1 and 1
  public void setToRandom()
  {