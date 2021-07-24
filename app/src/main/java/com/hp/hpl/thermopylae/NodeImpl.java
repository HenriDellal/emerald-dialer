package com.hp.hpl.thermopylae;

import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;

/**
 * Standard wrapper around spartan Node.

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
 */

public abstract class NodeImpl implements Node {

    public org.w3c.dom.Document getOwnerDocument() {
        return doc_;
    }

    public boolean equals(Object obj){
        if( obj instanceof NodeImpl ){
            NodeImpl that = (NodeImpl)obj;
            return this.spartan_.equals( that.spartan_ );
        }else
            return false;
    }

    public org.w3c.dom.Node getNextSibling() {
        return spartan_.getNextSibling()==null
            ? null
            : doc_.wrapper( spartan_.getNextSibling() );
    }

    public org.w3c.dom.Node getPreviousSibling() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("getPreviousSibling not yet implemented");
    }

    //////////////////////////////////////////////////////////////
    // BEGIN DOM 3

    public String getBaseURI()
    {
        throw new Error("not implemented");
    }

    public short compareTreePosition(Node other){
        throw new Error("not implemented");
    }


    public String getTextContent()
        throws org.w3c.dom.DOMException
    {
        throw new Error("not implemented");
    }

    public void setTextContent(String textContent)
        throws org.w3c.dom.DOMException
    {
        throw new Error("not implemented");
    }

    public boolean isSameNode(Node other){
        throw new Error("not implemented");
    }


    public String lookupNamespacePrefix(String namespaceURI){
        throw new Error("not implemented");
    }

    public String lookupNamespaceURI(String prefix){
        throw new Error("not implemented");
    }

    public boolean isEqualNode(Node arg,
                               boolean deep){
        throw new Error("not implemented");
    }

    public Node getInterface(String feature){
        throw new Error("not implemented");
    }

    public Object setUserData(String key,
                              Object data,
                              UserDataHandler handler){
        throw new Error("not implemented");
    }

    public Object getUserData(String key){
        throw new Error("not implemented");
    }

    //END DOM 3
    //////////////////////////////////////////////////////////////////

    protected NodeImpl(
                       DocumentImpl doc,
                       com.hp.hpl.sparta.Node spartan
                       )
    {
        spartan_ = spartan;
        doc_ = doc;
    }

    com.hp.hpl.sparta.Node getSpartan(){
        return spartan_;
    }

    /**
     * @link aggregation
     * @label spartan
     */
    private final com.hp.hpl.sparta.Node spartan_;
    private final DocumentImpl doc_;

    /////////////////////////////////////////////////

    /*
      static NodeImpl wrapper(
      DocumentImpl doc,
      com.hp.hpl.sparta.Node spartan
      )
      {
      NodeImpl result = (NodeImpl)spartan.getAnnotation();
      if( result == null ){
      result = new NodeImpl(doc,spartan);
      spartan.setAnnotation(result);
      }
      return result;
      }*/
}

// $Log: NodeImpl.java,v $
// Revision 1.2  2002/11/06 02:59:55  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.1.1.1  2002/08/19 05:04:14  eobrain
// import from HP Labs internal CVS
//
// Revision 1.5  2002/08/18 05:46:04  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.4  2002/06/21 00:33:29  eob
// Make work with old JDK 1.1.*
//
// Revision 1.3  2002/02/08 20:33:43  eob
// Added extra DOM3 stuff.
//
// Revision 1.2  2002/02/01 22:01:30  eob
// Comment change only.
//
// Revision 1.1  2002/01/05 07:33:59  eob
// initial
