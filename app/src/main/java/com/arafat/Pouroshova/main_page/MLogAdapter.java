package com.arafat.Pouroshova.main_page;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.arafat.complainbox.R;

import java.util.ArrayList;

public class MLogAdapter extends ArrayAdapter {

    Context context;
    ArrayList<String>dateArray;
    ArrayList<String>idArray;
    ArrayList<String>subArray;

    public MLogAdapter( Context context, int resource) {
        super(context, resource);
    }


    public MLogAdapter(Context context, ArrayList<String> dateArray, ArrayList<String> idArray, ArrayList<String> subArray) {
        super(context,R.layout.list_each_row_m_log,idArray);

        this.context =context;
        this.dateArray =dateArray;
        this.idArray =idArray;
        this.subArray =subArray;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.list_each_row_m_log, parent, false);

        if(convertView != null){

            TextView tvId =convertView.findViewById(R.id.tvId);
            TextView tvDate =convertView.findViewById(R.id.tvDate);
            TextView tvSub =convertView.findViewById(R.id.tvSub);

            Button btn;

            tvId.setText(idArray.get(position));
            tvDate.setText(dateArray.get(position));
            tvSub.setText(subArray.get(position));
        }



        return  convertView;
    }
}
