package com.example.snapchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.snapchat.model.Snap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SnapsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ListView snapsListView;
    ArrayList<String> emails = new ArrayList<>();
    ArrayList<String> snapID = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);
        mAuth = FirebaseAuth.getInstance();

        snapsListView = findViewById(R.id.snapsListView);



        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, emails);
        snapsListView.setAdapter(adapter);

        db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("snaps").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                emails.clear();
                snapID.clear();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Snap snap = documentSnapshot.toObject(Snap.class);
                    emails.add(snap.getFrom());
                    System.out.println("documentSnapshot.getID" + documentSnapshot.getId());
                    snapID.add(documentSnapshot.getId());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        AdapterView.OnItemClickListener adapterViewListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SnapsActivity.this, viewSnapActivity.class);
                intent.putExtra("docID", snapID.get(position));
                startActivity(intent);
            }
        };
        snapsListView.setOnItemClickListener(adapterViewListener);

    }

    // method called when the menu button of the device is pressed or if SnapsActivity.openOptionsMenu() is called
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create inflater to inflate menu - MenuInflater
        MenuInflater inflater = new MenuInflater(this);
        // inflate with snaps.xml, and parameter menu
        inflater.inflate(R.menu.snaps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.createSnap){
            // go to new Activity for uploading/taking images and setting text
            Intent intent = new Intent(this, CreateSnapActivity.class);
            startActivity(intent);
        } else if(item.getItemId() == R.id.logout){

            mAuth.signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
    }
}