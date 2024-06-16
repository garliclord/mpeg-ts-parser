package com.garliclord.spalk;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.garliclord.spalk.MpegTsParser.*;
import static org.junit.jupiter.api.Assertions.*;

class MpegTsParserTest {

    @Test @Disabled
    void parseSuccess() throws IOException {

        MpegTsParser parser = new MpegTsParser();
        FileInputStream fileInput = new FileInputStream("src/test/resources/test_success.ts");

        Result result = parser.parse(fileInput);

        assertTrue(result.isSuccessful());
        //TODO need to find expected results
//        assertEquals(List.of(0x0, 0x11, 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x1fff), result.pids());
    }

    @Test @Disabled
    void parseFailure() throws IOException {

        MpegTsParser parser = new MpegTsParser();
        FileInputStream fileInput = new FileInputStream("src/test/resources/test_failure.ts");

        Result result = parser.parse(fileInput);

        assertFalse(result.isSuccessful());
        //TODO need to find expected results
//        assertEquals(20535, result.errorPacketIndex());
//        assertEquals(3860580, result.errorByteOffset());
    }

    @Test
    void parseSuccessTest() throws IOException {

        MpegTsParser parser = new MpegTsParser();

        byte[] bytePairA = new byte[]{0x1f, 0x2d};
        byte[] bytePairB = new byte[]{0x23, 0x00};
        byte[] bytePairC = new byte[]{0x00, 0x00};

        ArrayList<Byte> list = new ArrayList<>();
        list.add(SYNC_BYTE);
        list.addAll(List.of(bytePairA[0], bytePairA[1]));
        list.addAll(Collections.nCopies(PACKET_LENGTH - 3, (byte) 0x00));
        list.add(SYNC_BYTE);
        list.addAll(List.of(bytePairB[0], bytePairB[1]));
        list.addAll(Collections.nCopies(PACKET_LENGTH - 3, (byte) 0x00));
        list.add(SYNC_BYTE);
        list.addAll(List.of(bytePairC[0], bytePairC[1]));
        list.addAll(Collections.nCopies(PACKET_LENGTH - 3, (byte) 0x00));


        byte[] array = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) array[i] = list.get(i);

        InputStream targetStream = new ByteArrayInputStream(array);

        Result result = parser.parse(targetStream);

        int pidA = twoBytesToPid(bytePairA);
        int pidB = twoBytesToPid(bytePairB);
        int pidC = twoBytesToPid(bytePairC);

        assertTrue(result.isSuccessful());
        assertEquals(List.of(pidC, pidB, pidA), result.pids());
    }

    @Test
    void parseFailureTest() throws IOException {

        MpegTsParser parser = new MpegTsParser();

        byte[] bytePairA = new byte[]{0x1f, 0x2d};
        byte[] bytePairB = new byte[]{0x23, 0x00};
        byte[] bytePairC = new byte[]{0x00, 0x00};

        byte NON_SYNC_BYTE = 0x34;

        ArrayList<Byte> list = new ArrayList<>();
        list.add(SYNC_BYTE);
        list.addAll(List.of(bytePairA[0], bytePairA[1]));
        list.addAll(Collections.nCopies(PACKET_LENGTH - 3, (byte) 0x00));
        list.add(NON_SYNC_BYTE);
        list.addAll(List.of(bytePairB[0], bytePairB[1]));
        list.addAll(Collections.nCopies(PACKET_LENGTH - 3, (byte) 0x00));
        list.add(SYNC_BYTE);
        list.addAll(List.of(bytePairC[0], bytePairC[1]));
        list.addAll(Collections.nCopies(PACKET_LENGTH - 3, (byte) 0x00));


        byte[] array = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) array[i] = list.get(i);

        InputStream targetStream = new ByteArrayInputStream(array);

        Result result = parser.parse(targetStream);

        assertFalse(result.isSuccessful());
        assertEquals(1, result.errorPacketIndex());
        assertEquals(189, result.errorByteOffset());
    }
}