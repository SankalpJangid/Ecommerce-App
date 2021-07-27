package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

public class YourOrderHistory extends AppCompatActivity {

    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;

    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_order_history);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recylelist);
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        ImageView imageView11 = findViewById(R.id.home_screen2);
        imageView11.setVisibility(View.GONE);
        ImageView g = findViewById(R.id.product_screen2);
        g.setVisibility(View.GONE);

        TextView show_mail = findViewById(R.id.adminLoginEmail);
        show_mail.setText(fauth.getCurrentUser().getEmail());

        String email = fauth.getCurrentUser().getEmail();
        if (email.equals("s@gmail.com")){
            findViewById(R.id.navbarID).setVisibility(View.VISIBLE);
            findViewById(R.id.usernavbarID).setVisibility(View.GONE);
            query = fstore.collection("completed_order");
        }else{
            findViewById(R.id.navbarID).setVisibility(View.GONE);
            findViewById(R.id.usernavbarID).setVisibility(View.VISIBLE);
            query = fstore.collection("completed_order").whereEqualTo("user_mail",email);
        }

        FirestoreRecyclerOptions<FetchAllCurrentOrder> options = new FirestoreRecyclerOptions.Builder<FetchAllCurrentOrder>().setQuery(query,FetchAllCurrentOrder.class).build();
        adapter = new FirestoreRecyclerAdapter<FetchAllCurrentOrder, OrderHistoryViewHolder>(options) {
            @NonNull
            @Override
            public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_layout,parent,false);
                return new OrderHistoryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position, @NonNull FetchAllCurrentOrder model) {
                holder.name.setText(model.getProduct_name());
                holder.history_id.setText(model.getOrder_ID());
                holder.price.setText(model.getProduct_price());
                holder.quant.setText(model.getProduct_quantity());
                holder.ema.setText(model.getUser_mail());
                holder.user_ad.setText(model.getUser_address());
                Glide.with(YourOrderHistory.this).load(model.getProduct_image()).into(holder.image);
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    public void clickCollection(View view){
        startActivity(new Intent(getApplicationContext(),AddItemIntoCollections.class));
    }

    public void clickProfile(View view){
        recreate();
    }

    public void clickLogout(View view){
        recreate();
    }

    public void addCollection(View view){
        startActivity(new Intent(getApplicationContext(), AddCollection.class));
    }

    public void Cart(View view){
        startActivity(new Intent(getApplicationContext(), CartPageAll.class));
    }

    public void clickCurrentOrder(View view){
        startActivity(new Intent(getApplicationContext(),Current_Order.class));
    }

    public void clickOrderHistory(View view){
        recreate();
    }

    class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView name,price,quant,ema,user_ad;
        TextView history_id;
        ImageView image;
        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            history_id = itemView.findViewById(R.id.order_history_id);
            name = itemView.findViewById(R.id.pname);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.thumbnail);
            user_ad = itemView.findViewById(R.id.order_history_user_address);
            ema = itemView.findViewById(R.id.order_user_mail);
            quant = itemView.findViewById(R.id.order_quantity);
        }
    }
    public void AllProducts(View view){
        startActivity(new Intent(getApplicationContext(),AllAvailableProduct.class));
    }
    public void bottomHome(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    public void savedAddress(View view){
        startActivity(new Intent(getApplicationContext(),SavedAddress.class));
    }

}
