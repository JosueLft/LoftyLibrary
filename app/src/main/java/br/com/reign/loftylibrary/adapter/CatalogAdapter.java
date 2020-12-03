package br.com.reign.loftylibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.model.Work;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private List<Work> listCatalogWorks;
    private Context context;
    private String workTitle;

    public CatalogAdapter(List<Work> listCatalogWorks, Context context) {
        this.listCatalogWorks = listCatalogWorks;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemWorkList = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.catalog_works_list,
                        parent,
                        false
                );

        return new ViewHolder(itemWorkList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Work works = listCatalogWorks.get(position);
        holder.txtWorkTitle.setText(works.getWorkTitle());
        holder.txtOrigin.setText(works.getDistributedBy());
        holder.txtWorkGenre.setText(works.getGenre());
        holder.txtType.setText(works.getType());
        Picasso.get()
                .load(works.getImgCoverWork())
                .into(holder.imgWorkCover);
    }

    @Override
    public int getItemCount() {
        return listCatalogWorks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llWork;
        private TextView txtWorkTitle;
        private TextView txtWorkGenre;
        private TextView txtOrigin;
        private TextView txtType;
        private ImageView imgWorkCover;

        public ViewHolder(View itemView) {
            super(itemView);

            llWork = itemView.findViewById(R.id.llWork);
            txtWorkTitle = itemView.findViewById(R.id.txtPostTitle);
            txtOrigin = itemView.findViewById(R.id.txtDistributedBy);
            txtWorkGenre = itemView.findViewById(R.id.txtGenre);
            txtType = itemView.findViewById(R.id.txtType);
            imgWorkCover = itemView.findViewById(R.id.workCover);
        }

        public LinearLayout getLlWork() {
            return llWork;
    }
        public void setLlWork(LinearLayout llWork) {
            this.llWork = llWork;
        }
        public TextView getTxtWorkTitle() {
            return txtWorkTitle;
        }
        public void setTxtWorkTitle(TextView txtWorkTitle) {
            this.txtWorkTitle = txtWorkTitle;
        }
        public TextView getTxtWorkGenre() {
            return txtWorkGenre;
        }
        public void setTxtWorkGenre(TextView txtWorkGenre) {
            this.txtWorkGenre = txtWorkGenre;
        }
        public TextView getTxtOrigin() {
            return txtOrigin;
        }
        public void setTxtOrigin(TextView txtOrigin) {
            this.txtOrigin = txtOrigin;
        }
        public ImageView getImgWorkCover() {
            return imgWorkCover;
        }
        public void setImgWorkCover(ImageView imgWorkCover) {
            this.imgWorkCover = imgWorkCover;
        }
        public TextView getTxtType() {
            return txtType;
        }
        public void setTxtType(TextView txtType) {
            this.txtType = txtType;
        }
    }
}