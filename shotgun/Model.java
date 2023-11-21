/**
 * <p>Title: Shotgun Project</p>
 * <p>Description: </p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Alex Ksikes
 * @version 2.1
**/

package shotgun;

import java.io.*;

/**
 * A model could either be a set of predictions or an N-class ensemble.
**/
public abstract class Model implements Comparable
{

  protected static final int ACC=0, RMS=1, ROC=2, ALL=3 , BEP=4 , PRE=5 , REC=6,
                             FSC=7, APR=8, LFT=9, CST=10, NRM=11, MXE=12, BSP=13;
  protected static final String[] measure={"ACC","RMS","ROC","ALL","BEP","PRE","REC",
                                           "FSC","APR","LFT","CST","NRM","MXE","BSP"};
  protected static final double eps=1.0e-99;

  protected int numModels;                    // number of models added
  protected double performance;               // performance of the model
  protected String name;                      // name of the model
  prot