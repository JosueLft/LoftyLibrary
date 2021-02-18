package br.com.reign.loftylibrary.fragments.index.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.adapter.IndexChapterAdapter;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.helper.ChaptersDAO;
import br.com.reign.loftylibrary.model.Chapter;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.utils.RecyclerItemClickListener;
import br.com.reign.loftylibrary.utils.SortPages;

public class ChaptersFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Comunication comunication;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbProject;
    private ValueEventListener listenerChapters;

    private IndexChapterAdapter chapterAdapter;
    private RecyclerView rvChapters;
    private ArrayList<Chapter> chapters = new ArrayList<>();

    private String workTitle;
    private String type;
    private String cover;
    private List<Chapter> listChapters = new ArrayList<>();

    OutputStream outputStream;

    public ChaptersFragment() {}

    public static ChaptersFragment newInstance(String param1, String param2) {
        ChaptersFragment fragment = new ChaptersFragment();
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

        View view = inflater.inflate(R.layout.fragment_chapters, container, false);

        initializeComponents(view);

        captureChapterTitle(workTitle, type);

        selectChapter(view);

        return view;
    }

    public void initializeComponents(View view) {
        rvChapters = view.findViewById(R.id.rvIndexChapters);
        rvChapters.setHasFixedSize(true);
        rvChapters.setLayoutManager(new LinearLayoutManager(getActivity()));

        chapterAdapter = new IndexChapterAdapter(chapters, getActivity().getApplicationContext());

        rvChapters.setAdapter(chapterAdapter);

        ChaptersDAO chaptersDAO = new ChaptersDAO(getActivity().getApplicationContext());
        listChapters = chaptersDAO.chaptersList();
    }

    public void receiveChapters(String workTitle, String workType) {
        this.workTitle = workTitle;
        type = workType;
    }

    public void captureChapterTitle(String title, String type) {
        dbProject = dbReference
                .child("chapters")
                .child(type)
                .child(title);

        loadContent();
        dbProject.addValueEventListener(listenerChapters);

    }

    public void loadContent() {
        listenerChapters = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapters.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(!(data.getKey().equalsIgnoreCase("cover")) && !(data.getKey().equalsIgnoreCase("currentDate"))) {
                        chapters.add(new MangaChapter(data.getKey()));
                    }
                }
                chapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public void selectChapter(View view) {
        rvChapters.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity().getApplicationContext(),
                rvChapters,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        IndexChapterAdapter.ViewHolder holder = new IndexChapterAdapter.ViewHolder(view);
                        String chapterTitle = String.valueOf(holder.getTxtWorkChapterTitle().getText());
                        if (comunication != null) {
                            comunication.invokeSelectedChapter("chaptersFragment", type, workTitle, chapterTitle);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        IndexChapterAdapter.ViewHolder holder = new IndexChapterAdapter.ViewHolder(view);
                        final String chapterTitle = String.valueOf(holder.getTxtWorkChapterTitle().getText());

                        dbProject = dbReference
                                .child("chapters")
                                .child(type)
                                .child(workTitle)
                                .child(chapterTitle);

                        dbProject.addValueEventListener(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SortPages compare = new SortPages();
                                for(DataSnapshot data : dataSnapshot.getChildren()) {
                                    if(!(data.getKey().equalsIgnoreCase("currentDate"))) {
                                        URL uri = null;
                                        try {
                                            uri = new URL(String.valueOf(data.getValue()));
                                            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
                                            InputStream input;
                                            try {
                                                input = connection.getInputStream();
                                            } catch (FileNotFoundException e) {
                                                URL u = new URL("https://firebasestorage.googleapis.com/v0/b/loftylibrary-116b9.appspot.com/o/404.png?alt=media&token=5d6ded59-50b5-4e75-81da-a7f4ff20398b");
                                                connection = (HttpURLConnection) u.openConnection();
                                                input = connection.getInputStream();
                                            }
                                            Bitmap page = BitmapFactory.decodeStream(input);

                                            File filePath = Environment.getExternalStorageDirectory();
                                            File dir = new File(Environment.getRootDirectory().getAbsolutePath() + "/chapters/" + workTitle + chapterTitle);
                                            dir.mkdir();
                                            File file = new File(dir, chapterTitle + ".jpg");
                                             outputStream = new FileOutputStream(file);
                                             page.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                             outputStream.flush();
                                             outputStream.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                Toast.makeText(
                                        getActivity().getApplicationContext(),
                                        "Capitulo " + chapterTitle + " baixado com sucesso!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));
    }
}