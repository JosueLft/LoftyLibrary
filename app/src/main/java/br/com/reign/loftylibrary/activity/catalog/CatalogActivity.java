package br.com.reign.loftylibrary.activity.catalog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.library.LibraryActivity;
import br.com.reign.loftylibrary.activity.manga.MangaActivity;
import br.com.reign.loftylibrary.activity.novel.NovelActivity;
import br.com.reign.loftylibrary.activity.settings.SettingsActivity;
import br.com.reign.loftylibrary.adapter.CatalogAdapter;
import br.com.reign.loftylibrary.model.Work;
import br.com.reign.loftylibrary.utils.CompareWorkByName;
import br.com.reign.loftylibrary.utils.MenuSelect;
import br.com.reign.loftylibrary.utils.RecyclerItemClickListener;

public class CatalogActivity extends AppCompatActivity {
    private TextView txtMangasIcon;
    private ImageView imgMangasIcon;
    private TextView txtNovelsIcon;
    private ImageView imgNovelsIcon;
    private TextView txtCatalogIcon;
    private ImageView imgCatalogIcon;
    private TextView txtLibraryIcon;
    private ImageView imgLibraryIcon;
    private TextView txtSettingsIcon;
    private ImageView imgSettingsIcon;
    MenuSelect menu = new MenuSelect();
    private List<TextView> components = new ArrayList<>();
    // Catalog variables
    private EditText editSearch;
    private RecyclerView rvWorks;
    private CatalogAdapter catalogAdapter;
    private ArrayList<Work> workList = new ArrayList<>();
    private DatabaseReference dbWorks;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listenerWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        initializeComponents();
        openMangas();
        openNovels();
        openLibrary();
        openSettings();
        menu.selectMenu(txtCatalogIcon, components);
        selectWork();
        search();
    }

    @Override
    public void onStart() {
        super.onStart();
        editSearch.setText("");
    }

    private void initializeComponents() {
        // Text View
        components.add(txtMangasIcon = findViewById(R.id.txtMangasIcon));
        components.add(txtNovelsIcon = findViewById(R.id.txtNovelsIcon));
        components.add(txtCatalogIcon = findViewById(R.id.txtCatalogIcon));
        components.add(txtLibraryIcon = findViewById(R.id.txtLibraryIcon));
        components.add(txtSettingsIcon = findViewById(R.id.txtSettingsIcon));
        // EditText
        editSearch = findViewById(R.id.editSearch);
        // RecyclerView
        rvWorks = findViewById(R.id.rvCatalogWorks);
        rvWorks.setHasFixedSize(true);
        rvWorks.setLayoutManager(new LinearLayoutManager(this));
        // Adapters
        catalogAdapter = new CatalogAdapter(workList, this);
        rvWorks.setAdapter(catalogAdapter);
        // Image View
        imgMangasIcon = findViewById(R.id.imgMangasIcon);
        imgNovelsIcon = findViewById(R.id.imgNovelsIcon);
        imgCatalogIcon = findViewById(R.id.imgCatalogIcon);
        imgLibraryIcon = findViewById(R.id.imgLibraryIcon);
        imgSettingsIcon = findViewById(R.id.imgSettingsIcon);
    }

    private void search() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String word = editSearch.getText().toString().trim();
                searchWord(word);
            }
        });
    }
    private void searchWord(final String word) {
        if (word.equalsIgnoreCase("")) {
            loadContent();
        } else {
            dbReference.child("works");
            listenerWorks = new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    workList.clear();
                    CompareWorkByName compare = new CompareWorkByName();
                    for (DataSnapshot category : dataSnapshot.getChildren()) {
                        for(DataSnapshot works : category.getChildren()) {
                            if(works.getKey().toUpperCase().contains(word.toUpperCase())) {
                                Work work = works.getValue(Work.class);
                                work.setWorkTitle(works.getKey());
                                work.setGenre("Gêneros: " + works.child("genre").getValue());
                                work.setImgCoverWork(String.valueOf(works.child("cover").getValue()));
                                work.setDistributedBy("Por: " + works.child("distributedBy").getValue());
                                work.setType(String.valueOf(works.child("type").getValue()));
                                workList.add(work);
                                workList.sort(compare);
                            }
                        }
                    }
                    catalogAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CatalogActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };
            dbWorks.addValueEventListener(listenerWorks);
        }
    }
    private void loadContent() {
        dbWorks = dbReference.child("works");
        listenerWorks = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workList.clear();
                CompareWorkByName compare = new CompareWorkByName();
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    for(DataSnapshot works : category.getChildren()) {
                        Work work = works.getValue(Work.class);
                        work.setWorkTitle(works.getKey());
                        work.setGenre("Gêneros: " + works.child("genre").getValue());
                        work.setImgCoverWork(String.valueOf(works.child("cover").getValue()));
                        work.setDistributedBy("Por: " + works.child("distributedBy").getValue());
                        work.setType(String.valueOf(works.child("type").getValue()));
                        workList.add(work);
                        workList.sort(compare);
                    }
                }
                catalogAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CatalogActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        dbWorks.addValueEventListener(listenerWorks);
    }
    public void selectWork() {
        rvWorks.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                rvWorks,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CatalogAdapter.ViewHolder holder = new CatalogAdapter.ViewHolder(view);
                        String workTitle = String.valueOf(holder.getTxtWorkTitle().getText());
                        String workType = String.valueOf(holder.getTxtType().getText());
                        Intent intent = new Intent(CatalogActivity.this, WorkActivity.class);
                        intent.putExtra("WorkTitle", workTitle);
                        intent.putExtra("Category", workType);
                        startActivity(intent);
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {}
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}
                }
        ));
    }

    private void openMangas() {
        imgMangasIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MangaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                menu.selectMenu(txtMangasIcon, components);
            }
        });
    }
    private void openNovels() {
        imgNovelsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NovelActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                menu.selectMenu(txtNovelsIcon, components);
            }
        });
    }
    private void openLibrary() {
        imgLibraryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                menu.selectMenu(txtLibraryIcon, components);
            }
        });
    }
    private void openSettings(){
        imgSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                menu.selectMenu(txtSettingsIcon, components);
            }
        });
    }
}