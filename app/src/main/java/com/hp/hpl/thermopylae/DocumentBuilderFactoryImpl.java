package com.hp.hpl.thermopylae;

import javax.xml.parsers.*;


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
   @version  $Date: 2002/08/19 05:04:19 $  $Revision: 1.1.1.1 $
   @author Eamonn O'Brien-Strain
 * @stereotype factory
 */

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    public DocumentBuilder newDocumentBuilder()
        throws ParserConfigurationException
    {
        return new DocumentBuilderImpl();
    }

    public Object getAttribute(String parm1) throws IllegalArgumentException {
        throw new IllegalArgumentException("thermopylae parser does not support attributes");
    }

    @Override
    public void setFeature(String s, boolean b) throws ParserConfigurationException {

    }

    @Override
    public boolean getFeature(String s) throws ParserConfigurationException {
        return false;
    }

    public void setAttribute(String parm1, Object parm2)
        throws IllegalArgumentException
    {
        throw new IllegalArgumentException("thermopylae parser does not support attributes");
    }

}


// $Log: DocumentBuilderFactoryImpl.java,v $
// Revision 1.1.1.1  2002/08/19 05:04:19  eobrain
// import from HP Labs internal CVS
//
// Revision 1.3  2002/08/18 05:45:27  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.2  2002/02/01 21:59:43  eob
// Comment change only.
//
// Revision 1.1  2002/01/04 18:31:35  eob
// initial
