package com.hp.hpl.thermopylae;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.TypeInfo;

import java.util.*;

/**
 * Wrapper around a {@link com.hp.hpl.sparta.Element sparta Element}.

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
   @version  $Date: 2002/10/30 16:41:39 $  $Revision: 1.2 $
   @author Eamonn O'Brien-Strain
   @author Sergio Marti
 * @todo make these DOM objects be part of Observer pattern
 * @stereotype container
 */

public class ElementImpl extends NodeImpl implements org.w3c.dom.Element {

    public String getAttribute(String name) {
        return getSpartanElement().getAttribute(name);
    }

    public String getAttributeNS(String parm1, String parm2) {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: getAttributeNS");
    }

    public Attr getAttributeNode(String name) {
        String value = getAttribute(name);
        return (value==null) ? null : new AttrImpl( this, name, value);
    }

    public Attr getAttributeNodeNS(String parm1, String parm2) {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: getAttributeNodeNS");
    }

    public NodeList getElementsByTagName(String tagName) {
        Enumeration elements;
        try{
            elements = getSpartanElement().xpathSelectElements(".//"+tagName);
        }catch(Exception e){
            e.printStackTrace();
            throw new Error("assertion violated: "+e);
        }
        return new NodeListImpl(
                                (DocumentImpl)getOwnerDocument(),
                                elements
                                );
    }

    public NodeList getElementsByTagNameNS(String parm1, String parm2) {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: getElementsByTagNameNS");
    }

    public String getTagName() {
        return getSpartanElement().getTagName();
    }

    public boolean hasAttribute(String parm1) {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: hasAttribute");
    }

    public boolean hasAttributeNS(String parm1, String parm2) {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: hasAttributeNS");
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    @Override
    public void setIdAttribute(String s, boolean b) throws DOMException {

    }

    @Override
    public void setIdAttributeNS(String s, String s1, boolean b) throws DOMException {

    }

    @Override
    public void setIdAttributeNode(Attr attr, boolean b) throws DOMException {

    }

    public void removeAttribute(String name) throws org.w3c.dom.DOMException {
        getSpartanElement().removeAttribute(name);
    }

    public void removeAttributeNS(String parm1, String parm2)
        throws org.w3c.dom.DOMException
    {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: removeAttributeNS");
    }

    public Attr removeAttributeNode(Attr parm1)
        throws org.w3c.dom.DOMException
    {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: removeAttributeNode");
    }

    public void setAttribute(String name, String value)
        throws org.w3c.dom.DOMException
    {
        getSpartanElement().setAttribute(name,value);
    }

    public void setAttributeNS(String parm1, String parm2, String parm3)
        throws org.w3c.dom.DOMException
    {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: setAttributeNS");
    }

    public Attr setAttributeNode(Attr parm1) throws org.w3c.dom.DOMException {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: setAttributeNode");
    }

    public Attr setAttributeNodeNS(Attr attr) throws org.w3c.dom.DOMException {
        /**@todo: Implement this org.w3c.dom.Element method*/
        throw new Error("not implemented: setAttributeNodeNS");
    }

    public org.w3c.dom.Node appendChild(org.w3c.dom.Node node)
        throws org.w3c.dom.DOMException
    {

        if( node instanceof NodeImpl ){
            NodeImpl nodeImpl = (NodeImpl)node;
            getSpartanElement().appendChild( nodeImpl.getSpartan() );
            return node;
        }else
            throw new org.w3c.dom.DOMException(
                                               org.w3c.dom.DOMException.HIERARCHY_REQUEST_ERR,
                                               "can only add root element to document"
                                               );

    }

    /** Return clone of node. */
    public org.w3c.dom.Node cloneNode(boolean deep) {
        com.hp.hpl.sparta.Node spartanClone
            = (com.hp.hpl.sparta.Node)getSpartanElement().clone();;
        return ((DocumentImpl)getOwnerDocument()).wrapper( spartanClone );
    }

    public NamedNodeMap getAttributes() {
        return new NamedNodeMapImpl( this );
    }

    public NodeList getChildNodes() {
        Vector list = new Vector();
        for(com.hp.hpl.sparta.Node c=getSpartanElement().getFirstChild();
            c!=null;
            c=c.getNextSibling()
            )
            list.addElement(c);
        return new NodeListImpl(
                                (DocumentImpl)getOwnerDocument(),
                                list
                                );
    }

    public org.w3c.dom.Node getFirstChild() {
        com.hp.hpl.sparta.Node firstChild
            = getSpartanElement().getFirstChild();
        return (firstChild==null )
            ? null
            : ((DocumentImpl)getOwnerDocument()).wrapper( firstChild );
    }

    public org.w3c.dom.Node getLastChild() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getLastChild");
    }

    public String getLocalName() {
        return null;
    }

    public String getNamespaceURI() {
        return null;
    }

    public String getNodeName() {
        return getSpartanElement().getTagName();
    }

    public short getNodeType() {
        return org.w3c.dom.Node.ELEMENT_NODE;
    }

    public String getNodeValue() throws org.w3c.dom.DOMException {
        return null;
    }

    public org.w3c.dom.Node getParentNode() {
        if( getSpartan().getParentNode()==null )
            return getOwnerDocument();
        else{
            com.hp.hpl.sparta.Element spartanParent
                = getSpartan().getParentNode();
            return ((DocumentImpl)getOwnerDocument()).wrapper( spartanParent );
        }

    }

    public String getPrefix() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getPrefix");
    }

    public boolean hasAttributes() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: hasAttributes");
    }

    @Override
    public short compareDocumentPosition(Node node) throws DOMException {
        return 0;
    }

    @Override
    public String lookupPrefix(String s) {
        return null;
    }

    @Override
    public boolean isDefaultNamespace(String s) {
        return false;
    }

    @Override
    public boolean isEqualNode(Node node) {
        return false;
    }

    @Override
    public Object getFeature(String s, String s1) {
        return null;
    }

    public boolean hasChildNodes() {
        return getSpartanElement().getFirstChild() != null;
    }

    public org.w3c.dom.Node insertBefore( org.w3c.dom.Node parm1,
                                          org.w3c.dom.Node parm2
                                          )
        throws org.w3c.dom.DOMException
    {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: insertBefore");
    }

    public boolean isSupported(String feature, String version) {
        if( feature.equals("NodeTestFilter") )
            return false;
        throw new Error(
                        "Method isSupported("+feature
                        +","+version+") not known."
                        );
    }

    public void normalize() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: normalize");
    }

    public org.w3c.dom.Node removeChild(org.w3c.dom.Node child)
        throws org.w3c.dom.DOMException
    {
        try{

            getSpartanElement().removeChild( ((NodeImpl)child).getSpartan() );
            return child;

        }catch( com.hp.hpl.sparta.DOMException e ){
            throw new org.w3c.dom.DOMException( e.code, e.getMessage() );
        }
    }

    public org.w3c.dom.Node replaceChild( org.w3c.dom.Node newChild,
                                          org.w3c.dom.Node oldChild
                                          )
        throws org.w3c.dom.DOMException
    {
        try{

            if( newChild instanceof ElementImpl )
                getSpartanElement().replaceChild( ((ElementImpl)newChild).getSpartanElement(),
                                                  ((NodeImpl)oldChild).getSpartan() );
            else if( newChild instanceof TextImpl )
                getSpartanElement().replaceChild( ((TextImpl)newChild).getSpartanText(),
                                                  ((NodeImpl)oldChild).getSpartan() );
            else
                throw new org.w3c.dom.DOMException(
                                                   org.w3c.dom.DOMException.HIERARCHY_REQUEST_ERR,
                                                   "Cannot replace node with "+newChild.getClass().getName()
                                                   );

            return oldChild;

        }catch( com.hp.hpl.sparta.DOMException e ){
            throw new org.w3c.dom.DOMException( e.code, e.getMessage() );
        }
    }

    public void setNodeValue(String parm1) throws org.w3c.dom.DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: setNodeValue");
    }

    public void setPrefix(String parm1) throws org.w3c.dom.DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: setPrefix");
    }

    ElementImpl( DocumentImpl doc, String tagName ){
        this(
             doc,
             new com.hp.hpl.sparta.Element( tagName )
                 );
    }


    ElementImpl(
                DocumentImpl doc,
                com.hp.hpl.sparta.Element spartan
                )
    {
        super(doc,spartan);
    }

    com.hp.hpl.sparta.Element getSpartanElement(){
        return (com.hp.hpl.sparta.Element)getSpartan();
    }

    /////////////////////////////////////////////////

}

// $Log: ElementImpl.java,v $
// Revision 1.2  2002/10/30 16:41:39  eobrain
// appendChild no longer throws DOMException
//
// Revision 1.1.1.1  2002/08/19 05:04:17  eobrain
// import from HP Labs internal CVS
//
// Revision 1.13  2002/08/18 05:45:43  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.12  2002/08/15 22:15:24  eob
// Constructor no longer needs documenent.  Fix clone.
//
// Revision 1.11  2002/06/21 00:32:43  eob
// Make work with old JDK 1.1.*
//
// Revision 1.10  2002/05/23 20:06:28  eob
// Add implementation of getChildNodes.
//
// Revision 1.9  2002/05/11 18:53:31  eob
// Implement replaceChild.
//
// Revision 1.8  2002/02/23 02:10:45  eob
// Exception handling.  Implement some methods that had been stubs.
//
// Revision 1.7  2002/02/04 22:11:16  eob
// Implement getAttributeNode
//
// Revision 1.6  2002/02/01 22:01:02  eob
// Comment change only.
//
// Revision 1.5  2002/01/05 08:14:48  eob
// Fix typo
//
// Revision 1.4  2002/01/05 08:11:36  eob
// fix typo
//
// Revision 1.3  2002/01/05 08:04:52  eob
// Factor out some functionality into NodeImpl.
//
// Revision 1.2  2002/01/04 00:51:57  eob
// Store wrapper as annotation of spartan object to avoid creating
// uncecessary wrapper objects, while still allowing garbage collection
// to clean the wrappers up.
//
// Revision 1.1  2002/01/04 19:57:15  eob
// initial
