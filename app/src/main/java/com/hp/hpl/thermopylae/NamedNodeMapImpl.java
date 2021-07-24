package com.hp.hpl.thermopylae;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import java.util.*;

/**
 * Wrapper around set of {@link org.w3c.dom.Attr}s.

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
   @version  $Date: 2003/01/27 23:30:58 $  $Revision: 1.2 $
   @author Eamonn O'Brien-Strain
   @author Sergio Marti
 * add Observer pattern
 * @stereotype container
 */

public class NamedNodeMapImpl implements NamedNodeMap {

    NamedNodeMapImpl( ElementImpl wrapperElement ){
        wrapperElement_ = wrapperElement;
        com.hp.hpl.sparta.Element spartanElement
            = wrapperElement.getSpartanElement();
        Map orderHack = new TreeMap();
        for( Enumeration i = spartanElement.getAttributeNames();
             i.hasMoreElements();
             ){
            String name = (String)i.nextElement();
            String value = spartanElement.getAttribute(name);
            AttrImpl attr = new AttrImpl(wrapperElement,name,value);
            //vector_.add( attr );
            orderHack.put(name,attr);
            dict_.put( name, attr );
        }
        //This is a hack to get the same order as Xerces for easier
        //diff testing
        for(Iterator i=orderHack.keySet().iterator(); i.hasNext(); )
            vector_.addElement( orderHack.get(i.next()) );
    }

    public int getLength() {
        return dict_.size();
    }

    public Node getNamedItem(String name) {
        return (AttrImpl)dict_.get(name);
    }

    public Node getNamedItemNS(String parm1, String parm2) {
        /**@todo: Implement this org.w3c.dom.NamedNodeMap method*/
        throw new Error("not implemented: getNamedItemNS");
    }

    public Node item(int i) {
        return (AttrImpl)vector_.elementAt(i);
    }

    public Node removeNamedItem(String name) throws DOMException {
        wrapperElement_.removeAttribute(name);
        vector_.removeElement(dict_.get(name));
        return (Node)dict_.remove(name);
    }

    public Node removeNamedItemNS(String parm1, String parm2)
        throws DOMException
    {
        /**@todo: Implement this org.w3c.dom.NamedNodeMap method*/
        throw new Error("not implemented: removeNamedItemNS");
    }

    public Node setNamedItem(Node parm1) throws DOMException {
        /**@todo: Implement this org.w3c.dom.NamedNodeMap method*/
        throw new Error("not implemented: setNamedItem");
    }

    public Node setNamedItemNS(Node parm1) throws DOMException {
        /**@todo: Implement this org.w3c.dom.NamedNodeMap method*/
        throw new Error("not implemented: setNamedItemNS");
    }

    /** @associates AttrImpl */
    private final Map dict_ = new HashMap();
    private final Vector vector_   = new Vector();
    private final ElementImpl wrapperElement_;
}

// $Log: NamedNodeMapImpl.java,v $
// Revision 1.2  2003/01/27 23:30:58  yuhongx
// Replaced Hashtable with HashMap.
//
// Revision 1.1.1.1  2002/08/19 05:04:16  eobrain
// import from HP Labs internal CVS
//
// Revision 1.8  2002/08/19 00:39:07  eob
// Tweak javadoc comment.
//
// Revision 1.7  2002/08/18 05:45:54  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.6  2002/06/21 00:33:06  eob
// Make work with old JDK 1.1.*
//
// Revision 1.5  2002/03/26 05:23:21  eob
// Comment change only
//
// Revision 1.4  2002/02/01 22:01:16  eob
// Comment change only.
//
// Revision 1.3  2002/01/05 21:31:30  eob
// Add hack to get same order as Xerces.
//
// Revision 1.2  2002/01/05 08:06:19  eob
// Implement remove
//
// Revision 1.1  2002/01/04 19:44:37  eob
// initial
