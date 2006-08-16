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
 * WhereFilter.java
 *
 * Created on 2003-05-04
 */

package com.rohanclan.ashpool.core.filter;

import java.util.*;

import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.Functions;
import com.rohanclan.ashpool.core.TableManager;

/**
 * Builds the where section of the xpath - this is a monster :)
 * @author  rob
 */
public class WhereFilter extends SQLFilter{
	
	//private List jointable;     //list of any/all join tables
	//private List joinfromc;     //list of from columns
	//private List jointoc;       //list of to columns
	private SelectFilter subS;  //for sub-selects 
	private boolean negitive = false;   //used for delete - the filter is the opposite
	
	public WhereFilter(TableManager tableman, CommandManager comman) throws Exception{
		super(tableman, comman);
		//jointable = new ArrayList();
		//joinfromc = new ArrayList();
		//jointoc   = new ArrayList();
		subS = new SelectFilter(tableman, comman);
	}
	
	public void setNot(boolean to){
		this.negitive = to;
	}
	
	public String createXPath(String sql) throws Exception {
		//if there is a sub query here, there should be
		//some values to replace
		sql = Functions.unEscapeSubQueries(sql);
		
		//this was originally written to look for the 'where' keyword
		sql = "where " + sql;
		
		//should be ready to tokenize now
		StringTokenizer stok = new StringTokenizer(sql," ");
		StringBuffer whereclause = new StringBuffer();
		
		//if(stok.hasMoreElements()){
			//if there is a where clause, the begining of the xpath command needs
			//to include all child nodes
			 /* [clientid = 3 or firstname = 'Steve'] */
			StringBuffer andorwhere = new StringBuffer();
			StringBuffer currentcolumn = new StringBuffer();
			StringBuffer searchop = new StringBuffer();
			
			
			while(stok.hasMoreElements()){
				//andorwhere = stok.nextElement().toString().toLowerCase();
				andorwhere.delete(0, andorwhere.length());
				
				//second or third go round this could be an and or or
				String conjuct = stok.nextElement().toString();
				if(conjuct.toLowerCase().equals("and") || conjuct.toLowerCase().equals("or")){
					andorwhere.append(conjuct.toLowerCase());
				}else{
					andorwhere.append(conjuct);
				}
				
				//look for a join
				/*if(andorwhere.toString().toLowerCase().equals("join")){
					//jointable = stok.nextElement().toString();
					jointable.add(stok.nextElement().toString());
					//on
					stok.nextElement();
					//joinfromc = stok.nextElement().toString();
					joinfromc.add(stok.nextElement().toString());
					//=
					stok.nextElement();
					//jointoc = stok.nextElement().toString();
					jointoc.add(stok.nextElement().toString());
					
					//reset andor where
					andorwhere.delete(0, andorwhere.length());
					if(stok.hasMoreElements()){
						andorwhere.append(stok.nextElement().toString());
					}
				}*/
				
				if(andorwhere.toString().toLowerCase().equals("where")){
					if(negitive){
						whereclause.append("[not (");
					}else{
						whereclause.append("[");
					}
				}else if(andorwhere.toString().equals(")")){
					whereclause.append(" " + andorwhere.toString() + " ");
					if(!stok.hasMoreElements()){
						break;
					}
				/*}else if(andorwhere.toString().toLowerCase().equals("order") 
					|| andorwhere.toString().toLowerCase().equals("by")){
					break;*/
				}else{
					if(andorwhere.toString().toLowerCase().equals("and") 
						|| andorwhere.toString().toLowerCase().equals("or")){
							whereclause.append(" "
								+ andorwhere.toString().toLowerCase() + " "
							);
					}else{
						whereclause.append(
							" " + andorwhere.toString() + " "
						);
					}
				}
				
				//check for a grouping marker (meaning a paren) and group if 
				//need be, else just add the column to the where clause
				andorwhere.delete(0, andorwhere.length());
				if(stok.hasMoreTokens()){
					andorwhere.append(stok.nextElement().toString());
				}
				//start of a group?
				if(andorwhere.toString().equals("(")){
					//open parent + real and or where clause
					whereclause.append(" " + andorwhere.toString() + " ");
					
					currentcolumn.delete(0, currentcolumn.length());
					currentcolumn.append(stok.nextElement().toString() + " ");
					
					whereclause.append(currentcolumn.toString());
				//end of a group?
				}else if(andorwhere.toString().equals(")")){
					whereclause.append(" " + andorwhere.toString());
					if(!stok.hasMoreElements()){
						break;
					}
				//and or or
				}else if(andorwhere.toString().toLowerCase().equals("and")
					|| andorwhere.toString().toLowerCase().equals("or")){
					whereclause.append(" " + andorwhere.toString().toLowerCase() + " ");
				
				//function?
				//this doesn't work well - if I am going to do this function stuff
				//I need a better way list?
				}else if(Functions.isDateFunction(andorwhere.toString().toLowerCase())){
					andorwhere.insert(0,"date:");
					//the (
					String funbuild = stok.nextToken().toString();
					andorwhere.append(funbuild);
					
					//should be the field name
					funbuild = stok.nextToken().toString();
					andorwhere.append("/*/*/");
					
					while(funbuild.equals(")")){
						andorwhere.append(funbuild);
						funbuild = stok.nextToken().toString();
					}
					andorwhere.append(funbuild);
					
					currentcolumn.delete(0,currentcolumn.length());
					currentcolumn.append(andorwhere + " ");
					whereclause.append(currentcolumn.toString());
				/* }else if(Functions.isMathFunction(andorwhere.toString().toLowerCase())){
					//add the namespace to the function
					andorwhere.insert(0,"math:");
				*/
				////////////////////////////////////////////////////////////////
				}else{
					currentcolumn.delete(0,currentcolumn.length());
					currentcolumn.append(andorwhere + " ");
					
					whereclause.append(currentcolumn.toString());
				}
				
				// this is the =, !=, >, like, etc section
				String opr = "";
				if(stok.hasMoreTokens()){
					opr = stok.nextElement().toString();
				}
				searchop.delete(0,searchop.length());
				if(opr.toString().toLowerCase().equals("not")){
					//catch 'not like'
					searchop.append("!");
					opr = stok.nextElement().toString();
				}else if(opr.toString().toLowerCase().equals("is")){
					//catch 'is not null' and 'is null'
					opr = stok.nextElement().toString();
					
					if(opr.toString().toLowerCase().equals("not")){
						opr = " != ";
					}else{
						opr = " = '' ";
					}
				}
				//where operator
				searchop.append(Functions.escapeOperator(opr));
				//System.out.println("wf_op: " + whereclause.toString() + " " + searchop.toString());
				
				///////////////////////////////////////////////////////////
				//where value likes
				//only for likes
				if(searchop.toString().equals("= contains(") 
					|| searchop.toString().equals("!= contains(")){
					//xslt 2.0 caused a bit of a change in the way ashpool needs
					//to handle like statements.
					StringBuffer searchstring = new StringBuffer();
					boolean negitive = false;
					 
					//do we need to add not?
					if(searchop.toString().startsWith("!")){
						negitive = true;
					}
					
					searchop.delete(0,searchop.length());
					  
					//first we have to remove the column we just added from the list
					whereclause.delete(
						whereclause.length() - currentcolumn.length(), whereclause.length()
					);
					  
					//searchop.append(currentcolumn + ",");
					
					//multi word search criteria 'United States'
					if(stok.hasMoreTokens()){
						String criteria = stok.nextElement().toString();
						if(criteria.startsWith("'")){
							while(!criteria.endsWith("'")){
								searchstring.append(" " + criteria.toString());
								criteria = stok.nextElement().toString();
							}
							searchstring.append(" " + criteria.toString());
						}/*else if(criteria.equals("(")){
							System.out.println("a subquery");
							try{
								String tmpq = "";
								StringBuffer subq = new StringBuffer();
								while(!tmpq.equals(")")){
									subq.append(" " + tmpq);
									tmpq = stok.nextElement().toString();
								}
								whereclause.append(executeQuery(
									subq.toString(),SelectFilter.FORSINGLE).toString().trim()
								);
							}catch(Exception e){
								System.err.println("Subquery failed " + e.toString());
								//e.printStackTrace(System.err);
							}
						}else{
							if(criteria.toString().toLowerCase().equals("null")){
								whereclause.append(" '' ");
							}else{
								whereclause.append(" " + criteria.toString() + " ");
							}
						}*/
					}
					
					if(searchstring.toString().trim().startsWith("'%") && searchstring.toString().trim().endsWith("%'")){
						searchop.append(" contains(" + currentcolumn + ",'" 
							+ searchstring.toString().trim().substring(2,searchstring.length()-3) + "') "
						);
					}else if(searchstring.toString().trim().endsWith("%'")){
						searchop.append(" starts-with(" + currentcolumn + ",'" 
							+ searchstring.toString().trim().substring(1,searchstring.length()-3) + "') "
						);
					}else if(searchstring.toString().trim().startsWith("'%")){
						searchop.append(" ends-with(" + currentcolumn + ",'" 
							+ searchstring.toString().trim().substring(2,searchstring.length()-2) + "') "
						);
					}else{
						searchop.append(" contains(" + currentcolumn + ",'" 
							+ searchstring.toString().trim().substring(1,searchstring.length()-2) + "') "
						);
					}
					
					if(negitive){ 
						whereclause.append(" not (" + searchop.toString() + ") "); 
					}else{
						//searchop.append(") ");
						whereclause.append(searchop.toString());
					}
					
				//non likes
				}else{
					whereclause.append(searchop.toString());
					
					///////////////////////////////////////////////////////////
					//where value non likes
					
					//multi word search criteria 'United States'
					if(stok.hasMoreTokens()){
						String criteria = stok.nextElement().toString();
						
						//System.out.println(criteria);
						
						if(criteria.startsWith("'")){
							while(!criteria.endsWith("'")){
								whereclause.append(" " + criteria.toString());
								criteria = stok.nextElement().toString();
							}
							whereclause.append(" " + criteria.toString());
						
						//sub-query
						}else if(criteria.equals("(")){
							//System.out.println("b subquery" + whereclause.toString());
							try{
								String tmpq = "";
								StringBuffer subq = new StringBuffer();
								while(!tmpq.equals(")")){
									subq.append(" " + tmpq);
									tmpq = stok.nextElement().toString();
								}
								whereclause.append(subS.executeQuery(
									subq.toString(),SelectFilter.FORSINGLE).toString().trim()
								);
								//criteria = "";
							}catch(Exception e){
								System.err.println("Subquery failed " + e.toString());
								//e.printStackTrace(System.err);
							}
						//if the said whatever = null or just specified a column
						}else{
							if(criteria.toString().toLowerCase().equals("null")){
								whereclause.append(" '' ");
							}else{
								whereclause.append(" " + criteria.toString() + " ");
							}
						}
					}
				}
				if(!stok.hasMoreElements()){
					break;
				}
			}
			//close out the where clause
			if(whereclause.toString().trim().length() > 0){
				if(negitive){
					whereclause.append(")]");
				}else{
					whereclause.append("]");
				}
			}
		//}
		
		return whereclause.toString();
	}
	
}
