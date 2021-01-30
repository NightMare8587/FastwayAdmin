package com.example.fastwayadmin.Tables;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastwayadmin.Dish.DishView;
import com.example.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class TableView extends RecyclerView.Adapter<TableView.TableAdapter> {
    List<String> tables = new ArrayList<>();
    List<String> status = new ArrayList<>();
    public TableView(List<String> tables,List<String> status){
        this.status = status;
        this.tables = tables;
    }

    @NonNull
    @Override
    public TableAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.table_info,parent,false);
        return new TableAdapter(view);
    }



    @Override
    public void onBindViewHolder(@NonNull TableAdapter holder, int position) {
        holder.tableNum.setText(tables.get(position));
        holder.status.setText(status.get(position));
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public static class TableAdapter extends RecyclerView.ViewHolder{
        TextView tableNum,status;
        public TableAdapter(@NonNull View itemView) {
            super(itemView);
            tableNum = itemView.findViewById(R.id.numberOfTable);
            status = itemView.findViewById(R.id.statusOfTable);
        }
    }
}
