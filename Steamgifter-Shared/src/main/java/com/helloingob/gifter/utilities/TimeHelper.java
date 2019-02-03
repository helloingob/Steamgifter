package com.helloingob.gifter.utilities;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TimeHelper {

    public static Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

}
