package br.com.reign.loftylibrary.controller;

public interface Comunication {

    public void captureTitle(String workTitle, String workType);
    public void captureChapters(String workTitle, String workType);
    public void captureInformations(String workType, String workGenre, String workDistributedBy);
    public void  invokeSelectedChapter(String summon, String type, String workTitle, String chapterTitle);
}