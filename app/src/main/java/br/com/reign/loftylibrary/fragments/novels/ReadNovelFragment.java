package br.com.reign.loftylibrary.fragments.novels;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.helper.ChaptersDAO;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.model.NovelChapter;

public class ReadNovelFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String summon;
    private String type;
    private String workTitle;
    private String chapterTitle;
    private String cover;

    private Comunication comunication;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbProject;
    private ArrayList<NovelChapter> chapters = new ArrayList<>();

    private TextView txtNovelChapterTitle;
    private TextView txtTranslatedBy;
    private TextView txtContentNovelChapter;

    private Button btnNextChapter;
    private Button btnPreviousChapter;
    private Button btnAddLibrary;
    private int chapterIndex;

    public ReadNovelFragment() {}

    public static ReadNovelFragment newInstance(String param1, String param2) {
        ReadNovelFragment fragment = new ReadNovelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
        if(!(activity instanceof Comunication)) {
            throw new RuntimeException("A activity deve implementar a interface Comunication!");
        }
        comunication = (Comunication) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_novel, container, false);

        initializeComponents(view);

        loadContent(chapterTitle);

        nextChapter();

        return view;
    }

    private void initializeComponents(View view) {
        txtNovelChapterTitle = view.findViewById(R.id.txtNovelChapterTitle);
        txtTranslatedBy = view.findViewById(R.id.txtTranslatedBy);
        txtContentNovelChapter = view.findViewById(R.id.txtContentNovelChapter);

        btnNextChapter = view.findViewById(R.id.btnNextChapter);
        btnPreviousChapter = view.findViewById(R.id.btnPreviousChapter);
        btnAddLibrary = view.findViewById(R.id.btnAddLibrary);
    }

    private void loadContent(final String chapterTitle) {
        dbProject = dbReference
                .child("chapters")
                .child(type)
                .child(workTitle)
                .child(chapterTitle);

        dbProject.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtNovelChapterTitle.setText(dataSnapshot.getKey());
                txtTranslatedBy.setText(String.valueOf(dataSnapshot.child("translatedBy").getValue()));
                txtContentNovelChapter.setText(String.valueOf(dataSnapshot.child("contentChapter").getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        markChapter(chapterTitle);
    }

    private void nextChapter() {
        dbProject = dbReference
                .child("chapters")
                .child(type)
                .child(workTitle);

        dbProject.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapters.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!(data.getKey().equalsIgnoreCase("cover")) && !(data.getKey().equalsIgnoreCase("currentDate"))) {
                        chapters.add(new NovelChapter(data.getKey()));
                    }
                }

                for (int i = 0; i < chapters.size(); i++) {
                    if (chapters.get(i).getChapterTitle().equals(chapterTitle)) {
                        chapterIndex = i;
                    }
                }
                if (chapterIndex == (chapters.size() - 1)) {
                    btnNextChapter.setVisibility(View.GONE);
                } else if (chapterIndex == 0) {
                    btnPreviousChapter.setVisibility(View.GONE);
                } else if (chapterIndex < (chapters.size() - 1)) {
                    btnNextChapter.setVisibility(View.VISIBLE);
                } else if (chapterIndex != 0) {
                    btnPreviousChapter.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnNextChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadContent(chapters.get(chapterIndex + 1).getChapterTitle());
                chapterIndex += 1;
                if(chapterIndex == (chapters.size() - 1)) {
                    btnNextChapter.setVisibility(View.GONE);
                } else if(chapterIndex == 0) {
                    btnPreviousChapter.setVisibility(View.GONE);
                } else if(chapterIndex < (chapters.size() - 1)){
                    btnNextChapter.setVisibility(View.VISIBLE);
                } else if(chapterIndex != 0){
                    btnPreviousChapter.setVisibility(View.VISIBLE);
                }
            }
        });

        btnPreviousChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chapterIndex != 0) {
                    loadContent(chapters.get(chapterIndex - 1).getChapterTitle());
                    chapterIndex -= 1;
                }
                if(chapterIndex == (chapters.size() - 1)) {
                    btnNextChapter.setVisibility(View.GONE);
                } else if(chapterIndex == 0) {
                    btnPreviousChapter.setVisibility(View.GONE);
                } else if(chapterIndex < (chapters.size() - 1)){
                    btnNextChapter.setVisibility(View.VISIBLE);
                } else if(chapterIndex != 0){
                    btnPreviousChapter.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void loadChapter(String summon, String type, String workTitle, String chapterTitle) {
        this.summon = summon;
        this.type = type;
        this.workTitle = workTitle;
        this.chapterTitle = chapterTitle;
    }

    private void markChapter(final String chapterTitle) {
        btnAddLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbProject = dbReference
                        .child("chapters")
                        .child("novels")
                        .child(workTitle);

                dbProject.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cover = String.valueOf(dataSnapshot.child("cover").getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                ChaptersDAO chaptersDAO = new ChaptersDAO(getActivity().getApplicationContext());
                NovelChapter chapter = new NovelChapter();
                chapter.setWorkTitle(workTitle);
                chapter.setChapterTitle(chapterTitle);
                chapter.setCover(cover);
                chapter.setType("novels");

                chaptersDAO.read(chapter);
            }
        });
    }
}