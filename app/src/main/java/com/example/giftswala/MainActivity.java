package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    FirebaseFirestore fstore;
    FirestoreRecyclerAdapter adapter;
    FirestoreRecyclerAdapter adapter2;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recylelist);
        recyclerView2 = findViewById(R.id.recylelist2);
        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();

        TextView show_mail = findViewById(R.id.adminLoginEmail);
        show_mail.setText(fauth.getCurrentUser().getEmail());

        ImageView h = findViewById(R.id.home_screen);
        h.setVisibility(View.GONE);
        ImageView g = findViewById(R.id.product_screen2);
        g.setVisibility(View.GONE);
        TextView ht = findViewById(R.id.home_text);
        ht.setTextColor(Color.parseColor("#DC2D33"));

        String email = fauth.getCurrentUser().getEmail();
        if (email.equals("s@gmail.com")){
            findViewById(R.id.navbarID).setVisibility(View.VISIBLE);
            findViewById(R.id.usernavbarID).setVisibility(View.GONE);
        }else{
            findViewById(R.id.navbarID).setVisibility(View.GONE);
            findViewById(R.id.usernavbarID).setVisibility(View.VISIBLE);
        }



        Query query = fstore.collection("trending_product").orderBy("product_timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<trending_product> options = new FirestoreRecyclerOptions.Builder<trending_product>().setQuery(query,trending_product.class).build();
        adapter = new FirestoreRecyclerAdapter<trending_product, trending_productViewHolder>(options) {
            @NonNull
            @Override
            public trending_productViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_layout,parent,false);
                return new trending_productViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull trending_productViewHolder holder, int position, @NonNull trending_product model) {
                holder.t1.setText(model.getProduct_name());
                Glide.with(MainActivity.this).load(model.getProduct_image()).into(holder.img);
                String price = "\u20b9"+model.getProduct_price();
                holder.t2.setText(price);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startSingleActivity(model.getProduct_collection(),model.getProduct_name());
                    }
                });
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);



        Query query2 = fstore.collection("collections").orderBy("collection_timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<All_Collections> options2  = new FirestoreRecyclerOptions.Builder<All_Collections>().setQuery(query2,All_Collections.class).build();
        adapter2 = new FirestoreRecyclerAdapter<All_Collections, All_CollectionsViewHolder>(options2) {
            @NonNull
            @Override
            public All_CollectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_layout,parent,false);
                return new All_CollectionsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull All_CollectionsViewHolder holder, int position, @NonNull All_Collections model) {
                holder.coltext.setText(model.getCollection_name());
                Glide.with(MainActivity.this).load(model.getCollection_image()).into(holder.colimg);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startProductPage(model.getCollection_name());
                    }
                });
            }
        };
        recyclerView2.setHasFixedSize(false);
        recyclerView2.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView2.setAdapter(adapter2);


    }

    private void startSingleActivity(String product_collection, String product_name) {
        Intent intent = new Intent(MainActivity.this,SingleProductPage.class);
        intent.putExtra("collection_name",product_collection);
        intent.putExtra("document_name",product_name);
        startActivity(intent);
    }

    private void startProductPage(String collection_name) {
        Intent intent = new Intent(MainActivity.this,ProductPage.class);
        intent.putExtra("name",collection_name);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter2.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter2.stopListening();
    }

    public void clickMenu(View view){
        openDrawer(drawerLayout);
    }

    static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);

    }
    public void clickLogo(View view){
        closeDrawer(drawerLayout);
    }

    static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void clickHome(View view){
        recreate();
    }

    public void clickCollection(View view){
        startActivity(new Intent(getApplicationContext(),AddItemIntoCollections.class));
    }

    public void clickProfile(View view){
        recreate();
    }

    public void clickLogout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
    }

    class trending_productViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView t1,t2;
        CardView cardView;
        public trending_productViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            t1 = itemView.findViewById(R.id.product);
            t2 = itemView.findViewById(R.id.productprice);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public void addCollection(View view){
        startActivity(new Intent(getApplicationContext(), AddCollection.class));
    }

    class All_CollectionsViewHolder extends RecyclerView.ViewHolder {
        ImageView colimg;
        TextView coltext;
        CardView cardView;
        public All_CollectionsViewHolder(@NonNull View itemView) {
            super(itemView);
            colimg = itemView.findViewById(R.id.collectionImage);
            coltext = itemView.findViewById(R.id.collectionName);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public void savedAddress(View view){
        startActivity(new Intent(getApplicationContext(),SavedAddress.class));
    }



    public void clickOrderHistory(View view){
        startActivity(new Intent(getApplicationContext(),YourOrderHistory.class));
    }
    public void clickCurrentOrder(View view){
        startActivity(new Intent(getApplicationContext(),Current_Order.class));
    }

    public void AllProducts(View view){
        startActivity(new Intent(getApplicationContext(),AllAvailableProduct.class));
    }
    public void bottomHome(View view){
        recreate();
    }
    public void Cart(View view){
        startActivity(new Intent(getApplicationContext(),CartPageAll.class));
    }
}
