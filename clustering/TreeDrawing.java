
/**
 * @author Alex Ksikes
 **/

import java.awt.*;
import java.io.*;

/**
 * This class makes a tree out of a balanced parenthesis string.
 * Example of a balanced parenthesis string: ((1)((2)(4))).
 * This class is used to draw a tree out of a cluster representation.
 * Still need improvements (did this very quickly)
**/
public class TreeDrawing extends Canvas
{

  String balancedString;      // the string we wish to draw as a tree.
  int xInit,yInit,yInc;
  Dimension d;                // dimension of the canvas to draw a tree.

  /**
   * Default constructor
  **/
  public TreeDrawing(String balancedString)
  {
    if (!checkBalanced(balancedString))
    {
      System.out.println("String is not balanced!");
    }
    else
    {
      System.out.println("String balanced okay.");
      this.balancedString=new String(balancedString);
      this.d=new Dimension(1000,1000);
      this.setSize(d);
      xInit=800;
      yInit=20;
      yInc=10;
    }
  }

  /**
   * Check if string s is well balanced.
  **/
  public boolean checkBalanced(String s)
  {
    int count=0;
    for (int i=0;i<s.length();i++)
    {
      if(s.charAt(i)=='(')
        count++;
      if(s.charAt(i)==')')
        count--;
    }
    return count==0;
  }

  /**
   * Get the left part of string s.
  **/
  public String getLeft(String s)
  {
    if (s.charAt(1)!='(')
    {
      return s;
    }
    else
    {
      int open=1;
      int index=1;
      char c;
      while(open!=0)
      {
        index++;
        c=s.charAt(index);
        if (c=='(')
          open++;
        if (c==')')
          open--;
      }
      //System.out.println("Left = "+s.substring(1,index+1));
      return s.substring(1,index+1);
    }