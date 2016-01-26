package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import junit.framework.TestCase;

/**
 * Created by mn-io on 25.01.2016.
 */
public class NfcMessageSendHandlerTest extends TestCase {

    public void testGetBytesFromBuffer() throws Exception {
        final NfcMessageSendHandler service = new NfcMessageSendHandler();

        service.byteBuffer = "Hello World".getBytes();

        byte[] bytesFromBuffer = service.getBytesFromBuffer(5);
        assertEquals(bytesFromBuffer.length, "Hello".getBytes().length);

        bytesFromBuffer = service.getBytesFromBuffer(5);
        assertEquals(bytesFromBuffer.length, " Worl".getBytes().length);

        bytesFromBuffer = service.getBytesFromBuffer(5);
        assertEquals(bytesFromBuffer.length, "d".getBytes().length);

        bytesFromBuffer = service.getBytesFromBuffer(5);
        assertNull(bytesFromBuffer);


        service.byteBuffer = "m".getBytes();

        bytesFromBuffer = service.getBytesFromBuffer(5);
        assertEquals(bytesFromBuffer.length, "m".getBytes().length);

        bytesFromBuffer = service.getBytesFromBuffer(5);
        assertNull(bytesFromBuffer);


        service.byteBuffer = "".getBytes();

        bytesFromBuffer = service.getBytesFromBuffer(5);
        assertNull(bytesFromBuffer);


        service.byteBuffer = null;

        bytesFromBuffer = service.getBytesFromBuffer(5);
        assertNull(bytesFromBuffer);
    }
}