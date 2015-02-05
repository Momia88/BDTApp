package com.coretronic.bdt.GoodFoodFinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.R;

public class GoodFoodFinderQuestionFragment extends Fragment {
    private View v;
    private Button backBtn,nextBtn;
    public static GoodFoodFinderQuestionFragment newInstance(int index) {
        GoodFoodFinderQuestionFragment f = new GoodFoodFinderQuestionFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        v = inflater.inflate(R.layout.goodfood_finder_fragment, container, false);
        TextView text = (TextView)v.findViewById(R.id.textquestion);
        int page=getShownIndex();
        if (page<5){
            text.setText(AppConfig.GoodFoodFinderQuestion[page]);
        }
        else{
            text.setText("");
        }
        return v;

    }



}
