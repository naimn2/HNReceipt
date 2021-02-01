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
import com.kakzain.hnreceipt.model.Lahan;

import java.util.ArrayList;
import java.util.List;

public class ListLahanAdapter extends RecyclerView.Adapter<ListLahanAdapter.ViewHolder> {
    private static final String TAG = ListLahanAdapter.class.getSimpleName();
    private Context context;
    private List<Lahan> listLahan;
    private OnMenuItemClickListenerCallback onMenuItemClickListenerCallback;

    public ListLahanAdapter(Context context) {
        this.context = context;
        this.listLahan = new ArrayList<>();
    }

    public ListLahanAdapter setData(List<Lahan> listLahan) {
        this.listLahan.clear();
        this.listLahan.addAll(listLahan);
        notifyDataSetChanged();
        return this;
    }

    public ListLahanAdapter setOnMenuItemClickListenerCallback(OnMenuItemClickListenerCallback onMenuItemClickListenerCallback) {
        this.onMenuItemClickListenerCallback = onMenuItemClickListenerCallback;
        return this;
    }

    @NonNull
    @Override
    public ListLahanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_nama_umum, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListLahanAdapter.ViewHolder holder, int position) {
        String text = (position+1)+". "+listLahan.get(position).getNamaLahan();
        holder.tvName.setText(text);
        holder.ivMore.setVisibility(View.INVISIBLE);
//        PopupMenu popupMenu = new PopupMenu(context, holder.ivMore);
//        popupMenu.getMenuInflater().inflate(R.menu.menu_item_list_lahan, popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                if (onMenuItemClickListenerCallback != null){
//                    onMenuItemClickListenerCallback.onRemoveClick(menuItem, position);
//                } else {
//                    Log.e(TAG, "onMenuItemClick: belum set on menu item click callback");
//                }
//                return true;
//            }
//        });
//        holder.ivMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupMenu.show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return listLahan.size();
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
