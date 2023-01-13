
/**
 * This class describes a n dimensional point
  * Author: Alex Ksikes
**/
public class PointND
{

  private int dimension;                // number of coordinates of a point
  private double[] coordinates;         // the coordinates of a point

  /**
   * Create a point centered at the origin of the specific dimension
  **/
  public PointND(int dimension)
  {
    this.dimension=dimension;
    coordinates=new double[dimension];
  }

  /**
   * Create a new point identical to point p
  **/
  public PointND(PointND p)
  {
    this.dimension=p.getDimension();
    this.coordinates=new double[dimension];
    for (int i=0;i<dimension;i++)
      this.coordinates[i]=p.getCoordinate(i);
  }

  /**
   * Create a new point identical to point p
  **/
  public void setToOrigin()
  {
    for (int i=0;i<dimension;i++)
      coordinates[i]=0;
  }