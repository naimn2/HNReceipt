package com.kakzain.hnreceipt.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.kakzain.hnreceipt.R;
import com.kakzain.hnreceipt.model.Karyawan;
import com.kakzain.hnreceipt.model.Kehadiran;

import java.util.ArrayList;
import java.util.List;

public class ListSopirAdapter extends RecyclerView.Adapter<ListSopirAdapter.ViewHolder> {
    private final Context context;
    private final List<Karyawan> listKaryawan;
    private final List<String> listIdKaryawan;
    private final List<Kehadiran> sopirDO;
    private OnSopirListenerCallback onSopirListenerCallback;

    public ListSopirAdapter(Context context, List<Kehadiran> sopirDO) {
        this.context = context;
        this.sopirDO = sopirDO;
        listKaryawan = new ArrayList<>();
        listIdKaryawan = new ArrayList<>();
    }

    public void setData(List<Karyawan> listKaryawan, List<String> listIdKaryawan) {
        this.listKaryawan.clear();
        this.listKaryawan.addAll(listKaryawan);
        this.listIdKaryawan.clear();
        this.listIdKaryawan.addAll(listIdKaryawan);
        notifyDataSetChanged();
    }

    public void setOnSopirListenerCallback(OnSopirListenerCallback onSopirListenerCallback) {
        this.onSopirListenerCallback = onSopirListenerCallback;
    }

    @NonNull
    @Override
    public ListSopirAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_sopir, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSopirAdapter.ViewHolder holder, int position) {
        Karyawan karyawan = listKaryawan.get(position);
        String idKaryawan = listIdKaryawan.get(position);
        holder.tvNama.setText(karyawan.getNama());
        holder.cbSopir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onSopirListenerCallback != null){
                    onSopirListenerCallback.onSopirCheckChange(b, position);
                }
            }
        });
        if (sopirDO != null) {
            for (Kehadiran currentKehadiran : sopirDO) {
                if (TextUtils.equals(currentKehadiran.getIdKaryawan(), idKaryawan)) {
                    holder.cbSopir.setChecked(true);
                    break;
                }
            }
        }

        if (position%2 == 0){
            holder.divider.setVisibility(View.INVISIBLE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listKaryawan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNama;
        private final AppCompatCheckBox cbSopir;
        private final View divider;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_itemListSopir_nama);
            cbSopir = itemView.findViewById(R.id.cb_itemListSopir_check);
            divider = itemView.findViewById(R.id.view_itemListSopir_divider);
        }
    }

    public interface OnSopirListenerCallback {
        void onSopirCheckChange(boolean b, int position);
    }
}
