
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