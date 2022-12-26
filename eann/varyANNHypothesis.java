
/**
 * This class describes an ANN hypothesis where the number of hidden neurons vary.
 * We decided not to make this class a subclass of fixedSizeANNHypothesis after carefull consideration
 * We chose to use an array for the representation instead of a vector because after consideration
 * having a vector does help much (still need to set the representation...)
 * Author: Alex Ksikes
*/
import java.util.*;

public class varyANNHypothesis extends Hypothesis
{

  /**************** Variables ********************/

  // Each coordinate i represents the weights of the neuron in position i in the hidden layer
  // The weights of each neuron i are represented as a 2d array
  // The first coordinate of the array represents the weights (from left to right)
  // of the backward edges, the second coordinate represents the forward edges (from left to right)
  private double[][][] representation;

  // Number of hidden neurons
  int numHiddenNeurons;

  // The fitness of this hypothesis is the RMS of the corressponding neural net
  double fitness;

  // Training accuracy      // may be removed
  double trainingAcc;
  double evalAcc;

  /**************** Methods **********************/

  // Create an hypothesis of size 3 by default
  public varyANNHypothesis()
  {
    this.numHiddenNeurons=3;
    representation=new double[numHiddenNeurons][][];
    for (int i=0;i<numHiddenNeurons;i++)
    {
      representation[i]=new double[numInputNeurons][numOutputNeurons];
    }
  }

  // Constructor
  public varyANNHypothesis(int numHiddenNeurons)
  {
    this.numHiddenNeurons=numHiddenNeurons;
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
    double[][] weights;
    for (int index=0;index<numHiddenNeurons;index++)
    {
      weights=getWeights(index);
      for(int i=0;i<numInputNeurons;i++)
      {
        weights[i][0]=Math.random()*2 - 1;
      }
      for (int i=0;i<numOutputNeurons;i++)
      {
        weights[0][i]=Math.random()*2 - 1;
      }
    }
  }

  // Compute fitness for this Hypothesis
  // The fitness represents 3 digits of the RMS of this hypothesis
  // So that we can keep the number of hidden neurons small by promoting smaller hypotheses