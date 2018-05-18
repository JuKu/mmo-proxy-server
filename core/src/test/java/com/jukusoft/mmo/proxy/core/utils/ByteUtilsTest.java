package com.jukusoft.mmo.proxy.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteUtilsTest {

    @Test
    public void testConstructor () {
        new ByteUtils();
    }

    @Test
    public void testBytesToHex () {
        assertEquals("0102", ByteUtils.bytesToHex(new byte[]{ 0x01, 0x02 }));
        assertEquals("CDEF", ByteUtils.bytesToHex(new byte[]{ (byte) 0xcd, (byte) 0xef }));
    }

    @Test
    public void testByteToHex () {
        assertEquals("00", ByteUtils.byteToHex((byte) 0x00));
        assertEquals("01", ByteUtils.byteToHex((byte) 0x01));
        assertEquals("02", ByteUtils.byteToHex((byte) 0x02));

        assertEquals("0A", ByteUtils.byteToHex((byte) 0x0A));
        assertEquals("EE", ByteUtils.byteToHex((byte) 0xEE));

        assertEquals("FF", ByteUtils.byteToHex((byte) 0xFF));
    }

}
