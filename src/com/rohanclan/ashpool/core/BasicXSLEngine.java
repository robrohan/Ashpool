/*
 * Ashpool - XML Database
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
 */ 
package com.rohanclan.ashpool.core;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//import org.apache.xalan.processor.TransformerFactoryImpl;

/**
 * A basic xslt transforming engine. Just applies an XSLT style sheet to an 
 * xml document.
 *
 * @author Rob Rohan
 */

/** creates a new instance of treebeard */
public class BasicXSLEngine {
	 
	/** which transformer to use: xalan by default */
	public static String XSLTFactory = "javax.xml.transform.TransformerFactory";
	//public static String SAXFactory  = "";
	//this shouldn't be hard coded really, but on mac System.getProperty("org.xml.sax.driver")
	//seems to always be null.
	public static String SAXDriver = "org.apache.crimson.parser.XMLReaderImpl";
	
	/** the params to pass to the style sheet */
	public java.util.Vector params;
	
	/** Creates a new instance of BasicXSLEngine */
	public BasicXSLEngine() 
	{
		doFactoryReset();
		params = new java.util.Vector();
	}
	
	/** change the xslt factory (call doFactoryRest) */
	public void setXSLFactory(String factory)
	{
		XSLTFactory = factory;
	}
	
	/** change the sax factory (call doFactoryRest) */
	/* public void setSAXFactory(String factory){
		SAXFactory = factory;
	} */
	
	/** set a single param */
	public void setParam(String name, String value)
	{
		params.add(name);
		params.add(value);
	}
	
	/** set fresh params */
	public void clearParams()
	{
		params.removeAllElements();
	}
	
	/** set a bunch of params */
	public void setParams(java.util.Vector inparams)
	{
		params = inparams;
	}
	
	public void doFactoryReset()
	{
		//seems to boom on OS X unless specified, but if done in java 1.5 it bombs
		//too so try to set it only if needed
		if(System.getProperty("org.xml.sax.driver") == null || System.getProperty("org.xml.sax.driver").equals(""))
		{
			System.setProperty("org.xml.sax.driver", SAXDriver);	
		}
	}
	
	/** Performs an xslt transformation
	 * @param xslin a stream containing the xsl style sheet
	 * @param xmlin a stream containing the xml
	 * @param output the stream to write the results to
	 */    
	public long transform(InputStream xmlin, InputStream xslin, OutputStream output) throws Exception {
		long start = System.currentTimeMillis();
		
		StreamResult strResult = null;
		//String media = null;
		
		 //get an instance and make a result object
		TransformerFactory tfactory = /*net.sf.saxon.TransformerFactoryImpl*/javax.xml.transform.TransformerFactory.newInstance();
		strResult = new StreamResult(output);
		
		//SAXTransformerFactory stf = (SAXTransformerFactory) tfactory;
		Templates stylesheet = tfactory.newTemplates(new StreamSource(xslin));
		Transformer transformer = stylesheet.newTransformer();
			
		//set params in the style sheets
		int psize=params.size();
		for(int i=0; i < psize-1; i++)
			transformer.setParameter(
				(String)params.elementAt(i), (String)params.elementAt(i+1)
			);
			
		//do the transformation
		StreamSource input = new StreamSource(xmlin);
		
		//Exception in thread "main" java.lang.OutOfMemoryError
		//happens right here if I only set to 16mb and try to execute
		//select country from factbook;
		transformer.transform(input, strResult);
		
		return System.currentTimeMillis() - start;
	}
}
