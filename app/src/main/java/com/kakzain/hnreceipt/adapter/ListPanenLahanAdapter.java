package com.kakzain.hnreceipt.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakzain.hnreceipt.db.lokal.MyConstants;
import com.kakzain.hnreceipt.R;
import com.kakzain.hnreceipt.model.Lahan;
import com.kakzain.hnreceipt.model.PanenSawitLahan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListPanenLahanAdapter extends RecyclerView.Adapter<ListPanenLahanAdapter.ViewHolder> {
    private final Context context;
    private final List<PanenSawitLahan> listPanenSawitLahan;
    private final Map<String, Lahan> mapNamaLahan;
    private OnMenuClickListenerCallback onMenuClickListenerCallback;

    public ListPanenLahanAdapter(Context context) {
        this.listPanenSawitLahan = new ArrayList<>();
        this.context = context;
        mapNamaLahan = MyConstants.getAllLahan(context);
    }

    public void setData(List<PanenSawitLahan> listPanenSawitLahan){
        this.listPanenSawitLahan.clear();
        this.listPanenSawitLahan.addAll(listPanenSawitLahan);
        notifyDataSetChanged();
    }

    public void updateLahan(List<Lahan> listLahan, List<String> ids) {
        this.mapNamaLahan.clear();
        for (int i = 0; i < listLahan.size(); i++) {
            Lahan lahan = listLahan.get(i);
            String idLahan = ids.get(i);
            this.mapNamaLahan.put(idLahan, lahan);
        }
        notifyDataSetChanged();
    }

    public void setOnMenuClickListenerCallback(OnMenuClickListenerCallback onMenuClickListenerCallback) {
        this.onMenuClickListenerCallback = onMenuClickListenerCallback;
    }

    @NonNull
    @Override
    public ListPanenLahanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_list_panen_lahan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPanenLahanAdapter.ViewHolder holder, int position) {
        PanenSawitLahan panenLahan = listPanenSawitLahan.get(position);
        int jumlahHadir = panenLahan.getKehadiran().size();
        int idLahan = panenLahan.getIdLahan();

        Lahan lahan = mapNamaLahan.get(String.valueOf(idLahan));
        if (lahan != null && lahan.isDeleted()){
            holder.tvLahan.setTextColor(Color.GRAY);
        }
        holder.tvLahan.setText(lahan==null?
                context.getString(R.string.unavailable): lahan.getNamaLahan());
        holder.tvJumlahHadir.setText(String.format("%d Hadir", jumlahHadir));
        holder.tvBersih.setText(panenLahan.getBeratBersih() + " Kg");
        holder.tvBrondol.setText(panenLahan.getBeratBrondol() + " Kg");

        if (onMenuClickListenerCallback == null){
            holder.ivMore.setVisibility(View.GONE);
        } else {
            holder.ivMore.setVisibility(View.VISIBLE);
        }
        PopupMenu menuMore = new PopupMenu(context, holder.ivMore);
        menuMore.getMenuInflater().inflate(R.menu.menu_item_list_panen_lahan, menuMore.getMenu());
        menuMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onMenuClickListenerCallback.onMenuDeleteClicked(menuItem, position);
                return true;
            }
        });
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMore.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPanenSawitLahan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLahan, tvJumlahHadir, tvBersih, tvBrondol;
        private final View divider;
        private final ImageView ivMore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLahan = itemView.findViewById(R.id.tv_itemListPanenLahan_lahan);
            tvJumlahHadir = itemView.findViewById(R.id.tv_itemListPanenLahan_jumlahHadir);
            divider = itemView.findViewById(R.id.view_itemListPanenLahan_divider);
            tvBersih = itemView.findViewById(R.id.tv_itemListPanenLahan_beratBersih);
            tvBrondol = itemView.findViewById(R.id.tv_itemListPanenLahan_beratBrondol);
            ivMore = itemView.findViewById(R.id.iv_itemListPanenLahan_more);
        }
    }

    public interface OnMenuClickListenerCallback {
        void onMenuDeleteClicked(MenuItem menuItem, int position);
    }
}
