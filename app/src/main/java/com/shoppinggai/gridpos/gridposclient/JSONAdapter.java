package com.shoppinggai.gridpos.gridposclient;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class JSONAdapter extends BaseAdapter implements ListAdapter {

    private final Activity activity;
    private final JSONArray jsonArray;

    public JSONAdapter (Activity activity, JSONArray jsonArray) {
        assert activity != null;
        assert jsonArray != null;

        this.jsonArray = jsonArray;
        this.activity = activity;
    }


    @Override public int getCount() {
        if (null==jsonArray)
            return 0;
        else
            return jsonArray.length();
    }

    @Override public JSONObject getItem(int position) {
        if (null==jsonArray) return null;
        else
            return jsonArray.optJSONObject(position);
    }

    @Override public long getItemId(int position) {
        JSONObject jsonObject = getItem(position);

        return jsonObject.optLong("id");
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.col_4_row, null);

        TextView col_1 =(TextView)convertView.findViewById(R.id.col_1);
        TextView col_2 =(TextView)convertView.findViewById(R.id.col_2);
        TextView col_3 =(TextView)convertView.findViewById(R.id.col_3);
        TextView col_4 =(TextView)convertView.findViewById(R.id.col_4);

        JSONObject json_data = getItem(position);

        try {
            if (null != json_data) {
                col_1.setText(json_data.getString("col_1"));
                col_2.setText(json_data.getString("col_2"));
                col_3.setText(json_data.getString("col_3"));
                col_4.setText(json_data.getString("col_4"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
