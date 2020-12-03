package br.com.reign.loftylibrary.utils;

import java.util.Comparator;

import br.com.reign.loftylibrary.model.Post;

public class CompareChapterByDate implements Comparator<Post> {

    @Override
    public int compare(Post post1, Post post2) {
        Long date1 = post1.getDate();
        Long date2 = post2.getDate();

        return date2.compareTo(date1);
    }
}