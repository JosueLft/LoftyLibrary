package br.com.reign.loftylibrary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.fragments.catalog.CatalogFragment;
import br.com.reign.loftylibrary.fragments.config.SettingsActivity;
import br.com.reign.loftylibrary.fragments.index.IndexFragment;
import br.com.reign.loftylibrary.fragments.index.internal.AboutFragment;
import br.com.reign.loftylibrary.fragments.index.internal.ChaptersFragment;
import br.com.reign.loftylibrary.fragments.library.LibraryFragment;
import br.com.reign.loftylibrary.fragments.mangas.MangasFragment;
import br.com.reign.loftylibrary.fragments.mangas.ReadMangaChapterFragment;
import br.com.reign.loftylibrary.fragments.novels.NovelsFragment;
import br.com.reign.loftylibrary.fragments.novels.ReadNovelFragment;

public class MainActivity extends AppCompatActivity implements Comunication {
    // mangas section
    private TextView txtMangasIcon;
    private ImageView imgMangasIcon;
    private MangasFragment mangasFragment;
    private ReadMangaChapterFragment readMangaChapterFragment;
    // novels section
    private TextView txtNovelsIcon;
    private ImageView imgNovelsIcon;
    private NovelsFragment novelsFragment;
    private ReadNovelFragment readNovelFragment;
    // catalog section
    private TextView txtCatalogIcon;
    private ImageView imgCatalogIcon;
    private CatalogFragment catalogFragment;
    // library section
    private TextView txtLibraryIcon;
    private ImageView imgLibraryIcon;
    private LibraryFragment libraryFragment;
    // Fragments internos Indice
    private IndexFragment indexFragment;
    private ChaptersFragment chaptersFragment;
    private AboutFragment aboutFragment;

    // Activity Settings
    private SettingsActivity settingsActivity;
    private TextView txtSettingsIcon;
    private ImageView imgSettingsIcon;

    private Handler mainHandler = new Handler();

    // Google AdMob
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
    }

    public void initializeComponents() {
        // Text View
        txtMangasIcon = findViewById(R.id.txtMangasIcon);
        txtNovelsIcon = findViewById(R.id.txtNovelsIcon);
        txtCatalogIcon = findViewById(R.id.txtCatalogIcon);
        txtLibraryIcon = findViewById(R.id.txtLibraryIcon);
        txtSettingsIcon = findViewById(R.id.txtSettingsIcon);

        // Image View
        imgMangasIcon = findViewById(R.id.imgMangasIcon);
        imgNovelsIcon = findViewById(R.id.imgNovelsIcon);
        imgCatalogIcon = findViewById(R.id.imgCatalogIcon);
        imgLibraryIcon = findViewById(R.id.imgLibraryIcon);
        imgSettingsIcon = findViewById(R.id.imgSettingsIcon);

        // Fragments
        novelsFragment = new NovelsFragment();
        readNovelFragment = new ReadNovelFragment();
        mangasFragment = new MangasFragment();
        readMangaChapterFragment = new ReadMangaChapterFragment();
        catalogFragment = new CatalogFragment();
        libraryFragment = new LibraryFragment();

        // Fragments internos index
        indexFragment = new IndexFragment();
        chaptersFragment = new ChaptersFragment();
        aboutFragment = new AboutFragment();

        // activity
        settingsActivity = new SettingsActivity();

        //Configurar objeto para o fragmento
        // Inicializando tela inicial
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContent, mangasFragment);
        transaction.commit();

        // invoca metodos de utilização do menu inferior
        openMangas();
        openNovels();
        openCatalog();
        openLibrary();
        openSettings();
    }

    public void openMangas() {
        txtMangasIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContent, mangasFragment);
                transaction.commit();

                txtMangasIcon.setVisibility(View.VISIBLE);
                txtNovelsIcon.setVisibility(View.GONE);
                txtCatalogIcon.setVisibility(View.GONE);
                txtLibraryIcon.setVisibility(View.GONE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
        imgMangasIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContent, mangasFragment);
                transaction.commit();

                txtMangasIcon.setVisibility(View.VISIBLE);
                txtNovelsIcon.setVisibility(View.GONE);
                txtCatalogIcon.setVisibility(View.GONE);
                txtLibraryIcon.setVisibility(View.GONE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
    }

    public void openNovels() {
        txtNovelsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContent, novelsFragment);
                transaction.commit();

                txtMangasIcon.setVisibility(View.GONE);
                txtNovelsIcon.setVisibility(View.VISIBLE);
                txtCatalogIcon.setVisibility(View.GONE);
                txtLibraryIcon.setVisibility(View.GONE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
        imgNovelsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContent, novelsFragment);
                transaction.commit();

                txtMangasIcon.setVisibility(View.GONE);
                txtNovelsIcon.setVisibility(View.VISIBLE);
                txtCatalogIcon.setVisibility(View.GONE);
                txtLibraryIcon.setVisibility(View.GONE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
    }

    public void openCatalog() {
        txtCatalogIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContent, catalogFragment);
                transaction.commit();

                txtMangasIcon.setVisibility(View.GONE);
                txtNovelsIcon.setVisibility(View.GONE);
                txtCatalogIcon.setVisibility(View.VISIBLE);
                txtLibraryIcon.setVisibility(View.GONE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
        imgCatalogIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContent, catalogFragment);
                transaction.commit();

                txtMangasIcon.setVisibility(View.GONE);
                txtNovelsIcon.setVisibility(View.GONE);
                txtCatalogIcon.setVisibility(View.VISIBLE);
                txtLibraryIcon.setVisibility(View.GONE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
    }

    public void openLibrary() {
        txtLibraryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContent, libraryFragment);
                transaction.commit();

                txtMangasIcon.setVisibility(View.GONE);
                txtNovelsIcon.setVisibility(View.GONE);
                txtCatalogIcon.setVisibility(View.GONE);
                txtLibraryIcon.setVisibility(View.VISIBLE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
        imgLibraryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContent, libraryFragment);
                transaction.commit();

                txtMangasIcon.setVisibility(View.GONE);
                txtNovelsIcon.setVisibility(View.GONE);
                txtCatalogIcon.setVisibility(View.GONE);
                txtLibraryIcon.setVisibility(View.VISIBLE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
    }

    public void openSettings(){
        txtSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

                txtMangasIcon.setVisibility(View.GONE);
                txtNovelsIcon.setVisibility(View.GONE);
                txtCatalogIcon.setVisibility(View.GONE);
                txtLibraryIcon.setVisibility(View.GONE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
        imgSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

                txtMangasIcon.setVisibility(View.GONE);
                txtNovelsIcon.setVisibility(View.GONE);
                txtCatalogIcon.setVisibility(View.GONE);
                txtLibraryIcon.setVisibility(View.GONE);
                txtSettingsIcon.setVisibility(View.GONE);
            }
        });
    }

    public void captureTitle(String workTitle, String workType) {
        indexFragment.receiveTitle(workTitle, workType);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContent, indexFragment);
        transaction.commit();
    }

    public void captureChapters(String workTitle, String workType) {
        chaptersFragment.receiveChapters(workTitle, workType);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.frameIndexContent, chaptersFragment)
                .addToBackStack(null)
                .commit();
    }

    public void captureInformations(String workType, String workGenre, String workDistributedBy) {
        aboutFragment.receiveInformations(workType, workGenre, workDistributedBy);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.frameIndexContent, aboutFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void invokeSelectedChapter(String summon, String type, String workTitle, String chapterTitle) {
        if(type.equalsIgnoreCase("mangas")) {
            readMangaChapterFragment.loadChapter(summon, type, workTitle, chapterTitle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction
                    .replace(R.id.frameContent, readMangaChapterFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            readNovelFragment.loadChapter(summon, type, workTitle, chapterTitle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction
                    .replace(R.id.frameContent, readNovelFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    public void initAdMob() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if(mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }
}