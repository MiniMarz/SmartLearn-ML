
/**
 * @author Alex Ksikes
 **/

import java.io.*;
import java.util.*;

/**
 * A cluster consists of a set of points and a representation.
**/
public class Cluster
{

  public static double[][] distances;   // holds pairwise distances between points in diagonal form
  private Vector points;                // holds the points a cluster has
  private int size;                     // number of points of a cluster
  private int id;                       // unique id of a cluster
  private String representation;        // rep-n of a cluster in balanced parenthesis form

  /**
   * Builds a cluster with one point.
  **/
  public Cluster(int point, int id)
  {
    this.points=new Vector();
    points.add(new Integer(point));
    this.size=1;
    this.id=id;
    this.representation="("+String.valueOf(point)+")";
  }

  /**
   * Merge two clusters.
  **/
  public void merge(Cluster c)
  {
    points.addAll(c.getPoints());
    size=points.size();
    updateRepresentation(c);
  }

  /**
   * Returns the mean distance between this and a cluster c.
  **/
  public double meanDistance(Cluster c)
  {
    double distance=0;
    for (int i=0; i<size; i++)
    {
      for (int j=0;j<c.getSize(); j++)
      {
        distance=distance + dist(getPoint(i),c.getPoint(j));
      }
    }
    return distance/(size*c.getSize());
  }

  /**
   * Returns the minimum distance between this and a cluster c.
  **/
  public double minimumDistance(Cluster c)
  {
    double currentDist;
    double minDist=dist(getPoint(0),c.getPoint(0));
    for (int i=0; i<size; i++)
    {
      for (int j=0;j<c.getSize(); j++)
      {
        currentDist=dist(getPoint(i),c.getPoint(j));
        if (currentDist<minDist)
          minDist=currentDist;
      }
    }
    return minDist;
  }

  /**
   * Compute the mean internal distance of this cluster.
  **/
  public double meanInternalDistance()
  {
    if (size==1)
      return 0;
    else
      return meanDistance(this)*size/(size-1);
  }

  /**
   * Get the point at the specified index i of this cluster.
  **/
  public int getPoint(int i)
  {
    return ((Integer) points.get(i)).intValue();
  }

  /**
   * Update the balanced parenthesis form of this cluster
   * after merging with a cluster c.
  **/
  public void updateRepresentation(Cluster c)
  {
    setRepresentation("(" + representation + "" + c.getRepresentation() + ")");
  }

  public void setRepresentation(String representation)
  {
    this.representation=representation;
  }

  public String getRepresentation()
  {
    return representation;
  }

  public Vector getPoints()
  {
    return points;
  }

  public int getSize()
  {
    return size;
  }

  public int getId()
  {
    return id;
  }

  public String toString()
  {
    return representation;
  }

  public boolean equals(Cluster c)
  {
    return (id==c.getId());
  }

  /**