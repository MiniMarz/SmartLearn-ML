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
  private Predictions nClassPred;

  public Ensemble(Targets[] targets, int size)
  {
    this.n=targets.length;
    this.numModels=0;
    for (int i=0; i<n; i++)
    {
      this.nClassBag[i]=new Predictions(targets[i]);
    }
    this.nClassPred=new Predictions(targets[0]);
    this.nClassPred.setNumModels(1);                         // ugly
  }

  public Ensemble(double perf)
  {
    this.performance=perf;
  }

  public Model justPerf()
  {
    computePerformance();
    return new Ensemble(getPerformance());
  }

  public void add(Predictions model)
  {
    this.nClassBag[model.getPredictedClass()].add(model);
    updateNClassPred();
    this.name=model.getName();
    this.numModels+=model.getNumModels();
  }

  public void sub(Predictions model)
  {
    this.nClassBag[model.getPredictedClass()].sub(model);
    updateNClassPred();
    this.name=model.getName();
    this.numModels-=model.getNumModels();
  }

  public double compute(int i)
  {
    return nClassPred.compute(i);
  }

  private void updateNClassPred()
  {
    int argMax=0;
    double max=nClassB