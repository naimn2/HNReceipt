package com.kakzain.hnreceipt.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakzain.hnreceipt.R;
import com.kakzain.hnreceipt.model.Karyawan;

import java.util.ArrayList;
import java.util.List;

public class ListKaryawanAdapter extends RecyclerView.Adapter<ListKaryawanAdapter.ViewHolder> {
    private static final String TAG = ListKaryawanAdapter.class.getSimpleName();
    private Context context;
    private List<Karyawan> listKaryawan;
    private OnMenuItemClickListenerCallback onMenuItemClickListenerCallback;

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

    public ListKaryawanAdapter setOnMenuItemClickListenerCallback(OnMenuItemClickListenerCallback onMenuItemClickListenerCallback) {
        this.onMenuItemClickListenerCallback = onMenuItemClickListenerCallback;
        return this;
    }

    @NonNull
    @Override
    public ListKaryawanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_nama_umum, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListKaryawanAdapter.ViewHolder holder, int position) {
        String text = (position+1)+". "+listKaryawan.get(position).getNama();
        holder.tvName.setText(text);
        PopupMenu popupMenu = new PopupMenu(context, holder.ivMore);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item_list_karyawan, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (onMenuItemClickListenerCallback != null){
                    onMenuItemClickListenerCallback.onRemoveClick(menuItem, position);
                } else {
                    Log.e(TAG, "onMenuItemClick: belum set on menu item click callback");
                }
                return true;
            }
        });
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listKaryawan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final ImageView ivMore;
        private final View divider;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_itemListUmum_nama);
            ivMore = itemView.findViewById(R.id.iv_itemListUmum_more);
            divider = itemView.findViewById(R.id.view_itemListUmum_divider);
        }
    }

    public interface OnMenuItemClickListenerCallback{
        void onRemoveClick(MenuItem menuItem, int i);
    }
}
