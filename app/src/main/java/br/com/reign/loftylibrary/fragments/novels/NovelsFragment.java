package br.com.reign.loftylibrary.fragments.novels;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.adapter.HomePostAdapter;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.model.NovelChapter;
import br.com.reign.loftylibrary.model.Post;
import br.com.reign.loftylibrary.utils.CompareChapterByDate;
import br.com.reign.loftylibrary.utils.RecyclerItemClickListener;

public class NovelsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerPost;
    private HomePostAdapter adapter;
    private ArrayList<Post> postItems = new ArrayList<>();
    private Content content;
    private DatabaseReference dbProject;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listenerNovels;
    private Comunication comunication;
    private String layout = "Painel";
    private Switch switchLayout;

    private String cover;
    private String chapterTitle;
    private String workTitle;
    private String dateChapter;

    public NovelsFragment() {}

    public static NovelsFragment newInstance(String param1, String param2) {
        NovelsFragment fragment = new NovelsFragment();
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
        View view = inflater.inflate(R.layout.fragment_novels, container, false);

        initializeComponents(view);
        readChapter(view);

//        switchLayout();

        return view;
    }

    private void initializeComponents(View view) {
        recyclerPost = view.findViewById(R.id.rvPost);
        recyclerPost.setHasFixedSize(true);
        recyclerPost.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Switch
        switchLayout = view.findViewById(R.id.switchLayout);

//        adapter = new HomePostAdapter(postItems, getActivity(), view, layout);

        recyclerPost.setAdapter(adapter);

        // inicializando objetos
        content = new Content();
        content.execute();
    }

    private class Content extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            dbProject = dbReference.child("chapters").child("novels");
            loadContent();

            dbProject.addValueEventListener(listenerNovels);

            return null;
        }
    }

    public void loadContent() {
        listenerNovels = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postItems.clear();
                CompareChapterByDate compare = new CompareChapterByDate();

                for(DataSnapshot novels : dataSnapshot.getChildren()) {
                    NovelChapter novel = novels.getValue(NovelChapter.class);
                    novel.setWorkTitle(novels.getKey());
                    novel.setCover(String.valueOf(novels.child("cover").getValue()));
                    for(DataSnapshot chapter : novels.getChildren()) {
                        if(!chapter.getKey().equalsIgnoreCase("cover") && !chapter.getKey().equalsIgnoreCase("currentDate")) {
                            novel.setChapterTitle(chapter.getKey());
                            if(!(chapter.child("currentDate").getValue() == null) && !(chapter.child("currentDate").getValue().equals(""))) {
                                novel.setDate(Long.parseLong(String.valueOf(chapter.child("currentDate").getValue())));
                            }
                            novel.setTranslatedBy(String.valueOf(chapter.child("translatedBy").getValue()));
                            Log.d("Chapter: ",
                                    "Obra: " + novel.getWorkTitle() + "\n" +
                                            "Capitulo: " + novel.getChapterTitle() + "\n" +
                                            "Tradução: " + novel.getTranslatedBy() + "\n" +
                                            "Data: " + novel.getDate()
                                    );
                            postItems.add(new Post(novel.getWorkTitle(), novel.getChapterTitle(), novel.getCover(), novel.getDate()));
                            postItems.sort(compare);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void readChapter(View view) {
        recyclerPost.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity().getApplicationContext(),
                recyclerPost,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        HomePostAdapter.ViewHolder holder = new HomePostAdapter.ViewHolder(view);
                        chapterTitle = String.valueOf(holder.getTxtChapterTitle().getText());
                        workTitle = String.valueOf(holder.getTxtPostTitle().getText());
                        dateChapter = String.valueOf(holder.getTxtDateChapter().getText());

                        if (comunication != null) {
                            comunication.invokeSelectedChapter("novelsFragment", "novels", workTitle, chapterTitle);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        HomePostAdapter.ViewHolder holder = new HomePostAdapter.ViewHolder(view);
                        chapterTitle = String.valueOf(holder.getTxtChapterTitle().getText());
                        workTitle = String.valueOf(holder.getTxtPostTitle().getText());

                        if(comunication != null) {
                            comunication.captureTitle(workTitle, "novels");
                        }
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }

//    public void switchLayout() {
//        switchLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(compoundButton.isChecked()) {
//                    layout = "Card";
//                    adapter = new HomePostAdapter(postItems, getActivity(), getView(), layout);
//                    recyclerPost.setAdapter(adapter);
//                } else {
//                    layout = "Painel";
//                    adapter = new HomePostAdapter(postItems, getActivity(), getView(), layout);
//                    recyclerPost.setAdapter(adapter);
//                }
//            }
//        });
//    }
}