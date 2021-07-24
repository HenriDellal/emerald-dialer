package com.hp.hpl.thermopylae;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.UserDataHandler;

/**
 * Standard wrapper around sparta Document.

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
   @version  $Date: 2002/08/21 20:18:42 $  $Revision: 1.2 $
   @author Eamonn O'Brien-Strain
 * @stereotype factory
 */

public class DocumentImpl implements org.w3c.dom.Document {

    public Attr createAttribute(String parm1) throws DOMException {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: createAttribute");
    }

    public Attr createAttributeNS(String parm1, String parm2)
        throws DOMException
    {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: createAttributeNS");
    }

    public CDATASection createCDATASection(String data) throws DOMException {
        return new TextImpl( this,
                             new com.hp.hpl.sparta.Text(data)
                                 );
    }

    public Comment createComment(String parm1) {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: createComment");
    }

    public DocumentFragment createDocumentFragment() {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: createDocumentFragment");
    }

    public org.w3c.dom.Element createElement(String tagName)
        throws DOMException
    {
        return new ElementImpl(this,tagName);
    }

    public org.w3c.dom.Element createElementNS(String parm1, String parm2)
        throws DOMException
    {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: createElementNS");
    }

    public EntityReference createEntityReference(String parm1)
        throws DOMException
    {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: createEntityReference");
    }

    public ProcessingInstruction createProcessingInstruction(String a,
                                                             String b
                                                             )
        throws DOMException
    {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: createProcessingInstruction");
    }

    public org.w3c.dom.Text createTextNode(String data) {
        return new TextImpl( this,
                             new com.hp.hpl.sparta.Text(data)
                                 );
    }

    public DocumentType getDoctype() {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: getDoctype");
    }

    public org.w3c.dom.Element getDocumentElement() {
        return spartan_.getDocumentElement()==null
            ? null
            : wrapper( spartan_.getDocumentElement() );
    }

    public org.w3c.dom.Element getElementById(String parm1) {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: getElementById");
    }

    @Override
    public String getInputEncoding() {
        return null;
    }

    @Override
    public String getXmlEncoding() {
        return null;
    }

    @Override
    public boolean getXmlStandalone() {
        return false;
    }

    @Override
    public void setXmlStandalone(boolean b) throws DOMException {

    }

    @Override
    public String getXmlVersion() {
        return null;
    }

    @Override
    public void setXmlVersion(String s) throws DOMException {

    }

    public NodeList getElementsByTagName(String parm1) {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: getElementsByTagName");
    }

    public NodeList getElementsByTagNameNS(String parm1, String parm2) {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: getElementsByTagNameNS");
    }

    public DOMImplementation getImplementation() {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: getImplementation");
    }

    public Node importNode(Node parm1, boolean parm2) throws DOMException {
        /**@todo: Implement this org.w3c.dom.Document method*/
        throw new Error("not implemented: importNode");
    }

    public Node appendChild(Node node) throws DOMException {
        if( node instanceof ElementImpl ){
            ElementImpl element = (ElementImpl)node;
            spartan_.setDocumentElement( element.getSpartanElement() );
            return element;
        }else
            throw new DOMException(
                                   DOMException.HIERARCHY_REQUEST_ERR,
                                   "can only add root element to document"
                                   );

    }

    public Node cloneNode(boolean parm1) {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: cloneNode");
    }

    public NamedNodeMap getAttributes() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getAttributes");
    }

    public NodeList getChildNodes() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getChildNodes");
    }

    public Node getFirstChild() {
        return wrapper( spartan_.getDocumentElement() );
    }

    public Node getLastChild() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getLastChild");
    }

    /** Always return null */
    public String getLocalName() {
        return null;
    }

    /** Always return null */
    public String getNamespaceURI() {
        return null;
    }

    public Node getNextSibling() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getNextSibling");
    }

    public String getNodeName() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getNodeName");
    }

    public short getNodeType() {
        return Node.DOCUMENT_NODE;
    }

    public String getNodeValue() throws DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getNodeValue");
    }

    public org.w3c.dom.Document getOwnerDocument() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getOwnerDocument");
    }

    public Node getParentNode() {
        return null;
    }

    public String getPrefix() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getPrefix");
    }

    public Node getPreviousSibling() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: getPreviousSibling");
    }

    public boolean hasAttributes() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: hasAttributes");
    }

    public boolean hasChildNodes() {
        return spartan_.getDocumentElement() != null;
    }

    public Node insertBefore(Node parm1, Node parm2) throws DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: insertBefore");
    }

    public boolean isSupported(String feature, String version) {
        if( feature.equals("NodeTestFilter") )
            return false;
        throw new Error(
                        "isSupported("+feature+","
                        +version+") not known."
                        );
    }

    public void normalize() {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: normalize");
    }

    public Node removeChild(Node parm1) throws DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: removeChild");
    }

    public Node replaceChild(Node parm1, Node parm2) throws DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: replaceChild");
    }

    public void setNodeValue(String parm1) throws DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: setNodeValue");
    }

    public void setPrefix(String parm1) throws DOMException {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: setPrefix");
    }

    //////////////////////////////////////////////////////////////////
    // BEGIN DOM3

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

    ///////////////////////////////////////////////////

    public Node adoptNode(Node source)
        throws DOMException
    {
        /**@todo: Implement this org.w3c.dom.Node method*/
        throw new Error("not implemented: setPrefix");
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return null;
    }

    public String getActualEncoding(){
        return actualEncoding_;
    }
    public void setActualEncoding(String actualEncoding){
        actualEncoding_ = actualEncoding;
    }

    public String getEncoding()
    {
        return encoding_;
    }

    public void setEncoding(String encoding)
    {
        encoding_ = encoding;
    }

    public String getVersion()
    {
        return version_;
    }

    public void setVersion(String version)
    {
        version_ = version;
    }

    public boolean getStandalone()
    {
        return standalone_;
    }

    public void setStandalone(boolean standalone)
    {
        standalone_ = standalone;
    }

    public boolean getStrictErrorChecking()
    {
        return strictErrorChecking_;
    }

    public void setStrictErrorChecking(boolean strictErrorChecking)
    {
        strictErrorChecking_ = strictErrorChecking;
    }

    public DOMErrorHandler getErrorHandler(){
        throw new Error("not implemented");
    }
    public void setErrorHandler(DOMErrorHandler errorHandler){
        throw new Error("not implemented");
    }

    public String getDocumentURI(){
        throw new Error("not implemented");
    }
    public void setDocumentURI(String documentURI){
        throw new Error("not implemented");
    }


    public void normalizeDocument(){
        throw new Error("not implemented");
    }

    public boolean canSetNormalizationFeature(String name,
                                              boolean state)
    {
        throw new Error("not implemented");
    }

    public void setNormalizationFeature(String name,
                                        boolean state)
        throws DOMException
    {
        throw new Error("not implemented");
    }

    public boolean getNormalizationFeature(String name)
        throws DOMException
    {
        throw new Error("not implemented");
    }

    public Node renameNode(Node n,
                           String namespaceURI,
                           String name)
        throws DOMException
    {
        throw new Error("not implemented");
    }

    private String encoding_ = null;
    private String actualEncoding_ = null;
    private String version_ = null;
    private boolean standalone_ = true;
    private boolean strictErrorChecking_ = true;

    // END DOM3
    //////////////////////////////////////////////////////////////////
    public boolean equals(Object obj){
        if( obj instanceof DocumentImpl ){
            DocumentImpl that = (DocumentImpl)obj;
            return this.spartan_.equals( that.spartan_ );
        }else
            return false;
    }

    com.hp.hpl.sparta.Document getSpartan(){
        return spartan_;
    }

    NodeImpl wrapper(
                     com.hp.hpl.sparta.Node spartan
                     )
    {
        if( spartan instanceof com.hp.hpl.sparta.Element )
            return wrapper(
                           (com.hp.hpl.sparta.Element)spartan
                           );
        else
            return wrapper(
                           (com.hp.hpl.sparta.Text)spartan
                           );
    }

    private ElementImpl wrapper(
                                com.hp.hpl.sparta.Element spartan
                                )
    {
        ElementImpl result = (ElementImpl)spartan.getAnnotation();
        if( result == null ){
            result = new ElementImpl(this,spartan);
            spartan.setAnnotation(result);
        }
        return result;
    }

    private TextImpl wrapper(
                             com.hp.hpl.sparta.Text spartan
                             )
    {
        TextImpl result = (TextImpl)spartan.getAnnotation();
        if( result == null ){
            result = new TextImpl(this,spartan);
            spartan.setAnnotation(result);
        }
        return result;
    }



    private DocumentImpl( com.hp.hpl.sparta.Document spartan ) {
        spartan_ = spartan;
    }

    /**
     * @link aggregation
     * @label spartan
     */
    private final com.hp.hpl.sparta.Document spartan_;

    /////////////////////////////////////////////////////

    static public DocumentImpl wrapper( com.hp.hpl.sparta.Document spartan ){
        DocumentImpl result = (DocumentImpl)spartan.getAnnotation();
        if( result == null ){
            result = new DocumentImpl(spartan);
            spartan.setAnnotation(result);
        }
        return result;
    }

}

// $Log: DocumentImpl.java,v $
// Revision 1.2  2002/08/21 20:18:42  eobrain
// Implement createCDATASection
//
// Revision 1.1.1.1  2002/08/19 05:04:17  eobrain
// import from HP Labs internal CVS
//
// Revision 1.12  2002/08/19 00:38:32  eob
// Tweak javadoc comment.
//
// Revision 1.11  2002/08/18 05:45:37  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.10  2002/08/15 22:21:09  eob
// Constructor no longer needs document
//
// Revision 1.9  2002/06/21 00:32:16  eob
// Make work with old JDK 1.1.*
//
// Revision 1.8  2002/02/23 02:09:29  eob
// Make wrapper method public.  Implement some methods that had been stubs.
//
// Revision 1.7  2002/02/08 20:33:29  eob
// Added extra DOM3 stuff.
//
// Revision 1.6  2002/02/01 22:00:47  eob
// Add DOM level 3 methods.
//
// Revision 1.5  2002/01/09 00:54:45  eob
// Handle null documentElement
//
// Revision 1.4  2002/01/05 07:58:54  eob
// Implement missing functionality.
//
// Revision 1.3  2002/01/04 00:49:35  eob
// Formatting change only
//
// Revision 1.2  2002/01/04 00:48:19  eob
// Store wrapper as annotation of spartan object to avoid creating
// uncecessary wrapper objects, while still allowing garbage collection
// to clean the wrappers up.
//
// Revision 1.1  2002/01/04 18:49:27  eob
// initial
