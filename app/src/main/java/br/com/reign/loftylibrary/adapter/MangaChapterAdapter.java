package br.com.reign.loftylibrary.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.utils.Zoom;

public class MangaChapterAdapter extends RecyclerView.Adapter<MangaChapterAdapter.ViewHolder> {

    private List<MangaChapter> listMangaChapter;
    private Context context;

    public MangaChapterAdapter(List<MangaChapter> listMangaChapter, Context context) {
        this.listMangaChapter = listMangaChapter;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemChapterList = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.manga_chapter,
                        parent,
                        false
                );
        return new ViewHolder(itemChapterList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MangaChapter chapter = listMangaChapter.get(position);
        Picasso.get()
                .load(chapter.getContentChapter())
                .placeholder(R.drawable.progress_animation)
                .into(holder.imgPageMangaChapter);
    }

    @Override
    public int getItemCount() {
        return listMangaChapter.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPageMangaChapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPageMangaChapter = itemView.findViewById(R.id.imgPageMangaChapter);
        }

        public ImageView getImgPageMangaChapter() {
            return imgPageMangaChapter;
        }
        public void setImgPageMangaChapter(ImageView imgPageMangaChapter) {
            this.imgPageMangaChapter = imgPageMangaChapter;
        }
    }
}