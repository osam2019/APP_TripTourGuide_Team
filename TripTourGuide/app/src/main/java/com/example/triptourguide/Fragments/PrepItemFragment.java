package com.example.triptourguide.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.triptourguide.Models.ItemPrepRecycleAdapter;
import com.example.triptourguide.R;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrepItemFragment extends Fragment {

    RecyclerView itemPrepRecycleView;
    private Map<String, Set<String>> conditionToItemsMap;
    private Set<String> choosenActivities;


    public PrepItemFragment(Map<String, Set<String>> ConditionToItemMap, Set<String> ChoosenActivities) {
        conditionToItemsMap = ConditionToItemMap;
        choosenActivities = ChoosenActivities;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_prep_item_fragment, container, false);
        itemPrepRecycleView = view.findViewById(R.id.item_prep_recycle_fragment);


        Set<String> items = new HashSet<>();
        for (String condition : choosenActivities) {
            if (conditionToItemsMap.containsKey(condition))
                items.addAll(conditionToItemsMap.get(condition));
        }

        itemPrepRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ItemPrepRecycleAdapter itemPrepAdapter = new ItemPrepRecycleAdapter(items, getActivity(),
                (TextView) view.findViewById(R.id.remaining_item_count), itemPrepRecycleView);

        SwipeDismissRecyclerViewTouchListener onTouchDismissListener = new SwipeDismissRecyclerViewTouchListener.Builder(
                itemPrepRecycleView,
                new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(View view) {
                        int id = itemPrepRecycleView.getChildLayoutPosition(view);
                        itemPrepAdapter.removeItemOnIndex(id);

                    }
                })
                .setIsVertical(false)
                .setItemTouchCallback(
                        new SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack() {
                            @Override
                            public void onTouch(int index) {
                                // Do what you want when item be touched
                            }
                        })
                .setItemClickCallback(new SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack() {
                    @Override
                    public void onClick(int position) {
                        // Do what you want when item be clicked
                    }
                }).create();

        itemPrepRecycleView.setOnTouchListener(onTouchDismissListener);
        itemPrepRecycleView.setAdapter(itemPrepAdapter);

        return view;
    }

}
