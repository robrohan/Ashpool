#!/bin/sh

ASHPOOL_CLASSPATH=bsf.jar:saxon8.jar:xercesImpl.jar:xml-apis.jar:jruby.jar:Ashpool.jar
ASHPOOL_SAXDRIVER=org.apache.xerces.parsers.SAXParser

# Used to run the built in command line application
java -Dorg.xml.sax.driver=$ASHPOOL_SAXDRIVER -cp $ASHPOOL_CLASSPATH com.rohanclan.ashpool.Ashpool $@

# Used to run the JDBC test application
#java -Dorg.xml.sax.driver=$ASHPOOL_SAXDRIVER -cp $ASHPOOL_CLASSPATH com.rohanclan.ashpool.JdbcTest $@
