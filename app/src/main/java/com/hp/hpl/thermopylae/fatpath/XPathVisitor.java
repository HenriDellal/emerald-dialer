package com.hp.hpl.thermopylae.fatpath;

import com.hp.hpl.sparta.xpath.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * Visitor that evaluates an xpath expression relative to a context
 * node by walking over the parse tree of the expression.

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
   @version  $Date: 2003/05/12 20:19:16 $  $Revision: 1.7 $
   @author Eamonn O'Brien-Strain
 * @stereotype visitor
 */

class XPathVisitor implements Visitor {

    /** Evaluate a relative xpath expression relative to a context
     * element by walking over the parse tree of th expression. */
    private XPathVisitor(XPath xpath, Node context) throws XPathException{
        xpath_ = xpath;
        contextNode_ = context;
        nodelistFiltered_ = new Vector(1);
        nodelistFiltered_.addElement( contextNode_ );


        for(Enumeration i=xpath.getSteps(); i.hasMoreElements(); ){
            Step step = (Step)i.nextElement();
            multiLevel_ = step.isMultiLevel();
            nodesetIterator_ = null;
            step.getNodeTest().accept(this);
            nodesetIterator_ = nodelistRaw_.elements();
            nodelistFiltered_.removeAllElements();
            while( nodesetIterator_.hasMoreElements() ){
                node_ = (Node)nodesetIterator_.nextElement();
                step.getPredicate().accept(this);
                Boolean expr = (Boolean)exprStack_.pop();
                if( expr.booleanValue() )
                    nodelistFiltered_.addElement( node_ );
            }
        }


        //convert result from list of nodes to list of strings
        if( xpath.isStringValue() ){
            int size = nodelistFiltered_.size();
            for( int i=0; i<size; ++i){
                Node node = (Node)nodelistFiltered_.elementAt(i);
                String string = ( node instanceof Attr )
                    ? ( (Attr)node ).getValue()
                    : ( (Text)node ).getData();
                nodelistFiltered_.setElementAt( string, i );
            }
        }

    }

    /** Evaluate a relative xpath expression relative to a context
     * element by walking over the parse tree of th expression. */
    public XPathVisitor(Element context, XPath xpath) throws XPathException{
        this(xpath,context);
        if( xpath.isAbsolute() )
            throw new XPathException(xpath,
                                     "Cannot use element as context node for absolute xpath");
    }


    /** Evaluate an absolute xpath expression in a document by walking
        over the parse tree of th expression. */
    public XPathVisitor(Document context, XPath xpath)
        throws XPathException
    {
        this(xpath,context);
    }

    public void visit(ThisNodeTest a){
        nodelistRaw_.removeAllElements();
        nodelistRaw_.add( contextNode_, ONE );
    }

    /** @throws XPathException if ".." applied to node with no parent. */
    public void visit(ParentNodeTest a) throws XPathException{
        nodelistRaw_.removeAllElements();
        Node parent = contextNode_.getParentNode();
        if( parent == null )
            throw new XPathException(xpath_,
                                     "Illegal attempt to apply \"..\" to node with no parent.");
        nodelistRaw_.add(parent, ONE );
    }

    public void visit(AllElementTest a){
        Vector oldNodeList = nodelistFiltered_;
        nodelistRaw_.removeAllElements();
        for(Enumeration i=oldNodeList.elements(); i.hasMoreElements(); )
            accumulateElements( (Node)i.nextElement() );
    }

    private void accumulateElements(Node node){
        if( node instanceof Document )
            accumulateElements( (Document)node );
        else
            accumulateElements( (Element)node );
    }

    private void accumulateElements(Document doc){
        Element child = doc.getDocumentElement();
        nodelistRaw_.add(child, ONE );
        if( multiLevel_ )
            accumulateElements( child );  //recursive call
    }

    private void accumulateElements(Element element){
        int position = 0;
        for(Node n=element.getFirstChild(); n!=null; n=n.getNextSibling() ){
            if( n instanceof Element ){
                nodelistRaw_.add(n, ++position);
                if( multiLevel_ )
                    accumulateElements( (Element)n );  //recursive call
            }
        }
    }

    public void visit(TextTest a){
        Vector oldNodeList = nodelistFiltered_;
        nodelistRaw_.removeAllElements();
        for(Enumeration i=oldNodeList.elements(); i.hasMoreElements(); ){
            Object node = i.nextElement();
            if( node instanceof Element ){
                Element element = (Element)node;
                for(Node n=element.getFirstChild();
                    n!=null;
                    n=n.getNextSibling()
                    )
                    if( n instanceof Text )
                        nodelistRaw_.add((Text)n);
            }
        }
    }



    public void visit(ElementTest test){
        String tagName = test.getTagName();
        Vector oldNodeList = nodelistFiltered_;
        nodelistRaw_.removeAllElements();
        for(Enumeration i=oldNodeList.elements(); i.hasMoreElements(); )
            accumulateMatchingElements( (Node)i.nextElement(),
                                        tagName );
    }

    private void accumulateMatchingElements(Node element, String tagName){
        int position = 0;
        for(Node n=element.getFirstChild(); n!=null; n=n.getNextSibling() ){
            if(  n instanceof Element ){
                Element child = (Element)n;
                if( child.getTagName().equals(tagName) )
                    nodelistRaw_.add(child, ++position);
                if( multiLevel_  )
                    accumulateMatchingElements( child, tagName );  //recursion
            }
        }
    }

    public void visit(AttrTest test){
        Vector oldNodeList = nodelistFiltered_;
        nodelistRaw_.removeAllElements();
        for(Enumeration i=oldNodeList.elements(); i.hasMoreElements(); ){
            Node node =  (Node)i.nextElement();
            if( node instanceof Element ){
                Element element = (Element)node;
                Attr attr = element.getAttributeNode( test.getAttrName() );
                if( attr != null )
                    nodelistRaw_.add(attr);
            }
        }
    }

    static private final Integer ONE = new Integer(1);
    static private final Boolean TRUE  = new Boolean( true );
    static private final Boolean FALSE = new Boolean( false );

    public void visit(TrueExpr a){
        exprStack_.push( TRUE );
    }
    public void visit(AttrExistsExpr a) throws XPathException{
        if( !(node_ instanceof Element ) )
            throw new XPathException(xpath_,
                                     "Cannot test attribute of document");
        Element element = (Element)node_;
        String attrValue = element.getAttribute( a.getAttrName() );
        boolean result = attrValue != null && attrValue.length()>0;
        exprStack_.push( result ? TRUE : FALSE );
    }
    public void visit(AttrEqualsExpr a) throws XPathException{
        if( !(node_ instanceof Element ) )
            throw new XPathException(xpath_,
                                     "Cannot test attribute of document");
        Element element = (Element)node_;
        String attrValue = element.getAttribute( a.getAttrName() );
        boolean result = a.getAttrValue().equals( attrValue );
        exprStack_.push( result ? TRUE : FALSE );
    }
    public void visit(AttrNotEqualsExpr a) throws XPathException{
        if( !(node_ instanceof Element ) )
            throw new XPathException(xpath_,
                                     "Cannot test attribute of document");
        Element element = (Element)node_;
        String attrValue = element.getAttribute( a.getAttrName() );
        boolean result = !a.getAttrValue().equals( attrValue );
        exprStack_.push( result ? TRUE : FALSE );
    }

    public void visit(AttrLessExpr a) throws XPathException{
        if( !(node_ instanceof Element ) )
            throw new XPathException(xpath_,
                                     "Cannot test attribute of document");
        Element element = (Element)node_;
        // Use jdk1.1 API to make code work with PersonalJava
        // double attrValue = Double.parseDouble( element.getAttribute( a.getAttrName() ) );
        double attrValue = Double.valueOf( element.getAttribute( a.getAttrName() ) ).doubleValue();
        boolean result = attrValue < a.getAttrValue();
        exprStack_.push( result ? TRUE : FALSE );
    }

    public void visit(AttrGreaterExpr a) throws XPathException{
        if( !(node_ instanceof Element ) )
            throw new XPathException(xpath_,
                                     "Cannot test attribute of document");
        Element element = (Element)node_;
        // Use jdk1.1 API to make code work with PersonalJava
        // double attrValue = Double.parseDouble( element.getAttribute( a.getAttrName() ) );
        double attrValue = Double.valueOf( element.getAttribute( a.getAttrName() ) ).doubleValue();
        boolean result = attrValue > a.getAttrValue();
        exprStack_.push( result ? TRUE : FALSE );
    }

    public void visit(TextExistsExpr a) throws XPathException
    {
        if( !(node_ instanceof Element ) )
            throw new XPathException(xpath_,
                                     "Cannot test attribute of document");
        Element element = (Element)node_;
        for(Node i=element.getFirstChild(); i!=null; i=i.getNextSibling() ){
            if( i instanceof Text ){
                exprStack_.push( TRUE );
                return;
            }       
        }
        exprStack_.push( FALSE );
    }
    public void visit(TextEqualsExpr a) throws XPathException
    {
        if( !(node_ instanceof Element ) )
            throw new XPathException(xpath_,
                                     "Cannot test attribute of document");
        Element element = (Element)node_;
        for(Node i=element.getFirstChild(); i!=null; i=i.getNextSibling() ){
            if( i instanceof Text ){
                Text text = (Text)i;
                if( text.getData().equals(a.getValue() ) ){
                    exprStack_.push( TRUE );
                    return;
                }
            }       
        }
        exprStack_.push( FALSE );
    }

    public void visit(TextNotEqualsExpr a) throws XPathException
    {
        if( !(node_ instanceof Element ) )
            throw new XPathException(xpath_,
                                     "Cannot test attribute of document");
        Element element = (Element)node_;
        for(Node i=element.getFirstChild(); i!=null; i=i.getNextSibling() ){
            if( i instanceof Text ){
                Text text = (Text)i;
                if( !text.getData().equals(a.getValue() ) ){
                    exprStack_.push( TRUE );
                    return;
                }
            }       
        }
        exprStack_.push( FALSE );
    }



    public void visit(PositionEqualsExpr a) throws XPathException{
        if( !(node_ instanceof Element ) )
            throw new XPathException(xpath_,
                                     "Cannot test position of document");
        Element element = (Element)node_;
        boolean result = ( nodelistRaw_.position(element) == a.getPosition() );
        exprStack_.push( result ? TRUE : FALSE );
    }


    /** Get all the elements or strings that match the xpath expression. */
    public Enumeration getResult(){
        return nodelistFiltered_.elements();
    }

    /** @associates Node. */
    private final NodeListWithPosition nodelistRaw_ = new NodeListWithPosition();
    private Vector nodelistFiltered_ = new Vector();
    private Enumeration nodesetIterator_ = null;
    private Node node_ = null;
    private final Stack exprStack_ = new Stack();
    /**
     * @label context
     */
    private /*final (JDK1.1 bug)*/ Node contextNode_;

    private boolean multiLevel_;

    private /*final (JDK1.1 bug)*/ XPath xpath_;
}


/** A list of nodes, together with the positions in their context of
    each node. */
class NodeListWithPosition
{
    Enumeration elements()
    {
        return vector_.elements();
    }

    void removeAllElements()
    {
        vector_.removeAllElements();
    }

    void add( Text text )
    {
        vector_.addElement( text );
    }

    void add( Attr attr )
    {
        vector_.addElement( attr );
    }

    void add( Node node, int position )
    {
        add( node, new Integer(position) );
    }

    void add( Node node, Integer position )
    {
        vector_.addElement( node );
        positions_.put( node, position );
    }

    int position( Node node )
    {
        return ( (Integer)positions_.get(node) ).intValue();
    }

    private final Vector vector_ = new Vector();
    private final Map positions_ = new HashMap();
}


// $Log: XPathVisitor.java,v $
// Revision 1.7  2003/05/12 20:19:16  eobrain
// Remove unused private method.
//
// Revision 1.6  2003/01/27 23:30:59  yuhongx
// Replaced Hashtable with HashMap.
//
// Revision 1.5  2003/01/09 01:20:42  yuhongx
// Use JDK1.1 API to make code work with PersonalJava.
//
// Revision 1.4  2002/12/05 04:36:37  eobrain
// Add support for greater than and less than in predicates.
//
// Revision 1.3  2002/10/30 16:29:18  eobrain
// Feature request [ 630127 ] Support /a/b[text()='foo']
// http://sourceforge.net/projects/sparta-xml/
//
// Revision 1.2  2002/09/18 05:26:46  eobrain
// Support xpath predicates of the form [1], [2], ...
//
// Revision 1.1.1.1  2002/08/19 05:04:23  eobrain
// import from HP Labs internal CVS
//
// Revision 1.10  2002/08/19 00:21:30  eob
// Make class package-private so as to remove clutter in Javadoc.
//
// Revision 1.9  2002/06/21 00:20:16  eob
// Make work with old JDK 1.1.*
//
// Revision 1.8  2002/06/14 19:34:11  eob
// Add handling of "text()" in XPath expressions.
//
// Revision 1.7  2002/06/04 05:28:56  eob
// Simplify use of visitor pattern to make code easier to understand.
// Fix bug when predicate in middle of XPath.
//
// Revision 1.6  2002/05/23 21:09:59  eob
// Better error reporting.
//
// Revision 1.5  2002/03/25 22:59:03  eob
// Handle case of attempt to do ".." from root
//
// Revision 1.4  2002/02/14 02:19:58  eob
// Comment change only.
//
// Revision 1.3  2002/02/04 22:06:11  eob
// Add handling of attribute xpath expressions that return strings.
//
// Revision 1.2  2002/02/01 22:03:05  eob
// Make consistent with sparta version of this class.
//
// Revision 1.1  2002/02/01 18:52:14  eob
// initial
