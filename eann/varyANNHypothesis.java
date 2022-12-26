
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