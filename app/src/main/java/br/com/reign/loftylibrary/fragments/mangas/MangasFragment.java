package br.com.reign.loftylibrary.fragments.mangas;

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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.adapter.HomePostAdapter;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.helper.ChaptersDAO;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.model.Post;
import br.com.reign.loftylibrary.utils.CompareChapterByDate;
import br.com.reign.loftylibrary.utils.RecyclerItemClickListener;

public class MangasFragment extends Fragment {

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
    private ValueEventListener listenerMangas;

    private String cover;
    private String chapterTitle;
    private String workTitle;
    private String dateChapter;
    private String layout = "Painel";
    private Switch switchLayout;

    private Comunication comunication;

    public MangasFragment() {}

    public static MangasFragment newInstance(String param1, String param2) {
        MangasFragment fragment = new MangasFragment();
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
        View view = inflater.inflate(R.layout.fragment_mangas, container, false);

        initializeComponents(view);
        readChapter();
//        switchLayout();

        return view;
    }

    private void initializeComponents(View view) {
        // Recycler Views
        recyclerPost = view.findViewById(R.id.rvPost);
        recyclerPost.setHasFixedSize(true);
        recyclerPost.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Switch
        switchLayout = view.findViewById(R.id.switchLayout);

        // Adapters
//        adapter = new HomePostAdapter(postItems, getActivity(), view, layout);

        recyclerPost.setAdapter(adapter);

        // Instancia de classes
        content = new Content();
        content.execute();
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

    // remover posteriormente
    private class Content extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            dbProject = dbReference.child("chapters").child("mangas");

            loadContent();

            dbProject.addValueEventListener(listenerMangas);
            return null;
        }
    }

    public void loadContent() {
        listenerMangas = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postItems.clear();
                CompareChapterByDate compare = new CompareChapterByDate();
                for(DataSnapshot mangas : dataSnapshot.getChildren()) {
                    MangaChapter manga = mangas.getValue(MangaChapter.class);
                    manga.setWorkTitle(mangas.getKey());
                    manga.setCover(String.valueOf(mangas.child("cover").getValue()));
                    for(DataSnapshot chapter : mangas.getChildren()) {
                        if(!chapter.getKey().equalsIgnoreCase("cover") && !chapter.getKey().equalsIgnoreCase("currentDate")) {
                            manga.setChapterTitle(chapter.getKey());
                            if(!(chapter.child("currentDate").getValue() == null) && !(chapter.child("currentDate").getValue().equals(""))) {
                                manga.setDate(Long.parseLong(String.valueOf(chapter.child("currentDate").getValue())));
                            }
                            postItems.add(new Post(manga.getWorkTitle(), manga.getChapterTitle(), manga.getCover(), manga.getDate()));
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

    private void readChapter() {
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
                            comunication.invokeSelectedChapter("mangasFragment", "mangas", workTitle, chapterTitle);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        HomePostAdapter.ViewHolder holder = new HomePostAdapter.ViewHolder(view);
                        chapterTitle = String.valueOf(holder.getTxtChapterTitle().getText());
                        workTitle = String.valueOf(holder.getTxtPostTitle().getText());

                        if(comunication != null) {
                            comunication.captureTitle(workTitle, "mangas");
                        }
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }
}