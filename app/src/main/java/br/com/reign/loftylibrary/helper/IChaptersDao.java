package br.com.reign.loftylibrary.helper;

import java.util.List;

import br.com.reign.loftylibrary.model.Chapter;

public interface IChaptersDao {

    boolean read(Chapter chapter);
    boolean delete(Chapter chapter);
    List<Chapter> chaptersList();
}