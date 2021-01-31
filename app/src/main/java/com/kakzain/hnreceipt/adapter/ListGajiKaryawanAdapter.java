package com.kakzain.hnreceipt.adapter;

import android.content.Context;
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
    private List<Penggajian> listPenggajian;
    private final Context context;

    public ListGajiKaryawanAdapter(@Nonnull Context context) {
        this.context = context;
        this.listPenggajian = new ArrayList<>();
    }

    public void setData(List<Penggajian> listPenggajian) {
        this.listPenggajian.clear();
        this.listPenggajian.addAll(listPenggajian);
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
        Karyawan karyawan = MyConstants.getAllKaryawan(context).get(penggajian.getIdKaryawan());
        holder.tvNama.setText(karyawan.getNama());
        holder.tvGaji.setText(
                UnitValidator.validateUnitCurrency((int) penggajian.getGaji().doubleValue()));
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
