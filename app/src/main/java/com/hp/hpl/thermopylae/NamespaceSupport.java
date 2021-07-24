package com.hp.hpl.thermopylae;

import java.util.*;

import com.hp.hpl.sparta.ParseException;

/** Streamlined thermopylae specific implementation of 
 *  the org.xml.sax.helpers.NamespaceSupport class.

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
   @version  $Date: 2003/01/27 23:30:59 $  $Revision: 1.2 $
   @author Sergio Marti
 **/

public class NamespaceSupport {

    static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
    static final String XMLPREF = "xml";

    private final Stack context_ = new Stack();
    private Map currContext_ = null;
    private final Stack oldContext_ = new Stack();

    private String defaultNS_ = "";

    /* Cache last prefix for speed */
    private String currPrefix_ = null;
    private String currFullPrefix_ = null;
    private String currNS_ = null;
    private int depth_ = 0;

    
    
    public NamespaceSupport() { }

    public void pushContext() {
        if (currContext_ != null)
            context_.push(currContext_);        
        if (!oldContext_.isEmpty()) {
            currContext_ = (Map)oldContext_.pop();
            currContext_.clear();
        }
        else
            currContext_ = new HashMap();
        
        depth_++;
    }
    
    Iterator popContext() {
        depth_--;
        if (depth_ < 0)
            currPrefix_ = null;
        
        oldContext_.push(currContext_);
        Iterator keySetIterator = currContext_.keySet().iterator();
        if (!context_.isEmpty())
            currContext_ = (Map)context_.pop();
        else
            currContext_ = null;
        return keySetIterator;
    }
    
    void declarePrefix(String prefix, String uri) {
        if (prefix.equals("xml") || prefix.equals("xmlns"))
            return;
        if (prefix.equals("")) {
            defaultNS_ = uri;
            return;
        }
        currContext_.put(prefix, uri);
    }
    
    String[] processName(String fullName, String[] result, boolean attribute) 
        throws ParseException {
        
        if (currPrefix_ != null && fullName.startsWith(currFullPrefix_)) {
            result[0] = currNS_;
            result[1] = fullName.substring(currFullPrefix_.length());
            result[2] = fullName;
            return result;
        }
        
        int a = fullName.indexOf(':');
        if (a > 0) {
            String prefix = fullName.substring(0,a);
            if (prefix.equals("xml") || prefix.equals("xmlns")) {
                return null;
            }
            
            result[1] = fullName.substring(a+1);            
            result[2] = fullName;
            
            result[0] = (String)currContext_.get(prefix);
            if (result[0] == null) {
                for (int i = context_.size()-1; i >= 0; i--) {
                    Map ht = (Map)(context_.elementAt(i));
                    result[0] = (String)(ht.get(prefix));
                    if (result[0] != null)
                        break;
                }
            }   
            
            if (result[0] == null)
                throw new ParseException("Error processing tag " + fullName +
                                         ". No namespace mapping found.");
            
            depth_ = 0;
            currNS_ = result[0];
            currPrefix_ = prefix;
            currFullPrefix_ = prefix + ":";         
        }
        else {
            if (attribute)
                result[0] = "";
            else
                result[0] = defaultNS_;
            result[1] = result[2] = fullName;
        }
        
        return result;
    }
}


// $Log: NamespaceSupport.java,v $
// Revision 1.2  2003/01/27 23:30:59  yuhongx
// Replaced Hashtable with HashMap.
//
// Revision 1.1.1.1  2002/08/19 05:04:16  eobrain
// import from HP Labs internal CVS
//
// Revision 1.5  2002/08/19 00:39:45  eob
// Tweak javadoc comment.
//
// Revision 1.4  2002/08/18 05:45:59  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.3  2002/08/17 00:54:14  sermarti
//
// Revision 1.2  2002/08/15 23:40:23  sermarti
//
// Revision 1.1  2002/08/09 22:36:49  sermarti
