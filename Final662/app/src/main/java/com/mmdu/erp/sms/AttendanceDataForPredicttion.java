package com.mmdu.erp.sms;

/**
 * Created by Tushar on 06/10/2018.
 */

public class AttendanceDataForPredicttion {

    String name,total,present;

    String required,leaves;

    public String getRequired() {
        return required;
    }


    public String getLeaves() {
        return leaves;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public AttendanceDataForPredicttion(String name, String required, String leaves) {
        this.name = name;
        this.required = required;
        this.leaves = leaves;
    }

    public AttendanceDataForPredicttion(String name, String total, String present, String leaves) {

        this.name = name;

        Double req = ((0.75*new Double(leaves)) - ((0.25 * new Double(present))))/0.25;
        if(req < 1)
            this.required= "0";
        else
            this.required= (new Integer(new Double(Math.ceil(req)).intValue())).toString();

        Double lev = ((0.75*new Double(leaves)) - ((0.25 * new Double(present))))/-0.75;
        if(lev < 1)
            this.leaves= "0";
        else
            this.leaves= (new Integer(new Double(Math.floor(lev)).intValue())).toString();
    }
}
