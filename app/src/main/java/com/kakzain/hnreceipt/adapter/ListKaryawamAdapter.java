package com.kakzain.hnreceipt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakzain.hnreceipt.R;
import com.kakzain.hnreceipt.model.Karyawan;

import java.util.ArrayList;

public class ListKaryawamAdapter extends RecyclerView.Adapter<ListKaryawamAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<Karyawan> listKaryawan;
    private OnClickListenerCallback onHadirListenerCallback;

    public ListKaryawamAdapter(Context context) {
        this.listKaryawan = new ArrayList<>();
        this.context = context;
    }

    public void setData(ArrayList<Karyawan> listKaryawan){
        this.listKaryawan.clear();
        this.listKaryawan.addAll(listKaryawan);
        notifyDataSetChanged();
    }

    public void setOnHadirListenerCallback(OnClickListenerCallback onHadirListenerCallback) {
        this.onHadirListenerCallback = onHadirListenerCallback;
    }

    @NonNull
    @Override
    public ListKaryawamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_karyawan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListKaryawamAdapter.ViewHolder holder, int position) {
        String nama = listKaryawan.get(position).getNama();
        holder.tvNama.setText(nama);
        holder.cbHadir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onHadirListenerCallback!=null){
                    onHadirListenerCallback.onHadirChanged(b, position);
                }
            }
        });
        if (position%2!=0 && position != listKaryawan.size()-1){
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listKaryawan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNama;
        private final CheckBox cbHadir;
        private final View divider;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_itemListKaryawan_nama);
            cbHadir = itemView.findViewById(R.id.cb_itemListKaryawan_check);
            divider = itemView.findViewById(R.id.view_itemListKaryawan_divider);
        }
    }

    public interface OnClickListenerCallback {
        void onHadirChanged(boolean b, int position);
    }
}
