/* -*- Mode: Java; tab-width: 4 -*-
 *
 * Copyright (c) 2004 Apple Computer, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.apple.dnssd;


/**
 * A base class for DNSSD listeners.
 */

public interface BaseListener {
    /**
     * Called to report DNSSD operation failures.<P>
     *
     * @param    service The service that encountered the failure.
     * <p>
     * @param    errorCode Indicates the failure that occurred. See {@link DNSSDException} for error codes.
     */
    void operationFailed(DNSSDService service, int errorCode);
}

