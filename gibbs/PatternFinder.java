
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