package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class SingleProductPage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    ImageView thumb;
    TextView name,rate,desc;
    RatingBar ratingBar;
    TextView update;
    Button addBtn,decBtn,incBtn;
    LinearLayout l1,l2;
    String product_name,product_price,product_image,product_desc,product_ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_page);
        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();

        l1 = findViewById(R.id.addToCartLayout);
        l2 = findViewById(R.id.buttonLayout);

        drawerLayout = findViewById(R.id.drawer_layout);
        thumb = findViewById(R.id.product_image);
        name = findViewById(R.id.single_product_name);
        rate = findViewById(R.id.product_rate);
        desc = findViewById(R.id.product_description);
        ratingBar = findViewById(R.id.product_rating);

        String collection_name = getIntent().getStringExtra("collection_name");
        String document = getIntent().getStringExtra("document_name");

        addBtn = findViewById(R.id.cart);
        decBtn = findViewById(R.id.subtractBtn);
        incBtn = findViewById(R.id.addBtn);
        update = findViewById(R.id.textShow);

        check(document,l1,l2,update);

        ImageView imageView11 = findViewById(R.id.home_screen2);
        imageView11.setVisibility(View.GONE);
        ImageView g = findViewById(R.id.product_screen2);
        g.setVisibility(View.GONE);

        TextView show_mail = findViewById(R.id.adminLoginEmail);
        show_mail.setText(fauth.getCurrentUser().getEmail());

        String mail = fauth.getCurrentUser().getEmail();
        if (mail.equals("s@gmail.com")){
            findViewById(R.id.navbarID).setVisibility(View.VISIBLE);
            findViewById(R.id.usernavbarID).setVisibility(View.GONE);
        }else{
            findViewById(R.id.navbarID).setVisibility(View.GONE);
            findViewById(R.id.usernavbarID).setVisibility(View.VISIBLE);
        }

        DocumentReference doc = fstore.collection(collection_name).document(document);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                product_name = documentSnapshot.getString("product_name");
                product_price = documentSnapshot.getString("product_price");
                product_desc = documentSnapshot.getString("product_desc");
                product_image = documentSnapshot.getString("product_image");
                product_ratings = documentSnapshot.getString("product_rating");
                String new_product_price = "\u20b9"+product_price;
                name.setText(product_name);
                rate.setText(new_product_price);
                desc.setText(product_desc);
                Glide.with(SingleProductPage.this).load(product_image).into(thumb);
                ratingBar.setRating(Float.parseFloat(product_ratings));
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l1.setVisibility(View.GONE);
                l2.setVisibility(View.VISIBLE);
                String quantity = update.getText().toString();
                addInDatabase(product_name,product_image,product_price,quantity,product_ratings);

            }
        });

        incBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int k = Integer.parseInt(update.getText().toString());
                k = k+1;
                String s = String.valueOf(k);
                update.setText(s);
                String email = fauth.getCurrentUser().getEmail();
                DocumentReference doc = fstore.collection("add_to_cart").document(email+product_name);
                doc.update("product_quantity",s);
            }
        });

        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int k = Integer.parseInt(update.getText().toString());
                if(k==1){
                    l2.setVisibility(View.GONE);
                    l1.setVisibility(View.VISIBLE);
                    String email = fauth.getCurrentUser().getEmail();
                    DocumentReference doc = fstore.collection("add_to_cart").document(email+product_name);
                    doc.delete();
                }
                else{
                    k = k-1;
                    String s = String.valueOf(k);
                    update.setText(s);
                    String email = fauth.getCurrentUser().getEmail();
                    DocumentReference doc = fstore.collection("add_to_cart").document(email+product_name);
                    doc.update("product_quantity",s);
                }
            }
        });
    }

    private void addInDatabase(String product_name, String product_image, String product_price, String quantity, String product_ratings) {
        String email = fauth.getCurrentUser().getEmail();
        DocumentReference doc = fstore.collection("add_to_cart").document(email+product_name);
        Map<String,String> info = new HashMap<>();
        info.put("user_mail",email);
        info.put("product_name",product_name);
        info.put("product_price",product_price);
        info.put("product_image",product_image);
        info.put("product_rating",product_ratings);
        info.put("product_quantity",quantity);
        doc.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SingleProductPage.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SingleProductPage.this, "Failed to add", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        String document = getIntent().getStringExtra("document_name");
        check(document,l1,l2,update);
    }

    private void check(String product_name, LinearLayout l1, LinearLayout l2, TextView update) {
        String email = fauth.getCurrentUser().getEmail();
        DocumentReference doc = fstore.collection("add_to_cart").document(email+product_name);
        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String quantity = value.getString("product_quantity");
                if (quantity != null){
                    System.out.println("good");
                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.VISIBLE);
                    update.setText(quantity);
                }else{
                    System.out.println("bad");
                    l1.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.GONE);
                }
            }
        });
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
    public void savedAddress(View view){
        startActivity(new Intent(getApplicationContext(),SavedAddress.class));
    }
}
