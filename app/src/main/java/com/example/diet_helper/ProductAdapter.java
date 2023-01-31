package com.example.diet_helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Diet;
import com.amplifyframework.datastore.generated.model.Product;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private final Diet diet;
    private final List<Product> products;
    private final LayoutInflater inflater;
    private final Activity activity;

    public ProductAdapter(Activity activity, List<Product> products, Diet diet) {
        this.diet = diet;
        this.activity = activity;
        this.products = products;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(Product product) {
        this.products.add(product);
    }

    public int getCount() {
        return this.products.size();
    }

    public Product getItem(int position) {
        return this.products.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = this.inflater.inflate(R.layout.activity_listview, null);
        }

        TextView text = view.findViewById(R.id.label);
        text.setText(this.products.get(position).getName());

        Button btn = view.findViewById(R.id.delete_diet_button);
        btn.setTag(position);
        btn.setOnClickListener(v -> {
            Amplify.DataStore.delete(this.products.get(position),
                    success -> Log.i("MyAmplifyApp", "Created a new product successfully"),
                    error -> Log.e("MyAmplifyApp", "Error creating product", error)
            );

            this.products.remove(position);
            notifyDataSetChanged();

        });

        return view;
    }
}
