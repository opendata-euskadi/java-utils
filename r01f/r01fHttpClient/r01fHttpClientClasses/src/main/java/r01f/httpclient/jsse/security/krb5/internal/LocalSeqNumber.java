/*
 * Portions Copyright 2000-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/*
 *  (C) Copyright IBM Corp. 1999 All Rights Reserved.
 *  Copyright 1997 The Open Group Research Institute.  All rights reserved.
 */

package r01f.httpclient.jsse.security.krb5.internal;

import r01f.httpclient.jsse.security.krb5.Confounder;

public class LocalSeqNumber implements SeqNumber {
    private int lastSeqNumber;

    public LocalSeqNumber() {
        randInit();
    }

    public LocalSeqNumber(int start) {
        init(start);
    }

    public LocalSeqNumber(Integer start) {
        init(start.intValue());
    }

    public synchronized void randInit() {
        /*
         * Sequence numbers fall in the range 0 through 2^32 - 1 and wrap
         * to zero following the value 2^32 - 1.
         * Previous implementations used signed sequence numbers.
         * Workaround implementation incompatibilities by not generating
         * initial sequence numbers greater than 2^30, as done
         * in MIT distribution.
         */
        // get the random confounder
        byte[] data = Confounder.bytes(4);
        data[0] = (byte)(data[0] & 0x3f);
        int result = ((data[3] & 0xff) |
                        ((data[2] & 0xff) << 8) |
                        ((data[1] & 0xff) << 16) |
                        ((data[0] & 0xff) << 24));
        if (result == 0) {
           result = 1;
        }
        lastSeqNumber = result;
    }

    public synchronized void init(int start) {
        lastSeqNumber = start;
    }

    public synchronized int current() {
        return lastSeqNumber;
    }

    public synchronized int next() {
        return lastSeqNumber + 1;
    }

    public synchronized int step() {
        return ++lastSeqNumber;
    }

}
