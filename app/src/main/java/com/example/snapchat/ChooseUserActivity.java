package com.example.snapchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseUserActivity extends AppCompatActivity {

    ListView chooseUserListView;

    // set two array lists that always correspond to each other, kind of like a map.
    private ArrayList<String> emails = new ArrayList<>();
    // Holds Document id's that belong to the user with the email address in the ArrayList above
    private ArrayList<String> documentIDs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        chooseUserListView = findViewById(R.id.chooseUserListView);

        getList();
        AdapterView.OnItemClickListener n = (parent, view, position, id) -> {
            sendSnapToUser(position); // saves snap as a sub-collection to the chosen user
            // Go back to SnapsActivity after clearing activity history.
            Intent intent = new Intent(ChooseUserActivity.this, SnapsActivity.class);
            // Flag clear top, wipes the history of the back button, so if clicked it stays where it's at and it doesn't go back to select user.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        };
        chooseUserListView.setOnItemClickListener(n);
    }

    // Saves snap as a sub-collection to the user that was chosen at index = position.
    // Creates Map of Snap fields sent as extras through CreateSnapActivity, finds the user by
    // position in the listView and index in documentIDs ArrayList set when populating the listView in getList()
    public void sendSnapToUser(int position){
        // Create a map of fields that holds all the information of a snap.
        Map<String, String> nestedSnaps = new HashMap<>();
        // Set the fields with data from the previous activity:
        nestedSnaps.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        nestedSnaps.put("imageName", getIntent().getStringExtra("imageName"));
        nestedSnaps.put("imageURL", getIntent().getStringExtra("imageURL"));
        nestedSnaps.put("message", getIntent().getStringExtra("message"));

        // Get the document ID of the user selected to send the snap to.
        // Document ID is set in the getList() method.
        String documentID = documentIDs.get(position);

        // Add the snap as a document in the sub-collection "snaps" for the user selected.
        FirebaseFirestore.getInstance().collection("users").document(documentID)
                .collection("snaps").add(nestedSnaps);
    }

    // Set an array adapter for our array list and populate the array, called every time we call with intent
    public void getList(){
        // Set an array adapter to populate our Array Lists of document Id's and emails
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, emails);
        chooseUserListView.setAdapter(adapter); // set the adapter to our ListView of users
        // Here we only need/want to read the emails and document id from all users in our database
        FirebaseFirestore.getInstance().collection("users").addSnapshotListener((values, error) -> {
            // Clear our emails and documentIDs lists.
            emails.clear();
            documentIDs.clear();
            // for each value found in the users collection
            for(DocumentSnapshot snapshot: values.getDocuments()){
                // Create instance of an object, set it to email
                Object email = snapshot.get("email");
                // if email is not null, get the email into a string and add to the arraylist.
                // add the corresponding document id that has that email into the documentIDs list.
                // Note: Because we can't have an email without an a document id, we can be sure that they will have corresponding indexes.
                if(email != null){
                    emails.add(email.toString());
                    documentIDs.add(snapshot.getId());
                    adapter.notifyDataSetChanged(); // adapter reloads the listview of users.
                }
            }
        });
    }


}