
import java.util.*;

public class HypothesisComparator implements Comparator
{

  public HypothesisComparator() {}

  // Compare two hypotheses based on their fitness value
  public int compare(Object o1,Object o2)
  {
    Hypothesis h1=(Hypothesi