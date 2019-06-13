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
 * Created by Tushar on 06/10/2018.
 */

public class AttendanceAdapterForPrediction extends RecyclerView.Adapter<AttendanceAdapterForPrediction.AttendanceViewHolder> {

    private Context mctx;
    private List<AttendanceDataForPredicttion> attendanceDataForPredicttionList;

    public AttendanceAdapterForPrediction(Context mctx, List<AttendanceDataForPredicttion> attendanceDataForPredicttionList) {
        this.mctx = mctx;
        this.attendanceDataForPredicttionList = attendanceDataForPredicttionList;
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_layout_prediction,parent,false);
        AttendanceViewHolder holder = new AttendanceViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {

        AttendanceDataForPredicttion attendanceDataForPredicttion = attendanceDataForPredicttionList.get(position);
        holder.subject.setText(attendanceDataForPredicttion.getName().toString());
        holder.required.setText(attendanceDataForPredicttion.getRequired().toString());
        holder.leaves.setText(attendanceDataForPredicttion.getLeaves().toString());

        if(attendanceDataForPredicttion.getName().toString().equals("Subject"))
        {
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.required.setTypeface(null, Typeface.BOLD);
            holder.leaves.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public int getItemCount() {
        return attendanceDataForPredicttionList.size();
    }

    class AttendanceViewHolder extends  RecyclerView.ViewHolder{

        TextView subject,required,leaves;

        public AttendanceViewHolder(View itemView) {
            super(itemView);

            subject = (TextView) itemView.findViewById(R.id.subject);
            required = (TextView) itemView.findViewById(R.id.required);
            leaves = (TextView) itemView.findViewById(R.id.leaves);

        }
    }
}