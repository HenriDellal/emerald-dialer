package com.hp.hpl.thermopylae;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Standard wrapper around spartan Text node.

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
   @version  $Date: 2003/06/19 20:19:27 $  $Revision: 1.4 $
   @author Eamonn O'Brien-Strain
 */

public class TextImpl extends NodeImpl implements Text, CDATASection {

    static private final NodeList EMPTY_NODELIST = new NodeList() {
        public Node item(int index) {
            return null;
        }
        public int getLength() {
            return 0;
        }
    };

    public Text splitText(int parm1) throws DOMException {
        throw new Error("not implemented: splitText()");
    }

    @Override
    public boolean isElementContentWhitespace() {
        return false;
    }

    public void appendData(String s) throws DOMException {
        getSpartanText().appendData(s);
    }
    public void deleteData(int parm1, int parm2) throws DOMException {
        throw new Error("not implemented: deleteData()");
    }
    public String getData() throws DOMException {
        return getSpartanText().getData();
    }
    public int getLength() {
        return getSpartanText().getData().length();
    }
    public void insertData(int parm1, String parm2) throws DOMException {
        throw new Error("not implemented: insertData()");
    }
    public void replaceData(int parm1, int parm2, String parm3)
        throws DOMException {
        throw new Error("not implemented: replaceData()");
    }
    public void setData(String s) throws DOMException {
        getSpartanText().setData(s);
    }
    public String substringData(int offset, int count) throws DOMException {
        return getSpartanText().getData().substring(offset, offset + count);
    }
    public Node appendChild(Node parm1) throws DOMException {
        throw new DOMException(
            DOMException.HIERARCHY_REQUEST_ERR,
            "Text nodes cannot have children.");
    }
    public Node cloneNode(boolean deep) {
        return new TextImpl(
            (DocumentImpl) getOwnerDocument(),
            (com.hp.hpl.sparta.Text) getSpartanText().clone());
    }
    public NamedNodeMap getAttributes() {
        return null;
    }
    public NodeList getChildNodes() {
        return EMPTY_NODELIST;
    }
    public Node getFirstChild() {
        return null;
    }
    public Node getLastChild() {
        return null;
    }

    /** Always return null */
    public String getLocalName() {
        return null;
    }

    /** Always return null */
    public String getNamespaceURI() {
        return null;
    }
    public String getNodeName() {
        return "#text";
    }
    public short getNodeType() {
        return Node.TEXT_NODE;
    }
    public String getNodeValue() throws DOMException {
        return getSpartanText().getData();
    }

    public Node getParentNode() {
        if (getSpartan().getParentNode() == null)
            return null;
        else {
            com.hp.hpl.sparta.Element spartanParent =
                getSpartan().getParentNode();
            return ((DocumentImpl) getOwnerDocument()).wrapper(spartanParent);
        }

    }

    public String getPrefix() {
        return null;
    }
    public boolean hasAttributes() {
        return false;
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

    /** ALways returns false. */
    public boolean hasChildNodes() {
        return false;
    }
    public Node insertBefore(Node parm1, Node parm2) throws DOMException {
        /**@todo: Implement this Node method*/
        throw new Error("not implemented: insertBefore()");
    }

    public boolean isSupported(String feature, String version) {
        if (feature
            .equals("http://xml.apache.org/xpath/features/whitespace-pre-stripping"))
            return false;
        else if (
            feature.equals("http://xml.apache.org/xalan/features/feed-events"))
            return false;
        else if (feature.equals("NodeTestFilter"))
            return false;
        throw new Error(
            "Method isSupported(" + feature + "," + version + ") not known.");
    }

    public void normalize() {
        /**@todo: Implement this Node method*/
        throw new Error("not implemented: normalize()");
    }
    public Node removeChild(Node parm1) throws DOMException {
        /**@todo: Implement this Node method*/
        throw new Error("not implemented: removeChild()");
    }
    public Node replaceChild(Node parm1, Node parm2) throws DOMException {
        /**@todo: Implement this Node method*/
        throw new Error("not implemented: replaceChild()");
    }
    public void setNodeValue(String data) throws DOMException {
        getSpartanText().setData(data);
    }
    public void setPrefix(String parm1) throws DOMException {
        /**@todo: Implement this Node method*/
        throw new Error("not implemented: setPrefix()");
    }

    ////////////////////////////////////////////////////
    // BEGIN DOM 3

    public boolean getIsWhitespaceInElementContent() {
        /**@todo: Implement this Node method*/
        throw new Error("not implemented: setPrefix()");
    }

    public String getWholeText() {
        /**@todo: Implement this Node method*/
        throw new Error("not implemented: setPrefix()");
    }

    public Text replaceWholeText(String content) throws DOMException {
        /**@todo: Implement this Node method*/
        throw new Error("not implemented: setPrefix()");
    }

    // END DOM 3
    ////////////////////////////////////////////////////

    com.hp.hpl.sparta.Text getSpartanText() {
        return (com.hp.hpl.sparta.Text) getSpartan();
    }

    TextImpl(DocumentImpl doc, com.hp.hpl.sparta.Text spartan) {
        super(doc, spartan);
    }

}

// $Log: TextImpl.java,v $
// Revision 1.4  2003/06/19 20:19:27  eobrain
// Flesh out by implementing some unimplemented functions.
//
// Revision 1.3  2002/11/06 02:59:55  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.2  2002/08/21 20:19:10  eobrain
// Also implement CDATASection.
//
// Revision 1.1.1.1  2002/08/19 05:04:13  eobrain
// import from HP Labs internal CVS
//
// Revision 1.6  2002/08/18 05:46:24  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.5  2002/06/21 00:34:49  eob
// Make work with old JDK 1.1.*
//
// Revision 1.4  2002/05/02 23:01:50  eob
// Handle test fot NodeTestFilter feature.
//
// Revision 1.3  2002/02/23 01:44:56  eob
// Implement some methods that had just been stubs.
//
// Revision 1.2  2002/02/08 20:34:04  eob
// Added extra DOM3 stuff.
//
// Revision 1.1  2002/01/05 07:40:08  eob
// initial
