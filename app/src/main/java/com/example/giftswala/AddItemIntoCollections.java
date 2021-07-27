package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddItemIntoCollections extends AppCompatActivity {

    EditText e1,e2,e3,e4;
    ImageView i1;
    Spinner a1;
    Button b;
    FirebaseFirestore fstore;
    DrawerLayout drawerLayout;
    public Uri imageuri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_into_collections);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        e1 = findViewById(R.id.productname);
        e2 = findViewById(R.id.productprice);
        e3 = findViewById(R.id.productdesc);
        a1 = findViewById(R.id.collectName);
        e4 = findViewById(R.id.productrating);
        b = findViewById(R.id.submit);
        i1 = findViewById(R.id.collectioncover);
        fstore = FirebaseFirestore.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);



        CollectionReference d = fstore.collection("collections");
        d.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> detail1 = new ArrayList<>();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    All_Collections note = documentSnapshot.toObject(All_Collections.class);
                    String title = note.getCollection_name();
                    System.out.println(title);
                    detail1.add(title);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddItemIntoCollections.this,R.layout.support_simple_spinner_dropdown_item,detail1);
                System.out.println(detail1);
                a1.setAdapter(adapter);
            }
        });

        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });


    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();
            i1.setImageURI(imageuri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final String Random = UUID.randomUUID().toString();
        final ProgressDialog pd = new ProgressDialog(AddItemIntoCollections.this);
        pd.setTitle("uploading....");
        pd.show();
        System.out.println("1");
        StorageReference ref = storageReference.child("Product_images/" + Random);
        System.out.println("good");
        ref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(AddItemIntoCollections.this, "image uploaded", Toast.LENGTH_SHORT).show();
                geturl(Random);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddItemIntoCollections.this, "image not uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                System.out.println("2");
                pd.setMessage("Percentage: "+ (int)progressPercentage + "%");
            }
        });
    }

    private void geturl(final String random) {
        StorageReference ref = storageReference.child("Product_images/"+ random);
        b = findViewById(R.id.submit);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //This is the url of image to show in the layout
                final String url = uri.toString();
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference doc2 = fstore.collection("all_products").document(e1.getText().toString());
                        DocumentReference doc = fstore.collection(a1.getSelectedItem().toString()).document(e1.getText().toString());
                        Map<String,String> info = new HashMap<>();
                        info.put("product_name",e1.getText().toString());
                        info.put("product_desc",e3.getText().toString());
                        info.put("product_price",e2.getText().toString());
                        info.put("product_rating",e4.getText().toString());
                        info.put("product_image",url);
                        doc.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                doc.update("product_timestamp", FieldValue.serverTimestamp());
                                e1.setText("");
                                e2.setText("");
                                e3.setText("");
                                e4.setText("");
                                doc2.set(info);
                                doc2.update("product_collection",a1.getSelectedItem().toString());
                                doc2.update("product_timestamp",FieldValue.serverTimestamp());
                                Toast.makeText(AddItemIntoCollections.this, "add to collection", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddItemIntoCollections.this, "failed to add", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddItemIntoCollections.this, "Failed to add a collection", Toast.LENGTH_SHORT).show();
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
        recreate();
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
        startActivity(new Intent(getApplicationContext(), SavedAddress.class));
    }

    public void clickOrderHistory(View view){
        startActivity(new Intent(getApplicationContext(),YourOrderHistory.class));
    }
    public void clickCurrentOrder(View view){
        startActivity(new Intent(getApplicationContext(),Current_Order.class));
    }
}
