package com.kakzain.hnreceipt.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakzain.hnreceipt.R;
import com.kakzain.hnreceipt.db.lokal.MyConstants;
import com.kakzain.hnreceipt.helper.UnitValidator;
import com.kakzain.hnreceipt.model.Karyawan;
import com.kakzain.hnreceipt.model.Penggajian;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class ListGajiKaryawanAdapter extends RecyclerView.Adapter<ListGajiKaryawanAdapter.ViewHolder> {
    private final List<Penggajian> listPenggajian;
    private Map<String, Karyawan> mapKaryawan;
    private final Context context;

    public ListGajiKaryawanAdapter(@Nonnull Context context) {
        this.context = context;
        this.listPenggajian = new ArrayList<>();
        this.mapKaryawan = MyConstants.getAllKaryawan(context);
    }

    public void setData(List<Penggajian> listPenggajian) {
        this.listPenggajian.clear();
        this.listPenggajian.addAll(listPenggajian);
        notifyDataSetChanged();
    }

    public void updateKaryawan(List<Karyawan> listKaryawan, List<String> ids) {
        this.mapKaryawan.clear();
        for (int i = 0; i < listKaryawan.size(); i++) {
            mapKaryawan.put(ids.get(i), listKaryawan.get(i));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListGajiKaryawanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_gaji_karyawan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListGajiKaryawanAdapter.ViewHolder holder, int position) {
        Penggajian penggajian = listPenggajian.get(position);
        Karyawan karyawan = mapKaryawan.get(penggajian.getIdKaryawan());
        if (karyawan != null && karyawan.isDeleted()){
            holder.tvNama.setTextColor(Color.RED);
            holder.tvGaji.setTextColor(Color.RED);
        }
        holder.tvNama.setText(karyawan==null?context.getString(R.string.unavailable):karyawan.getNama());
        holder.tvGaji.setText(
                UnitValidator.validateUnitCurrency((int) penggajian.getGaji()));
    }

    @Override
    public int getItemCount() {
        return listPenggajian.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNama, tvGaji;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_itemListGajiKaryawan_nama);
            tvGaji = itemView.findViewById(R.id.tv_itemListGajiKaryawan_gaji);
        }
    }
}
