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
    this.weight = 1;  // by default each weight is set to 1
  }

  /**
   * Set the weight of this example based on the kernel width.
   * If kernel = 0 then unweighted kNN.
   * If kernel = 1 then weighted kNN.
  */
  public void setWeight(int kernel)
  {
    this.weight = 1.0/Math.exp(kernel * relativeDistance);
  }
