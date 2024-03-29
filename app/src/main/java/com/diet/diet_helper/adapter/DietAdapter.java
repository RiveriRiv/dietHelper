package com.diet.diet_helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.diet.diet_helper.MyAmplifyApp;
import com.diet.diet_helper.dao.AppDatabase;
import com.diet.diet_helper.dao.DietDao;
import com.diet.diet_helper.model.Diet;
import com.diet.diet_helper.ProductActivity;
import com.diet.diet_helper.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class DietAdapter extends BaseAdapter {

    private final List<Diet> diets;
    private final LayoutInflater inflater;
    private final Activity activity;

    public DietAdapter(Activity activity, List<Diet> diets) {
        this.activity = activity;
        this.diets = diets;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(Diet diet) {
        this.diets.add(diet);
    }

    public int getCount() {
        return this.diets.size();
    }

    public Diet getItem(int position) {
        return this.diets.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = this.inflater.inflate(R.layout.activity_listview, null);
        }

        TextInputEditText text = view.findViewById(R.id.label);
        text.setText(this.diets.get(position).getName());

        MaterialButton runBtn = view.findViewById(R.id.run_diet);
        runBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(activity, ProductActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            myIntent.putExtra("diet", this.diets.get(position).getId());
            activity.getApplicationContext().startActivity(myIntent);
        });

        Button deleteBtn = view.findViewById(R.id.delete_diet_button);
        deleteBtn.setTag(position);

        deleteBtn.setOnClickListener(v -> {
            AppDatabase appDatabase = ((MyAmplifyApp) activity.getApplicationContext()).getAppDatabase();
            final DietDao dietDao = appDatabase.dietDao();

            AsyncTask.execute(() -> {
                dietDao.deleteDiet(this.diets.get(position));

                activity.runOnUiThread(() -> {
                    this.diets.remove(position);
                    notifyDataSetChanged();
                });
            });
        });

        Button editBtn = view.findViewById(R.id.edit_diet);
        editBtn.setTag(position);

        editBtn.setOnClickListener(v -> {
            text.setFocusable(true);
            text.setFocusableInTouchMode(true);
            text.requestFocus();

            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(text, InputMethodManager.SHOW_IMPLICIT);
        });

        text.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                text.setFocusable(false);
                text.setFocusableInTouchMode(false);
                return true;
            }
            return false;
        });

        text.setOnEditorActionListener((v, actionId, event) -> {
            AppDatabase appDatabase = ((MyAmplifyApp) activity.getApplicationContext()).getAppDatabase();
            final DietDao dietDao = appDatabase.dietDao();

                AsyncTask.execute(() -> {
                    Diet diet = diets.get(position);
                    diet.setName(text.getText().toString());
                    dietDao.updateDiet(diet);
                });
                text.clearFocus();
                return true;
        });

        View rootView = activity.findViewById(android.R.id.content);

        rootView.setOnClickListener(v -> {
            text.clearFocus();
        });

        notifyDataSetChanged();

        return view;
    }
}
