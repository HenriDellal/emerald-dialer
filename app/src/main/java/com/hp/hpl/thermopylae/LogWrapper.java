package com.hp.hpl.thermopylae;

import org.xml.sax.*;

import com.hp.hpl.sparta.ParseLog;

/**
 * Sparta ParseLog wrapper around w3c ErrorHandler.

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

class LogWrapper implements ParseLog {

    public LogWrapper(ErrorHandler handler) {
        handler_ = handler;
    }

    public void error(String message, String systemId, int lineNum){
        message(message,systemId,lineNum);
    }

    public void warning(String message, String systemId, int lineNum){
        message(message,systemId,lineNum);
    }

    public void note(String message, String systemId, int lineNum){
        message(message,systemId,lineNum);
    }

    private void message(String message, String systemId, int lineNum){
        try{
            handler_.error(new SAXParseException(
                                                 message,
                                                 "",
                                                 systemId,
                                                 lineNum,
                                                 0
                                                 ));
        }catch(SAXException e){
            throw new Error("Assertion violation: error handler error method should not throw exception");
        }
    }

    private final ErrorHandler handler_;
}

// $Log: LogWrapper.java,v $
// Revision 1.2  2002/11/06 02:59:55  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.1.1.1  2002/08/19 05:04:16  eobrain
// import from HP Labs internal CVS
//
// Revision 1.5  2002/08/18 05:45:48  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.4  2002/08/05 20:04:32  sermarti
//
// Revision 1.3  2002/01/09 00:55:02  eob
// Add warning.
//
// Revision 1.2  2002/01/08 19:59:37  eob
// Distinguish error from note.
//
// Revision 1.1  2002/01/04 00:41:51  eob
// initial
