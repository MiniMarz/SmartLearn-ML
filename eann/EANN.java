
/**
 * This class is the entry point to the program
  * Author: Alex Ksikes
 */

import java.io.*;

public class EANN {

  // This is the main function
  public static void main(String[] args) throws IOException
  {
    if (args.length!=3)
    {
      System.out.println("Wrong usage. Type java EANN [population size] [selection size]" +
      "[mutation rate] [mode] [initial number of hidden neurons] [training set]");
    }
    // p is the number of hypotheses in population
    int p=Integer.valueOf(args[0]).intValue();
    // r is the fraction of the population to be replaced by Crossover at each step
    double r=Double.valueOf(args[1]).doubleValue();
    // m is the rate of mutation
    double m=Double.valueOf(args[2]).doubleValue();
    // mode is 0 if the number of hidden neurons is fixed and 1 otherwise
    int mode=Integer.valueOf(args[3]).intValue();
    // initNumHiddenNeurons is the maximum number of hidden neurons allowed for an initial population
    int initNumHiddenNeurons=Integer.valueOf(args[4]).intValue();
    // the training set
    DataSet trainingSet=new DataSet(args[5]);