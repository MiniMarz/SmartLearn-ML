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
 * A class encapsulating an N-class ensemble.
 * This class is used for N-class classification.
**/
public class Ensemble extends Model
{

  private int n;
  private Predictions[] nClassBag;
  private P