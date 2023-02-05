
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

    // Return the best value of k and of the kernel width
    return getBestValues();

  }

  /**
   * Test the test set with values with given values of k[] and kernel[].
  **/
  public void test(int[] bestK, int [] bestKernel)
  {
    // Initialize numCorrect and squaredError arrays
    numCorrect = new int[bestKernel.length][bestK.length];
    squaredError = new double[bestKernel.length][bestK.length];
    predictions = new double[testSetSize][bestKernel.length][bestK.length];

    // Set up the number of nearest neighbors and the kernel widths
    this.k = bestK;
    this.kernel = bestKernel;

    // Evaluate each example from the test set onto the training set
    kNNExample testExample;
    for (int i=0; i < testSetSize; i++)
    {
      testExample = testSet[i];   // see about evaluation set if makes more sense
      predictions[i]=testSingle(testExample, trainSet);
      if (i%25==0)
        printSimple(i);
    }
    printResults(testSetSize-1);
    System.out.println();
    System.out.println("-------------------------------------------------------");
    printPredictions();
  }

  /**
   * Test the single example on a train set
   * Updates the squared error and the number of correctly classified examples
  **/
  private double[][] testSingle (Example testExample, kNNExample[] trainSet)
  {
    // Sort the train set to get the nearest neighbors
    // first set the distance each example is from the test example
    for (int i=0; i < trainSet.length; i++)
      trainSet[i].setRelativeDist(testExample);
    // only sort if the maximum of neighbors < the size of the train set
    if (k[0] < trainSetSize - 1)
    {
      kNNComparator comparator = new kNNComparator();
      Arrays.sort(trainSet, comparator);
    }

    // Update the squared error and the number of correctly classified examples
    // for each kernel width and each number of nearest neighbors considered
    int targetLabel = testExample.getClassLabel();
    kNNExample neighbor;     // neighbor considered
    double neighborWeight;   // its weight
    double neighborValue;    // its value (ie class label)
    double sumWeightedValue; // weighted sum of each neighbor so far
    double sumAllWeight;     // sum of the weights of each neighbor so far
    double probaLabel;
    double[][] predictions=new double[kernel.length][k.length];
    int kIndex;              // indexes k[] array
    // for each kernel width from kernel[]
    for (int kernelIndex=0; kernelIndex < kernel.length; kernelIndex++)
    {
      sumWeightedValue = 0;
      sumAllWeight = 0;
      kIndex = 0;
      // for each neighbor from 0 to max k[]
      for (int neighborNo=0; neighborNo < k[k.length-1]; neighborNo++)
      {
        // compute the probability of each neighbor
        neighbor = trainSet[neighborNo];
        neighbor.setWeight(kernel[kernelIndex]);
        neighborWeight = neighbor.getWeight();
        neighborValue = neighbor.getClassLabel();
        sumWeightedValue = sumWeightedValue + neighborValue * neighborWeight;
        sumAllWeight = sumAllWeight + neighborWeight;
        probaLabel = sumWeightedValue / sumAllWeight;
        // update results after having seen k[kIndex] - 1 neighbors
        if (neighborNo == k[kIndex]-1)
        {
          if (Math.abs(targetLabel - probaLabel) <= 0.5)
          {
            (numCorrect[kernelIndex][kIndex])++;
          }
          squaredError[kernelIndex][kIndex] = squaredError[kernelIndex][kIndex] + Math.pow(targetLabel - probaLabel,2);
          predictions[kernelIndex][kIndex] = probaLabel;
          kIndex++;
        }
      }
    }
    return predictions;
  }

  /**
   * Returns as first coordinate the best kernel.
   * Returns as second coordinate the best number of nearest neighbors.
   * Assumes train or test method has been called before.
  **/
  private int[] getBestValues()
  {
    double lowestError = 0;
    int[] bestValues = new int[2];
    for (int kernelIndex=0; kernelIndex < kernel.length; kernelIndex++)
    {
      for (int kIndex=0; kIndex < k.length; kIndex++)
      {
        if (squaredError[kernelIndex][kIndex] < lowestError)
        {
          lowestError = squaredError[kernelIndex][kIndex];
          bestValues[0] = kernel[kernelIndex];
          bestValues[1] = k[kIndex];
        }
      }
    }
    return bestValues;
  }

  /**
   * Scale the feature weights by 1/(max-min) or 1/var depending on mode
   * using a train set.
  **/
  private void scaleFeatureWeights(int mode)
  {
    double max, min, var, sum, sumSquared;
    int numAttributeVal = dataFile.getAttributeNum();
    double[] attributeVal = new double[trainSetSize];
    double[] featureWeights = new double[numAttributeVal];
    double[] featureWeights1 = new double[numAttributeVal];
    double[] featureWeights2 = new double[numAttributeVal];

    // Find max min and variance of each attribute value
    for (int i=0; i < numAttributeVal; i++)
    {
      sum = 0;
      sumSquared = 0;
      for (int j=0; j < trainSetSize; j++)
      {
        attributeVal[j] = dataFile.getAttribute(j,i);
        sum = sum + attributeVal[j];
        sumSquared = sumSquared + Math.pow(attributeVal[j],2);
      }
      Arrays.sort(attributeVal);
      max = attributeVal[trainSetSize-1];
      min = attributeVal[0];
      var = sumSquared/trainSetSize - Math.pow(sum/trainSetSize,2);
      featureWeights[i] = 1;