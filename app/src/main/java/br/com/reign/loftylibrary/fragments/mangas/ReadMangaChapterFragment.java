package br.com.reign.loftylibrary.fragments.mangas;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ZoomControls;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.adapter.MangaChapterAdapter;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.helper.ChaptersDAO;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.utils.SortPages;

public class ReadMangaChapterFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String chapterTitle;
    private String workTitle;
    private String type;
    private String cover;

    private Comunication comunication;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbProject;

    private MangaChapterAdapter mangaChapterAdapter;
    private RecyclerView rvMangaChapterContent;
    private ArrayList<MangaChapter> chapterPages = new ArrayList<>();
    private ArrayList<MangaChapter> chapters = new ArrayList<>();

    private Button btnNextChapter;
    private Button btnPreviousChapter;
    private Button btnAddLibrary;

    private int chapterIndex;

    private ZoomControls zoom;

    // Google AdMob
    AdView adsBlockChapter;

    // teste
//    private WebView disqusComent;
    private StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    public ReadMangaChapterFragment() {}

    public static ReadMangaChapterFragment newInstance(String param1, String param2) {
        ReadMangaChapterFragment fragment = new ReadMangaChapterFragment();
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
        View view = inflater.inflate(R.layout.fragment_read_manga_chapter, container, false);

        initializeComponents(view);

        loadContent(chapterTitle);

        zoomIn();
        zoomOut();
        nextChapter();

        return view;
    }

    public void initializeComponents(View view) {
        rvMangaChapterContent = view.findViewById(R.id.rvMangaContent);
        rvMangaChapterContent.setLayoutManager(new LinearLayoutManager(getActivity()));

        mangaChapterAdapter = new MangaChapterAdapter(chapterPages, getActivity());

        rvMangaChapterContent.setAdapter(mangaChapterAdapter);

        zoom = view.findViewById(R.id.zoomControlMangaChapter);

        btnNextChapter = view.findViewById(R.id.btnNextChapter);
        btnPreviousChapter = view.findViewById(R.id.btnPreviousChapter);
        btnAddLibrary = view.findViewById(R.id.btnAddLibrary);

//        // webview
//        disqusComent = view.findViewById(R.id.disqusComent);
//        disqusComent.setWebViewClient(new WebViewClient());
//        WebSettings webSettings = disqusComent.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//
//        disqusComent.loadData(setDisqusComent(), "text/html", "UTF-8");

        // Google AdMob
        adsBlockChapter = new AdView(getActivity());
        adsBlockChapter.setAdSize(AdSize.BANNER);
        adsBlockChapter.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        initAdMob(view);

    }

    @JavascriptInterface
    public String setDisqusComent() {
        StrictMode.setThreadPolicy(policy);
        Elements d = null;
        String comentsHTML;

        Document doc;
        try {
            doc = Jsoup.connect("https://saikaiscan.com.br/novels/god-of-slaughter-gos/post/capitulo-238-assimilacao/25140").get();

            d = doc.select("div[id=reader-comments]");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return String.valueOf(d);
    }

    private void loadContent(final String chapterTitle) {

        dbProject = dbReference
                .child("chapters")
                .child(type)
                .child(workTitle)
                .child(chapterTitle);

        dbProject.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterPages.clear();
                SortPages compare = new SortPages();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(!(data.getKey().equalsIgnoreCase("currentDate"))) {
                        chapterPages.add(new MangaChapter(Integer.parseInt(String.valueOf(data.getKey())), String.valueOf(data.getValue())));
                        chapterPages.sort(compare);
                    }
                }

                mangaChapterAdapter.notifyDataSetChanged();
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
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(!(data.getKey().equalsIgnoreCase("cover")) && !(data.getKey().equalsIgnoreCase("currentDate"))) {
                        chapters.add(new MangaChapter(data.getKey()));
                    }
                }

                for(int i = 0; i < chapters.size(); i++) {
                    if(chapters.get(i).getChapterTitle().equals(chapterTitle)) {
                        chapterIndex = i;
                    }
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
        this.type = type;
        this.workTitle = workTitle;
        this.chapterTitle = chapterTitle;

    }

    public void zoomIn() {
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float x = rvMangaChapterContent.getScaleX();
                float y = rvMangaChapterContent.getScaleY();
                rvMangaChapterContent.setScaleX((float) (x + 0.2));
                rvMangaChapterContent.setScaleY((float) (y + 0.2));
            }
        });
    }

    public void zoomOut() {
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float x = rvMangaChapterContent.getScaleX();
                float y = rvMangaChapterContent.getScaleY();
                if(x == 1 && y == 1) {
                    rvMangaChapterContent.setScaleX(x);
                    rvMangaChapterContent.setScaleY(y);
                } else {
                    rvMangaChapterContent.setScaleX((float) (x - 0.2));
                    rvMangaChapterContent.setScaleY((float) (y - 0.2));
                }
            }
        });
    }

    private void markChapter(final String chapterTitle) {
        btnAddLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbProject = dbReference
                        .child("chapters")
                        .child("mangas")
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
                MangaChapter chapter = new MangaChapter();
                chapter.setWorkTitle(workTitle);
                chapter.setChapterTitle(chapterTitle);
                chapter.setCover(cover);
                chapter.setType("mangas");

                chaptersDAO.read(chapter);
            }
        });
    }

    public void initAdMob(View view) {
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adsBlockChapter = view.findViewById(R.id.adsBlockChapter);
        AdRequest adRequest = new AdRequest.Builder().build();
        adsBlockChapter.loadAd(adRequest);
    }
}