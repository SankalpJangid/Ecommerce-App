package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProductPage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView t1;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        drawerLayout = findViewById(R.id.drawer_layout);
        t1 = findViewById(R.id.name);
        fauth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recylelist);
        fstore = FirebaseFirestore.getInstance();
        String name = getIntent().getStringExtra("name");
        t1.setText(name.toString());

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
        }else{
            findViewById(R.id.navbarID).setVisibility(View.GONE);
            findViewById(R.id.usernavbarID).setVisibility(View.VISIBLE);
        }

        Query query = fstore.collection(name).orderBy("product_timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<FetchAllProductCollection> options = new FirestoreRecyclerOptions.Builder<FetchAllProductCollection>().setQuery(query,FetchAllProductCollection.class).build();
        adapter = new FirestoreRecyclerAdapter<FetchAllProductCollection, FetchViewHolder>(options) {
            @NonNull
            @Override
            public FetchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_page,parent,false);
                return new FetchViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FetchViewHolder holder, int position, @NonNull FetchAllProductCollection model) {
                userCheck(holder.remove,holder.add_to_trending);
                holder.t2.setText(model.getProduct_name());
                String price = "\u20b9"+model.getProduct_price();
                holder.t3.setText(price);
                Glide.with(ProductPage.this).load(model.getProduct_image()).into(holder.i1);
                holder.ratingBar.setRating(Float.parseFloat(model.getProduct_rating()));
                check(model.getProduct_name(),holder.l1,holder.l2,holder.update);
                holder.add_to_trending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addToTrending(model.getProduct_image(),model.getProduct_name(),model.getProduct_price());
                    }
                });
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeProduct(model.getProduct_name());
                    }
                });
                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToSinglePageProduct(model.getProduct_name());
                    }
                });
                holder.addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.l1.setVisibility(View.GONE);
                        holder.l2.setVisibility(View.VISIBLE);
                        String quantity = holder.update.getText().toString();
                        addInDatabase(model.getProduct_name(),model.getProduct_price(),model.getProduct_image(),model.getProduct_rating(),quantity);
                    }
                });
                holder.incBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int k = Integer.parseInt(holder.update.getText().toString());
                        k = k+1;
                        String s = String.valueOf(k);
                        holder.update.setText(s);
                        String email = fauth.getCurrentUser().getEmail();
                        DocumentReference doc = fstore.collection("add_to_cart").document(email+model.getProduct_name());
                        doc.update("product_quantity",s);
                    }
                });
                holder.decBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int k = Integer.parseInt(holder.update.getText().toString());
                        if(k==1){
                            holder.l2.setVisibility(View.GONE);
                            holder.l1.setVisibility(View.VISIBLE);
                            String email = fauth.getCurrentUser().getEmail();
                            DocumentReference doc = fstore.collection("add_to_cart").document(email+model.getProduct_name());
                            doc.delete();
                        }
                        else{
                            k = k-1;
                            String s = String.valueOf(k);
                            holder.update.setText(s);
                            String email = fauth.getCurrentUser().getEmail();
                            DocumentReference doc = fstore.collection("add_to_cart").document(email+model.getProduct_name());
                            doc.update("product_quantity",s);
                        }
                    }
                });
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void addToTrending(String product_image, String product_name, String product_price) {
        String name = getIntent().getStringExtra("name");
        AlertDialog.Builder ad = new AlertDialog.Builder(ProductPage.this);
        ad.setTitle("Add to Trending List");
        ad.setMessage("Are you sure you want to add in trending list");
        ad.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference doc = fstore.collection("trending_product").document(product_name);
                Map<String,String> info = new HashMap<>();
                info.put("product_collection",name);
                info.put("product_name",product_name);
                info.put("product_image",product_image);
                info.put("product_price",product_price);
                doc.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        doc.update("product_timestamp", FieldValue.serverTimestamp());
                        Toast.makeText(ProductPage.this, "Successfully Added in Trending List", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductPage.this, "Failed To Add", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog a = ad.create();
        a.show();

    }

    private void removeProduct(String product_name) {
        String name = getIntent().getStringExtra("name");
        AlertDialog.Builder ad = new AlertDialog.Builder(ProductPage.this);
        ad.setTitle("Remove Product");
        ad.setMessage("Are you sure you want to remove the product");
        ad.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference doc2 = fstore.collection("all_products").document(product_name);
                DocumentReference doc = fstore.collection(name).document(product_name);
                doc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        doc2.delete();
                        Toast.makeText(ProductPage.this, "Successfully Removed", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductPage.this, "Failed To Remove", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog a = ad.create();
        a.show();



    }

    private void userCheck(Button remove, Button add_to_trending) {
        String mail = fauth.getCurrentUser().getEmail();
        if (mail.equals("s@gmail.com")){
            remove.setVisibility(View.VISIBLE);
            add_to_trending.setVisibility(View.VISIBLE);
        }else{
            remove.setVisibility(View.GONE);
            add_to_trending.setVisibility(View.GONE);
        }
    }

    private void goToSinglePageProduct(String product_name) {
        String name = getIntent().getStringExtra("name");
        Intent intent = new Intent(ProductPage.this,SingleProductPage.class);
        intent.putExtra("collection_name",name);
        intent.putExtra("document_name",product_name);
        startActivity(intent);
    }

    private void check(String product_name, LinearLayout l1, LinearLayout l2, TextView update) {
        String email = fauth.getCurrentUser().getEmail();
        DocumentReference doc = fstore.collection("add_to_cart").document(email+product_name);
        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String quantity = value.getString("product_quantity");
                if (quantity != null){
                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.VISIBLE);
                    update.setText(quantity);
                }else{
                    l1.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.GONE);
                }
            }
        });

    };

    private void addInDatabase(String product_name, String product_price, String product_image, String product_rating, String quantity) {
        String email = fauth.getCurrentUser().getEmail();
        DocumentReference doc = fstore.collection("add_to_cart").document(email+product_name);
        Map<String,String> info = new HashMap<>();
        info.put("user_mail",email);
        info.put("product_name",product_name);
        info.put("product_price",product_price);
        info.put("product_image",product_image);
        info.put("product_rating",product_rating);
        info.put("product_quantity",quantity);
        doc.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProductPage.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductPage.this, "Failed to add", Toast.LENGTH_SHORT).show();
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

    class FetchViewHolder extends RecyclerView.ViewHolder {
        ImageView i1;
        TextView t2,t3;
        RatingBar ratingBar;
        LinearLayout l1,l2;
        Button addBtn,decBtn,incBtn;
        TextView update;
        CardView card;
        Button remove,add_to_trending;
        public FetchViewHolder(@NonNull View itemView) {
            super(itemView);
            i1 = itemView.findViewById(R.id.thumbnail);
            t2 = itemView.findViewById(R.id.pname);
            t3 = itemView.findViewById(R.id.price);
            ratingBar = itemView.findViewById(R.id.rating);
            card = itemView.findViewById(R.id.card);
            remove = itemView.findViewById(R.id.remove_product);
            add_to_trending = itemView.findViewById(R.id.add_to_trending_product);

            l1 = itemView.findViewById(R.id.addToCartLayout);
            l2 = itemView.findViewById(R.id.buttonLayout);
            addBtn = itemView.findViewById(R.id.cart);
            decBtn = itemView.findViewById(R.id.subtractBtn);
            incBtn = itemView.findViewById(R.id.addBtn);
            update = itemView.findViewById(R.id.textShow);
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
