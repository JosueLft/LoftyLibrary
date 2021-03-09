package br.com.reign.loftylibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.model.Chapter;

public class IndexChapterAdapter extends RecyclerView.Adapter<IndexChapterAdapter.ViewHolder> {

    private List<Chapter> listChapters;
    private Context context;

    public IndexChapterAdapter(List<Chapter> listChapters, Context context) {
        this.listChapters = listChapters;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemChapterList = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.index_chapters,
                        parent,
                        false
                );
        return new ViewHolder(itemChapterList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chapter chapter = listChapters.get(position);
        holder.txtWorkChapterTitle.setText(chapter.getChapterTitle());
    }

    @Override
    public int getItemCount() {
        return listChapters.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtWorkChapterTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtWorkChapterTitle = itemView.findViewById(R.id.chaptersIndex);
        }
        public TextView getTxtWorkChapterTitle() {
            return txtWorkChapterTitle;
        }
        public void setTxtWorkChapterTitle(TextView txtWorkChapterTitle) {
            this.txtWorkChapterTitle = txtWorkChapterTitle;
        }
    }
}