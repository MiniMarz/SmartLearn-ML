
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
      {
        oldMeans[j]=new PointND(mu[j]);
      }
      // classify each instance x[i] to its nearest class
      // first we need to clear the class array since we are reclassifying
      for (int j=0;j<k;j++)
      {
        w[j]=new Vector();        // could use clear but then have to init...
      }

      for (int i=0;i<n;i++)
      {
        classify(x[i]);
      }
      // recompute each mean
      computeMeans();
      // compute the largest change in mu[j]
      maxDeltaMeans=maxDeltaMeans(oldMeans);
      numIterations++;
    }
    // now we find the quality of the model
    modelQuality(x);
  }

  /**
   * Find the quality of the model
  **/
  private void modelQuality(PointND[] x)
  {
    // compute the standard deviation of each cluster
    computeDeviation();
    // compute the prior of each cluster
    computePriors();
    // compute the log likelihood of each cluster
    computeLogLikelihood(x);
    // find the minimum description length of the model
    computeMDL();
  }


  /**
   * Classifies the point x to the nearest class
  **/
  private void classify(PointND x)
  {
    double dist=0;
    double smallestDist;
    int nearestClass;

    // compute the distance x is from mean mu[0]
    smallestDist=x.dist(mu[0]);
    nearestClass=0;

    // compute the distance x is from the other classes
    for(int j=1;j<k;j++)
    {
      dist=x.dist(mu[j]);
      if (dist<smallestDist)
      {
        smallestDist=dist;
        nearestClass=j;
      }
    }
    // classify x into class its nearest class
    w[nearestClass].add(x);
  }

  /**
   * Recompute mu[j] as the average of all points classified to the class w[j]
  **/
  private void computeMeans()
  {
    int numInstances;               // number of instances in each class w[j]
    PointND instance;

    // init the means to zero
    for (int j=0;j<k;j++)
      mu[j].setToOrigin();

    // recompute the means of each cluster
    for (int j=0;j<k;j++)
    {
      numInstances=w[j].size();
      for (int i=0;i<numInstances;i++)
      {
        instance=(PointND) (w[j].get(i));
        mu[j].add(instance);
      }
      mu[j].multiply(1.0/numInstances);
    }
  }

  /**
   * Compute the maximum change over each mean mu[j]
  **/
  private double maxDeltaMeans(PointND[] oldMeans)
  {
    double delta;
    oldMeans[0].subtract(mu[0]);
    double maxDelta=oldMeans[0].max();
    for (int j=1;j<k;j++)
    {
      oldMeans[j].subtract(mu[j]);
      delta=oldMeans[j].max();
      if (delta > maxDelta)
        maxDelta=delta;
    }
    return maxDelta;
  }

  /**
   * Report the results
   * Assumes the algorithm was run
  **/
  public void printResults()
  {
    System.out.println("********************************************");
    System.out.println("Trying " + k + " clusters...");
    System.out.println("Converged after " + numIterations + " iterations");
    for (int j=0;j<k;j++)
    {
      System.out.println();
      System.out.println("Gaussian no. " + (j+1));
      System.out.println("---------------");
      System.out.println("mean " + mu[j]);
      System.out.println("sigma " + sigma[j]);
      System.out.println("prior " + prior[j]);
    }
    System.out.println();
    System.out.println("Model quality:");
    System.out.println("Log-Likelihood " + logLikelihood);
    System.out.println("MdL " + MDL);
  }

  /**
   * Write into a file the k Gaussians (one for each column)