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
  private double momentum = 0.2;	// momentum term in order not to get stuck in local minima
  private double learningRate = 0.3;	// learning rate changes the speed of how convergence