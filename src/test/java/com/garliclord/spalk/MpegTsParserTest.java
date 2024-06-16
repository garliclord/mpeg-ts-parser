package com.garliclord.spalk;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MpegTsParserTest {

    @Test
    void parseSuccess() throws FileNotFoundException {

        MpegTsParser parser = new MpegTsParser();
        FileInputStream fileInput = new FileInputStream("src/test/resources/test_success.ts");

        Result result = parser.parse(fileInput);

        assertEquals(List.of(0x0, 0x11, 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x1fff), result.pids());
    }

    @Test
    void parseFailure() throws FileNotFoundException {

        MpegTsParser parser = new MpegTsParser();
        FileInputStream fileInput = new FileInputStream("src/test/resources/test_failure.ts");

        Result result = parser.parse(fileInput);

        assertEquals(20535, result.errorPacketIndex());
        assertEquals(3860580, result.errorByteOffset());
    }
}