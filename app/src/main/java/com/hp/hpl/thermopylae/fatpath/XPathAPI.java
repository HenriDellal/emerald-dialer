package com.hp.hpl.thermopylae.fatpath;

import java.io.IOException;
import java.util.*;
import org.w3c.dom.*;
import com.hp.hpl.sparta.xpath.*;

/**
 * Facade class providing a convenient API to the XPath parser and
 * evaluator.  For efficiency, parsed XPath expressions are cached.
 * For ease of migration from Xalan XPath this class has the same name
 * and has the same method names as the Xalan XPathAPI class.  See <a
 * href="http://home.earthlink.net/~huston2/dp/facade.html">Facade
 * Pattern</a>

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
   @version  $Date: 2002/12/13 23:49:24 $  $Revision: 1.2 $
   @author Eamonn O'Brien-Strain
 * @see org.apache.xpath.XPathAPI
*/

public class XPathAPI {

    /** @return all the elements that match the relative XPath
        expression with respect to the context element. */
    static public Enumeration selectElementIterator(Element element,
                                                    String xpathString)
        throws XPathException, IOException
    {
        XPath xpath = XPath.get(xpathString);
        if( xpath.isStringValue() )
            throw new XPathException(xpath,"\""+xpathString
                                     +"\" evaluates to string not element");
        XPathVisitor visitor = new XPathVisitor( element, xpath );
        return visitor.getResult();
    }

    /** @return all the strings that match the relative XPath
        expression with respect to the context element. */
    static public Enumeration selectStringIterator(Element element,
                                                   String xpathString)
        throws XPathException, IOException
    {
        XPath xpath = XPath.get(xpathString);
        if( !xpath.isStringValue() )
            throw new XPathException(xpath,"\""+xpathString
                                     +"\" evaluates to element not string");
        XPathVisitor visitor = new XPathVisitor( element, xpath );
        return visitor.getResult();
    }

    /** @return all the elements in the document that match the
        absolute XPath expression. */
    static public Enumeration selectElementIterator(Document doc,
                                                    String xpathString)
        throws XPathException, IOException
    {
        if( xpathString.charAt(0) != '/' )
            xpathString = "/"+xpathString;
        XPath xpath = XPath.get(xpathString);
        if( xpath.isStringValue() )
            throw new XPathException(xpath,"\""+xpathString
                                     +"\" evaluates to string not element");
        XPathVisitor visitor = new XPathVisitor( doc, xpath );
        return visitor.getResult();
    }

    /** @return all the strings in the document that match the
        absolute XPath expression. */
    static public Enumeration selectStringIterator(Document doc,
                                                   String xpathString)
        throws XPathException, IOException
    {
        if( xpathString.charAt(0) != '/' )
            xpathString = "/"+xpathString;
        XPath xpath = XPath.get(xpathString);
        if( !xpath.isStringValue() )
            throw new XPathException(xpath,"\""+xpathString
                                     +"\" evaluates to element not string");
        XPathVisitor visitor = new XPathVisitor( doc, xpath );
        return visitor.getResult();
    }

    /** @return the first element that matches the relative XPath
        expression with respect to the context element, or null if
        there is no match.

        @todo make more efficient by short-circuiting the search.  */
    static public Element selectSingleElement(Element element,
                                              String xpathString)
        throws XPathException, IOException
    {
        Enumeration iter = selectElementIterator(element,xpathString);
        if( iter.hasMoreElements() )
            return (Element)iter.nextElement();
        else
            return null;
    }

    /** @return the first element in this document that matches the
        absolute XPath expression, or null if there is no match.

        * @todo make more efficient by short-circuiting the search.  */
    static public Element selectSingleElement(Document doc, String xpathString)
        throws XPathException, IOException
    {
        if( xpathString.charAt(0) != '/' )
            xpathString = "/"+xpathString;
        Enumeration iter = selectElementIterator(doc,xpathString);
        if( iter.hasMoreElements() )
            return (Element)iter.nextElement();
        else
            return null;
    }

    /** @return the first element that matches the relative XPath
        expression with respect to the context element, or null if
        there is no match.

        @todo make more efficient by short-circuiting the search.  */
    static public String selectSingleString(Element element,
                                            String xpathString)
        throws XPathException, IOException
    {
        Enumeration iter = selectStringIterator(element,xpathString);
        if( iter.hasMoreElements() )
            return (String)iter.nextElement();
        else
            return null;
    }


    /** @return the first string in this document that matches the
        absolute XPath expression, or null if there is no match.

        * @todo make more efficient by short-circuiting the search.  */
    static public String selectSingleString(Document doc, String xpathString)
        throws XPathException, IOException
    {
        if( xpathString.charAt(0) != '/' )
            xpathString = "/"+xpathString;
        Enumeration iter = selectStringIterator(doc,xpathString);
        if( iter.hasMoreElements() )
            return (String)iter.nextElement();
        else
            return null;
    }

}

// $Log: XPathAPI.java,v $
// Revision 1.2  2002/12/13 23:49:24  eobrain
// Fix javadoc.
//
// Revision 1.1.1.1  2002/08/19 05:04:23  eobrain
// import from HP Labs internal CVS
//
// Revision 1.6  2002/08/19 00:19:14  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.5  2002/08/18 04:27:22  eob
// Remove deprecated method.
//
// Revision 1.4  2002/05/23 21:08:23  eob
// Better error reporting.
//
// Revision 1.3  2002/03/25 22:50:49  eob
// Move XPath object caching to XPath class.
//
// Revision 1.2  2002/02/04 22:05:48  eob
// Add handling of attribute xpath expressions that return strings.
//
// Revision 1.1  2002/02/01 19:20:40  eob
// initial
