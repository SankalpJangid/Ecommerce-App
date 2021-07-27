package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CartPageAll extends AppCompatActivity implements PaymentResultListener {

    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter,adapter1;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    int total = 0;
    RecyclerView recyclerView1;
    TextView total_price,textTotalAmount,textTotalBill;
    Button checkoutBtn;
    Spinner a1;
    int itemCount;
    Button addnewaddress;
    LinearLayout checkoutLayout;
    String user_phone_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_page_all);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recylelist);
        recyclerView1 = findViewById(R.id.recylelist2);
        fauth = FirebaseAuth.getInstance();
        total_price = findViewById(R.id.total_price);
        fstore = FirebaseFirestore.getInstance();
        textTotalAmount = findViewById(R.id.textTotalAmount);
        textTotalBill = findViewById(R.id.textTotalBill);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        String mail = fauth.getCurrentUser().getEmail();
        a1 = findViewById(R.id.chooseAddress);
        checkoutLayout = findViewById(R.id.checkoutLayout);
        addnewaddress = findViewById(R.id.addnewaddress);

        String email = fauth.getCurrentUser().getEmail();
        if (email.equals("s@gmail.com")){
            findViewById(R.id.navbarID).setVisibility(View.VISIBLE);
            findViewById(R.id.usernavbarID).setVisibility(View.GONE);
        }else{
            findViewById(R.id.navbarID).setVisibility(View.GONE);
            findViewById(R.id.usernavbarID).setVisibility(View.VISIBLE);
        }

        Query qqq = fstore.collection("add_to_cart").whereEqualTo("user_mail",mail);
        qqq.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                System.out.println(value.getDocuments());
                if (value.getDocuments().isEmpty()){
                    total_price.setVisibility(View.GONE);
                    textTotalAmount.setVisibility(View.GONE);
                    textTotalBill.setVisibility(View.GONE);
                    checkoutLayout.setVisibility(View.GONE);
                }else{
                    total_price.setVisibility(View.VISIBLE);
                    textTotalAmount.setVisibility(View.VISIBLE);
                    textTotalBill.setVisibility(View.VISIBLE);
                    checkoutLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        DocumentReference for_phoneNo = fstore.collection("Users").document(fauth.getCurrentUser().getEmail());
        for_phoneNo.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                user_phone_no = value.getString("phone");
            }
        });


        Query d = fstore.collection("saved_address").whereEqualTo("user_mail",mail);
        d.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> detail1 = new ArrayList<>();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    AddressInformation note = documentSnapshot.toObject(AddressInformation.class);
                    String title = note.getUser_home();
                    System.out.println(title);
                    detail1.add(title);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CartPageAll.this,R.layout.support_simple_spinner_dropdown_item,detail1);
                if (adapter.getCount() == 0){
                    addnewaddress.setVisibility(View.VISIBLE);
                    addnewaddress.setText("Add Address");
                    checkoutBtn.setVisibility(View.GONE);
                    a1.setVisibility(View.GONE);
                }else{
                    addnewaddress.setVisibility(View.GONE);
                    checkoutBtn.setVisibility(View.VISIBLE);
                    a1.setVisibility(View.VISIBLE);
                    System.out.println(detail1);
                    a1.setAdapter(adapter);
                }

            }
        });



        addnewaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddressPage.class));
            }
        });

        /*checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query product = fstore.collection("add_to_cart").whereEqualTo("user_mail",mail);
                product.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    ArrayList<String> image = new ArrayList<>();
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> price = new ArrayList<>();
                    ArrayList<String> productquantity = new ArrayList<>();
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            FetchAllCartProduct note = documentSnapshot.toObject(FetchAllCartProduct.class);
                            image.add(note.getProduct_image());
                            names.add(note.getProduct_name());
                            price.add(note.getProduct_price());
                            productquantity.add(note.getProduct_quantity());
                        }
                        String name = a1.getSelectedItem().toString();
                        sendInformation(name,image,names,price,productquantity);
                    }
                });
            }
        });*/

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checkout checkout = new Checkout();
                checkout.setKeyID("rzp_test_PgXUnx2nqsOWGE");
                checkout.setImage(R.drawable.ic_shopping_cart_black_24dp);
                JSONObject object = new JSONObject();
                int amount = Math.round(total * 100);
                try {
                    object.put("name","CART");
                    object.put("description","Test Amount");
                    object.put("theme.color","#DC2D33");
                    object.put("currency","INR");
                    object.put("amount",amount);
                    object.put("prefill.contact",user_phone_no);
                    object.put("prefill.email",fauth.getCurrentUser().getEmail());
                    checkout.open(CartPageAll.this,object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        Query query = fstore.collection("add_to_cart").whereEqualTo("user_mail",mail);
        FirestoreRecyclerOptions<FetchAllCartProduct> options = new FirestoreRecyclerOptions.Builder<FetchAllCartProduct>().setQuery(query,FetchAllCartProduct.class).build();
        adapter = new FirestoreRecyclerAdapter<FetchAllCartProduct, FetchAllProductCollectionViewHolder>(options) {
            @NonNull
            @Override
            public FetchAllProductCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_page_layout,parent,false);
                return new FetchAllProductCollectionViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FetchAllProductCollectionViewHolder holder, int position, @NonNull FetchAllCartProduct model) {
                holder.pname.setText(model.getProduct_name());
                holder.price.setText(model.getProduct_price());
                holder.quantity.setText(model.getProduct_quantity());
                holder.ratingBar.setRating(Float.parseFloat(model.getProduct_rating()));
                Glide.with(CartPageAll.this).load(model.getProduct_image()).into(holder.i1);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mail = fauth.getCurrentUser().getEmail();
                        DocumentReference doc = fstore.collection("add_to_cart").document(mail+model.getProduct_name());
                        doc.delete();
                        recreate();
                    }
                });
            }
        };
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);




        Query query2 = fstore.collection("add_to_cart").whereEqualTo("user_mail",mail);
        FirestoreRecyclerOptions<FetchAllCartProduct> options2 = new FirestoreRecyclerOptions.Builder<FetchAllCartProduct>().setQuery(query2,FetchAllCartProduct.class).build();
        adapter1 = new FirestoreRecyclerAdapter<FetchAllCartProduct, TotalBillViewHolder>(options2) {
            @NonNull
            @Override
            public TotalBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_layout,parent,false);
                return new TotalBillViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TotalBillViewHolder holder, int position, @NonNull FetchAllCartProduct model) {
                holder.n.setText(model.getProduct_name());
                holder.q.setText(model.getProduct_quantity());
                holder.p.setText(model.getProduct_price());
                int quantity = Integer.valueOf(model.getProduct_quantity());
                int price = Integer.valueOf(model.getProduct_price());
                total += quantity*price;
                System.out.println(total);
                total_price.setText(String.valueOf(total));
            }

        };
        recyclerView1.setHasFixedSize(false);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(adapter1);
    }

    private void sendInformation(String name, ArrayList<String> image, ArrayList<String> names, ArrayList<String> price, ArrayList<String> productquantity) {
        String mail = fauth.getCurrentUser().getEmail();
        Query q = fstore.collection("saved_address").whereEqualTo("user_mail",mail).whereEqualTo("user_home",name);
        q.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            String home,area,street,location,Address;
            String product_information="";
            Map<String,Map> j = new HashMap<>();

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    AddressInformation note = documentSnapshot.toObject(AddressInformation.class);
                    home = note.getUser_home();
                    area = note.getUser_area();
                    street = note.getUser_street();
                    location = note.getUser_location();
                }
                Address = (home+" "+street+" "+area);
                for (int i = 0; i < names.size(); i++) {
                    String new_name = UUID.randomUUID().toString();
                    DocumentReference doc = fstore.collection("current_order").document(new_name);
                    Map<String,String> info = new HashMap<>();
                    info.put("user_mail",fauth.getCurrentUser().getEmail());
                    info.put("product_name",names.get(i)+"\n");
                    info.put("product_price",price.get(i)+"\n");
                    info.put("product_quantity",productquantity.get(i)+"\n");
                    info.put("product_image",image.get(i));
                    info.put("order_ID",new_name);
                    info.put("user_location",location);
                    info.put("user_address",Address);
                    doc.set(info);
                    j.put("product"+String.valueOf(i),info);

                }
                System.out.println(j.size());

                for (int i = 0; i < j.size(); i++) {
                    product_information += j.get("product"+String.valueOf(i)).toString()+"\n\n\n\n";
                }

                System.out.println(product_information);
                SmsManager smsManager = SmsManager.getDefault();
                String longMessage = product_information + "\n" + "Address : "+"\n"+Address;
                ArrayList<String> parts = smsManager.divideMessage(longMessage);
                smsManager.sendMultipartTextMessage("9001177514",null, parts,null,null);

                for (int i = 0; i < names.size(); i++) {
                    DocumentReference docs = fstore.collection("add_to_cart").document(mail+names.get(i));
                    docs.delete();
                }
                Toast.makeText(CartPageAll.this, "Order Successfully Placed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),Current_Order.class));
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter1.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter1.stopListening();
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

    @Override
    public void onPaymentSuccess(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment detail");
        builder.setMessage(s);
        builder.show();
        recreate();

    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        recreate();
    }

    class FetchAllProductCollectionViewHolder extends RecyclerView.ViewHolder {
        ImageView i1;
        TextView pname,price,quantity;
        RatingBar ratingBar;
        ImageButton delete;
        public FetchAllProductCollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            i1 = itemView.findViewById(R.id.thumbnail);
            pname = itemView.findViewById(R.id.pname);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            ratingBar = itemView.findViewById(R.id.rating);
            delete = itemView.findViewById(R.id.imageButton);
        }
    }

    class TotalBillViewHolder extends RecyclerView.ViewHolder {
        TextView n,q,p;
        public TotalBillViewHolder(@NonNull View itemView) {
            super(itemView);
            n = itemView.findViewById(R.id.pdname);
            q = itemView.findViewById(R.id.pdquant);
            p = itemView.findViewById(R.id.pdprice);
        }
    }

    public void clickCurrentOrder(View view){
        startActivity(new Intent(getApplicationContext(),Current_Order.class));
    }

    public void clickOrderHistory(View view){
        startActivity(new Intent(getApplicationContext(),YourOrderHistory.class));
    }

    public void savedAddress(View view){
        startActivity(new Intent(getApplicationContext(),SavedAddress.class));
    }
}
