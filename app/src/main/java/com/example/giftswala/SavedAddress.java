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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;

public class SavedAddress extends AppCompatActivity {

    Button b1;
    DrawerLayout drawerLayout;
    FirebaseFirestore fstore;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_address);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recycleaddress);
        b1 = findViewById(R.id.newPage);
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        String email = fauth.getCurrentUser().getEmail();

        ImageView imageView11 = findViewById(R.id.home_screen2);
        imageView11.setVisibility(View.GONE);
        ImageView g = findViewById(R.id.product_screen2);
        g.setVisibility(View.GONE);

        TextView show_mail = findViewById(R.id.adminLoginEmail);
        show_mail.setText(fauth.getCurrentUser().getEmail());

        if (email.equals("s@gmail.com")){
            findViewById(R.id.navbarID).setVisibility(View.VISIBLE);
            findViewById(R.id.usernavbarID).setVisibility(View.GONE);
        }else{
            findViewById(R.id.navbarID).setVisibility(View.GONE);
            findViewById(R.id.usernavbarID).setVisibility(View.VISIBLE);
        }



        System.out.println(email);

        Query query = fstore.collection("saved_address").whereEqualTo("user_mail",email);
        FirestoreRecyclerOptions<AddressInformation> options = new FirestoreRecyclerOptions.Builder<AddressInformation>().setQuery(query,AddressInformation.class).build();
        adapter = new FirestoreRecyclerAdapter<AddressInformation, AddressInformationViewHolder>(options) {
            @NonNull
            @Override
            public AddressInformationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_address_layout_all,parent,false);
                return new AddressInformationViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AddressInformationViewHolder holder, int position, @NonNull AddressInformation model) {
                holder.t1.setText(model.getUser_home());
                holder.t2.setText(model.getUser_area());
                holder.b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remove(model.getAddress_id());
                    }
                });
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddressPage.class));
            }
        });

    }

    private void remove(String address_id) {
        DocumentReference doc = fstore.collection("saved_address").document(address_id);
        doc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SavedAddress.this, "Successfully Removed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SavedAddress.this, "Failed To Remove", Toast.LENGTH_SHORT).show();
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

    class AddressInformationViewHolder extends RecyclerView.ViewHolder {
        TextView t1,t2;
        Button b1;
        public AddressInformationViewHolder(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.address_home);
            t2 = itemView.findViewById(R.id.address_detail);
            b1 = itemView.findViewById(R.id.remove_address);
        }
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

    public void savedAddress(View view){
        recreate();
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
    public void AllProducts(View view){
        startActivity(new Intent(getApplicationContext(),AllAvailableProduct.class));
    }
    public void bottomHome(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

}
