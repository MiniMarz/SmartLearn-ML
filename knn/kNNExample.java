/**
 * @author Alex Ksikes
 **/

import ann.*;

/**
 * This class describes a particular example for the kNN algorithm.
 */
public class kNNExample extends Example
{

  private double relativeDistance;         // The distance this example is from another example
  private double weight;                   // Weight given to this example
  private int id;                          // A unique id for this example
  private static double[] featureWeights;  // Used to compute weighted distance

  /**
   * Constructor
  */
  public kNNExample(Example example, int id)
  {
    super(example);
    this.id = id;
    this.weight = 1;  // by de