package com.arafat.Pouroshova.complain_info.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.arafat.complainbox.R;

import java.util.ArrayList;

public class ComplainListAdapter extends ArrayAdapter {

    Context context;
    ArrayList<String> typeIDList =new ArrayList<>();
    ArrayList<String> desList =new ArrayList<>();
    ArrayList<String> dateList =new ArrayList<>();
    ArrayList<String> latList =new ArrayList<>();
    ArrayList<String> langList =new ArrayList<>();
    ArrayList<String> remList =new ArrayList<>();
    ArrayList<String> image_pathList =new ArrayList<>();


    public ComplainListAdapter( Context context,ArrayList<String> typeIDList,ArrayList<String> desList,ArrayList<String> dateList,ArrayList<String> latList,ArrayList<String> langList,ArrayList<String> remList,ArrayList<String> image_pathList) {
        super(context, R.layout.list_each_row_m_log,typeIDList);

        this.context = context;
        this.typeIDList = typeIDList;
        this.desList = desList;
        this.dateList = dateList;
        this.latList = latList;
        this.langList = langList;
        this.remList = remList;
        this.image_pathList = image_pathList;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_each_row_m_log, parent, false);
        }

        TextView tvId =convertView.findViewById(R.id.tvId);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvSub = convertView.findViewById(R.id.tvSub);
        Button btnShow = convertView.findViewById(R.id.btnShow);

        tvId.setText((position+1)+"");
        tvDate.setText(dateList.get(position));

        if(typeIDList.get(position).equals("1")){

            tvSub.setText("বাড়িঘর");

        }else if(typeIDList.get(position).equals("2")){

            tvSub.setText("স্ট্রাকচারাল");

        }else if(typeIDList.get(position).equals("3")){

            tvSub.setText("পানি");

        }else if(typeIDList.get(position).equals("4")){

            tvSub.setText("রাস্তা");

        }else if(typeIDList.get(position).equals("5")){

            tvSub.setText("ব্রিজ");

        }else if(typeIDList.get(position).equals("6")){

            tvSub.setText("টার্মিনাল");

        }else if(typeIDList.get(position).equals("7")){
            tvSub.setText("অন্যান্য");
        }


        return convertView;
    }
}
