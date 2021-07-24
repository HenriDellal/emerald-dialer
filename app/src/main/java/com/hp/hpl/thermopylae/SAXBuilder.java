/*
 * Created on 01-Jun-2003 by eob
 */
package com.hp.hpl.thermopylae;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.Text;

/**
 * Use to build a Sparta DOM from a SAX parser.
 * Inspired by <a href="http://www.jdom.org/docs/apidocs/org/jdom/input/SAXBuilder.html">
 *  class in JDOM</a>.
 * 
   <blockquote><small> Copyright (C) 2003 Hewlett-Packard Company.
   This file is part of Sparta, an XML Parser, DOM, and XPath library.
   This library is free software; you can redistribute it and/or
   modify it under the terms of the <a href="doc-files/LGPL.txt">GNU
   Lesser General Public License</a> as published by the Free Software
   Foundation; either version 2.1 of the License, or (at your option)
   any later version.  This library is distributed in the hope that it
   will be useful, but WITHOUT ANY WARRANTY; without even the implied
   warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
   PURPOSE. </small></blockquote>

 * @version $Revision: 1.2 $
 * @author eob
 */
public class SAXBuilder extends DefaultHandler {

    public Document getParsedDocument() {
        return doc_;
    }

    /* 
     * @see org.xml.sax.DocumentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) {
        if (text_ == null) {
            text_ = new Text("");
            parent_.appendChild(text_);
        }
        text_.appendData(ch, start, length);
    }

    /* 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName) {
        parent_ = parent_.getParentNode();
    }

    /* 
     * @see org.xml.sax.DocumentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] ch, int start, int length) {
        characters(ch, start, length);

    }

    /** 
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(
        String uri,
        String localName,
        String qName,
        Attributes atts) {

        text_ = null;
        Element elem = new Element(qName);
        int n = atts.getLength();
        for (int i = 0; i < n; ++i)
            elem.setAttribute(atts.getQName(i), atts.getValue(i));
        if (parent_ == null)
            doc_.setDocumentElement(elem);
        else
            parent_.appendChild(elem);
        parent_ = elem;
    }

    private final Document doc_ = new Document();
    private Element parent_ = null;
    private Text text_ = null;

}

// $Log: SAXBuilder.java,v $
// Revision 1.2  2003/06/26 03:27:18  eobrain
// Add missing copyright notice.
//
// Revision 1.1  2003/06/19 20:20:04  eobrain
// Use to build a Sparta DOM from a SAX parser.
//