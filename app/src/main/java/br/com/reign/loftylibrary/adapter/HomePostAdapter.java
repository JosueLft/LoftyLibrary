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
import br.com.reign.loftylibrary.model.Post;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.ViewHolder> {

    private List<Post> listPost;
    private Context context;

    private String layout;

    public HomePostAdapter(List<Post> listPosts, Context context, String layout) {
        this.listPost = listPosts;
        this.context = context;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = null;
        if(layout.equalsIgnoreCase("Painel")) {
            itemList = LayoutInflater
                    .from(parent.getContext())
                    .inflate(
                            R.layout.home_list_posts,
                            parent,
                            false
                    );
        } else if(layout.equalsIgnoreCase("Card")) {
            itemList = LayoutInflater
                    .from(parent.getContext())
                    .inflate(
                            R.layout.home_image_posts,
                            parent,
                            false
                    );

        }
        return new ViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post posts = listPost.get(position);
        holder.txtPostTitle.setText(posts.getTxtWorkTitle());
        holder.txtChapterTitle.setText(posts.getTxtChapterTitle());
        holder.txtDateChapter.setText(posts.date());
        Picasso.get()
                .load(posts.getImg())
                .placeholder(R.drawable.progress_animation)
                .into(holder.workCover);
    }

    @Override
    public int getItemCount() {
//        return listPost.size();
        return listPost.size();
    }

    //criando uma inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtPostTitle;
        private TextView txtChapterTitle;
        private TextView txtDateChapter;
        private ImageView workCover;

        public ViewHolder(View itemView ) {
            super(itemView);
            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            txtChapterTitle = itemView.findViewById(R.id.txtGenre);
            workCover = itemView.findViewById(R.id.workCover);
            txtDateChapter = itemView.findViewById(R.id.txtDateChapter);
        }

        public TextView getTxtPostTitle() {
            return txtPostTitle;
        }
        public void setTxtPostTitle(TextView txtPostTitle) {
            this.txtPostTitle = txtPostTitle;
        }
        public TextView getTxtChapterTitle() {
            return txtChapterTitle;
        }
        public void setTxtChapterTitle(TextView txtChapterTitle) {
            this.txtChapterTitle = txtChapterTitle;
        }
        public ImageView getWorkCover() {
            return workCover;
        }
        public void setWorkCover(ImageView workCover) {
            this.workCover = workCover;
        }
        public TextView getTxtDateChapter() {
            return txtDateChapter;
        }
        public void setTxtDateChapter(TextView txtDateChapter) {
            this.txtDateChapter = txtDateChapter;
        }
    }
}