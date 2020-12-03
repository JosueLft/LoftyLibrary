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
import br.com.reign.loftylibrary.model.MangaChapter;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.MyViewHolder> {

    private List<Chapter> listChapters;
    private Context context;
    public LibraryAdapter(List<Chapter> listChapters, Context context) {
        this.listChapters = listChapters;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.library_read_chapters,
                        parent,
                        false
                );
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chapter chapter = listChapters.get(position);
        Picasso.get()
                .load(chapter.getCover())
                .into(holder.workCover);
        holder.txtWorkTitle.setText(chapter.getWorkTitle());
        holder.txtChapterTitle.setText(chapter.getChapterTitle());
    }

    @Override
    public int getItemCount() {
        return listChapters.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView workCover;
        TextView txtWorkTitle;
        TextView txtChapterTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            workCover = itemView.findViewById(R.id.workCover);
            txtWorkTitle = itemView.findViewById(R.id.txtWorkTitle);
            txtChapterTitle = itemView.findViewById(R.id.txtChapterTitle);
        }
    }
}