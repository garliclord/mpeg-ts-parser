package com.garliclord.spalk;

public class Main {
    public static void main(String[] args) {

        //cat test_success.ts | java -classpath .\target\classes com.garliclord.spalk.Main

        MpegTsParser parser = new MpegTsParser();
        Result result = parser.parse(System.in);

        if(result.isSuccessful()){
            for(int pid : result.pids()) {
                System.out.println(pid);
            }
            System.exit(0);
        } else {
            System.out.printf("Error: No sync byte present in packet %d, offset %d", result.errorPacketIndex(), result.errorByteOffset());
            System.exit(1);
        }
    }
}