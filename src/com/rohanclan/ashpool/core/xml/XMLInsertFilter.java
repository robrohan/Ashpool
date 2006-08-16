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
 * XMLInsertFilter.java
 *
 * Created on March 15, 2003, 9:40 AM
 */

package com.rohanclan.ashpool.core.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Inserts an xml fragment into an xml document right after
 * the <i>tablemarker</i>
 * @author  rob
 */
public class XMLInsertFilter extends XMLFilterImpl {
	
	private String tablemarker="";
	//the document fragment 
	private org.w3c.dom.Document resultset;
	
	/** Creates a new instance of XMLInsertFilter */
	public XMLInsertFilter(XMLReader reader, String tablemarker, String XMLFrag) {
		super(reader);
		this.tablemarker = tablemarker;
		
		try{
			javax.xml.parsers.DocumentBuilderFactory factory 
				= javax.xml.parsers.DocumentBuilderFactory.newInstance();
			javax.xml.parsers.DocumentBuilder builder 
				= factory.newDocumentBuilder();
			resultset = builder.parse(
				new java.io.ByteArrayInputStream(
					("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>" + XMLFrag).getBytes()
				)
			);
		}catch (Exception e){
			System.err.println("Could not make XMLFrag into DOM " + e.toString());
			e.printStackTrace(System.err);
		}
	}
	
	public org.w3c.dom.Document getFragmentDOM(){
		return this.resultset;
	}
	
	public void startElement(String uri, String localName, 
		String qName, Attributes attributes) throws SAXException {
		
		//this is the begining of the table, try to add the new record
		if(qName.equals(tablemarker)){
			//pass up the table marker
			super.startElement(uri, localName, qName, attributes);
			
			//add the row marker
			org.w3c.dom.Node fnode = resultset.getFirstChild();
			
			super.startElement(uri, fnode.getNodeName(), 
				fnode.getNodeName(), attributes);
			
			//try to add in all the columns
			fnode = fnode.getFirstChild();
			while(fnode != null){
				super.startElement(uri, fnode.getNodeName(), 
					fnode.getNodeName(), attributes);
				if(fnode.getFirstChild() != null && fnode.getFirstChild().getNodeValue() != null)
					super.characters(fnode.getFirstChild().getNodeValue().toCharArray(), 
						0, 
						fnode.getFirstChild().getNodeValue().length()
					);
				super.endElement(uri, fnode.getNodeName(), fnode.getNodeName());
				//get the next column
				fnode = fnode.getNextSibling();
			}
			
			//close out the new row
			fnode = resultset.getFirstChild();
			super.endElement(uri, fnode.getNodeName(), fnode.getNodeName());
		}else{
			super.startElement(uri, localName, qName, attributes);
		}
		
	}
}
