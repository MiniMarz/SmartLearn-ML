
/**
 * Implementation of the k-means algorithm
  * Author: Alex Ksikes
**/

import java.util.*;
import java.io.*;

public class kMeans
{

  private int n;                          // number of instances to classify
  private int d;                          // number of coordinates of each point
  private int k;                          // number of clusters
  private PointND[] mu;                   // coordinate of means mu[j] of each cluster j
  private Vector[] w;                     // holds the points classified into each class w[j]
  private PointND[] sigma;                // holds the standard deviation of each class i
  private double[] prior;                 // holds the prior of each class i
  private double logLikelihood;           // holds the log likelihood of each of the k Gaussians
  private double MDL;                     // the minimum description length of the model
  private int numIterations;

  /**
   * Default constructor
  **/
  public kMeans()
  {
  }

  /**
   * Intialize the parameters of the k-means algorithm
   * Randomly assign a point in x to each mean mu[j]
  **/
  private void init(PointND[] x,int k)
  {
    this.n=x.length;
    this.d=x[0].getDimension();
    this.k=k;
    this.mu=new PointND[k];
    this.w=new Vector[k];
    this.numIterations=0;
    this.sigma=new PointND[k];
    this.prior=new double[k];

    // randomly assign a point in x to each mean mu[j]
    PointND randomPoint;
    for (int j=0;j<k;j++)
    {
      randomPoint=x[(int)(Math.random()*(n-1))];
      mu[j]=new PointND(randomPoint);
      // each prior and standard deviation are set to zero
      sigma[j]=new PointND(d);
      prior[j]=0;
    }
  }

  /**
   * Runs the k-means algorithm with k clusters on the set of instances x
   * Then find the quality of the model
  **/
  public void run(PointND[] x,int k,double epsilon)
  {
    double maxDeltaMeans=epsilon+1;
    PointND[] oldMeans=new PointND[k];
    // initialize n,k,mu[j]
    init(x,k);
    // iterate until there is no change in mu[j]
    while (maxDeltaMeans > epsilon)
    {
      // remember old values of the each mean
      for (int j=0;j<k;j++)