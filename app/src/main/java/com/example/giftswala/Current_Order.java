package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class Current_Order extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    FirestoreRecyclerAdapter adapter;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current__order);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recylelist);


        ImageView imageView11 = findViewById(R.id.home_screen2);
        imageView11.setVisibility(View.GONE);

        ImageView g = findViewById(R.id.product_screen2);
        g.setVisibility(View.GONE);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        TextView show_mail = findViewById(R.id.adminLoginEmail);
        show_mail.setText(fauth.getCurrentUser().getEmail());

        String email = fauth.getCurrentUser().getEmail();
        if (email.equals("s@gmail.com")){
            findViewById(R.id.navbarID).setVisibility(View.VISIBLE);
            findViewById(R.id.usernavbarID).setVisibility(View.GONE);
            query = fstore.collection("current_order");
        }else{
            findViewById(R.id.navbarID).setVisibility(View.GONE);
            findViewById(R.id.usernavbarID).setVisibility(View.VISIBLE);
            query = fstore.collection("current_order").whereEqualTo("user_mail",email);
        }


        FirestoreRecyclerOptions<FetchAllCurrentOrder> options = new FirestoreRecyclerOptions.Builder<FetchAllCurrentOrder>().setQuery(query,FetchAllCurrentOrder.class).build();
        adapter = new FirestoreRecyclerAdapter<FetchAllCurrentOrder, FetchAllCurrentOrderViewHolder>(options) {
            @NonNull
            @Override
            public FetchAllCurrentOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_order_layout,parent,false);
                return new FetchAllCurrentOrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FetchAllCurrentOrderViewHolder holder, int position, @NonNull FetchAllCurrentOrder model) {
                check_user_login(model.getUser_mail(),holder.cancel,holder.deleivered,holder.l1,holder.user,model.getUser_location(),model.getUser_address(),holder.map,holder.l2,holder.user_add);
                holder.id.setText(model.getOrder_ID());
                holder.name.setText(model.getProduct_name());
                holder.price.setText(model.getProduct_price());
                holder.quant.setText(model.getProduct_quantity());
                Glide.with(Current_Order.this).load(model.getProduct_image()).into(holder.image);
                holder.deleivered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delevered(model.getOrder_ID(),model.getUser_mail(),model.getProduct_name(),model.getProduct_price(),model.getProduct_quantity(),model.getProduct_image(),model.getUser_address());
                    }
                });
                holder.map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String map = model.getUser_location();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                        startActivity(intent);

                    }
                });
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void check_user_login(String user_mail, Button cancel, Button deleivered, LinearLayout l1, TextView user, String user_location, String user_address, Button map, LinearLayout l2, TextView user_add) {
        String admin = fauth.getCurrentUser().getEmail();
        if (admin.equals("s@gmail.com")){
            cancel.setVisibility(View.VISIBLE);
            deleivered.setVisibility(View.VISIBLE);
            l1.setVisibility(View.VISIBLE);
            user.setText(user_mail);
            l2.setVisibility(View.VISIBLE);
            user_add.setText(user_address);
            map.setVisibility(View.VISIBLE);
        }else{
            cancel.setVisibility(View.GONE);
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.GONE);
            map.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            user_add.setVisibility(View.GONE);
            deleivered.setVisibility(View.GONE);
        }
    }

    private void delevered(String order_id, String user_mail, String product_name, String product_price, String product_quantity, String product_image, String user_address) {
        DocumentReference doc = fstore.collection("current_order").document(order_id);
        doc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DocumentReference doc2 = fstore.collection("completed_order").document(order_id);
                Map<String,String> info = new HashMap<>();
                info.put("order_ID",order_id);
                info.put("user_mail",user_mail);
                info.put("product_name", product_name);
                info.put("product_price", product_price);
                info.put("product_quantity",product_quantity);
                info.put("product_image",product_image);
                info.put("user_address",user_address);
                doc2.set(info);
                Toast.makeText(Current_Order.this, "Successfully delivered", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Current_Order.this, "Failed to delivered", Toast.LENGTH_SHORT).show();
            }
        });
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

    public void savedAddress(View view){
        startActivity(new Intent(getApplicationContext(),SavedAddress.class));
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

    class FetchAllCurrentOrderViewHolder extends RecyclerView.ViewHolder {
        TextView id,name,price,quant,user,user_add;
        ImageView image;
        LinearLayout l1,l2;
        Button cancel, deleivered,map;
        public FetchAllCurrentOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.order_id);
            name = itemView.findViewById(R.id.pname);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.thumbnail);
            quant = itemView.findViewById(R.id.order_quantity);
            cancel = itemView.findViewById(R.id.cancel);
            deleivered = itemView.findViewById(R.id.delivered);
            l1 = itemView.findViewById(R.id.forAdminUse);
            l2 = itemView.findViewById(R.id.user_address);
            user_add = itemView.findViewById(R.id.order_user_address);
            map = itemView.findViewById(R.id.maplocation);
            user = itemView.findViewById(R.id.order_user);
        }
    }

    public void clickOrderHistory(View view){
        startActivity(new Intent(getApplicationContext(),YourOrderHistory.class));
    }
    public void clickCurrentOrder(View view){
        recreate();
    }

    public void AllProducts(View view){
        startActivity(new Intent(getApplicationContext(),AllAvailableProduct.class));
    }
    public void bottomHome(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

}
