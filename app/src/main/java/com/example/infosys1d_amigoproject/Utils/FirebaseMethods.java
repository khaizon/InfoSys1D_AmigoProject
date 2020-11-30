package com.example.infosys1d_amigoproject.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.infosys1d_amigoproject.R;
import com.example.infosys1d_amigoproject.models.Userdataretrieval;
import com.example.infosys1d_amigoproject.models.users_display;
import com.example.infosys1d_amigoproject.models.users_private;
import com.example.infosys1d_amigoproject.models.Userdataretrieval;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseMethods {


    final String TAG = "FirebaseMethods";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthstatelistner;
    private String userID;
    private FirebaseDatabase mfirebasedatabase;
    private DatabaseReference myRef;


    private Context mContext;

    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
        mfirebasedatabase = FirebaseDatabase.getInstance();
        myRef = mfirebasedatabase.getReference();

        if(mAuth.getCurrentUser()!=null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }


    public boolean checkifemailexists(String email, DataSnapshot dataSnapshot){
        Log.d(TAG, "checkifemailexists: checking if "+email+" is already in use");

        users_private usersprivate = new users_private();

        //every iteration gets 1 child of the root node and all its info
        //0th iteration is users_display
        //1st iteration is users_private
        for(DataSnapshot ds: dataSnapshot.getChildren()){
            Log.d(TAG, "checkifemailexists: datasnapshot "+ds);
            usersprivate.setEmail(ds.getValue(users_private.class).getEmail());

            Log.d(TAG, "checkifemailexists: datasnapshot user email matches "+ usersprivate.getEmail());
            if(usersprivate.getEmail().equals(email)){
                Log.d(TAG, "checkifemailexists: Found a match!");
                return true;
            }


        }

        return false;

    }






    /**
     * Register a new email and password to Firebase Authentication
     * @param firstname
     * @param lastname
     * @param email
     * @param password
     */
    public void registerNewEmail(final String firstname, final String lastname, final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Send verification email
                    sendVerificationEmail();


                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    setUserID(mAuth.getCurrentUser().getUid());
                    Toast.makeText(mContext, "Account created", Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(mContext, "Account Creation failed",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(mContext,"Could not send verification email", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }


    /**
     * Add information to user_display and user_private nodes
     * @param firstname
     * @param lastname
     * @param email
     */

    public void addNewUser(String firstname, String lastname, String email){
        users_private usersprivate1 = new users_private(email, firstname, lastname, userID);



        myRef.child(mContext.getString(R.string.db_usersprivate)).child(userID).setValue(usersprivate1);

        users_display display = new users_display(firstname+" "+lastname);


        ArrayList<String> skilllist = new ArrayList<>();
        skilllist.add("Python");
        skilllist.add("Java");
        skilllist.add("CSS/HTML/JS");
        skilllist.add("C");

        //debugging chips
        display.setSkills(skilllist);

        myRef.child(mContext.getString(R.string.db_usersdisplay)).child(userID).setValue(display);





    }

    /**
     * Retrieves the profile display information for the user that is currently logged in
     * database: users_display node
     * @param dataSnapshot
     * @return
     */
    public Userdataretrieval getUserData(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserData: retrieving user information from firebase ");

        users_display display = new users_display();
        users_private privatedata = new users_private();

        for(DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals(mContext.getString(R.string.db_usersdisplay))){
                Log.d(TAG, "getUserData: getting display data : datasnapshot: "+ ds);
                try {
                    display.setName(ds.child(userID).getValue(users_display.class).getName());
                    display.setAbout_me(ds.child(userID).getValue(users_display.class).getAbout_me());
                    display.setBio(ds.child(userID).getValue(users_display.class).getBio());
                    display.setProjects_completed(ds.child(userID).getValue(users_display.class).getProjects_completed());
                    display.setLooking_for(ds.child(userID).getValue(users_display.class).getLooking_for());
                    display.setSkills(ds.child(userID).getValue(users_display.class).getSkills());
                    display.setChats(ds.child(userID).getValue(users_display.class).getChats());
                    display.setCurrent_projects(ds.child(userID).getValue(users_display.class).getCurrent_projects());
                    display.setProjects_completed_list(ds.child(userID).getValue(users_display.class).getProjects_completed_list());
                    Log.d(TAG, "getUserData: retrieved user display data "+display.toString());
                }catch (NullPointerException E){
                    Log.d(TAG, "getUserData: null field encountered ");
                }
            }
            if(ds.getKey().equals(mContext.getString(R.string.db_usersprivate))){
                Log.d(TAG, "getUserData: getting private data : datasnapshot: "+ ds);
                try {
                    privatedata.setFirstname(ds.child(userID).getValue(users_private.class).getFirstname());
                    privatedata.setLastname(ds.child(userID).getValue(users_private.class).getLastname());
                    privatedata.setEmail(ds.child(userID).getValue(users_private.class).getEmail());
                    privatedata.setUser_id(ds.child(userID).getValue(users_private.class).getUser_id());
                    Log.d(TAG, "getUserData: retrieved user private data "+privatedata.toString());
                }catch (NullPointerException E){
                    Log.d(TAG, "getUserData: null field encountered");
                }
            }
        }
        return new Userdataretrieval(display, privatedata);
    }


    public String getUserID() {
        return userID;
    }

    private void setUserID(String userID) {
        this.userID = userID;
    }
}