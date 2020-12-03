package br.com.reign.loftylibrary.fragments.catalog;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.adapter.CatalogAdapter;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.fragments.index.IndexFragment;
import br.com.reign.loftylibrary.model.Work;
import br.com.reign.loftylibrary.utils.CompareWorkByName;
import br.com.reign.loftylibrary.utils.RecyclerItemClickListener;

public class CatalogFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText editSearch;
    private RecyclerView rvWorks;

    private CatalogAdapter catalogAdapter;
    private ArrayList<Work> workList = new ArrayList<>();
    private DatabaseReference dbWorks;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listenerWorks;
    private Comunication comunication;

    // Fragmentos
    private IndexFragment indexFragment;

    public CatalogFragment() {}

    public static CatalogFragment newInstance(String param1, String param2) {
        CatalogFragment fragment = new CatalogFragment();
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
    public void onStart() {
        super.onStart();
        editSearch.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        initializeComponents(view);
        selectWork(view);
        search();

        return view;
    }

    private void search() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

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
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

            dbWorks.addValueEventListener(listenerWorks);
        }
    }

    private void initializeComponents(View view) {
        // EditText
        editSearch = view.findViewById(R.id.editSearch);

        // RecyclerView
        rvWorks = view.findViewById(R.id.rvCatalogWorks);
        rvWorks.setHasFixedSize(true);
        rvWorks.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Adapters
        catalogAdapter = new CatalogAdapter(workList, getActivity());

        rvWorks.setAdapter(catalogAdapter);

        // Class
        indexFragment = new IndexFragment();
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
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        dbWorks.addValueEventListener(listenerWorks);
    }

    public void selectWork(View view) {
        rvWorks.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity().getApplicationContext(),
                rvWorks,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CatalogAdapter.ViewHolder holder = new CatalogAdapter.ViewHolder(view);
                        String workTitle = String.valueOf(holder.getTxtWorkTitle().getText());
                        String workType = String.valueOf(holder.getTxtType().getText());

                        if(comunication != null) {
                            comunication.captureTitle(workTitle, workType);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }
}