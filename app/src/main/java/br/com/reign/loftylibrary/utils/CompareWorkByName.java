package br.com.reign.loftylibrary.utils;

import java.util.Comparator;

import br.com.reign.loftylibrary.model.Work;

public class CompareWorkByName implements Comparator<Work> {

    @Override
    public int compare(Work work1, Work work2) {
        String title1 = work1.getWorkTitle();
        String title2 = work2.getWorkTitle();

        return title1.compareTo(title2);
    }
}