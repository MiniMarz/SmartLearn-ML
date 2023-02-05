
/**
 * @author Alex Ksikes
 **/

import java.util.*;
import ann.*;

/**
 * Implementation of the k-nearest neighbor algorithm
 * In classification assumes 0 or 1 class labels
 * In regression assumes real labels are distributed between 0 or 1
*/
public class kNN
{

  private DataSet dataFile;         // the data set
  private int[] k;                  // holds the number of nearest neighbors
  private int[] kernel;             // holds the kernel widths
  private int trainSetSize;         // size of the training set
  private int testSetSize;          // size of the final test set
  private kNNExample[] trainSet;    // the training set
  private kNNExample[] testSet;     // the final test set
  private int[][] numCorrect;       // keeps track of the number correctly classified instances
  private double[][] squaredError;  // keeps track of the squared error
  private double[][][] predictions;

  /**
   * Initialize the training set and the test set of the kNN algorithm
   * The test set is made from the all the other examples not taken for the training
  **/
  public kNN(DataSet dataFile, int trainSetSize)
  {
    // Fix the data set from which the training set and test are made
    this.dataFile = dataFile;

    // Set up the training set
    this.trainSetSize = trainSetSize;
    trainSet = new kNNExample[trainSetSize];
    for (int i=0; i < trainSetSize; i++)
      trainSet[i] = new kNNExample(dataFile.getExample(i), i);

    // Set up the test set
    this.testSetSize = dataFile.size() - trainSetSize;
    testSet = new kNNExample[testSetSize];
    int index = 0;
    for (int i=trainSetSize; i < dataFile.size(); i++)
    {
      testSet[index] = new kNNExample(dataFile.getExample(i), i);
      index++;
    }

    // Scale feature weights (use the train set for that)
    scaleFeatureWeights(1);
  }

  /**
   * Use LOOCV to select the best values of k and of the kernel width
   * Train using values of k taken from k[] and kernel widths from kernel[]
   * Assumes k[] and kernelWidth[] are sorted
   * Returns the best k and kernel width (first coordinate and second resptively)
  **/
  public int[] train(int k[], int kernel[])
  {
    // Initialize numCorrect and squaredError arrays
    numCorrect = new int[kernel.length][k.length];
    squaredError = new double[kernel.length][k.length];
    predictions = new double[trainSetSize][kernel.length][k.length];

    // Set up the number of nearest neighbors and the kernel widths
    this.k =k;
    this.kernel = kernel;

    // Repeteadly test each query example on the remaining part of the training set
    // and update the correclty classified examples and their squared error
    kNNExample queryExample;                                       // the query example
    kNNExample[] subTrainSet = new kNNExample[trainSetSize - 1];   // the remaing train set
    int index = 0;
    for (int i=0; i < trainSetSize; i++)
    {
      index = 0;
      queryExample = trainSet[i];
      // set up the sub-training set on wich the query example will be tested
      for (int j=0; j < trainSetSize; j++)
      {
        if (!queryExample.equals(trainSet[j]))
        {
          subTrainSet[index] = trainSet[j];
          index++;
        }
      }
      // test the query example on this sub-training set
      predictions[i]=testSingle(queryExample, subTrainSet);
      // print the results with smallest value of kernel width and smallest k
      if (i%25 == 0)
        printSimple(i);
    }

    // Print the final results
    printResults(trainSetSize-1);
    System.out.println("-------------------------------------------------------");
    //printPredictions();
