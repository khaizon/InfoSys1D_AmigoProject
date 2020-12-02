package com.example.infosys1d_amigoproject.projectmanagement_tab;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infosys1d_amigoproject.MainActivity;
import com.example.infosys1d_amigoproject.R;
import com.example.infosys1d_amigoproject.Utils.FirebaseMethods;
import com.example.infosys1d_amigoproject.models.users_display;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class EditPojects extends AppCompatActivity {

    ImageView imageView;
    Button button,create_project;
    private static final int PICK_IMAGE = 1;
    public Uri imageUri;
    Uri downloadUrl;
    FirebaseStorage storage;
    StorageReference storageRef;
    TextInputLayout textInputLayout,textInputLayoutdescrip;
    String randomKey;
    ArrayList<String> skills;
    DatabaseReference myref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userref = FirebaseDatabase.getInstance().getReference();

    FirebaseMethods firebaseMethods;
    Project project;



    private Context mcontext;
    private ChipGroup mfilters;
    private ArrayList<String> selectedChipData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pojects);
        imageView = findViewById(R.id.editPimageview2);
        textInputLayout = findViewById(R.id.editPtextInputLayout);
        textInputLayoutdescrip = findViewById(R.id.editPtextInputLayout_description);
        button = findViewById(R.id.editPbutton);
        create_project = findViewById(R.id.editPbutton_create_project);
        firebaseMethods = new FirebaseMethods(getApplicationContext());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        selectedChipData = new ArrayList<>();


        mcontext = getApplicationContext();
        String[] filterList = mcontext.getResources().getStringArray(R.array.skills_list);

        mfilters = findViewById(R.id.editPfilterChipGroup);

        LayoutInflater inflater_0 = LayoutInflater.from(mcontext);
        for(String text: filterList){
            Chip newChip = (Chip) inflater_0.inflate(R.layout.chip_filter,null,false);
            System.out.println("skills asdf" + text);
            newChip.setText(text);
            mfilters.addView(newChip);}


        Intent intent = getIntent();
        String project_id = intent.getStringExtra("Project ID");
        myref = FirebaseDatabase.getInstance().getReference().child("Projects").child(project_id);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                project = snapshot.getValue(Project.class);
                Picasso.get().load(project.getThumbnail()).into(imageView);
                textInputLayout.getEditText().setText(project.getProjectitle());
                textInputLayoutdescrip.getEditText().setText(project.getProjectdescription());
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

                for (int i = 0; i < filterList.length; i++){
                    Chip chip = (Chip) mfilters.getChildAt(i);
                    if ( project.getSkillsrequired().contains(chip.getText().toString())){
                        chip.setChecked(true);
                    }

                }

                userref = FirebaseDatabase.getInstance().getReference().child("users_display").child(project.getCreatedby());
                userref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users_display user = snapshot.getValue(users_display.class);
                        Picasso.get().load(user.getProfile_picture()).into(imageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        create_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedChipData.clear();
                for(int i = 0; i<mfilters.getChildCount(); i++){
                    Chip chip = (Chip)mfilters.getChildAt(i);
                    if(chip.isChecked()){
                        selectedChipData.add(chip.getText().toString());
                    }
                }
                String projectKey = myref.child("Projects").push().getKey();
                Project new_proj = new Project(downloadUrl.toString(), textInputLayout.getEditText().getText().toString(),
                        textInputLayoutdescrip.getEditText().getText().toString(),
                        selectedChipData, new ArrayList<String>(Arrays.asList(firebaseMethods.getUserID())),
                        firebaseMethods.getUserID(), projectKey);

                myref.child("Projects").child(projectKey).setValue(new_proj);
                Intent intent1 = new Intent(EditPojects.this,MainActivity.class);
                startActivity(intent1);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(gallery,1);
            }
        });







    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData() != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            uploadpicture();
        }
        else{
            imageView.setImageResource(R.drawable.ic_android);
        }
    }

    private void uploadpicture() {
        randomKey = UUID.randomUUID().toString();
        StorageReference newRef = storageRef.child("images/" + randomKey);
        newRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        newRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = uri;
                            }
                        });
                        Snackbar.make(findViewById(R.id.upload), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                    };
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}