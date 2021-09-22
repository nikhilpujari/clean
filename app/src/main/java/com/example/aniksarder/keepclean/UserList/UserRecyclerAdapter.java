package com.example.aniksarder.keepclean.UserList;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aniksarder.keepclean.R;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>{

    public List<User> userList ;
    public Context context ;

    public  UserRecyclerAdapter(List<User> userList){
        this.userList=userList;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

         holder.UserName.setText(userList.get(position).getName());

         String user_image = userList.get(position).getImage();
         holder.setUserImage(user_image);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

         View mView;

         TextView UserName;
         CircleImageView UserProfileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            UserName = mView.findViewById(R.id.showUserName);

          }



        public void setUserImage(String downloadUri) {

            UserProfileImage = mView.findViewById(R.id.showUserProfile);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_image);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).into(UserProfileImage);

        }

    }



    }

