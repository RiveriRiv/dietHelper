package com.example.diet_helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Diet;

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

        TextView text = view.findViewById(R.id.run_diet);
        text.setText(this.diets.get(position).getName());
        text.setOnClickListener(v -> {
            Intent myIntent = new Intent(activity, ProductActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            myIntent.putExtra("diet", String.valueOf(this.diets.get(position).getId()));
            activity.getApplicationContext().startActivity(myIntent);
        });

        Button btn = view.findViewById(R.id.delete_diet_button);
        btn.setTag(position);
        btn.setOnClickListener(v -> {
            Amplify.DataStore.delete(this.diets.get(position),
                    success -> Log.i("MyAmplifyApp", "Created a new diet successfully"),
                    error -> Log.e("MyAmplifyApp",  "Error creating diet", error)
            );

            this.diets.remove(position);
        });

        notifyDataSetChanged();

        return view;
    }
}
