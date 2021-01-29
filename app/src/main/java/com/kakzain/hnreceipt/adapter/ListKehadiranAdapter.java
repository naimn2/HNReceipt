package com.kakzain.hnreceipt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakzain.hnreceipt.MyConstants;
import com.kakzain.hnreceipt.R;
import com.kakzain.hnreceipt.model.Karyawan;

import java.util.ArrayList;
import java.util.Arrays;

public class ListKehadiranAdapter extends RecyclerView.Adapter<ListKehadiranAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<Karyawan> listKaryawan;
    private OnClickListenerCallback onHadirListenerCallback;

    public ListKehadiranAdapter(Context context) {
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
    public ListKehadiranAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_karyawan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListKehadiranAdapter.ViewHolder holder, int position) {
        String nama = listKaryawan.get(position).getNama();
        holder.tvNama.setText(nama);
        ArrayAdapter<String> spinHadirAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                MyConstants.getNamaPosisiList(context, "Absen")
        );
        holder.spinHadir.setAdapter(spinHadirAdapter);
        holder.spinHadir.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (onHadirListenerCallback!=null){
                    onHadirListenerCallback.onHadirChanged(i, position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    private ArrayList<String> concatArray(String[] init, String[] array){
        ArrayList<String> lahanArray = new ArrayList<>(Arrays.asList(init));
        lahanArray.addAll(Arrays.asList(array));
        return lahanArray;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNama;
        private final Spinner spinHadir;
        private final View divider;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_itemListKaryawan_nama);
            spinHadir = itemView.findViewById(R.id.spin_itemListKaryawan_check);
            divider = itemView.findViewById(R.id.view_itemListKaryawan_divider);
        }
    }

    public interface OnClickListenerCallback {
        void onHadirChanged(int i, int position);
    }
}
