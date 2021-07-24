package com.hp.hpl.thermopylae;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.DOMImplementation;
import org.xml.sax.*;

import com.hp.hpl.sparta.*;
import com.hp.hpl.sparta.Parser;


/**
 * Standard wrapper around sparta parser.

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
   @version  $Date: 2002/11/06 02:59:55 $  $Revision: 1.2 $
   @author Eamonn O'Brien-Strain
 * @see com.hp.hpl.sparta.ParseSource
 * @stereotype factory
 */

public class DocumentBuilderImpl extends DocumentBuilder {

    public DocumentBuilderImpl() {
    }

    public boolean isNamespaceAware() {
        return false;
    }

    public DOMImplementation getDOMImplementation() {
        return new DOMImplementationImpl();
    }

    public org.w3c.dom.Document parse(InputSource in)
        throws SAXException, IOException
    {
        try{
            String sysId = in.getSystemId();
	    if (sysId == null)
                sysId = "file://unknown";
            String encoding = in.getEncoding();
            com.hp.hpl.sparta.Document doc;
            if( in.getCharacterStream() != null )
                doc = Parser.parse(sysId,
				   in.getCharacterStream(),
				   log_,
				   encoding );
            else if( in.getByteStream() != null )
                doc = Parser.parse(sysId,
				   in.getByteStream(),
				   log_,
				   encoding );
            else if( in.getSystemId() != null ){
                InputStream istream = new URL(sysId).openStream();
                doc = Parser.parse(sysId, istream, log_ );
            }else
                throw new IOException("nothing in InputSource");
            return DocumentImpl.wrapper( doc );
        }catch(ParseException e){
            throw new org.xml.sax.SAXException(e);
        }
    }

    public void setEntityResolver(EntityResolver parm1) {
        /**@todo: implement this javax.xml.parsers.DocumentBuilder abstract method*/
    }

    public boolean isValidating() {
        return false;
    }

    public org.w3c.dom.Document newDocument() {
        return DocumentImpl.wrapper( new com.hp.hpl.sparta.Document() );
    }

    /** Precondition handler.error() should not throw an exception. */
    public void setErrorHandler(ErrorHandler handler) {
        log_ = new LogWrapper(handler);
    }

    private ParseLog log_ = null;

}

// $Log: DocumentBuilderImpl.java,v $
// Revision 1.2  2002/11/06 02:59:55  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.1.1.1  2002/08/19 05:04:18  eobrain
// import from HP Labs internal CVS
//
// Revision 1.8  2002/08/19 00:38:00  eob
// Tweak javadoc comment.
//
// Revision 1.7  2002/08/18 05:45:31  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.6  2002/08/15 23:40:23  sermarti
//
// Revision 1.5  2002/05/09 20:59:01  eob
// Replace use of deprecated stuff with Parser.parse
//
// Revision 1.4  2002/02/01 22:00:00  eob
// Comment change only.
//
// Revision 1.3  2002/01/08 19:58:59  eob
// Handle case when systemId is null.
//
// Revision 1.2  2002/01/04 00:46:11  eob
// Implement setErrorHandler
//
// Revision 1.1  2002/01/04 18:38:41  eob
// initial
