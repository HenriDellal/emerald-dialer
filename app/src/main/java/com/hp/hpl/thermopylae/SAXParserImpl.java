package com.hp.hpl.thermopylae;

import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.SAXParser;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.sparta.ParseException;

/** JAXP compatible XMLReader wrapper for Sparta.

   <blockquote><small> Copyright (C) 2002 Hewlett-Packard Company.
   This file is part of Sparta, an XML Parser, DOM, and XPath library.
   This library is free software; you can redistribute it and/or
   modify it under the terms of the <a href="doc-files/LGPL.txt">GNU
   Lesser General Public License</a> as published by the Free Software
   Foundation; either version 2.1 of the License, or (at your option)
   any later version.  This library is distributed in the hope that it
   will be useful, but WITHOUT ANY WARRANTY; without even the implied
   warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
   PURPOSE. </small></blockquote>
   @version  $Date: 2003/07/18 00:03:50 $  $Revision: 1.5 $
   @author Sergio Marti
*/

public class SAXParserImpl extends SAXParser {

    private final XMLReaderImpl reader_;
    private boolean nsAware_ = false;

    SAXParserImpl(boolean nsAware) {
        super();
        nsAware_ = nsAware;
        reader_ = new XMLReaderImpl(nsAware);
    }

    /** Returns the SAX parser that is encapsultated by the implementation 
     of this class.
     @deprecated because org.xml.sax.Parser is deprecated
     */
    public org.xml.sax.Parser getParser() {
        return new org.xml.sax.helpers.XMLReaderAdapter(reader_);
    }

    // Returns the XMLReader that is encapsulated by the implementation of 
    // this class.          
    public XMLReader getXMLReader() {
        return reader_;
    }

    // Returns the particular property requested for in the underlying 
    // implementation of XMLReader.
    public java.lang.Object getProperty(java.lang.String name) {
        return reader_.getProperty(name);
    }

    // Sets the particular property in the underlying implementation of 
    // XMLReader.    
    public void setProperty(java.lang.String name, java.lang.Object value) {
        reader_.setProperty(name, value);
    }

    // Indicates whether or not this parser is configured to understand 
    // namespaces.
    public boolean isNamespaceAware() {
        return nsAware_;
    }

    // Indicates whether or not this parser is configured to validate XML 
    // documents.
    public boolean isValidating() {
        return false;
    }

    // Parse the content of the file specified as XML using the specified 
    // DefaultHandler.
    public void parse(java.io.File f, DefaultHandler dh)
        throws SAXException, IOException {
        reader_.init(dh);
        try {
            com.hp.hpl.sparta.Parser.parse(
                f.toURL().toString(),
                new FileReader(f),
                reader_);
        } catch (ParseException pe) {
            dh.fatalError(
                new SAXParseException(
                    pe.getMessage(),
                    "",
                    f.toString(),
                    -1,
                    -1,
                    pe));
        }
    }

    // Parse the content given InputSource as XML using the specified 
    // DefaultHandler.    
    public void parse(InputSource is, DefaultHandler dh)
        throws SAXException, IOException {
        reader_.init(dh);
        reader_.parse(is);
    }

    // Parse the content of the given InputStream instance as XML using 
    // the specified DefaultHandler.
    public void parse(java.io.InputStream is, DefaultHandler dh)
        throws SAXException, IOException {
        parse(new InputSource(is), dh);
    }

    // Parse the content of the given InputStream instance as XML using 
    // the specified DefaultHandler.
    public void parse(
        java.io.InputStream is,
        DefaultHandler dh,
        java.lang.String systemId)
        throws SAXException, IOException {
        reader_.init(dh);
        try {
            com.hp.hpl.sparta.Parser.parse(systemId, is, reader_);
        } catch (ParseException pe) {
            dh.fatalError(
                new SAXParseException(
                    pe.getMessage(),
                    "",
                    systemId,
                    -1,
                    -1,
                    pe));
        }
    }

    // Parse the content described by the giving Uniform Resource Identifier 
    // (URI) as XML using the specified DefaultHandler.
    public void parse(java.lang.String uri, DefaultHandler dh)
        throws SAXException, IOException {
        reader_.init(dh);
        reader_.parse(uri);
    }

    /* Old HandlerBase parse methods
       // Parse the content of the file specified as XML using the specified 
       // HandlerBase.
       public void parse(java.io.File f, HandlerBase hb);
    
       // Parse the content given InputSource as XML using the specified 
       // HandlerBase.    
       public void parse(InputSource is, HandlerBase hb);
    
       // Parse the content of the given InputStream instance as XML using 
       // the specifiedHandlerBase.
       public void parse(java.io.InputStream is, HandlerBase hb);
    
       // Parse the content of the given InputStream instance as XML using 
       // the specified HandlerBase.
       public void parse(java.io.InputStream is, HandlerBase hb,
       java.lang.String systemId);
    
       // Parse the content described by the giving Uniform Resource Identifier 
       // (URI) as XML using the specified HandlerBase.
       public void parse(java.lang.String uri, HandlerBase hb);
    
    */

}

// $Log: SAXParserImpl.java,v $
// Revision 1.5  2003/07/18 00:03:50  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.4  2003/06/19 20:17:46  eobrain
// Avoid deprecation warnings.
//
// Revision 1.3  2003/05/12 20:10:11  eobrain
// Organize imports.
//
// Revision 1.2  2002/12/05 04:37:14  eobrain
// Deprecate getParser.
//
// Revision 1.1.1.1  2002/08/19 05:04:13  eobrain
// import from HP Labs internal CVS
//
// Revision 1.7  2002/08/19 00:40:57  eob
// Tweak javadoc comment -- add period (full stop) so that Javadoc knows
// where is end of summary.
//
// Revision 1.6  2002/08/18 05:46:20  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.5  2002/08/17 00:54:14  sermarti
//
// Revision 1.4  2002/08/15 23:40:23  sermarti
//
// Revision 1.3  2002/08/09 22:36:49  sermarti
//
// Revision 1.2  2002/08/05 20:04:32  sermarti
//
// Revision 1.1  2002/07/24 23:55:43  sermarti
// SAX parser wrapper for Sparta that is JAXP compliant.
