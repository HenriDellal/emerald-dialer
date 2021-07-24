package com.hp.hpl.thermopylae;

import javax.xml.parsers.*;
import org.xml.sax.*;

/** SAXParserFactory implementation for Sparta XML parser wrapper
    Currently returns a Thermopylae SAXParser with or without 
    namespace support and with no validation. If the application
    requests validation support and the Crimson parser is available,
    a crimson parser is returned.


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
   @version  $Date: 2002/08/19 05:04:18 $  $Revision: 1.1.1.1 $
   @author Sergio Marti
*/
   

public class SAXParserFactoryImpl extends SAXParserFactory {

    private boolean isValidating_ = false;
    private boolean isNamespaceAware_ = true;
    private boolean validate_ = false;
    private boolean nsAware_ = false;

    /*
      private SAXParserFactory thirdParty_ = null;
    */

    public SAXParserFactoryImpl() {
        super();

        /* Deactivate checking for crimson to use as validating parser
           try {       
           Class c = 
           Class.forName("org.apache.crimson.jaxp.SAXParserFactoryImpl");
           if (c != null) {
           java.lang.reflect.Constructor cons = c.getConstructor(null);
           if (cons != null)
           thirdParty_ = (SAXParserFactory)cons.newInstance(null);
           }
           isValidating_ = true;
           isNamespaceAware_ = true;
           } catch (Throwable t) { 
           //      System.out.println("Class not found: " + t.getMessage());
           }
        */
    }

    public SAXParser newSAXParser()
        throws ParserConfigurationException, SAXException
    {
        /* Deactivate using third party parser  
           SAXParser parser;
           if (validate_ && (thirdParty_ != null))
           parser = thirdParty_.newSAXParser();
           else
           parser = new SAXParserImpl(nsAware_);
        */

        return new SAXParserImpl(nsAware_);
    }

    public boolean getFeature(String parm1) throws IllegalArgumentException {
        throw new IllegalArgumentException("thermopylae parser does not support features");
    }

    public void setFeature(String parm1, boolean parm2)
        throws IllegalArgumentException
    {
        throw new IllegalArgumentException("thermopylae parser does not support features");
    }

    public boolean isNamespaceAware() {
        return isNamespaceAware_;
    }

    public boolean isValidating() {
        return isValidating_;
    }

    public void setNamespaceAware(boolean awareness) {
        nsAware_ = awareness;
        /* Deactivate using third party parser  
           if (thirdParty_ != null)
           thirdParty_.setNamespaceAware(awareness);
        */
    }
    
    public void setValidating(boolean validate) {
        validate_ = validate;
        /* Deactivate using third party parser
           if (thirdParty_ != null)
           thirdParty_.setValidating(validate);
        */
    }
}

// $Log: SAXParserFactoryImpl.java,v $
// Revision 1.1.1.1  2002/08/19 05:04:18  eobrain
// import from HP Labs internal CVS
//
// Revision 1.6  2002/08/18 05:46:14  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.5  2002/08/17 00:54:14  sermarti
//
// Revision 1.4  2002/08/15 23:40:23  sermarti
//
// Revision 1.3  2002/08/09 22:36:49  sermarti
//
// Revision 1.2  2002/08/07 22:08:08  sermarti
//
// Revision 1.1  2002/07/24 23:55:43  sermarti
// SAX parser wrapper for Sparta that is JAXP compliant.
