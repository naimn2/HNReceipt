package com.kakzain.hnreceipt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakzain.hnreceipt.model.Karyawan;

import java.util.ArrayList;
import java.util.List;

public class ListKaryawanAdapter extends RecyclerView.Adapter<ListKaryawanAdapter.ViewHolder> {
    private Context context;
    private List<Karyawan> listKaryawan;

    public ListKaryawanAdapter(Context context) {
        this.context = context;
        this.listKaryawan = new ArrayList<>();
    }

    public ListKaryawanAdapter setData(List<Karyawan> listKaryawan) {
        this.listKaryawan.clear();
        this.listKaryawan.addAll(listKaryawan);
        notifyDataSetChanged();
        return this;
    }

    @NonNull
    @Override
    public ListKaryawanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListKaryawanAdapter.ViewHolder holder, int position) {
        String text = (position+1)+". "+listKaryawan.get(position).getNama();
        holder.tvName.setText(text);
    }

    @Override
    public int getItemCount() {
        return listKaryawan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(android.R.id.text1);
        }
    }
}
