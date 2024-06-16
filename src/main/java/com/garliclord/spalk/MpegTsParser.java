package com.garliclord.spalk;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MpegTsParser {
    public static final byte SYNC_BYTE = 0x47;
    public static final int PACKET_LENGTH = 188;
    public static final String PID_MASK = "000111111111111";

    public Result parse(InputStream input) throws IOException {
        try (PositionalBufferedInputStream bin = new PositionalBufferedInputStream(input)) {
            int start = goToStartOfCompletePacket(bin);

            if (start != SYNC_BYTE) {
                // no sync byte found
                //TODO log it
                return new Result(false, List.of(), -1, -1);
            }

            List<Integer> pids = new ArrayList<>();
            long packetCount = 0;

            int temp = 0;
            while (temp != -1) {
                byte[] pidContainer = new byte[2];
                bin.read(pidContainer);
                pids.add(twoBytesToPid(pidContainer));
                bin.skip(PACKET_LENGTH - 3);
                packetCount++;
                temp = bin.read();
                if (temp != SYNC_BYTE && temp != -1) {
                    //TODO log it
                    return new Result(false, List.of(), packetCount, bin.getPosition());
                }
            }

            pids.sort(Integer::compareTo);

            return new Result(true, pids, -1, -1);

        }
    }

    private static int goToStartOfCompletePacket(PositionalBufferedInputStream bin) throws IOException {
        int temp = bin.read();
        do {
            if (temp == SYNC_BYTE) {
                bin.mark(PACKET_LENGTH);
                bin.skip(PACKET_LENGTH - 1);
            }
            temp = bin.read();
        } while ((temp != -1 && temp != SYNC_BYTE));
        bin.reset();
        return temp;
    }

    public static int twoBytesToPid(byte[] twoBytes) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(twoBytes[0]);
        bb.put(twoBytes[1]);
        short shortVal = bb.getShort(0);

        //Get last 5 bits of the first byte and all 8 bits of the second
        int mask = Integer.parseUnsignedInt(PID_MASK, 2);
        return shortVal & mask;
    }
}
