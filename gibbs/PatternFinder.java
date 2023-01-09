
package gibbs;

import java.util.*;
import java.io.*;

/**
 * @author Alex Ksikes
 * @version 1.0
 *
 * Application of the Gibbs sampling algorithm technique
 * Detecting motifs in multiple sequences: we are given a set of N sequences
 * and we seek mutually similar segments of specified length L in each sequences.
 *
 * Note that selecting a segment at random according to the weights {Ax=Qx/Px} is an
 * application of the Gibbs algorithm.  An hypothesis hx, that the true segment is x, is
 * selected with probability P(hx/D)~Ax.  Here the posterior is calculated explicitely
 * from the training data D, that defines the pattern description probabilities and
 * the background probabilities.
 *
 */

public class PatternFinder
{

  private final static int NUM_ITER=1000;
  private final static int MAX_NO_UPDATE=100;
  private final static int MAX_EPOCH=10;
  protected static final double eps=1.0e-99;
  private final static char[] ENGLISH={'A','B','C','D','E','F','G','H','I','J','K','L','M',
                                       'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
  private final static char[] DNA={'A','C','G','T'};

  private char[] symbol;            // alphabet (english, french, DNA/protein...)
  private int numSymbols;           // number of symbols in the alphabet
  private String[] sequence;        // set of sequences
  private int N;                    // number of sequences
  private int L;                    // pattern length
  private double[][] q;             // patern description
  private double[] p;               // background probabilities
  private int[] a;                  // starting point of the pattern in each sequence
  private int[] besta;              // best starting point of the pattern in each sequence
  private double bestLikelihood;    // best likelihood of the patterns
  private int noUpdate;             // heuristic for convergence
  private int totalLength;          // length of all sequences
  private double m;                 // speudo-count (number of virtual examples)

  /**
   * Constructor
   *
   * @param dataFile The file containing the sequences.
   * @param L The length of the pattern.
   * @param alphabet 0 for english alphabet, 1 for DNA.
  **/
  public PatternFinder(String dataFile, int L, int alphabet)
  {
    // select the alphabet
    switch (alphabet)
    {
      case 0:
        this.symbol=ENGLISH;
        break;
      case 1:
        this.symbol=DNA;
        break;
      default:
        System.exit(0);
    }
    this.numSymbols=symbol.length;
    // read each sequence from the datafile
    try
    {
      BufferedReader bf=new BufferedReader(new FileReader(dataFile));
      int size=0;
      while (bf.readLine()!=null)
        size++;
      this.N=size;
      this.sequence=new String[N];
      this.totalLength=0;
      bf=new BufferedReader(new FileReader(dataFile));
      for (int k=0; k<N; k++)
      {
        this.sequence[k]=bf.readLine();
        this.totalLength+=sequence[k].length();
      }
    }
    catch (IOException e)
    {
      System.err.println(e.toString());
      e.printStackTrace();
    }
    // all other initializations
    this.L=L;
    this.q=new double[L][numSymbols];
    this.p=new double[numSymbols];
    this.a=new int[N];
    this.besta=new int[N];
    this.m=Math.sqrt(N);       // dependant on the number of sequences.
  }

  /**
   * Runs the Gibbs sampling algorithm
   *
   * @param mode 1 for model refinement.
  **/
  public void run(int mode)
  {
    // Initialize the patterns by selecting random starting points for the pattern in each sequence
    init();

    // Iterate until the model converges or until a max number of iterations
    int iteration=0, Z;
    double [] A;
    while(!converged() && iteration<=NUM_ITER)
    {
      // Predictive step
      // choose at random one the sequences (Z)
      Z=(int) (Math.random()*N);
      // calculate the model from the current position in all sequences except Z
      calculateModel(Z);

      // Sampling step
      // calculate the probability of every possible segment in the sequence Z
      int numSegments=sequence[Z].length()-L;
      A=new double[numSegments];
      for (int i=0; i<numSegments; i++)
        A[i]=computeWeight(Z,i);
      // select a segment at random considering these weights (A)
      // its position becomes the new position a[Z]
      a[Z]=select(A);

      // Compute the likelihood ratio of the patterns
      updateBestModel();

      // Report the best set of patterns
      report(iteration);
      iteration++;
    }

    // Model refinement
    if (mode==1)
    {
      refine();
      System.out.println("After Model Refinement: ");
      report(iteration-1);
    }
  }

  /**
   * Initialize the algorithm by selecting random starting points for the
   * pattern in each sequence and other natural init.
  **/
  private void init()
  {
    noUpdate=0;
    // chose random starting point of the pattern.
    for (int k=0; k<N; k++)
    {
      a[k]=(int) (Math.random()*(sequence[k].length()-L+1));
      besta[k]=a[k];
    }
    // initialize model and background probabilities.
    for (int i=0; i<L; i++)
      for (int j=0; j<numSymbols; j++)
        q[i][j]=1.0/numSymbols;
    for (int j=0; j<numSymbols; j++)
      p[j]=1.0/numSymbols;
    // initialize the best likelihood found so far.
    calculateModel(-1);
    bestLikelihood=computeLikelihood();
  }

  /**
   * The algorithm has converged if the best set of patterns hasn't changed for
   * a specified number of iterations.
   * Note that the last model isn't necessarly the best model since this randomized
   * algorithm will get out of local minima by having to backtrack.
   *
   * @return true if the algorithm has converged, 0 otherwise.
  **/
  private boolean converged()
  {
    if (noUpdate==MAX_NO_UPDATE)
    {
      noUpdate=0;
      return true;
    }
    return false;
  }

  /**
   * Calculate the pattern description probabilities and the background probabilities
   * of the model (set of patterns).
   * Uses speudo-count to avoid undersampling issues, the prior probabilities are chosen
   * to be 1/(number of letters in the alphabet).
   *
   * @param Z the sequence to be excluded, use -1 if no sequence to exclude.
  **/
  private void calculateModel(int Z)
  {
    // compute the pattern description q[i][j]
    int[] n=new int[L];       // number of symbols j at position i

    for (int j=0; j<symbol.length; j++)
    {
      for (int i=0; i<L; i++)
      {
        for (int k=0; k<N; k++)
        {
          if (k!=Z && getPattern(k,a[k]).charAt(i)==symbol[j])
            n[i]++;
        }
        q[i][j]=(n[i]+m*1.0/numSymbols)/(N-1+m);
      }
      Arrays.fill(n,0);
    }

    // compute the backgroung propababilities p[j]
    n=new int[numSymbols];        // number of symbols j
    for (int j=0; j<symbol.length; j++)
    {
      for (int k=0; k<N; k++)
      {
        if (k!=Z)
        {
          for (int i=0; i<sequence[k].length(); i++)
          {
            if ( !(i>=a[k] && i<=a[k]+L) && sequence[k].charAt(i)==symbol[j])
              n[j]++;
          }
        }
      }
      p[j]=(n[j]+m*1.0/numSymbols)/(totalLength-((N-1)*L+1)+m);
    }
  }

  /**
   * Calculate the probability of a segment according to the model and to the
   * background probabilities.
   * Chosing such a likelihood ratio gives more weight to symbols that appear
   * less in the background and less weight to symbols that we find all over the
   * background (reduces the probability to have gotten these symbols by chance).
   *
   * @param k The sequence where the segment is located.
   * @param spos The starting position of the pattern.
   *
   * @return The weight given to this segment.
  **/
  private double computeWeight(int k, int spos)
  {
    String segment=getPattern(k,spos);
    int symbolNo;
    double Q=1,P=1;
    for (int i=0; i<segment.length(); i++)
    {
      symbolNo=Arrays.binarySearch(symbol,segment.charAt(i));
      Q*=q[i][symbolNo];
      P*=p[symbolNo];
    }
    // avoids machine precision problem (may not be necessary)
    if (P<=eps)
      return Q/eps;

    return Q/P;
  }

  /**
   * Select a segment at random considering an array of weights of each segment.
   *
   * @param weight Array of weights, weight[i] is the weight of segment i.
   *
   * @return The index of the chosen segment.
  **/
  private int select(double[] weight)
  {
    double sum=0;
    for (int i=0; i<weight.length; i++)
      sum+=weight[i];
    for (int i=0; i<weight.length; i++)
      weight[i]=weight[i]/sum;
    double flip=Math.random();
    if (flip>=0 && flip<=weight[0])
      return 0;
    else
    {
      int i;
      for (i=1; i<weight.length; i++)
      {
        if (flip>=weight[i-1] && flip<=weight[i])
          break;
      }
      return i;
    }
  }

  /**
   * Compute the likelihood of the current set of patterns.
   *
   * @return The likelihood the model.
  **/
  public double computeLikelihood()
  {
    calculateModel(-1);
    double likelihood=0;
    for (int k=0; k<N; k++)
    {
      likelihood+=Math.log(computeWeight(k,a[k]));
    }
    return likelihood;
  }

  /**
   * Print the best model seen so far.
  **/
  private void report(int iteration)
  {
    System.out.println("Iteration: "+iteration);
    for (int k=0; k<N; k++)
    {
      System.out.println("sequence "+k+" position "+besta[k]+" "+getPattern(k,besta[k]));
    }
    System.out.println("likelihood ratio: "+bestLikelihood);
    System.out.println();
  }

  /**
   * Scan the sequences around the patterns of the best model, considering all
   * possible model shifts.
   * Model refinement gives substantial improvement as the program might get
   * trapped in a shifted model.
  **/
  private void refine()
  {
    int[] lShift=new int[N];
    int[] rShift=new int[N];
    // copy starting points of the patterns
    for (int k=0; k<N; k++)
    {
      lShift[k]=a[k];
      rShift[k]=a[k];