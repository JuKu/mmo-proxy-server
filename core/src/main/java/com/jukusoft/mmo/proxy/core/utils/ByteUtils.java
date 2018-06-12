package com.jukusoft.mmo.proxy.core.utils;

public class ByteUtils {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    protected ByteUtils () {
        //
    }

    /**
    * converts byte array to hex string
     *
     * @param bytes array of bytes to convert
     *
     * @author https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
     *
     * @return hex string
    */
    public static String bytesToHex (byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    public static String byteToHex (byte type) {
        byte[] bytes = new byte[] { type };

        return bytesToHex(bytes);
    }

    public static int byteToUnsignedInt (byte type) {
        return type & 0xFF;
    }

    /**
     * combines 2 integers to 1 long for optimization of databases and so on.
     *
     * @param x integer1
     * @param y integer2
     *
     * @return long
     */
    public static long getLongFromIntegers(int x, int y) {
        /*
         * You can save numbers from -2. 147. 483. 648 to 2. 147. 483. 648 in 1
         * integer A long is combined with 2 integers
         */

        return (((long) x) << 32) | (y & 0xffffffffL);
    }

    /**
     * If 2 integers are combined to 1 long, this method can return the first
     * integer
     *
     * @param l
     *            long
     *
     * @return long
     */
    public static int getFirstIntegerFromLong(long l)  {
        return (int) (l >> 32);
    }

    /**
     * If 2 integers are combined to 1 long, this method can return the second
     * integer
     *
     * @param l
     *            long
     *
     * @return integer
     */
    public static int getSecondIntegerFromLong(long l)  {
        return (int) l;
    }

}
