package com.hp.hpl.thermopylae;

import org.w3c.dom.*;

/**
 * Standard wrapper around sparta stuff.

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
 */

public class DOMImplementationImpl implements DOMImplementation {

    public DOMImplementationImpl() {
    }

    public Document createDocument(String a, String b, DocumentType c)
        throws DOMException
    {
        /**@todo: Implement this org.w3c.dom.DOMImplementation method*/
        throw new Error("Method createDocument() not yet implemented.");
    }

    @Override
    public Object getFeature(String s, String s1) {
        return null;
    }

    public DocumentType createDocumentType(String a, String b, String c)
        throws DOMException
    {
        /**@todo: Implement this org.w3c.dom.DOMImplementation method*/
        throw new Error("Method createDocumentType() not yet implemented.");
    }

    public boolean hasFeature(String parm1, String parm2) {
        /**@todo: Implement this org.w3c.dom.DOMImplementation method*/
        throw new Error("Method hasFeature() not yet implemented.");
    }

    ///////////////////////////////////////////
    // BEGIN DOM3


    public DOMImplementation getInterface(String feature){
        /**@todo: Implement this org.w3c.dom.DOMImplementation method*/
        throw new Error("Method hasFeature() not yet implemented.");
    }


    // END DOM 3
    ////////////////////////////////////////////
}

// $Log: DOMImplementationImpl.java,v $
// Revision 1.1.1.1  2002/08/19 05:04:19  eobrain
// import from HP Labs internal CVS
//
// Revision 1.4  2002/08/18 05:45:23  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.3  2002/06/21 00:31:19  eob
// Make work with old JDK 1.1.*
//
// Revision 1.2  2002/02/08 20:32:53  eob
// Added extra DOM3 stuff.
//
// Revision 1.1  2002/01/04 18:35:05  eob
// initial
