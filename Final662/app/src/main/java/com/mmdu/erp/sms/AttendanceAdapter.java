package com.mmdu.erp.sms;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tushar on 12/09/2018.
 */

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private Context mctx;
    private List<AttendanceData> attendanceDataList;

    public AttendanceAdapter(Context mctx, List<AttendanceData> attendanceDataList) {
        this.mctx = mctx;
        this.attendanceDataList = attendanceDataList;
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mctx);
        View view = inflater.inflate(R.layout.card_layout,parent,false);
        AttendanceViewHolder holder = new AttendanceViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {

        AttendanceData attendanceData = attendanceDataList.get(position);
        holder.subject.setText(attendanceData.getName().toString());
        holder.present.setText(attendanceData.getPresent().toString());
        holder.absent.setText(attendanceData.getAbsent().toString());
        holder.percentage.setText(attendanceData.getPercentage().toString());

        if(attendanceData.getName().toString().equals("Subject"))
        {
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.present.setTypeface(null, Typeface.BOLD);
            holder.absent.setTypeface(null, Typeface.BOLD);
            holder.percentage.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public int getItemCount() {
        return attendanceDataList.size();
    }

    class AttendanceViewHolder extends  RecyclerView.ViewHolder{

        TextView subject,present,absent,percentage;

        public AttendanceViewHolder(View itemView) {
            super(itemView);

            subject = (TextView) itemView.findViewById(R.id.subject);
            present = (TextView) itemView.findViewById(R.id.present);
            absent = (TextView) itemView.findViewById(R.id.absent);
            percentage = (TextView) itemView.findViewById(R.id.percent);

        }
    }
}
