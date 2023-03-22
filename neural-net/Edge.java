/**
 * Class Edge for the neural network
 * Each edge has an assoicated weight that changes along the way
 * Author: Alex Ksikes
**/

import java.lang.Math;
import java.io.Serializable;

public class Edge implements Serializable
{
  /*********** Variables **************/

  private double weight;            // the weight associated with this edge
  private Neuron source;            // the neuron it is connected to in layer i
  private Neuron dest;	            // the neuron it is connected to in layer i+1
  private int id;	            // identifier for the edge
  private int mode;	            // the mode to use for update rule
  private double deltaWeight;       // the change in weight of this edge
  private double deltaWeightOld;    // keep track of the old value of the change in weight

  // backpropation parameters
  private double momentum ;	// momentum term in order not to get stuck in local minima
  private double learningRate;	// learning rate changes the speed of how convergence takes place

  /*********** Methods *****************/

  /**
  * Edge Constructor
  * @param parent is the neuron it is connected to in layer i
  * @param child is the neuron it is connected to in layer i+1
  * @param lr is the learning rate it is initialized to
  * @param idd is the id of this Edge
  * @param momentum is the momentum rate this edge is initialized to
  * @param mode indicates the mode (Batch or Stochastic) to use for training
  **/
  public Edge(Neuron parent, Neuron child, double lr, int idd, double momentum, int mode)
  {
    this.source=par