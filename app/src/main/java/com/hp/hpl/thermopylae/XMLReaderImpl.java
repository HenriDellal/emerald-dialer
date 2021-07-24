package com.hp.hpl.thermopylae;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.sparta.*;
// import org.xml.sax.helpers.NamespaceSupport;


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
   @version  $Date: 2003/11/01 05:44:07 $  $Revision: 1.5 $
   @author Sergio Marti
*/

public class XMLReaderImpl implements XMLReader, ParseHandler {

    private ParseSource     parseSource_      = null;

    private ContentHandler  contentHandler_   = null;
    private DTDHandler      dtdHandler_       = null;
    private EntityResolver  entityResolver_   = null;
    private ErrorHandler    errorHandler_     = null;


    /* Prototype namespace support */
    private final boolean nsAware_;
    private NamespaceSupport nsSupport_ = null;
    private String[] nameInfo_ = new String[3];


    XMLReaderImpl(boolean nsAware) {
        nsAware_ = nsAware;
        if (nsAware) {
            nsSupport_ = new NamespaceSupport();
        }
    }

    void init(DefaultHandler dh) {
        setContentHandler(dh);
        setDTDHandler(dh);
        setEntityResolver(dh);
        setErrorHandler(dh);
    }

    // XMLReader methods

    // Return the current content handler.
    public ContentHandler getContentHandler() {
        return contentHandler_;
    }


    // Return the current DTD handle.
    public DTDHandler getDTDHandler() {
        return dtdHandler_;
    }


    // Return the current entity resolver.
    public EntityResolver getEntityResolver() {
        return entityResolver_;
    }


    // Return the current error handler.
    public ErrorHandler getErrorHandler() {
        return errorHandler_;
    }



    // Look up the value of a feature.
    public boolean getFeature(java.lang.String name) {
        return false;
    }


    // Look up the value of a property.
    public java.lang.Object getProperty(java.lang.String name) {
        return null;
    }

    // Parse an XML document.
    public void parse(InputSource input)
        throws SAXException, IOException {
        String sysID = "file:unknown";
        try {
            String uri = input.getSystemId();
            if (uri != null)
                sysID = uri;

            Reader reader = input.getCharacterStream();
            InputStream stream = input.getByteStream();
            String encoding = input.getEncoding();
            if (reader != null) {
                com.hp.hpl.sparta.Parser.parse(sysID, reader, null, encoding, this);
            }
            else if (stream != null) {
                com.hp.hpl.sparta.Parser.parse(sysID, stream, null, encoding, this);
            }
            else if (uri != null) {
                URL url = new URL(uri);
                InputStream istream = url.openStream();
                if( istream == null ){
                    String msg = "Cannot open "+uri;
                    if (errorHandler_ != null)
                       errorHandler_.fatalError(new SAXParseException(msg,"",sysID,-1,-1));
                    throw new IOException(msg);
                }
                com.hp.hpl.sparta.Parser.parse(uri, istream, null, encoding, this);
            }
            else {
                if (errorHandler_ != null)
                    errorHandler_.fatalError(new SAXParseException("Nothing in InputSource", "", sysID, 0, 0));
            }
        } catch (EncodingMismatchException eme) {
            if (errorHandler_ != null)
                errorHandler_.fatalError(new SAXParseException(eme.getMessage(), "", sysID, -1, -1, eme));
        } catch (ParseException pe) {
            if (errorHandler_ != null)
                errorHandler_.fatalError(new SAXParseException(pe.getMessage(), "", sysID, -1, -1, pe));
        }
    }

    // Parse an XML document from a system identifier (URI).
    public void parse(java.lang.String systemId)
        throws SAXException, IOException {
        parse(new InputSource(systemId));
    }


    // Allow an application to register a content event handler.
    public void setContentHandler(ContentHandler handler) {
        contentHandler_ = handler;
        if (contentHandler_ != null)
            contentHandler_.setDocumentLocator(new LocatorImpl());
    }

    // Allow an application to register a DTD event handler.
    public void setDTDHandler(DTDHandler handler) {
        dtdHandler_ = handler;
    }

    // Allow an application to register an entity resolver.
    public void setEntityResolver(EntityResolver resolver) {
        entityResolver_ = resolver;
    }

    // Allow an application to register an error event handler.
    public void setErrorHandler(ErrorHandler handler) {
        errorHandler_ = handler;
    }

    // Set the state of a feature.
    public void setFeature(java.lang.String name, boolean value) { }

    // Set the value of a property.
    public void setProperty(java.lang.String name, java.lang.Object value) { }



    // ParseHandler methods

    public void setParseSource(ParseSource ps) {
        parseSource_ = ps;
    }

    public ParseSource getParseSource() {
        return parseSource_;
    }

    public void startDocument()
        throws ParseException {

        if (contentHandler_ != null) {
            try {
                contentHandler_.startDocument();
            } catch (SAXException se) {
                throw new ParseException("Exception in startDocument", se);
            }
        }
    }

    public void endDocument()
        throws ParseException {
        if (contentHandler_ != null) {
            try {
                contentHandler_.endDocument();
            } catch (SAXException se) {
                throw new ParseException("Exception in endDocument", se);
            }
        }
    }

    public void startElement(Element element)
        throws ParseException {

        if (nsAware_) {
            nsSupport_.pushContext();
        }

        if (contentHandler_ != null) {

            nameInfo_[0] = "";
            nameInfo_[1] = "";

            String[] res;

            String aName, aValue;
            AttributesImpl attrs = new AttributesImpl();
            Enumeration e = element.getAttributeNames();
            while (e.hasMoreElements()) {
                aName = (String)e.nextElement();
                aValue = element.getAttribute(aName);
                if (nsAware_) {
                    if (aName.startsWith("xmlns:")) {
                        String prefix = aName.substring(6);
                        nsSupport_.declarePrefix(prefix, aValue);
                        try {
                            contentHandler_.startPrefixMapping(prefix, aValue);
                        } catch (SAXException se) {
                            throw new ParseException("Exception in startPrefixMapping", se);
                        }
                        continue;
                    }

                    res = nsSupport_.processName(aName, nameInfo_, true);
                    if (res == null)
                        throw new ParseException("Unknown prefix: " + aName);
                }

                /*
                                                System.out.println("Attr: namespace: " + nameInfo_[0] +
                                                ". lName: " + nameInfo_[1] + ". fullName: "
                                                + nameInfo_[2] + ". value: " + aValue);
                */

                attrs.addAttribute(nameInfo_[0], nameInfo_[1], aName, "",
                                   aValue);
            }

            nameInfo_[0] = "";
            nameInfo_[1] = "";

            String fullName = element.getTagName();
            if (nsAware_) {
                res = nsSupport_.processName(fullName, nameInfo_, false);
                if (res == null)
                    throw new ParseException("Unknown prefix: " +
                                             fullName);
            }

            /*
                    System.out.println("Start Elem: namespace: " + nameInfo_[0] +
                    ". localName: " + nameInfo_[1] +
                    ". fullName: " + fullName);
            */

            try {
                contentHandler_.startElement(nameInfo_[0], nameInfo_[1],
                                             fullName, attrs);
            } catch (SAXException se) {
                throw new ParseException("Exception in startElement", se);
            }
        }
    }

    public void endElement(Element element)
        throws ParseException {
        if (contentHandler_ != null) {
            nameInfo_[0] = "";
            nameInfo_[1] = "";

            String fullName = element.getTagName();
            if (nsAware_) {
                /*String[] res =*/ nsSupport_.processName(fullName, nameInfo_,
                                                      false);
            }

            /*
                    System.out.println("End Elem: namespace: " + nameInfo_[0] +
                    ". localName: " + nameInfo_[1] +
                    ". fullName: " + fullName);
            */

            try {
                contentHandler_.endElement(nameInfo_[0], nameInfo_[1],
                                           fullName);
            } catch (SAXException se) {
                throw new ParseException("Exception in endElement", se);
            }
        }
        if (nsAware_) {
            Iterator prefixSet = nsSupport_.popContext();
            if (contentHandler_ != null) {
                try {
                    while (prefixSet.hasNext())
                        contentHandler_.endPrefixMapping((String)prefixSet.next());
                } catch (SAXException se) {
                    throw new ParseException("Exception in endElement", se);
                }
            }
        }
    }

    public void characters(char[] buf, int offset, int len)
        throws ParseException {
        if (contentHandler_ != null) {
            boolean whitespace = true;
            for (int i = 0; i < len; i++) {
                if (buf[i] > ' ')
                    whitespace = false;
            }
            if (whitespace) {
                try {
                    contentHandler_.ignorableWhitespace(buf, offset, len);
                } catch (SAXException se) {
                    throw new ParseException("Exception in characters", se);
                }
            }
            else {
                try {
                    contentHandler_.characters(buf, offset, len);
                } catch (SAXException se) {
                    throw new ParseException("Exception in characters", se);
                }
            }
        }
    }

    int getLineNumber() {
        if (parseSource_ != null)
            return parseSource_.getLineNumber();
        else
            return -1;
    }

    String getSystemId() {
        if(parseSource_ != null)
            return parseSource_.getSystemId();
        else
            return null;
    }


    class LocatorImpl implements Locator {

        public int getColumnNumber() {
            return -1;
        }

        public int getLineNumber() {
            return XMLReaderImpl.this.getLineNumber();
        }

        public String getPublicId() {
            return null;
        }

        public String getSystemId() {
            return XMLReaderImpl.this.getSystemId();
        }
    }
}

// $Log: XMLReaderImpl.java,v $
// Revision 1.5  2003/11/01 05:44:07  eobrain
// Avoid creating synthetic accessors.
//
// Revision 1.4  2003/01/27 23:30:59  yuhongx
// Replaced Hashtable with HashMap.
//
// Revision 1.3  2002/11/06 02:59:55  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.2  2002/09/18 05:31:07  eobrain
// Improved error handling when cannot open URI.
//
// Revision 1.1.1.1  2002/08/19 05:04:13  eobrain
// import from HP Labs internal CVS
//
// Revision 1.9  2002/08/19 00:41:23  eob
// Tweak javadoc comment -- add period (full stop) so that Javadoc knows
// where is end of summary.
//
// Revision 1.8  2002/08/18 05:46:30  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.7  2002/08/17 00:54:14  sermarti
//
// Revision 1.6  2002/08/17 00:38:24  sermarti
//
// Revision 1.5  2002/08/15 23:40:23  sermarti
//
// Revision 1.4  2002/08/09 22:36:49  sermarti
//
// Revision 1.3  2002/08/05 20:04:32  sermarti
//
// Revision 1.2  2002/08/01 23:29:17  sermarti
// Much faster Sparta parsing.
// Has debug features enabled by default. Currently toggled
// in ParseCharStream.java and recompiled.
//
// Revision 1.1  2002/07/24 23:55:43  sermarti
// SAX parser wrapper for Sparta that is JAXP compliant.
