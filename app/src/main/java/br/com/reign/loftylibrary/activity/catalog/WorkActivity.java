package br.com.reign.loftylibrary.activity.catalog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.manga.ReadMangaActivity;
import br.com.reign.loftylibrary.adapter.IndexChapterAdapter;
import br.com.reign.loftylibrary.model.Chapter;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.utils.RecyclerItemClickListener;

public class WorkActivity extends AppCompatActivity {

    private TextView txtWorkTitle;
    private TextView txtWorkSynopsis;
    private ImageView imgWorkCover;
    private Button btnSynopsis;
    private Button btnChaptersList;
    private Button btnAbout;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private String workTitle = null;
    private String workType = null;
    private String workSynopsis = null;
    private String distributedBy = null;
    private String workGenre = null;
    private RecyclerView rvChapterList;
    private DatabaseReference dbProject;
    private ValueEventListener listenerChapters;
    private IndexChapterAdapter chapterAdapter;
    private ArrayList<Chapter> chapters = new ArrayList<>();
    private TextView txtAboutType;
    private TextView txtAboutGenre;
    private TextView txtAboutOrigin;
    private ConstraintLayout panelAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        initializeComponents();
        receivingWork();
        recoverWork();
        openTab();
        loadContent();
        txtWorkSynopsis.setVisibility(View.VISIBLE);
        txtWorkSynopsis.setText(workSynopsis);
        selectChapter();
    }
    public void initializeComponents() {
        // TextView
        txtWorkTitle = findViewById(R.id.txtWorkTitle);
        txtWorkSynopsis = findViewById(R.id.txtWorkSynopsis);
        txtAboutType = findViewById(R.id.txtAboutType);
        txtAboutGenre = findViewById(R.id.txtAboutGenre);
        txtAboutOrigin = findViewById(R.id.txtAboutOrigin);
        // ConstraintLayout
        panelAbout = findViewById(R.id.panelAbout);
        // ImageView
        imgWorkCover = findViewById(R.id.imgWorkCover);
        // Button
        btnSynopsis = findViewById(R.id.btnSynopsis);
        btnChaptersList = findViewById(R.id.btnChaptersList);
        btnAbout = findViewById(R.id.btnAbout);
        // RecyclerView
        rvChapterList = findViewById(R.id.rvChapterList);
        rvChapterList.setHasFixedSize(true);
        rvChapterList.setLayoutManager(new LinearLayoutManager(this));
        chapterAdapter = new IndexChapterAdapter(chapters, this);
        rvChapterList.setAdapter(chapterAdapter);
    }
    public void recoverWork() {
        Query query;
        query = dbReference.child("works")
                .child(workType)
                .child(workTitle);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtWorkTitle.setText(dataSnapshot.getKey());
                Picasso.get()
                        .load(String.valueOf(dataSnapshot.child("cover").getValue()))
                        .into(imgWorkCover);
                workSynopsis = String.valueOf(dataSnapshot.child("description").getValue());
                distributedBy = String.valueOf(dataSnapshot.child("distributedBy").getValue());
                workGenre = String.valueOf(dataSnapshot.child("genre").getValue());
                workType = String.valueOf(dataSnapshot.child("type").getValue());
                txtWorkSynopsis.setText(workSynopsis);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    public void receivingWork() {
        workTitle = getIntent().getExtras().getString("WorkTitle");
        workType = getIntent().getExtras().getString("Category");
    }
    public void openTab() {
        txtWorkSynopsis.setVisibility(View.VISIBLE);
        txtWorkSynopsis.setText(workSynopsis);
        btnSynopsis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvChapterList.setVisibility(View.GONE);
                panelAbout.setVisibility(View.GONE);
                txtWorkSynopsis.setVisibility(View.VISIBLE);
                txtWorkSynopsis.setText(workSynopsis);
            }
        });
        btnChaptersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtWorkSynopsis.setVisibility(View.GONE);
                panelAbout.setVisibility(View.GONE);
                rvChapterList.setVisibility(View.VISIBLE);
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtWorkSynopsis.setVisibility(View.GONE);
                rvChapterList.setVisibility(View.GONE);
                panelAbout.setVisibility(View.VISIBLE);
                txtAboutType.setText(workType);
                txtAboutGenre.setText(workGenre);
                txtAboutOrigin.setText(distributedBy);
            }
        });
    }
    public void loadContent() {
        dbProject = dbReference
                .child("chapters")
                .child(workType)
                .child(workTitle);
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
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        dbProject.addValueEventListener(listenerChapters);
    }
    public void selectChapter() {
        rvChapterList.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                rvChapterList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        IndexChapterAdapter.ViewHolder holder = new IndexChapterAdapter.ViewHolder(view);
                        String chapterTitle = String.valueOf(holder.getTxtWorkChapterTitle().getText());
                        Intent intent = new Intent(WorkActivity.this, ReadMangaActivity.class);
                        intent.putExtra("WorkTitle", workTitle);
                        intent.putExtra("ChapterTitle", chapterTitle);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {}
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}
                }));
    }
}