/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.client.impl.parser;
/*
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

import org.jdiameter.api.*;
import org.jdiameter.client.api.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.*;

/**
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
class AvpSetImpl implements AvpSet {

  // FIXME: by default 3588.4-1 says: 'M' should be set to true;
  // FIXME: by default 3588.x says: if grouped has at least on AVP with 'M' set, it also has to have 'M' set! - TODO: add backmapping.

  private static final Logger logger = LoggerFactory.getLogger(AvpSetImpl.class);
  
  private static final long serialVersionUID = 1L;
  private static final ElementParser parser = new ElementParser();

    List<Avp> avps = new ArrayList<Avp>();

    AvpSetImpl() {

    }

    public Avp getAvp(int avpCode) {
        for (Avp avp : this.avps) {
            if (avp.getCode() == avpCode) {
                return avp;
            }
        }
        return null;
    }

    public Avp getAvpByIndex(int avpIndex) {
        return this.avps.get(avpIndex);
    }

    public Avp getAvp(int avpCode, long vendorId) {
        for (Avp avp : this.avps) {
            if (avp.getCode() == avpCode && avp.getVendorId() == vendorId) {
              return avp;
            }
        }
        return null;
    }

    public AvpSet getAvps(int avpCode) {
        AvpSet result = new AvpSetImpl();
        for (Avp avp : this.avps) {
            if (avp.getCode() == avpCode) {
                result.addAvp(avp);
            }
        }
        return result;
    }



    public AvpSet getAvps(int avpCode, long vendorId) {
        AvpSet result = new AvpSetImpl();
        for (Avp avp : this.avps) {
            if (avp.getCode() == avpCode && avp.getVendorId() == vendorId) {
              result.addAvp(avp);
            }
        }
        return result;
    }

    public AvpSet removeAvp(int avpCode) {
        return removeAvp(avpCode, 0);
    }
    
    public AvpSet removeAvp(int avpCode, long vendorId) {
        AvpSet result = new AvpSetImpl();
      //  for (Avp avp : this.avps) {
      //    if (avp.getCode() == avpCode) {
      //      result.addAvp(avp);
      //      this.avps.remove(avp);
      //    }
      //  }
        Iterator<Avp> it = this.avps.iterator();
        while(it.hasNext()) {
          Avp avp = it.next();
          if (avp.getCode() == avpCode && avp.getVendorId() == vendorId) {
            result.addAvp(avp);
            it.remove();
          }
        }
        return result;
      }

    public Avp removeAvpByIndex(int i) {
        return (i >= this.avps.size()) ? null : this.avps.remove(i);
    }

    public Avp[] asArray() {
        return this.avps.toArray(new Avp[avps.size()]);
    }

    public Avp addAvp(int avpCode, long value, boolean asUnsigned) {
        Avp res = new AvpImpl(avpCode, 0, 0, asUnsigned ? parser.intU32ToBytes(value) : parser.int64ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value, boolean mFlag, boolean pFlag, boolean asUnsigned) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags , 0, asUnsigned ? parser.intU32ToBytes(value) : parser.int64ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value, long vndId, boolean mFlag, boolean pFlag, boolean asUnsigned) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, asUnsigned ? parser.intU32ToBytes(value) : parser.int64ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public void insertAvp(int index, Avp... avps) {
        this.avps.addAll(index, Arrays.asList(avps));
    }

    public void insertAvp(int index, AvpSet avpSet) {
        this.avps.addAll(index, Arrays.asList(avpSet.asArray()));
    }

    public Avp insertAvp(int index, int avpCode, long value, boolean asUnsigned) {
        Avp res = new AvpImpl(avpCode, 0, 0, asUnsigned ? parser.intU32ToBytes(value) : parser.int64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value, boolean mFlag, boolean pFlag, boolean asUnsigned) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags , 0, asUnsigned ? parser.intU32ToBytes(value) : parser.int64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value, long vndId, boolean mFlag, boolean pFlag, boolean asUnsigned) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, asUnsigned ? parser.intU32ToBytes(value) : parser.int64ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public AvpSet insertGroupedAvp(int index, int avpCode) {
        AvpImpl res = new AvpImpl(avpCode, 0, 0, null);
        this.avps.add(index, res);
        
        try {
            return res.getGrouped();
        } catch (AvpDataException e) {
            logger.error("insert avp failed", e);
        }
        return null;
    }

    public int size() {
        return this.avps.size();
    }

    public void addAvp(AvpSet avpSet) {
        for (Avp a:avpSet) avps.add(a);
    }

    public void addAvp(Avp... avps) {
        for (Avp a : avps) {
          // No need to clone AVP, right?
          // Avp res = new AvpImpl(a);
          if(a != null) {
            this.avps.add(a);
          }
        }
    }

    public Avp addAvp(int avpCode, byte[] rawData) {
        Avp res = new AvpImpl(avpCode, 0, 0, rawData);
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, byte[] rawData, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags , 0, rawData);
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, byte[] rawData, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, rawData);
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, int value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.int32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, int value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.int32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, int value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.int32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.int64ToBytes(value) );
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.int64ToBytes(value) );
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.int64ToBytes(value) );
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, float value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.float32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, float value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.float32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, float value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
         Avp res = new AvpImpl(avpCode, flags, vndId, parser.float32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, double value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.float64ToBytes(value));
        this.avps.add(res);
        return res;

    }

    public Avp addAvp(int avpCode, double value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.float64ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, double value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.float64ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, String value, boolean asOctetString) {
        try {
            Avp res = new AvpImpl(avpCode, 0, 0, asOctetString ? parser.octetStringToBytes(value) : parser.utf8StringToBytes(value)
            );
            this.avps.add(res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, String value, boolean mFlag, boolean pFlag,  boolean asOctetString) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(avpCode, flags, 0, asOctetString ? parser.octetStringToBytes(value) : parser.utf8StringToBytes(value)
            );
            this.avps.add(res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, String value, long vndId, boolean mFlag, boolean pFlag,  boolean asOctetString) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(avpCode, flags, vndId, asOctetString ? parser.octetStringToBytes(value) : parser.utf8StringToBytes(value)
            );
            this.avps.add(res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, URI value) {
        try {
            Avp res = new AvpImpl(avpCode, 0, 0, parser.octetStringToBytes(value.toString()));
            this.avps.add(res);
            return res;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, URI value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(avpCode, flags, 0, parser.octetStringToBytes(value.toString()));
            this.avps.add(res);
            return res;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, URI value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(avpCode, flags, vndId, parser.octetStringToBytes(value.toString()));
            this.avps.add(res);
            return res;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, InetAddress value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.addressToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, InetAddress value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.addressToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, InetAddress value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.addressToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, Date value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.dateToBytes(value));
        this.avps.add(res);
        return res;

    }

    public Avp addAvp(int avpCode, Date value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.dateToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, Date value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.dateToBytes(value));
        this.avps.add(res);
        return res;
    }

    public AvpSet addGroupedAvp(int avpCode) {
        AvpImpl res = new AvpImpl(avpCode, 0, 0, null);
        this.avps.add(res);
        
        try {
            return res.getGrouped();
        } catch (AvpDataException e) {
            logger.error("add avp failed", e);
        }
        return null;
    }

    public AvpSet addGroupedAvp(int avpCode, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        AvpImpl res = new AvpImpl(avpCode, flags, 0, null);
        this.avps.add(res);
        
        try {
            return res.getGrouped();
        } catch (AvpDataException e) {
            logger.error("add avp failed", e);
        }
        return null;
    }

    public AvpSet addGroupedAvp(int avpCode, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        AvpImpl res = new AvpImpl(avpCode, flags, vndId, null);
        this.avps.add(res);
        
        try {
            return res.getGrouped();
        } catch (AvpDataException e) {
            logger.error("add avp failed", e);
        }
        return null;
    }

    public Avp insertAvp(int index, int avpCode, byte[] value) {
        Avp res = new AvpImpl(avpCode, 0, 0, value);
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, byte[] value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, value);
        this.avps.add(index, res);
        return res;

    }

    public Avp insertAvp(int index, int avpCode, byte[] value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, value);
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, int value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.int32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, int value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.int32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, int value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.int32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.int64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.int64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.int64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, float value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.float32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, float value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.float32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, float value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.float32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, double value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.float64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, double value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.float64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, double value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.float64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, String value, boolean asOctetString) {
        try {
            Avp res = new AvpImpl(avpCode, 0, 0, asOctetString ? parser.octetStringToBytes(value) :
            parser.utf8StringToBytes(value));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, String value, boolean mFlag, boolean pFlag, boolean asOctetString) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(avpCode, flags, 0, asOctetString ? parser.octetStringToBytes(value) :
            parser.utf8StringToBytes(value));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    public Avp insertAvp(int index, int avpCode, String value, long vndId, boolean mFlag, boolean pFlag, boolean asOctetString) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(avpCode, flags, vndId, asOctetString ? parser.octetStringToBytes(value) :
            parser.utf8StringToBytes(value));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, URI value) {
        try {
            Avp res = new AvpImpl(avpCode, 0, 0, parser.octetStringToBytes(value.toString()));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, URI value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(avpCode, flags, 0, parser.octetStringToBytes(value.toString()));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, URI value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(avpCode, flags, vndId, parser.octetStringToBytes(value.toString()));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, InetAddress value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.addressToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, InetAddress value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.addressToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, InetAddress value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.addressToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, Date value) {
        Avp res = new AvpImpl(avpCode, 0, 0, parser.dateToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, Date value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, 0, parser.dateToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, Date value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(avpCode, flags, vndId, parser.dateToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public AvpSet insertGroupedAvp(int index, int avpCode, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        AvpImpl res = new AvpImpl(avpCode, flags, 0, null);
        this.avps.add(index, res);

        try {
            return res.getGrouped();
        } catch (AvpDataException e) {
            logger.error("insert avp failed", e);
        }
        return null;
    }

    public AvpSet insertGroupedAvp(int index, int avpCode, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        AvpImpl res = new AvpImpl(avpCode, flags, vndId, null);
        this.avps.add(index, res);
        
        try {
            return res.getGrouped();
        } catch (AvpDataException e) {
            logger.error("insert avp failed", e);
        }
        return null;
    }

    public boolean isWrapperFor(Class<?> aClass) throws InternalException {
        return false;
    }

    public <T> T unwrap(Class<T> aClass) throws InternalException {
        return null;
    }

    public Iterator<Avp> iterator() {
      // Iterator contract demands it to be able to remove items
      // return Collections.unmodifiableList(this.avps).iterator();
      return this.avps.iterator();
    }

    @Override
    public String toString() {
      return new StringBuffer("AvpSetImpl [avps=").append(avps).append("]@").append(super.hashCode()).toString();
    }
}
