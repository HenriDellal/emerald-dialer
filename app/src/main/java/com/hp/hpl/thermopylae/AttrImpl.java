package com.hp.hpl.thermopylae;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * Wrapper around sparta attribute.

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
   @author Eamonn O'Brien-Strain */

public class AttrImpl implements Attr {

    AttrImpl(ElementImpl element, String name, String value) {
        element_ = element;
        name_ = name;
        value_ = value;
    }

    public String getName() {
        return name_;
    }

    public Element getOwnerElement() {
        /**@todo: Implement this org.w3c.dom.Attr method*/
        throw new Error("getOwnerElement");
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    @Override
    public boolean isId() {
        return false;
    }

    public boolean getSpecified() {
        /**@todo: Implement this org.w3c.dom.Attr method*/
        throw new Error("getSpecified");
    }

    public String getValue() {
        return value_;
    }

    public void setValue(String parm1) throws org.w3c.dom.DOMException {
        /**@todo: Implement this org.w3c.dom.Attr method*/
        throw new Error("setValue");
    }

    public Node appendChild(Node parm1) throws org.w3c.dom.DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("appendChild");
    }

    public Node cloneNode(boolean parm1) {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("cloneNode");
    }

    public NamedNodeMap getAttributes() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("getAttributes");
    }

    public NodeList getChildNodes() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("getChildNodes");
    }

    public Node getFirstChild() {
        return null;
    }

    public Node getLastChild() {
        return null;
    }

    public String getLocalName() {
        return null;
    }

    public String getNamespaceURI() {
        return null;
    }

    public Node getNextSibling() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("getNextSibling");
    }

    public String getNodeName() {
        return name_;
    }

    public short getNodeType() {
        return Node.ATTRIBUTE_NODE;
    }

    public String getNodeValue() throws org.w3c.dom.DOMException {
        return value_;
    }

    public Document getOwnerDocument() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("getOwnerDocument");
    }

    public Node getParentNode() {
        return element_;
    }

    public String getPrefix() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("getPrefix");
    }

    public Node getPreviousSibling() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("getPreviousSibling");
    }

    public boolean hasAttributes() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("hasAttributes");
    }

    public boolean hasChildNodes() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("hasChildNodes");
    }

    public Node insertBefore(Node parm1, Node parm2)
        throws org.w3c.dom.DOMException
    {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("insertBefore");
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
        throw new Error("normalize");
    }

    public Node removeChild(Node parm1) throws org.w3c.dom.DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("removeChild");
    }

    public Node replaceChild(Node parm1, Node parm2)
        throws org.w3c.dom.DOMException
    {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("replaceChild");
    }

    public void setNodeValue(String parm1) throws org.w3c.dom.DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("setNodeValue");
    }

    public void setPrefix(String parm1) throws org.w3c.dom.DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("setPrefix");
    }
    //////////////////////////////////////////////////////////////
    // BEGIN DOM 3

    public String getBaseURI()
    {
        throw new Error("not implemented");
    }

    @Override
    public short compareDocumentPosition(Node node) throws DOMException {
        return 0;
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

    @Override
    public String lookupPrefix(String s) {
        return null;
    }

    @Override
    public boolean isDefaultNamespace(String s) {
        return false;
    }


    public String lookupNamespacePrefix(String namespaceURI){
        throw new Error("not implemented");
    }

    public String lookupNamespaceURI(String prefix){
        throw new Error("not implemented");
    }

    @Override
    public boolean isEqualNode(Node node) {
        return false;
    }

    @Override
    public Object getFeature(String s, String s1) {
        return null;
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


    private final ElementImpl element_;
    private final String name_;
    private final String value_;
}

// $Log: AttrImpl.java,v $
// Revision 1.1.1.1  2002/08/19 05:04:19  eobrain
// import from HP Labs internal CVS
//
// Revision 1.6  2002/08/19 00:37:38  eob
// Tweak javadoc comment.
//
// Revision 1.5  2002/08/18 05:45:17  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.4  2002/06/21 00:30:45  eob
// Make work with old JDK 1.1.*
//
// Revision 1.3  2002/02/08 20:32:37  eob
// Added extra DOM3 stuff.
//
// Revision 1.2  2002/01/22 18:28:37  eob
// Implement getFirstChild/getLastChild
//
// Revision 1.1  2002/01/04 19:56:49  eob
// initial
