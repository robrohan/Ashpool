/*
 * Ashpool - XML Database
 * Copyright (C) 2003 Rob Rohan
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 * About.java
 *
 * Created on March 26, 2003, 11:08 PM
 */

package com.rohanclan.ashpool.cmds;

/**
 * Silly little about screen
 * @author  rob
 */
public class About implements AshpoolCmd {
	public static final String VERSION = "0.3.5";
	public static final String YEAR="2003";
	
	/** Creates a new instance of About */
	public About() {;}

	public void doAction() throws Exception {
	System.out.println("");
	System.out.println("                        . + + + + + + @ .");
	System.out.println("                  # $ % + & * & & & & & = $ - ;");
	System.out.println("              > , '                         ) ' !");
	System.out.println("            ~ , {                               ] ^");
	System.out.println("          / (                                     _ : <");
	System.out.println("        [ [      << Ashpool version "+VERSION+" >>         } >");
	System.out.println("      | $               <xml_database/>              1 2 >");
	System.out.println("      3          (c)"+YEAR+" Rohan Clan (Rob Rohan)         4 5");
	System.out.println("    6 7                                                   8 7");
	System.out.println("  9 0                       @ < a * >                     9 #");
	System.out.println("  b c                 d e 5 ^ , + f 2 g >   >               h 7");
	System.out.println("  i               7 j + + + + + + + + + + k + %             l k");
	System.out.println("> m           n g + + + + + + + + + + + + o + + g n         > o");
	System.out.println("> p         q ^ + + + + + + + + + + + + + + + e r ^ n       > +");
	System.out.println("s t         5 + + + + + + + + + + + + + + + + h ! + u       & v");
	System.out.println("+ >       w + + + + + + + + + + + + + + + + + x y + +       z A");
	System.out.println("+ >     q + + + + ! o + + + + + + + + + + + + y q v 6 B m h C");
	System.out.println("D >   E + + d D F G H 5 d + + h d + + + d j d d + 9     9");
	System.out.println("> I   ( + ~ J 3 K K f L   | f M   N + h");
	System.out.println("  m O + +   + 7 9 P Q R >   Q R S T U");
	System.out.println("  R = + - O V   w 3   m >     R + # [ W");
	System.out.println("  z ^ + 2   f     +   m >     X , Y [ + 2 >");
	System.out.println("  * + + +   5 + + !   Z >       >   `  .a");
	System.out.println("    % + + | H a l   ..Z               q >");
	System.out.println("      + 8 + K +.) y  .");
	System.out.println("      @.+ A u R + W");
	System.out.println("        V #.");
	System.out.println("          x u                                       .");
	System.out.println("            ..$..                                 %.");
	System.out.println("              > o y                           &.u >");
	System.out.println("                  C $ = ~               ~ d g *");
	System.out.println("                        ) o ^ ^ ^ ^ ^ l )");
	System.out.println("");
	}
	
}
