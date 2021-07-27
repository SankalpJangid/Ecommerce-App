package com.example.giftswala;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCollection extends AppCompatActivity {
    FirebaseFirestore fstore;
    public Uri imageuri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    DrawerLayout drawerLayout;
    ImageView i;
    EditText e;
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collection);
        e = findViewById(R.id.collectionName);
        b = findViewById(R.id.submit);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        drawerLayout = findViewById(R.id.drawer_layout);
        fstore = FirebaseFirestore.getInstance();
        i = findViewById(R.id.collectioncover);


        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference doc = fstore.collection("collections").document(e.getText().toString());
                Map<String,String> info = new HashMap<>();
                info.put("collection_name",e.getText().toString());
                info.put("collection_image","");
                doc.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        doc.update("collection_timestamp", FieldValue.serverTimestamp());
                        Toast.makeText(AddCollection.this, "Added", Toast.LENGTH_SHORT).show();
                        e.setText("");
                     }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCollection.this, "failed to add", Toast.LENGTH_SHORT).show();
                    }
                });
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
            i.setImageURI(imageuri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final String Random = UUID.randomUUID().toString();
        final ProgressDialog pd = new ProgressDialog(AddCollection.this);
        pd.setTitle("uploading....");
        pd.show();
        System.out.println("1");
        StorageReference ref = storageReference.child("images/" + Random);
        System.out.println("good");
        ref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(AddCollection.this, "image uploaded", Toast.LENGTH_SHORT).show();
                geturl(Random);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddCollection.this, "image not uploaded", Toast.LENGTH_SHORT).show();
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
        StorageReference ref = storageReference.child("images/"+ random);
        b = findViewById(R.id.submit);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //This is the url of image to show in the layout
                final String url = uri.toString();
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference doc = fstore.collection("collections").document(e.getText().toString());
                        Map<String,String> info = new HashMap<>();
                        info.put("collection_name",e.getText().toString());
                        info.put("collection_image",url);
                        doc.set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                doc.update("collection_timestamp", FieldValue.serverTimestamp());
                                e.setText("");
                                Toast.makeText(AddCollection.this, "add to collection", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddCollection.this, "failed to add", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddCollection.this, "Failed to add a collection", Toast.LENGTH_SHORT).show();
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
        recreate();
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
