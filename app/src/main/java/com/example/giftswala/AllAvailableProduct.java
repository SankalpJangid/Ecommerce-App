package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AllAvailableProduct extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_available_product);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recylelist);
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        TextView show_mail = findViewById(R.id.adminLoginEmail);
        show_mail.setText(fauth.getCurrentUser().getEmail());

        ImageView h = findViewById(R.id.home_screen2);
        h.setVisibility(View.GONE);
        ImageView g = findViewById(R.id.product_screen);
        g.setVisibility(View.GONE);
        TextView ht = findViewById(R.id.product_text);
        ht.setTextColor(Color.parseColor("#DC2D33"));

        Query query = fstore.collection("all_products").orderBy("product_timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<FetchAllProductCollection> options = new FirestoreRecyclerOptions.Builder<FetchAllProductCollection>().setQuery(query,FetchAllProductCollection.class).build();
        adapter = new FirestoreRecyclerAdapter<FetchAllProductCollection, AvailableProductViewHolder>(options) {
            @NonNull
            @Override
            public AvailableProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_available_product_layout,parent,false);
                return new AvailableProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AvailableProductViewHolder holder, int position, @NonNull FetchAllProductCollection model) {
                String price = "\u20B9"+model.getProduct_price();
                holder.t1.setText(model.getProduct_name());
                holder.t2.setText(price);
                Glide.with(AllAvailableProduct.this).load(model.getProduct_image()).into(holder.i1);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToSinglePageActivity(model.getProduct_name(),model.getProduct_collection());
                    }
                });
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);
    }

    private void goToSinglePageActivity(String product_name, String product_collection) {
        Intent intent = new Intent(AllAvailableProduct.this,SingleProductPage.class);
        intent.putExtra("collection_name",product_collection);
        intent.putExtra("document_name",product_name);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
    public void addCollection(View view){
        startActivity(new Intent(getApplicationContext(), AddCollection.class));
    }
    public void savedAddress(View view){
        startActivity(new Intent(getApplicationContext(),SavedAddress.class));
    }

    public void Cart(View view){
        startActivity(new Intent(getApplicationContext(),CartPageAll.class));
    }

    public void clickOrderHistory(View view){
        startActivity(new Intent(getApplicationContext(),YourOrderHistory.class));
    }
    public void clickCurrentOrder(View view){
        startActivity(new Intent(getApplicationContext(),Current_Order.class));
    }


    class AvailableProductViewHolder extends RecyclerView.ViewHolder {
        ImageView i1;
        TextView t1,t2;
        CardView cardView;
        public AvailableProductViewHolder(@NonNull View itemView) {
            super(itemView);
            i1 = itemView.findViewById(R.id.productImage);
            t1 = itemView.findViewById(R.id.productName);
            t2 = itemView.findViewById(R.id.productPrice);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
    public void AllProducts(View view){
        recreate();
    }
    public void bottomHome(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

}
