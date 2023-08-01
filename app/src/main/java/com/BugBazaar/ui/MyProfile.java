package com.BugBazaar.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.BugBazaar.utils.DeviceDetails;
import com.BugBazaar.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

        public class MyProfile extends AppCompatActivity {
            private FirebaseStorage firebaseStorage;


            private static final int SELECT_PHOTO_REQUEST = 1;
            private ImageView imageView;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_myprofile);
                FirebaseApp.initializeApp(this);
                firebaseStorage = FirebaseStorage.getInstance();
                imageView = findViewById(R.id.imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectPhotoFromGallery();
                    }
                });



                // Load and display the image from internal storage
                loadAndDisplayImage();
                loadProfileData();
            }

            private void loadProfileData() {
                // Get SharedPreferences instance
                SharedPreferences sharedPreferences = getSharedPreferences("sharedprefs", Context.MODE_PRIVATE);

                // Check if the SharedPreferences file exists and the data is saved
                if (sharedPreferences.contains("name") && sharedPreferences.contains("email")
                        && sharedPreferences.contains("mobile") && sharedPreferences.contains("address")) {
                    // Data exists in SharedPreferences, load and set the EditText values
                    String nameData = sharedPreferences.getString("name", "");
                    String emailData = sharedPreferences.getString("email", "");
                    String mobileData = sharedPreferences.getString("mobile", "");
                    String addressData = sharedPreferences.getString("address", "");
                    Log.d("hello",nameData);

                    txtViewName.setText(nameData);
                    txtViewEmail.setText(emailData);
                    txtViewMobile.setText(mobileData);
                    txtViewAddress.setText(addressData);

                    // Get a reference to the Firebase Storage location where you want to upload the image

                }
            }


            private void selectPhotoFromGallery() {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO_REQUEST);
            }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                if (requestCode == SELECT_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        try {
                            Uri selectedImageUri = data.getData();
                            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                            imageView.setImageBitmap(imageBitmap);

                            // Save the image to internal storage
                            saveImageToInternalStorage(imageBitmap);
                            uploadImageToFirebaseStorage(selectedImageUri);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }



            private void uploadImageToFirebaseStorage(Uri imageUri) {
               String device= DeviceDetails.getDeviceName();
                // Get a reference to the Firebase Storage location where you want to upload the image
                StorageReference storageRef = firebaseStorage.getReference().child(device+"/" + System.currentTimeMillis() + ".png");

                // Upload the image
                storageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            Log.d("hello","success");
                            // Image upload successful, do something if needed
                        })
                        .addOnFailureListener(exception -> {
                            Log.d("hello","fail");
                            // Handle unsuccessful uploads, do something if needed
                        });
            }



            private void loadAndDisplayImage() {
                try {
                    String filename = "my_image.png";
                    File directory = getFilesDir();
                    File file = new File(directory, filename);

                    if (file.exists()) {
                        Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        imageView.setImageBitmap(imageBitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void saveImageToInternalStorage(Bitmap imageBitmap) {
                try {
                    String filename = "my_image.png";
                    File directory = getFilesDir();
                    File file = new File(directory, filename);

                    FileOutputStream outputStream = new FileOutputStream(file);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

            //Make all Text Views editable when "Edit Button" is clicked.
            private void makeAllTextViewsEditable() {
                // Hide the "Edit Profile" button
                editProfileBtn.setVisibility(View.GONE);
                // Show the "Save Profile" button
                saveProfile.setVisibility(View.VISIBLE);

                // Make all TextViews invisible
                txtViewName.setVisibility(View.INVISIBLE);
                txtViewEmail.setVisibility(View.INVISIBLE);
                txtViewMobile.setVisibility(View.INVISIBLE);
                txtViewAddress.setVisibility(View.INVISIBLE);

                // Make all EditTexts visible and set their text to match the corresponding TextViews
                editTxtName.setVisibility(View.VISIBLE);
                editTxtName.setText(txtViewName.getText());

                editTxtEmail.setVisibility(View.VISIBLE);
                editTxtEmail.setText(txtViewEmail.getText());

                editTextMobile.setVisibility(View.VISIBLE);
                editTextMobile.setText(txtViewMobile.getText());

                editTxtAddress.setVisibility(View.VISIBLE);
                editTxtAddress.setText(txtViewAddress.getText());
            }
            //Saving data entered in editViews by user to local variables.
            private void saveDataToLocalVariable() {
                // Get the text entered by the user in the EditTexts
                String editedName = editTxtName.getText().toString();
                String editedEmail = editTxtEmail.getText().toString();
                String editedMobile = editTextMobile.getText().toString();
                String editedAddress = editTxtAddress.getText().toString();

                // You can save these strings to local variables here or perform any other desired operation with them
                //<<<<<<AMIT KUMAR you can use these variables for fetching and storing contents via content providers>>>>>>
                String nameData = editedName;
                String emailData = editedEmail;
                String mobileData = editedMobile;
                String addressData = editedAddress;

                //
// Inside your activity or fragment
                SharedPreferences sharedPreferences = getSharedPreferences("sharedprefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", nameData);
                editor.putString("email", emailData);
                editor.putString("mobile", mobileData);
                editor.putString("address", addressData);

// Commit the changes to save the data
                editor.apply();

                uploaddatatofirebase();

                //



                // Display a toast message to indicate that the data is saved
                Toast.makeText(this, "Profile has been updated", Toast.LENGTH_SHORT).show();
                revertToTextViews();
            }

            private void uploaddatatofirebase() {



            }

            //After clicking on "Save Profile" revert all EditTextViews into TextViews. Also Hide "Save Profile" button and "Edit Profile" button is visible.
            private void revertToTextViews() {
                // Show the "Edit Profile" button
                editProfileBtn.setVisibility(View.VISIBLE);
                // Hide the "Save profile" button
                saveProfile.setVisibility(View.INVISIBLE);

                // Hide the EditTexts and show the TextViews again
                editTxtName.setVisibility(View.INVISIBLE);
                editTxtEmail.setVisibility(View.INVISIBLE);
                editTextMobile.setVisibility(View.INVISIBLE);
                editTxtAddress.setVisibility(View.INVISIBLE);

                txtViewName.setVisibility(View.VISIBLE);
                txtViewEmail.setVisibility(View.VISIBLE);
                txtViewMobile.setVisibility(View.VISIBLE);
                txtViewAddress.setVisibility(View.VISIBLE);

                // Update the TextViews with the latest data from the EditTexts
                txtViewName.setText(editTxtName.getText());
                txtViewEmail.setText(editTxtEmail.getText());
                txtViewMobile.setText(editTextMobile.getText());
                txtViewAddress.setText(editTxtAddress.getText());
            }

            // Handle "Edit Profile" button click
            public void onSaveProfileClick(View view) {
                // Get the text entered by the user in the EditTexts
                String editedName = editTxtName.getText().toString();
                String editedEmail = editTxtEmail.getText().toString();
                String editedMobile = editTextMobile.getText().toString();
                String editedAddress = editTxtAddress.getText().toString();

                // Update the TextViews with the edited text from EditTexts
                txtViewName.setText(editedName);
                txtViewEmail.setText(editedEmail);
                txtViewMobile.setText(editedMobile);
                txtViewAddress.setText(editedAddress);

                // Hide the EditTexts and show the TextViews again
                editTxtName.setVisibility(View.INVISIBLE);
                editTxtEmail.setVisibility(View.INVISIBLE);
                editTextMobile.setVisibility(View.INVISIBLE);
                editTxtAddress.setVisibility(View.INVISIBLE);

                txtViewName.setVisibility(View.VISIBLE);
                txtViewEmail.setVisibility(View.VISIBLE);
                txtViewMobile.setVisibility(View.VISIBLE);
                txtViewAddress.setVisibility(View.VISIBLE);


