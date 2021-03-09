package br.com.reign.loftylibrary.activity.manga;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.account.LoginActivity;
import br.com.reign.loftylibrary.adapter.MangaChapterAdapter;
import br.com.reign.loftylibrary.fragments.InfoFragment;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.utils.RecyclerItemClickListener;
import br.com.reign.loftylibrary.utils.SortPages;

public class ReadMangaActivity extends AppCompatActivity {

    private MangaChapterAdapter mangaChapterAdapter;
    private String chapterTitle;
    private String workTitle;
    private RecyclerView rvMangaContent;
    private ArrayList<MangaChapter> chapterPages = new ArrayList<>();
    private ArrayList<MangaChapter> chapters = new ArrayList<>();
    private MangaChapter manga = new MangaChapter();
    private Button btnNextChapter;
    private Button btnPreviousChapter;
    private Button btnAddLibrary;
    private Button btnInfo;
    private TextView txtCurrentChapterTitle;
    private int chapterIndex;
    private FrameLayout frameInfo;
    // Firebase
    private DatabaseReference dbReference;
    //  zoom variables
    private float originalScaleX, originalScaleY;
    // Fragment
    private InfoFragment infoFragment;
    private Button btnCloseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_manga);

        initializeComponents();
        receivingChapter();
        loadContent(chapterTitle);
        nextChapter();
        addLibrary();
        zoom();
        info();
    }

    private void info() {
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameInfo.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameInfo, infoFragment);
                transaction.commit();
            }
        });

        btnCloseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameInfo.setVisibility(View.GONE);
            }
        });
    }

    private void initializeComponents() {
        txtCurrentChapterTitle = findViewById(R.id.txtCurrentChapterTitle);
        //RecyclerView
        rvMangaContent = findViewById(R.id.rvMangaContent);
        rvMangaContent.setLayoutManager(new LinearLayoutManager(this));
        //Adapter
        mangaChapterAdapter = new MangaChapterAdapter(chapterPages, this);
        rvMangaContent.setAdapter(mangaChapterAdapter);
        // Controllers
        btnNextChapter = findViewById(R.id.btnNextChapter);
        btnPreviousChapter = findViewById(R.id.btnPreviousChapter);
        btnAddLibrary = findViewById(R.id.btnAddLibrary);
        btnInfo = findViewById(R.id.btnInfo);
        // Views
        frameInfo = findViewById(R.id.frameInfo);
        infoFragment = new InfoFragment();
        btnCloseInfo = findViewById(R.id.btnCloseInfo);
    }

    private void receivingChapter() {
        workTitle = getIntent().getExtras().getString("WorkTitle");
        chapterTitle = getIntent().getExtras().getString("ChapterTitle");
        txtCurrentChapterTitle.setText(chapterTitle);
    }

    private void loadContent(final String chapterTitle) {
        dbReference = FirebaseDatabase.getInstance().getReference()
                .child("chapters")
                .child("mangas")
                .child(workTitle)
                .child(chapterTitle);
        dbReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterPages.clear();
                SortPages compare = new SortPages();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(!(data.getKey().equalsIgnoreCase("currentDate")) && !data.getKey().equalsIgnoreCase("date")) {
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
    }

    private void nextChapter() {
        dbReference = FirebaseDatabase.getInstance().getReference()
                .child("chapters")
                .child("mangas")
                .child(workTitle);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapters.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(!(data.getKey().equalsIgnoreCase("cover")) && !(data.getKey().equalsIgnoreCase("currentDate")) && !data.getKey().equalsIgnoreCase("date")) {
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
                txtCurrentChapterTitle.setText(chapters.get(chapterIndex + 1).getChapterTitle());
                chapterTitle = chapters.get(chapterIndex + 1).getChapterTitle();
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
                    txtCurrentChapterTitle.setText(chapters.get(chapterIndex - 1).getChapterTitle());
                    chapterTitle = chapters.get(chapterIndex - 1).getChapterTitle();
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

    private void addLibrary() {
        btnAddLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getUid() == null) {
                    Intent intent = new Intent(ReadMangaActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    final String id = FirebaseAuth.getInstance().getUid();
                    dbReference = FirebaseDatabase.getInstance().getReference()
                            .child("chapters")
                            .child("mangas")
                            .child(workTitle);
                    dbReference.addValueEventListener(new ValueEventListener() { // metodo utilizado para salvar um capitulo na biblioteca do usuario atual
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            manga.setCover(String.valueOf(dataSnapshot.child("cover").getValue()));
                            long currentDate = System.currentTimeMillis();
                            manga.setChapterTitle(chapterTitle);
                            manga.setDate(currentDate);
                            manga.setWorkTitle(workTitle);
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(id)
                                    .collection("library")
                                    .document(workTitle)
                                    .set(manga)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(
                                                    ReadMangaActivity.this,
                                                    "Adicionado a biblioteca com sucesso",
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

    public void zoom() {
        originalScaleX = rvMangaContent.getScaleX();
        originalScaleY = rvMangaContent.getScaleY();
        rvMangaContent.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                rvMangaContent,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if((rvMangaContent.getScaleX() >= (originalScaleX * 3)) &&  (rvMangaContent.getScaleY() >= (originalScaleY * 3))) {
                            rvMangaContent.setScaleX(originalScaleX);
                            rvMangaContent.setScaleY(originalScaleY);
                        } else {
                            rvMangaContent.setScaleX((float) (rvMangaContent.getScaleX() + 0.2));
                            rvMangaContent.setScaleY((float) (rvMangaContent.getScaleY() + 0.2));
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        rvMangaContent.setScaleX(originalScaleX);
                        rvMangaContent.setScaleY(originalScaleY);
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
    }
}