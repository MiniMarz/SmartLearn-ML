
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