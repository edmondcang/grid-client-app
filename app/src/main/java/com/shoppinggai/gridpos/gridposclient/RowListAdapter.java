package com.shoppinggai.gridpos.gridposclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class RowListAdapter extends ArrayAdapter<String> {

    Context context;

    Integer[] ids;
    String[] titles;
    String[] uppers;
    String[] lowers;

    class ViewHolder {

        TextView row_id;
        TextView title;
        TextView upper;
        TextView lower;

        ViewHolder(View v) {
            row_id = (TextView) v.findViewById(R.id.row_id);
            title = (TextView) v.findViewById(R.id.title);
            upper = (TextView) v.findViewById(R.id.upper);
            lower = (TextView) v.findViewById(R.id.lower);
        }
    }

    public RowListAdapter(Context context, Integer[] ids, String[] titles, String[] uppers, String[] lowers) {

        super(context, R.layout.list_row, R.id.title, titles);

        this.context = context;

        this.ids = ids;
        this.titles = titles;
        this.uppers = uppers;
        this.lowers = lowers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.list_row, parent, false);

            holder = new ViewHolder(row);

            row.setTag(holder);
//            Log.d("HOLDER", "Creating a new row");
        } else {
            holder = (ViewHolder) row.getTag();
//            Log.d("HOLDER", "Recycling stuff");
        }

        if (ids != null)
            holder.row_id.setText(ids[position].toString());
        if (titles != null)
            holder.title.setText(titles[position]);
        if (uppers != null)
            holder.upper.setText(uppers[position]);
        if (lowers != null)
            holder.lower.setText(lowers[position]);

//        return super.getView(position, convertView, parent);
        return row;
    }
}