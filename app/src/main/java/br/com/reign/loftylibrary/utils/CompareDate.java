package br.com.reign.loftylibrary.utils;

import android.os.Build;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;

import br.com.reign.loftylibrary.model.Chapter;

public class CompareDate implements Comparator<Chapter> {
    private static Instant instant;
    private static ZonedDateTime zdt;
    private static LocalDate m = null;
    private static LocalDate today = null;

    @Override
    public int compare(Chapter chapter1, Chapter chapter2) {
        Long date1 = chapter1.getDate();
        Long date2 = chapter2.getDate();

        return date2.compareTo(date1);
    }

    public static Boolean upToThreeDays(Date externalDate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant = externalDate.toInstant();
            zdt = instant.atZone(ZoneId.systemDefault());
            m = zdt.toLocalDate();
            today = LocalDate.now();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(today.equals(m)) {
                return true;
            } else if(today.minusDays(1).equals(m)) {
                return true;
            } else if(today.minusDays(2).equals(m)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean upToOneDay(Date externalDate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instant = externalDate.toInstant();
            zdt = instant.atZone(ZoneId.systemDefault());
            m = zdt.toLocalDate();
            today = LocalDate.now();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(today.equals(m)) {
                return true;
            }
        }
        return false;
    }
}
