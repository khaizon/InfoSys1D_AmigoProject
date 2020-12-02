package com.example.infosys1d_amigoproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infosys1d_amigoproject.R;
import com.example.infosys1d_amigoproject.chat_tab.MessageActivity;
import com.example.infosys1d_amigoproject.models.Userdataretrieval;
import com.example.infosys1d_amigoproject.models.users_display;
import com.example.infosys1d_amigoproject.models.users_private;
import com.example.infosys1d_amigoproject.projectmanagement_tab.Project;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.Viewholder> {

    private Context mcontext;
    private ArrayList<Userdataretrieval> mUsers;
    private ArrayList<Userdataretrieval> mUsersAll;
    Activity activity;
    DatabaseReference myref;
    private boolean ischat;

    public ApplicantAdapter(Context mcontext, ArrayList<Userdataretrieval> mUsers){
        this.mUsers = mUsers;
        this.mcontext = mcontext;
        this.activity = activity;
        this.ischat = ischat;
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        private ImageView icon_on, icon_off;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.Chat_username);
            profile_image = itemView.findViewById(R.id.Chat_Profile_Image);
        }
    }

    public void setmUsersAll(ArrayList<Userdataretrieval> mUsersAll) {
        this.mUsersAll = mUsersAll;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.useritem_applicant, parent, false);


        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        Userdataretrieval user = mUsers.get(position);

        users_display userdisplay = user.getUsersdisplay();
        final users_private userprivate = user.getUsersprivate();
        holder.username.setText(userdisplay.getName());
        if(userdisplay.getProfile_picture().equals("none")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Picasso.get().load(userdisplay.getProfile_picture()).into(holder.profile_image);
        }

        System.out.println(userdisplay.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, MessageActivity.class);
                intent.putExtra("userid", userprivate.getUser_id());
                mcontext.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}