import java.io.*;

// Class to test the mystery dataset on my saved network
public class NeuralTest {

  public static void main(String args[])
  {
    if (args.length!=2)
    {
      System.out.println("Wrong usage. Type java NeuralTest [evaluationFile] [savedANN]");
    }
    else
    {
      DataSet mysteryDataSet=new DataSet(args[0]);
      NeuralN