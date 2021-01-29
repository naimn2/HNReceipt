package com.kakzain.hnreceipt.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakzain.hnreceipt.R;
import com.kakzain.hnreceipt.activity.CreateDOActivity;
import com.kakzain.hnreceipt.helper.DateFormatter;
import com.kakzain.hnreceipt.model.DeliveryOrder;

import java.util.ArrayList;
import java.util.List;

public class ListDeliveryOrderAdapter extends RecyclerView.Adapter<ListDeliveryOrderAdapter.ViewHolder> {
    public static final long LOAD_LIMIT = 10;
    private final Context context;
    private List<DeliveryOrder> listDO;
    private List<String> listIds;
    private boolean isEndOfData;

    public ListDeliveryOrderAdapter(Context context) {
        this.context = context;
        isEndOfData = false;
        listDO = new ArrayList<>();
        listIds = new ArrayList<>();
    }

    public void setData(List<DeliveryOrder> listDO, List<String> listIds) {
        this.listDO.clear();
        this.listDO.addAll(listDO);
        this.listIds.clear();
        this.listIds.addAll(listIds);
        notifyDataSetChanged();
    }

    public void setEndOfData(boolean endOfData) {
        isEndOfData = endOfData;
    }

    @NonNull
    @Override
    public ListDeliveryOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_delivery_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDeliveryOrderAdapter.ViewHolder holder, int position) {
        DeliveryOrder mDO = listDO.get(position);
        holder.tvTanggal.setText(DateFormatter.getDate(mDO.getTanggal().toDate().getTime()));
        holder.tvBeratTotal.setText(String.valueOf(mDO.getBeratTotal())+" Kg");
        // SET VISIBILITY DRAFT CONDITION
        if (!mDO.isCommitted()){
            holder.tvDraft.setVisibility(View.VISIBLE);
        } else {
            holder.tvDraft.setVisibility(View.INVISIBLE);
        }
        // SET VIEW
        if (mDO.getPanenSawitLahan() != null) {
            holder.tvJumlahPanen.setText(String.valueOf(mDO.getPanenSawitLahan().size())+" Lahan");
        } else {
            holder.tvJumlahPanen.setText(String.valueOf(0)+" Lahan");
        }
        // VISIBILITY LOAD MORE CONTROLLER
        if (position == listDO.size()-1 && listDO.size() >= LOAD_LIMIT && !isEndOfData){
            holder.pbLoadMore.setVisibility(View.VISIBLE);
        } else {
            holder.pbLoadMore.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateDOActivity.class);
                intent.putExtra(CreateDOActivity.ID_DO_EXTRA, listIds.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDO.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTanggal, tvJumlahPanen, tvBeratTotal, tvDraft;
        private final ProgressBar pbLoadMore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_itemListDO_tanggal);
            tvJumlahPanen = itemView.findViewById(R.id.tv_itemListDO_jumlahPanen);
            tvBeratTotal = itemView.findViewById(R.id.tv_itemListDO_beratTotal);
            pbLoadMore = itemView.findViewById(R.id.pb_itemListDO_loadMore);
            tvDraft = itemView.findViewById(R.id.tv_itemListDO_draft);
        }
    }
}
