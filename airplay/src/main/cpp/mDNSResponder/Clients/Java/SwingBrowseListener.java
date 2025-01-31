/* -*- Mode: Java; tab-width: 4 -*-
 *
 * Copyright (c) 2004 Apple Computer, Inc. All rights reserved.
 *
 * Disclaimer: IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
 * ("Apple") in consideration of your agreement to the following terms, and your
 * use, installation, modification or redistribution of this Apple software
 * constitutes acceptance of these terms.  If you do not agree with these terms,
 * please do not use, install, modify or redistribute this Apple software.
 *
 * In consideration of your agreement to abide by the following terms, and subject
 * to these terms, Apple grants you a personal, non-exclusive license, under Apple's
 * copyrights in this original Apple software (the "Apple Software"), to use,
 * reproduce, modify and redistribute the Apple Software, with or without
 * modifications, in source and/or binary forms; provided that if you redistribute
 * the Apple Software in its entirety and without modifications, you must retain
 * this notice and the following text and disclaimers in all such redistributions of
 * the Apple Software.  Neither the name, trademarks, service marks or logos of
 * Apple Computer, Inc. may be used to endorse or promote products derived from the
 * Apple Software without specific prior written permission from Apple.  Except as
 * expressly stated in this notice, no other rights or licenses, express or implied,
 * are granted by Apple herein, including but not limited to any patent rights that
 * may be infringed by your derivative works or by other works in which the Apple
 * Software may be incorporated.
 *
 * The Apple Software is provided by Apple on an "AS IS" basis.  APPLE MAKES NO
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
 * WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND OPERATION ALONE OR IN
 * COMBINATION WITH YOUR PRODUCTS.
 *
 * IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION AND/OR DISTRIBUTION
 * OF THE APPLE SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER THEORY OF CONTRACT, TORT
 * (INCLUDING NEGLIGENCE), STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import javax.swing.*;

import com.apple.dnssd.*;


/**
 * Use this to schedule BrowseListener callbacks via SwingUtilities.invokeAndWait().
 */

public class SwingBrowseListener implements Runnable, BrowseListener {
    /**
     * Create a listener for DNSSD that will call your listener on the Swing/AWT event thread.
     */
    public SwingBrowseListener(BrowseListener listener) {
        fListener = listener;
        fErrorCode = 0;
    }

    /**
     * (Clients should not call this method directly.)
     */
    public void operationFailed(DNSSDService service, int errorCode) {
        fBrowser = service;
        fErrorCode = errorCode;
        this.schedule();
    }

    /**
     * (Clients should not call this method directly.)
     */
    public void serviceFound(DNSSDService browser, int flags, int ifIndex,
                             String serviceName, String regType, String domain) {
        fBrowser = browser;
        fIsAdd = true;
        fFlags = flags;
        fIndex = ifIndex;
        fService = serviceName;
        fRegType = regType;
        fDomain = domain;
        this.schedule();
    }

    /**
     * (Clients should not call this method directly.)
     */
    public void serviceLost(DNSSDService browser, int flags, int ifIndex,
                            String serviceName, String regType, String domain) {
        fBrowser = browser;
        fIsAdd = false;
        fFlags = flags;
        fIndex = ifIndex;
        fService = serviceName;
        fRegType = regType;
        fDomain = domain;
        this.schedule();
    }

    /**
     * (Clients should not call this method directly.)
     */
    public void run() {
        if (fErrorCode != 0)
            fListener.operationFailed(fBrowser, fErrorCode);
        else if (fIsAdd)
            fListener.serviceFound(fBrowser, fFlags, fIndex, fService, fRegType, fDomain);
        else
            fListener.serviceLost(fBrowser, fFlags, fIndex, fService, fRegType, fDomain);
    }

    protected void schedule() {
        try {
            SwingUtilities.invokeAndWait(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected BrowseListener fListener;

    protected boolean fIsAdd;
    protected DNSSDService fBrowser;
    protected int fFlags;
    protected int fIndex;
    protected int fErrorCode;
    protected String fService;
    protected String fRegType;
    protected String fDomain;
}

