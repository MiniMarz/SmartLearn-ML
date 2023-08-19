
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
    // Step 1
    Neuron input;
    double x;
    for (int j=0;j<inputNum;j++)
    {
      input=(Neuron) neurons.get(neuronsIndex[0][j]);
      x=example.getAttribute(j);
      input.setValue(x);
    }
    // Step 2
    Neuron n;
    for (int i=1;i<layerNo;i++)
    {
      for (int j=0;j<neuronsIndex[i].length;j++)
      {
        n=(Neuron) neurons.elementAt(neuronsIndex[i][j]);
        n.calValue();
      }
    }
  }


  /**
  * Train the network using this example
  * @param example contains the input attributes
  * Step 1: run network on this training example
  * Step 2: perform backpropagation based on the class label and network output for this example
  **/
  private void trainSingle(Example example)
  {
    runNetwork(example);
    // compute the change in weight
    backPropagation(example);
    // update the weights after having seen this single example
    updateWeights();
  }

  /**
  * Train the network using all the examples in the training set
  * @param example contains the input attributes
  **/
  private void trainAll(DataSet trainingSet)
  {
    Example example;
    for (int i=0;i<trainingSet.size();i++)
    {
      example=trainingSet.getExample(i);
      runNetwork(example);
      // update the change in weight of each edge
      backPropagation(example);
    }
    // now update the weights of each edge
    updateWeights();
  }

  /**
  * Update the weights of this ANN and reinitialize the change in weights
  **/
  public void updateWeights()
  {
    Edge e;
    for (int i=0;i<edges.size();i++)
    {
      e=(Edge) edges.get(i);
      e.updateWeight();
      e.initDeltaWeight();
    }
  }

  /**
  * To test a single element
  * Assume there is only one output neuron, so the class label is simply based on whether the output value
  * is more than 0.5 or less than 0.5. If output neuron >0.5 return 1 else return 0.
  * @param example contains the input attribute
  * @return the class it should be in
  **/
  public int testSingle(Example example)
  {
    Neuron output;
    runNetwork(example);
    output=(Neuron) neurons.elementAt(neuronsIndex[layerNo-1][0]);
    if (output.getValue()>0.5)
      return 1;
    else
      return 0;
  }

  /**
  * To test the accuracy level of the entire data set
  * @param testSet contains all the example to test for accuracy
  * @param return the percentage of correct classification
  **/
  public double testDataSet(DataSet testSet)
  {
    Example example;
    int label;
    int testValue;
    int numCorrect=0;
    int numExamples=testSet.size();
    for (int i=0;i<numExamples;i++)
    {
      example=testSet.getExample(i);
      testValue=testSingle(example);
      label=example.getClassLabel();
      if (testValue==label)
        numCorrect++;
    }
    return ((1.0*numCorrect)/numExamples);
  }

  // Compute the root mean squared error of this network
  public double computeRMS(DataSet trainingSet)
  {
    Example example;
    Neuron output;
    double probaOutput;
    int targetOutput;
    int numExamples=trainingSet.size();
    double sum=0;
    for (int i=0;i<numExamples;i++)
    {
      example=trainingSet.getExample(i);
      runNetwork(example);
      output=(Neuron) neurons.elementAt(neuronsIndex[layerNo-1][0]);
      probaOutput=output.getValue();
      targetOutput=example.getClassLabel();
      sum=Math.pow(probaOutput-targetOutput,2) + sum;
    }
    return Math.sqrt(sum/numExamples);
  }

  // Report the accuracy (RMS error and training and validation accuracy after every n epoch)
  // and write into these data
  public void reportAccuracy(DataSet trainingSet,DataSet evaluationSet,int epoch,int n,FileWriter out) throws IOException
  {
    double trainingAcc=0;
    double validationAcc=0;
    double trainingRMSE=0;
    double validationRMSE=0;

    // report training and validation accuracy after every n epochs
    if (epoch%n==0)
    {
      System.out.println("Epoch : " + epoch);
      trainingAcc=testDataSet(trainingSet);
      System.out.println("Training Acc : " + trainingAcc);