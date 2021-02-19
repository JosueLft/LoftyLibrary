package br.com.reign.loftylibrary.model;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.text.SimpleDateFormat;

public class Post {

    private String txtWorkTitle;
    private String txtChapterTitle;
    private String img;
    private long date;


    public Post() {
    }

    public Post(String txtWorkTitle, String txtChapterTitle, String img, long date) {
        this.txtWorkTitle = txtWorkTitle;
        this.txtChapterTitle = txtChapterTitle;
        this.img = img;
        this.date = date;
    }

    public String getTxtWorkTitle() {
        return txtWorkTitle;
    }

    public void setTxtWorkTitle(String txtWorkTitle) {
        this.txtWorkTitle = txtWorkTitle;
    }

    public String getTxtChapterTitle() {
        return txtChapterTitle;
    }

    public void setTxtChapterTitle(String txtChapterTitle) {
        this.txtChapterTitle = txtChapterTitle;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String date() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String d = sdf.format(date);
        return d;
    }
}
