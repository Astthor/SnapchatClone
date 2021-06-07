package com.example.snapchat.repository;

import android.content.Intent;
import android.widget.Toast;

import com.example.snapchat.ChooseUserActivity;
import com.example.snapchat.CreateSnapActivity;
import com.example.snapchat.model.Snap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Repo {

    private static Repo repo = new Repo();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Repo r(){return repo;}

    /*public Snap uploadImage(byte[] data, String imageName){
        //FirebaseStorage.getInstance().getReference().child("images").child(imageName);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // set the location for our storage to upload
        StorageReference snapImageRef = storage.getReference().child("images").child(imageName);
        Snap snap = new Snap();
        // upload the image:
        snapImageRef.putBytes(data).addOnFailureListener(e -> {
            snap.setImageURL("FAIL");
            //Toast.makeText(CreateSnapActivity.this, "upload failed", Toast.LENGTH_SHORT).show();
        })

                .addOnSuccessListener(snapshot -> {
            //String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            System.out.println("snapIm.getDownloadUrl.tostring " + snapImageRef.getDownloadUrl().toString());

            // attention here, need to know if I get the url.
            //Snap snap = new Snap(currentUserEmail, snapImageRef.getDownloadUrl().toString(), )
            snapImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                System.out.println("in method in repo: " + downloadUri.toString());
                snap.setImageURL(downloadUri.toString());
                *//*Intent intent = new Intent(CreateSnapActivity.this, ChooseUserActivity.class);
                intent.putExtra("imageURL", downloadUri.toString());
                intent.putExtra("imageName", imageName);
                intent.putExtra("message", messageEditText.getText().toString());
                startActivity(intent);*//*
                return snap;
            });
        });
        //return snap;
    }*/


}
