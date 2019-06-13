package com.mmdu.erp.sms;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tushar on 12/09/2018.
 */

public class AttendanceAdapterForCalendar extends RecyclerView.Adapter<AttendanceAdapterForCalendar.AttendanceViewHolder> {

    private Context mctx;
    private List<Lectures> attendanceDataList;

    public AttendanceAdapterForCalendar(Context mctx, List<Lectures> attendanceDataList) {
        this.mctx = mctx;
        this.attendanceDataList = attendanceDataList;
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mctx);
        View view = inflater.inflate(R.layout.card_layout_for_calendar_view,null);
        AttendanceViewHolder holder = new AttendanceViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {

        Lectures attendanceData = attendanceDataList.get(position);
        holder.subject.setText(attendanceData.getSubject().toString().substring(2));
        holder.status.setText(attendanceData.getStatus().toString());
        holder.period.setText(attendanceData.getSubject().toString().substring(0,1));
    }

    @Override
    public int getItemCount() {
        return attendanceDataList.size();
    }

    class AttendanceViewHolder extends  RecyclerView.ViewHolder{

        TextView subject,status,period;

        public AttendanceViewHolder(View itemView) {
            super(itemView);

            subject = (TextView) itemView.findViewById(R.id.subject);
            status = (TextView) itemView.findViewById(R.id.status);
            period = (TextView) itemView.findViewById(R.id.period);


        }
    }
}
