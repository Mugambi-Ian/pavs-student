package com.nenecorp.pavsstudent.Utility.Resources;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class NCTimeDate {
    public static String getTimeStampUtc() {
        return DateTime.now(DateTimeZone.UTC).toString(ISODateTimeFormat.dateTime());
    }

    public static String getTimeStampUtc(long instant) {
        return ISODateTimeFormat.dateTime().withZoneUTC().print(instant);
    }
}
