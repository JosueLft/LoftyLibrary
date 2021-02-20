package br.com.reign.loftylibrary.utils;

import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MenuSelect {

    public void selectMenu(TextView component, List<TextView> components) {
        for(TextView txt : components){
            txt.setVisibility(View.GONE);
        }
        component.setVisibility(View.VISIBLE);
    }
}