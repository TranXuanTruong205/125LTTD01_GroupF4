package com.dinerestaurant.app.ui.reservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.TableItem;

import java.util.List;

public class TableGridAdapter extends BaseAdapter {

    private Context context;
    private List<TableItem> tables;
    private OnTableSelectedListener listener;

    public interface OnTableSelectedListener {
        void onTableSelected(TableItem table);
    }

    public TableGridAdapter(Context context, List<TableItem> tables, OnTableSelectedListener listener) {
        this.context = context;
        this.tables = tables;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return tables.size();
    }

    @Override
    public Object getItem(int position) {
        return tables.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
        }

        TextView tvNumber = convertView.findViewById(R.id.tv_table_number);
        TextView tvCapacity = convertView.findViewById(R.id.tv_capacity);

        TableItem table = tables.get(position);
        tvNumber.setText("Bàn " + table.getTableNumber());
        tvCapacity.setText(table.getCapacity() + " chỗ");

        convertView.setOnClickListener(v -> listener.onTableSelected(table));

        // Đổi màu nếu bàn available
        convertView.setBackgroundColor(table.getStatus().equals("available") ?
                context.getResources().getColor(android.R.color.holo_green_light) :
                context.getResources().getColor(android.R.color.darker_gray));

        return convertView;
    }
}