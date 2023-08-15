
import java.util.*;
import java.io.*;
import java.util.zip.*;

/**
  * NeuralNet Class : A neural network consists of edges and neuron
  * Author: Alex Ksikes
  **/
public class NeuralNet implements Serializable
{

  /**************** Variables ********************/

  final static int MAXNEURONS     = 60;	// maximum number of neurons in each layer
  final static int BATCHMODE      = 0;  // batch mode for training
  final static int STOCHASTICMODE = 1;	// stochastic mode for training
  Vector neurons;		        // set of neurons in this neural network
  Vector edges;			        // set of edges in this neural network
  int [][] neuronsIndex;	        // index of neurons. First component in this 2D array is the layer,
                                        // second component is the layer numbering
  int inputNum;				// number of input neurons
  int outputNum;			// number of output neurons
  int layerNo;			        // number of layers
  double learningRate;			// learning rate for the entire neural network
  double momentum;			// momentum term
  double errAcc;		        // global error in the network
  int mode;				// the mode to use for training

  /**************** Methods **********************/

  /**
  * Main Constructor
  * Sets up the Multi-layer Neural Network
  * @param layerNo indicates the number of layers
  * @param []numInLayer contains the number of neurons in each layer,
  * from input layer to hidden layers to output layer
  * @param learningRate sets how fast the networks changes its weight
  * @param momentum determine how much the weight change is based on past update
  * @param mode indicates the mode (Batch or Stochastic) to use for training
  * Step 1 : Creates all the neurons in the network, ordered by layers,
  * keeping the index of the neuron in neuronsIndex[][]
  * Step 2 : Calls MLPNetworkSetup to set up the edges between the neurons,
  * determined by the layers they are in.
  **/
  public NeuralNet(int layerNo, int[] numInLayer, double learningRate, double momentum, int mode)
  {
    // Set up the neuron index array
    neuronsIndex=new int[layerNo][];
    for (int i=0; i<layerNo; i++)
      neuronsIndex[i]=new int[numInLayer[i]];

    this.layerNo=layerNo;
    this.learningRate=learningRate;
    this.momentum=momentum;
    this.mode=mode;
    this.inputNum=numInLayer[0];
    this.outputNum=numInLayer[layerNo-1];
    this.neurons=new Vector();

    // Step 1
    int id=0;
    // in layer i
    for (int i=0;i<layerNo;i++)
    {
      // and layer numbering j
      for (int j=0;j<numInLayer[i];j++)
      {
        neurons.add(id,new Neuron(id));
        neuronsIndex[i][j]=id;          // keeping track of the index of each neuron
        id++;
      }
    }
    // Step 2
    MLPNetworkSetup(numInLayer);
  }

  /**
  * Set up the forward backward edges relationship in the neural network in MLP fashion
  * @param numInLayer indicates the number of neuron in each layer
  * Steps  : Create an edge for each neuron in layer i and neuron in layer i+1.
  *          Add this edge to the set of forward edges for the neuron in layer i
  *	     Add this edge to the set of backward edges for the neuron in layer i+1
  *          Call the edge reset function to set random initial weights.
  **/
  private  void MLPNetworkSetup(int [] numInLayer)
  {
    this.edges=new Vector();
    Neuron parent;
    Neuron child;
    Edge e;
    int id=0;
    // in layer i
    for (int i=0;i<layerNo-1;i++)
    {
      // and layer numbering j
      for (int j=0;j<numInLayer[i];j++)
      {
        parent=(Neuron) neurons.get(neuronsIndex[i][j]);  // get neuron at this location
        for (int k=0;k<numInLayer[i+1];k++)
        {
          child=(Neuron) neurons.get(neuronsIndex[i+1][k]);
          e=new Edge(parent,child,learningRate,id,momentum,mode);
          parent.addForwardEdge(e);
          child.addBackwardEdge(e);
          edges.add(id,e);
          id++;
        }
      }
    }
    // now we can call the reset function on each edge
    for (int i=0;i<edges.size();i++)
      ((Edge) edges.get(i)).reset();

  }

  /**
  * Print out all the weights of all the edges
  * A useful debugging tool to see whether your neural network is indeed changing the weights
  **/
  public void printWeight()
  {
    for (int i=0;i<edges.size();i++)
    {
      System.out.print("Weight of edge "+i+": "+ ((Edge) edges.get(i)) .getWeight()+"  ");
    }
  }

  /**
  * run the network given an array of attributes from an example
  * @param Example example contains the input attributes
  * Step 1: Set all the input neurons [neurons in layer 0] with the attributes in this example
  * Step 2: Calculate the value of each neuron beginning from the input layer to the output layer.
  **/
  private void runNetwork(Example example)
  {