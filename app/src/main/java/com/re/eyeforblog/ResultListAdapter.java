package com.re.eyeforblog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ResultListAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> resultList;

    public ResultListAdapter(Context context, List<String> resultList) {
        super(context, 0, resultList);
        this.context = context;
        this.resultList = resultList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        String listItem = resultList.get(position);

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(listItem);

        return convertView;
    }
}