package com.garliclord.spalk;

import java.util.List;

public record Result(boolean isSuccessful, List<Integer> pids, int errorPacketIndex, int errorByteOffset) {
}
