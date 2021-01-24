package com.kakzain.hnreceipt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakzain.hnreceipt.R;
import com.kakzain.hnreceipt.model.PanenSawitLahan;

import java.util.ArrayList;
import java.util.List;

public class ListPanenLahanAdapter extends RecyclerView.Adapter<ListPanenLahanAdapter.ViewHolder> {
    private Context context;
    private List<PanenSawitLahan> listPanenSawitLahan;

    public ListPanenLahanAdapter(Context context) {
        this.listPanenSawitLahan = new ArrayList<>();
        this.context = context;
    }

    public void setData(List<PanenSawitLahan> listPanenSawitLahan){
        this.listPanenSawitLahan.clear();
        this.listPanenSawitLahan.addAll(listPanenSawitLahan);
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ListPanenLahanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_list_panen_lahan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPanenLahanAdapter.ViewHolder holder, int position) {
        int jumlahHadir = listPanenSawitLahan.get(position).getKehadiran().size();
        int iLahan = listPanenSawitLahan.get(position).getIdLahan();

        holder.tvLahan.setText("Lahan "+(iLahan+1));
        holder.tvJumlahHadir.setText(String.valueOf(jumlahHadir)+" Hadir");
        if (position%2 == 0){
            holder.divider.setVisibility(View.INVISIBLE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listPanenSawitLahan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLahan, tvJumlahHadir;
        private final View divider;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLahan = itemView.findViewById(R.id.tv_itemListPanenLahan_lahan);
            tvJumlahHadir = itemView.findViewById(R.id.tv_itemListPanenLahan_jumlahHadir);
            divider = itemView.findViewById(R.id.view_itemListPanenLahan_divider);
        }
    }
}
