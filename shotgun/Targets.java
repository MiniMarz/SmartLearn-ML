
/**
 * <p>Title: Shotgun Project</p>
 * <p>Description: </p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Alex Ksikes
 * @version 2.1
**/

package shotgun;

import java.util.*;
import java.io.*;

/**
 * A class encapsulating the targets of a set of examples.
**/
public class Targets
{

  private int[] trueValue;       // holds the targets of each example
  private int total_true_0;      // total number of true value 0
  private int total_true_1;      // total number of true value 1

  /**
   * Builds a set of targets given a file of label for each example.
   *
   * @param trueValue The file containing the labels.