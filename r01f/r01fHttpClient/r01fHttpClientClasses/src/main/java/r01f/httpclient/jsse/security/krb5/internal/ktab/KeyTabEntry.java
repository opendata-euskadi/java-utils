/*
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

package r01f.httpclient.jsse.security.krb5.internal.ktab;

import r01f.httpclient.jsse.security.krb5.*;
import r01f.httpclient.jsse.security.krb5.internal.*;

import java.io.UnsupportedEncodingException;

/**
 * This class represents a Key Table entry. Each entry contains the service principal of
 * the key, time stamp, key version and secret key itself.
 *
 * @author Yanni Zhang
 */
public class KeyTabEntry implements KeyTabConstants {
    PrincipalName service;
    Realm realm;
    KerberosTime timestamp;
    int keyVersion;
    int keyType;
    byte[] keyblock = null;
    boolean DEBUG = Krb5.DEBUG;

    public KeyTabEntry (PrincipalName new_service, Realm new_realm, KerberosTime new_time,
                        int new_keyVersion, int new_keyType, byte[] new_keyblock) {
        service = new_service;
        realm = new_realm;
        timestamp = new_time;
        keyVersion = new_keyVersion;
        keyType = new_keyType;
        if (new_keyblock != null) {
            keyblock = new_keyblock.clone();
        }
    }

    public PrincipalName getService() {
        return service;
    }

    public EncryptionKey getKey() {
        EncryptionKey key = new EncryptionKey(keyblock,
                                              keyType,
                                              Integer.valueOf(keyVersion));
        return key;
    }

    public String getKeyString() {
        StringBuffer sb = new StringBuffer("0x");
        for (int i = 0; i < keyblock.length; i++) {
            sb.append(Integer.toHexString(keyblock[i]&0xff));
        }
        return sb.toString();
    }
    public int entryLength() {
        int totalPrincipalLength = 0;
        String[] names = service.getNameStrings();
        for (int i = 0; i < names.length; i++) {
            try {
                totalPrincipalLength += principalSize + names[i].getBytes("8859_1").length;
            } catch (UnsupportedEncodingException exc) {
            }
        }

        int realmLen = 0;
        try {
            realmLen = realm.toString().getBytes("8859_1").length;
        } catch (UnsupportedEncodingException exc) {
        }

        int size = principalComponentSize +  realmSize + realmLen
            + totalPrincipalLength + principalTypeSize
            + timestampSize + keyVersionSize
            + keyTypeSize + keySize + keyblock.length;

        if (DEBUG) {
            System.out.println(">>> KeyTabEntry: key tab entry size is " + size);
        }
        return size;
    }

    public KerberosTime getTimeStamp() {
        return timestamp;
    }
}
