package com.garliclord.spalk;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        MpegTsParser parser = new MpegTsParser();
        Result result = null;
        try {
            result = parser.parse(System.in);
        } catch (IOException e) {
            System.out.printf("Error: %s", e.getMessage());
            System.exit(1);
        }

        if(result.isSuccessful()){
            for(int pid : result.pids()) {
                System.out.println(pid);
            }
            System.exit(0);
        } else if(result.errorPacketIndex() == -1 && result.errorByteOffset() == -1) {
            System.out.print("Error: No sync byte present");
            System.exit(1);
        } else {
            System.out.printf("Error: No sync byte present in packet %d, offset %d", result.errorPacketIndex(), result.errorByteOffset());
            System.exit(1);
        }
    }
}