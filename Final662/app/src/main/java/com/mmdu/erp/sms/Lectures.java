package com.mmdu.erp.sms;

/**
 * Created by Tushar on 05/10/2018.
 */

public class Lectures {

    private String Subject;
    private String status;

    public Lectures(String subject, String status)
    {
        this.Subject = subject;
        this.status = status;
    }

    public String getSubject() {
        return Subject;
    }

    public String getStatus() {
        return status;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
