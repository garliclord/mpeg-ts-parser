package com.garliclord.spalk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class MpegTsParser {
    static final byte SYNC_BYTE = 0x47;
    static final int PACKET_LENGTH = 188;
    int errorPacketIndex = -1;
    int errorByteOffset = -1;

    public Result parse(InputStream input) {
        try (BufferedInputStream bin = new BufferedInputStream(input)) {

            int temp;
            do {
                temp = bin.read();
//                System.out.println(temp);
                if (temp == SYNC_BYTE) {
                    bin.mark(PACKET_LENGTH);
                    bin.skip(PACKET_LENGTH - 1);
                    temp = bin.read();
                    if (temp == SYNC_BYTE) {
                        //then break out
                        bin.reset();
                    }

                    // mark this position then reset back to it if another syncbyte found in 188 bytes
                    //now we know this is a true start of a packet
                    //then parse out the pid from 2nd and 3rd bytes, skip to 188th byte then repeat
                }
            } while ((temp != -1 && temp != SYNC_BYTE));


            // found start of first complete packet
            int start = temp;
            byte[] pidContainer = new byte[2];
            List<Integer> pids = new ArrayList<>();
            int packetCount = 0;

            //foreach packet get pid
            //assume we start with syncbyte
            if (start == SYNC_BYTE) {
                while (temp != -1) {
                    temp = bin.read(pidContainer);

                        pids.add(twoBytesToPid(pidContainer));
                        bin.skip(PACKET_LENGTH - 3);
                        packetCount ++;
                        if (bin.read() != SYNC_BYTE){
                            return new Result(false, List.of(), packetCount, errorByteOffset);
                            //TODO need to keep count of byteoffset
                        };
                }
            } else {
                System.out.println("input doesn't have a syncbyte??");
            }

            //output result
            return new Result(true, pids, -1, -1);

        } catch (IOException e) {
            return new Result(false, List.of(), errorPacketIndex, errorByteOffset);
        }
    }

    private int twoBytesToPid(byte[] twoBytes) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(twoBytes[0]);
        bb.put(twoBytes[1]);
        short shortVal = bb.getShort(0);

        //Get last 5 bits of the first byte and all 8 bits of the second
        int mask = Integer.parseUnsignedInt("000111111111111", 2);
        return shortVal & mask;
    }
}
