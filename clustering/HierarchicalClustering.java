
/**
 * @author Alex Ksikes
 **/

import java.io.*;
import java.util.*;

/**
 * Implements agglomerative hirarchical clustering.
**/
public class HierarchicalClustering
{

  Vector clusters;      // keeps track of clusters
  int size;             // number of clusters considered at a given momment

  /**
   * Default constructor
  **/
  public HierarchicalClustering()
  {
  }

  /**
   * Initialize the algorithm.
  **/
  public void init(String filename)
  {
    Cluster.setDistances(filename);

//    // for experiments: scale the distances
//    Cluster.scaleDistances();

    this.clusters=new Vector();
    // each point is in its own cluster
    this.size=Cluster.distances.length;
    for (int i=0; i<size; i++)
      clusters.add(new Cluster(i,i));
  }

  /**
   * Returns the cluster at the specified index i.
  **/
  public Cluster getCluster(int i)
  {
    return ((Cluster) clusters.get(i));
  }

  /**
   * Returns the two closest clusters.
   * Assumes at least two clusters are left.
  **/
  public Cluster[] findNearest()
  {
    Cluster[] closest=new Cluster[2];
    Cluster c1=getCluster(0);
    Cluster c2=getCluster(1);
    double minMeanDist=c1.meanDistance(c2);