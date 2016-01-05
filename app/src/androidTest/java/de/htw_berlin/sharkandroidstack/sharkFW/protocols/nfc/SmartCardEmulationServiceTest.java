package de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc;

import junit.framework.TestCase;

public class SmartCardEmulationServiceTest extends TestCase {

    public void testGetBytesFromBuffer() throws Exception {
        final SmartCardEmulationService service = new SmartCardEmulationService();

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