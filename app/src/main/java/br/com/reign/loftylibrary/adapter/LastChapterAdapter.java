package br.com.reign.loftylibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.model.Chapter;

public class LastChapterAdapter extends RecyclerView.Adapter<LastChapterAdapter.MyViewHolder> {

    private List<Chapter> listChapters;
    private Context context;

    public LastChapterAdapter(List<Chapter> listChapters, Context context) {
        this.listChapters = listChapters;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.library_last_read_chapter,
                        parent,
                        false
                );
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chapter mangaChapter =  listChapters.get(position);
        Picasso.get()
                .load(mangaChapter.getCover())
                .into(holder.workCover);
        holder.txtWorkTitle.setText(mangaChapter.getWorkTitle());
        holder.txtLastChapter.setText(mangaChapter.getChapterTitle());
    }

    @Override
    public int getItemCount() {
        return listChapters.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView workCover;
        TextView txtWorkTitle;
        TextView txtLastChapter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            workCover = itemView.findViewById(R.id.workCover);
            txtWorkTitle = itemView.findViewById(R.id.txtWorkTitle);
            txtLastChapter = itemView.findViewById(R.id.txtLastChapter);
        }

        public ImageView getWorkCover() {
            return workCover;
        }
        public void setWorkCover(ImageView workCover) {
            this.workCover = workCover;
        }
        public TextView getTxtWorkTitle() {
            return txtWorkTitle;
        }
        public void setTxtWorkTitle(TextView txtWorkTitle) {
            this.txtWorkTitle = txtWorkTitle;
        }
        public TextView getTxtLastChapter() {
            return txtLastChapter;
        }
        public void setTxtLastChapter(TextView txtLastChapter) {
            this.txtLastChapter = txtLastChapter;
        }
    }
}