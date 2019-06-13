package com.mmdu.erp.sms;

/**
 * Created by Tushar on 12/09/2018.
 */

public class AttendanceData {

    String name;
    String present;
    String absent;

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public AttendanceData(String name, String present, String absent, String percentage) {

        this.name = name;
        this.present = present;
        this.absent = absent;
        this.percentage = percentage;
    }

    String percentage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getAbsent() {
        return absent;
    }

    public void setAbsent(String absent) {
        this.absent = absent;
    }
}
