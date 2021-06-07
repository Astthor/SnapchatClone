package com.example.snapchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.ChineseCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.snapchat.model.Snap;
import com.example.snapchat.repository.Repo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class CreateSnapActivity extends AppCompatActivity {

    // User set variables:
    private ImageView createSnapImageView;
    private EditText messageEditText;

    // Generate random ID for the image with jpg extension:
    private String imageName = UUID.randomUUID().toString() + ".jpg";

    /*
    Methods in this Activity:

    public void nextClicked(View view)
    public Bitmap drawTextToBitmap(Bitmap bitmap)
    public void chooseImageClicked(View view)
    public void getPhoto()
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_snap);

        createSnapImageView = findViewById(R.id.createSnapImageView);
        messageEditText = findViewById(R.id.messageEditText);

    }

    // set the imageView, call drawTextOnBitmap to add text to the image, upload the image.
    // if successful, create intent and start next activity where user chooses a user to send to
    public void nextClicked(View view){
        // Enable the drawing cache and draw the view in a bitmap
        createSnapImageView.setDrawingCacheEnabled(true);
        createSnapImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) createSnapImageView.getDrawable()).getBitmap();

        bitmap = drawTextToBitmap(bitmap);
        // getting our image into the correct format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // convert to bytes:
        byte[] data = baos.toByteArray();

        // set the location for our storage to upload
        StorageReference snapImageRef = FirebaseStorage.getInstance().getReference().child("images").child(imageName);

        // upload the image:
        snapImageRef.putBytes(data).addOnFailureListener(e -> {
            Toast.makeText(CreateSnapActivity.this, "upload failed", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(snapshot -> {
            //String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            System.out.println("snapIm.getDownloadUrl.tostring " + snapImageRef.getDownloadUrl().toString());
            snapImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                Intent intent = new Intent(CreateSnapActivity.this, ChooseUserActivity.class);
                intent.putExtra("imageURL", downloadUri.toString());
                intent.putExtra("imageName", imageName);
                intent.putExtra("message", messageEditText.getText().toString());
                startActivity(intent);
            });
        });
    }

    // Setting text to our bitmap/image:
    public Bitmap drawTextToBitmap(Bitmap bitmap){
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null){
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are immutable, so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap); // new canvas for our bitmap

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // new anti-aliased paint
        textPaint.setColor(Color.BLACK); // set text color
        textPaint.setTextSize((int)(40)); // text size in pixels
        textPaint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // text shadow

        // Align our text to the center, variables:
        textPaint.setTextAlign(Paint.Align.CENTER);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) /2 ));
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        // ---> stackOverflow: https://stackoverflow.com/questions/11120392/android-center-text-on-canvas

        // draw the text with textPaint values set:
        canvas.drawText(messageEditText.getText().toString(), xPos, yPos, textPaint);

        return bitmap;
    }

    // our Choose Image button was clicked:
    public void chooseImageClicked(View view){
        // Check if we have permission to access external storage, if not, ask the user.
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            // if we do, call getPhoto()
            getPhoto();
        }
    }

    // Calls an Activity using an intent provided by Android that lets us browse and pick an image from the phones Media.
    // Automatically calls onActivityResult upon a result.
    public void getPhoto() {
        System.out.println("Picking a new photo here!");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    // The method called when user picks a photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // location of our selected image:
        Uri selectedImage = data.getData();

        // check if the requestcode matches the requestcode set in getPhoto,
        // that the result is okay and that we have some data to work with.
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                // Get an instance of the bitmap and set it to the imageView:
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                createSnapImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // When a request for a permission is made the following method will be called:
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // check if proper requestCode :
        if (requestCode == 1) {
            // check if grantResults is PERMISSION_GRANTED - if so, then call the getPhoto method.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

}