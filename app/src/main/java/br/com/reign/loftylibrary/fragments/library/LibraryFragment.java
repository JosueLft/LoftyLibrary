package br.com.reign.loftylibrary.fragments.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.adapter.LastChapterAdapter;
import br.com.reign.loftylibrary.adapter.LibraryAdapter;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.helper.ChaptersDAO;
import br.com.reign.loftylibrary.model.Chapter;
import br.com.reign.loftylibrary.model.Manga;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.utils.CompareDate;
import br.com.reign.loftylibrary.utils.CompareName;
import br.com.reign.loftylibrary.utils.RecyclerItemClickListener;

public class LibraryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView rvLibrary;
    private RecyclerView rvLastChapter;
    private TextView txtRecentRead;
    private TextView txtFollowing;
    private List<Chapter> listChapters = new ArrayList<>();
    private List<Chapter> listLastChapters = new ArrayList<>();
    private LibraryAdapter libraryAdapter;
    private LastChapterAdapter lastChapterAdapter;

    private Comunication comunication;

    public LibraryFragment() {}

    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        initializeComponents(view);

        if(!listChapters.isEmpty()) {
            loadChaptersAlreadyRead();
            loadLastChaptersRead();
        } else {
            txtRecentRead.setText("Nenhuma obra cadastrada!");
            txtFollowing.setVisibility(View.GONE);
        }

        selectWork(view);
        return view;
    }

    private void initializeComponents(View view) {
        rvLibrary = view.findViewById(R.id.rvLibrary);
        rvLastChapter = view.findViewById(R.id.rvLastChapters);

        txtRecentRead = view.findViewById(R.id.txtRecentRead);
        txtFollowing = view.findViewById(R.id.txtFollowing);

        ChaptersDAO chaptersDAO = new ChaptersDAO(getActivity().getApplicationContext());
        listChapters = chaptersDAO.chaptersList();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadChaptersAlreadyRead() {
        CompareDate compare = new CompareDate();
        listChapters.sort(compare);
        libraryAdapter = new LibraryAdapter(listChapters, getActivity().getApplicationContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvLibrary.setLayoutManager(layoutManager);
        rvLibrary.setHasFixedSize(true);
        rvLibrary.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayout.HORIZONTAL));
        rvLibrary.setAdapter(libraryAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadLastChaptersRead() {
        CompareDate compare = new CompareDate();
        listChapters.sort(compare);
        List<Chapter> list = listChapters;

        for(Chapter chapter : list){
            if(!listLastChapters.contains(chapter)) {
                listLastChapters.add(chapter);
            }
        }
        CompareName compareName = new CompareName();
        listLastChapters.sort(compareName);

        lastChapterAdapter = new LastChapterAdapter(listLastChapters, getActivity().getApplicationContext());

        LinearLayoutManager layout = new LinearLayoutManager(getActivity().getApplicationContext());
        rvLastChapter.setLayoutManager(layout);
        rvLastChapter.setHasFixedSize(true);
        rvLastChapter.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayout.VERTICAL));
        rvLastChapter.setAdapter(lastChapterAdapter);
    }

    public void selectWork(View view) {
        rvLastChapter.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity().getApplicationContext(),
                rvLastChapter,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onItemClick(View view, int position) {
                        LastChapterAdapter.MyViewHolder holder = new LastChapterAdapter.MyViewHolder(view);
                        String workTitle = String.valueOf(holder.getTxtWorkTitle().getText());
                        String lastChapter = String.valueOf(holder.getTxtLastChapter().getText());


                        if (comunication != null) {
                            comunication.invokeSelectedChapter("mangasFragment", "mangas", workTitle, lastChapter);
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onLongItemClick(View view, int position) {
                        LastChapterAdapter.MyViewHolder holder = new LastChapterAdapter.MyViewHolder(view);
                        String workTitle = String.valueOf(holder.getTxtWorkTitle().getText());

                        Chapter chapter = new Chapter();

                        chapter.setWorkTitle(workTitle);

//                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity().getApplicationContext());
//                        dialog.setTitle("Remover: " + workTitle);
//                        dialog.setMessage("Deseja Remover a obra: " + workTitle + "?");
//
//                        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
                        ChaptersDAO chaptersDAO = new ChaptersDAO(getActivity().getApplicationContext());
                        if(chaptersDAO.delete(chapter)) {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Sucesso ao remover obra!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Erro ao remover obra!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        if(!listChapters.isEmpty()) {
                            loadChaptersAlreadyRead();
                            loadLastChaptersRead();
                        } else {
                            txtRecentRead.setText("Nenhuma obra cadastrada!");
                            txtFollowing.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }
}