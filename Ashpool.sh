#!/bin/sh

LIB_DIR=lib
ASHPOOL_CLASSPATH=Ashpool.jar:$LIB_DIR/bsf.jar:$LIB_DIR/saxon8.jar:$LIB_DIR/xercesImpl.jar:$LIB_DIR/xml-apis.jar:$LIB_DIR/jruby.jar:$LIB_DIR/js.jar
ASHPOOL_SAXDRIVER=org.apache.xerces.parsers.SAXParser

# Used to run the built in command line application
java -Dorg.xml.sax.driver=$ASHPOOL_SAXDRIVER -cp $ASHPOOL_CLASSPATH com.rohanclan.ashpool.Ashpool $@

# Used to run the JDBC test application
#java -Dorg.xml.sax.driver=$ASHPOOL_SAXDRIVER -cp $ASHPOOL_CLASSPATH com.rohanclan.ashpool.JdbcTest $@
