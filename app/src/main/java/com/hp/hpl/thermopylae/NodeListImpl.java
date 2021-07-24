package com.hp.hpl.thermopylae;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.*;

/**
 * Wrapper around a vector of ElementImpl.

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
   @version  $Date: 2002/08/19 05:04:14 $  $Revision: 1.1.1.1 $
   @author Eamonn O'Brien-Strain
 * @stereotype container
 */

public class NodeListImpl implements NodeList {

    NodeListImpl(DocumentImpl doc, Enumeration i) {
        list_ = new Vector();
        while(i.hasMoreElements()){
            com.hp.hpl.sparta.Node spartanNode
                = (com.hp.hpl.sparta.Node)i.nextElement();

            list_.addElement( doc.wrapper( spartanNode ) );
        }

    }

    NodeListImpl(DocumentImpl doc, Vector list) {
        this( doc, list.elements() );
    }

    public int getLength() {
        return list_.size();
    }

    public Node item(int i) {
        return i<list_.size()
            ? (Node)list_.elementAt(i)
            : null;
    }

    /** @associates ElementImpl */
    private /*final (JDK11 problems)*/ Vector list_;

}

// $Log: NodeListImpl.java,v $
// Revision 1.1.1.1  2002/08/19 05:04:14  eobrain
// import from HP Labs internal CVS
//
// Revision 1.8  2002/08/18 05:46:09  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.7  2002/06/21 00:34:13  eob
// Make work with old JDK 1.1.*
//
// Revision 1.6  2002/05/23 21:33:50  eob
// Add constructor that takes vector.  This optimization was done because
// of what performance profiling showed.
//
// Revision 1.5  2002/02/06 00:00:05  eob
// Handle case of getting non-existent node.
//
// Revision 1.4  2002/02/01 22:01:43  eob
// Comment change only.
//
// Revision 1.3  2002/01/05 08:06:55  eob
// tweak
//
// Revision 1.2  2002/01/04 00:52:35  eob
// Store wrapper as annotation of spartan object to avoid creating
// uncecessary wrapper objects, while still allowing garbage collection
// to clean the wrappers up.
//
// Revision 1.1  2002/01/04 18:42:51  eob
// initial
